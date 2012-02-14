/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.vnd.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorKeyConstants;
import org.kuali.kfs.vnd.VendorParameterConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.VendorUtils;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.businessobject.VendorTaxChange;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class VendorMaintainableImpl extends FinancialSystemMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VendorMaintainableImpl.class);

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#setGenerateDefaultValues(boolean)
     */
	@Override
    public void setGenerateDefaultValues(String docTypeName) {
        super.setGenerateDefaultValues(docTypeName);
        if (this.getBusinessObject().getBoNotes().isEmpty()) {
            setVendorCreateAndUpdateNote(VendorConstants.VendorCreateAndUpdateNotePrefixes.ADD);
        }
    }

    /**
     * Overrides the kuali default documents title with a Vendor-specific document title style
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getDocumentTitle(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public String getDocumentTitle(MaintenanceDocument document) {
        String documentTitle = "";
        // Check if we are choosing to override the Kuali default document title.
        if (SpringContext.getBean(ParameterService.class).getIndicatorParameter(VendorDetail.class, VendorParameterConstants.OVERRIDE_VENDOR_DOC_TITLE)) {
            // We are overriding the standard with a Vendor-specific document title style.
            if (document.isOldBusinessObjectInDocument()) {
                documentTitle = "Edit Vendor - ";
            }
            else {
                documentTitle = "New Vendor - ";
            }

            try {
                Person initUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPersonByPrincipalName(document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId());
                documentTitle += initUser.getCampusCode();
            }
            catch (Exception e) {
                throw new RuntimeException("Document Initiator not found " + e.getMessage());
            }

            VendorDetail newBo = (VendorDetail) document.getNewMaintainableObject().getBusinessObject();

            if (StringUtils.isNotBlank(newBo.getVendorName())) {
                documentTitle += " '" + newBo.getVendorName() + "'";
            } 
            else {            
                if (StringUtils.isNotBlank(newBo.getVendorFirstName())) {
                    documentTitle += " '" + newBo.getVendorFirstName() + " ";
                    if (StringUtils.isBlank(newBo.getVendorLastName())) {
                        documentTitle += "'";
                    }
                }
                
                if (StringUtils.isNotBlank(newBo.getVendorLastName())) {
                    if (StringUtils.isBlank(newBo.getVendorFirstName())) {
                        documentTitle += " '";
                    }
                    documentTitle += newBo.getVendorLastName() + "'";
                }
            }

            if (newBo.getVendorHeader().getVendorForeignIndicator()) {
                documentTitle += " (F)";
            }

            if (!newBo.isVendorParentIndicator()) {
                documentTitle += " (D)";
            }
        }
        else { // We are using the Kuali default document title.
            documentTitle = super.getDocumentTitle(document);
        }
        return documentTitle;
    }

    @Override
    public void doRouteStatusChange(DocumentHeader header) {
        super.doRouteStatusChange(header);
        VendorDetail vendorDetail = (VendorDetail) getBusinessObject();
        KualiWorkflowDocument workflowDoc = header.getWorkflowDocument();

        // This code is only executed when the final approval occurs
        if (workflowDoc.stateIsProcessed()) {
            // This id and versionNumber null check is needed here since those fields are always null for a fresh maintenance doc.
            if (vendorDetail.isVendorParentIndicator() && vendorDetail.getVendorHeaderGeneratedIdentifier() != null) { 
                VendorDetail previousParent = SpringContext.getBean(VendorService.class).getParentVendor(vendorDetail.getVendorHeaderGeneratedIdentifier());
                // We'll only need to do the following if the previousParent is not the same as the current vendorDetail, because
                // the following lines are for vendor parent indicator changes.
                if (vendorDetail.getVendorDetailAssignedIdentifier() == null || 
                        previousParent.getVendorHeaderGeneratedIdentifier().intValue() != vendorDetail.getVendorHeaderGeneratedIdentifier().intValue() || 
                        previousParent.getVendorDetailAssignedIdentifier().intValue() != vendorDetail.getVendorDetailAssignedIdentifier().intValue()) {
                    previousParent.setVendorParentIndicator(false);
                    addNoteForParentIndicatorChange(vendorDetail, previousParent, header.getDocumentNumber());
                    SpringContext.getBean(BusinessObjectService.class).save(previousParent);
                }
            }

            // If this is a pre-existing parent vendor, and if the Tax Number or the Tax Type Code will change, log the change in the
            // Tax Change table.
            if (vendorDetail.isVendorParentIndicator()) {
                VendorDetail oldVendorDetail = SpringContext.getBean(VendorService.class).getVendorDetail(vendorDetail.getVendorHeaderGeneratedIdentifier(), vendorDetail.getVendorDetailAssignedIdentifier());
                if (ObjectUtils.isNotNull(oldVendorDetail)) {
                    VendorHeader oldVendorHeader = oldVendorDetail.getVendorHeader();
                    VendorHeader newVendorHeader = vendorDetail.getVendorHeader();

                    if (ObjectUtils.isNotNull(oldVendorHeader)) { // Does not apply if this is a new parent vendor.
                        String oldVendorTaxNumber = oldVendorHeader.getVendorTaxNumber();
                        String oldVendorTaxTypeCode = oldVendorHeader.getVendorTaxTypeCode();

                        String vendorTaxNumber = newVendorHeader.getVendorTaxNumber();
                        String vendorTaxTypeCode = newVendorHeader.getVendorTaxTypeCode();

                        if ((!StringUtils.equals(vendorTaxNumber, oldVendorTaxNumber)) || (!StringUtils.equals(vendorTaxTypeCode, oldVendorTaxTypeCode))) {
                            VendorTaxChange taxChange = new VendorTaxChange(vendorDetail.getVendorHeaderGeneratedIdentifier(), SpringContext.getBean(DateTimeService.class).getCurrentTimestamp(), oldVendorTaxNumber, oldVendorTaxTypeCode, GlobalVariables.getUserSession().getPerson().getPrincipalId());
                            SpringContext.getBean(BusinessObjectService.class).save(taxChange);
                        }
                    }
                }
            }

        }//endif stateIsProcessed()
    }
    
    /**
     * Add a note to the previous parent vendor to denote that parent vendor indicator change had occurred.
     * 
     * @param newVendorDetail The current vendor
     * @param oldVendorDetail The parent vendor of the current vendor prior to this change.
     * @param documentNumber The document number of the document where we're attempting the parent vendor indicator change.
     */
    private void addNoteForParentIndicatorChange(VendorDetail newVendorDetail, VendorDetail oldVendorDetail, String documentNumber) {
        String noteText = VendorUtils.buildMessageText(VendorKeyConstants.MESSAGE_VENDOR_PARENT_TO_DIVISION, documentNumber, newVendorDetail.getVendorName() + " (" + newVendorDetail.getVendorNumber() + ")");   
        Note newBONote = new Note();
        newBONote.setNoteText(noteText);
        try {
            NoteService noteService = SpringContext.getBean(NoteService.class);
            newBONote = noteService.createNote(newBONote, oldVendorDetail);
            noteService.save(newBONote);
        }
        catch (Exception e) {
            throw new RuntimeException("Caught Exception While Trying To Add Note to Vendor", e);
        }
        oldVendorDetail.getBoNotes().add(newBONote);
        
    }
    
    /**
     * Refreshes the vendorDetail. Currently we need this mainly for refreshing the soldToVendor object after returning from the
     * lookup for a sold to vendor.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        PersistableBusinessObject oldBo = (PersistableBusinessObject) document.getOldMaintainableObject().getBusinessObject();
        if (ObjectUtils.isNotNull(oldBo)) {
            oldBo.refreshNonUpdateableReferences();
        }
        VendorDetail newBo = (VendorDetail) document.getNewMaintainableObject().getBusinessObject();
        // Here we have to temporarily save vendorHeader into a temp object, then put back
        // the vendorHeader into the newBo after the refresh, so that we don't lose the
        // values
        VendorHeader tempHeader = newBo.getVendorHeader();
        newBo.refreshNonUpdateableReferences();
        newBo.setVendorHeader(tempHeader);
        super.refresh(refreshCaller, fieldValues, document);
    }

    /**
     * Temporarily saves vendorHeader into a temp object, then put back the vendorHeader into the VendorDetail after the refresh, so
     * that we don't lose the values
     */
    public void refreshBusinessObject() {
        VendorDetail vd = (VendorDetail) getBusinessObject();
        // Here we have to temporarily save vendorHeader into a temp object, then put back
        // the vendorHeader into the VendorDetail after the refresh, so that we don't lose the
        // values
        VendorHeader tempHeader = vd.getVendorHeader();
        vd.refreshNonUpdateableReferences();
        vd.setVendorHeader(tempHeader);
    }


    /**
     * Checks whether the vendor has already had a vendor detail assigned id. If not, it will call the private method to set the
     * detail assigned id. The method will also call the vendorService to determine whether it should save the vendor header (i.e.
     * if this is a parent) and will save the vendor header accordingly. This is because we are not going to save vendor header
     * automatically along with the saving of vendor detail, so if the vendor is a parent, we have to save the vendor header
     * separately. Restriction-related information will be changed based on whether the Vendor Restricted Indicator was changed. If
     * the Tax Number or Tax Type code have changed, the fact will be recorded with a new record in the Tax Change table. Finally
     * the method will call the saveBusinessObject( ) of the super class to save the vendor detail.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        VendorDetail vendorDetail = (VendorDetail) super.getBusinessObject();
        VendorHeader vendorHeader = vendorDetail.getVendorHeader();

        // Update miscellaneous information and save the Vendor Header if this is a parent vendor.
        setVendorName(vendorDetail);
        vendorHeader.setVendorHeaderGeneratedIdentifier(vendorDetail.getVendorHeaderGeneratedIdentifier());
        if (ObjectUtils.isNull(vendorDetail.getVendorDetailAssignedIdentifier())) {
            setDetailAssignedId(vendorDetail);
        }
        if (vendorDetail.isVendorParentIndicator()) {
            SpringContext.getBean(VendorService.class).saveVendorHeader(vendorDetail);
        }
        super.saveBusinessObject();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterEdit()
     */
    @Override
    public void processAfterEdit( MaintenanceDocument document, Map<String,String[]> parameters ) {
        setVendorCreateAndUpdateNote(VendorConstants.VendorCreateAndUpdateNotePrefixes.CHANGE);
        super.processAfterEdit(document, parameters);
    }

    /**
     * Checks whether the previous note was an "Add" with the same document number as this one
     * 
     * @param prefix String to determine if it is a note "Add" or a note "Change"
     */
    private void setVendorCreateAndUpdateNote(String prefix) {
        boolean shouldAddNote = true;
        if (prefix.equals(VendorConstants.VendorCreateAndUpdateNotePrefixes.CHANGE)) {
            // Check whether the previous note was an "Add" with the same document number as this one
            if (!this.getBusinessObject().getBoNotes().isEmpty()) {
                Note previousNote = this.getBusinessObject().getBoNote(this.getBusinessObject().getBoNotes().size() - 1);
                if (previousNote.getNoteText().contains(this.documentNumber)) {
                    shouldAddNote = false;
                }
            }
        }
        if (shouldAddNote) {
            Note newBONote = new Note();
            newBONote.setNoteText(prefix + " vendor document ID " + this.documentNumber);
            try {
                newBONote = SpringContext.getBean(NoteService.class).createNote(newBONote, this.getBusinessObject());
            }
            catch (Exception e) {
                throw new RuntimeException("Caught Exception While Trying To Add Note to Vendor", e);
            }
            this.getBusinessObject().getBoNotes().add(newBONote);
        }
    }

    /**
     * Concatenates the vendorLastName and a delimiter and the vendorFirstName fields into vendorName field of the vendorDetail
     * object.
     * 
     * @param vendorDetail VendorDetail The vendor whose name field we are trying to assign
     */
    private void setVendorName(VendorDetail vendorDetail) {
        if (vendorDetail.isVendorFirstLastNameIndicator()) {
            vendorDetail.setVendorName(vendorDetail.getVendorLastName() + VendorConstants.NAME_DELIM + vendorDetail.getVendorFirstName());
        }
    }

    /**
     * If the vendorFirstLastNameIndicator is true, this method will set the vendor first name and vendor last name fields from the
     * vendorName field, then set the vendorName field to null. Then it sets the businessObject of this maintainable to the
     * VendorDetail object that contains our modification to the name fields.
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#saveBusinessObject()
     */
    @Override
    public void setBusinessObject(PersistableBusinessObject bo) {
        VendorDetail originalBo = (VendorDetail) bo;
        String vendorName = originalBo.getVendorName();
        if (originalBo.isVendorFirstLastNameIndicator() && ObjectUtils.isNotNull(vendorName)) {
            int start = vendorName.indexOf(VendorConstants.NAME_DELIM);
            if (start >= 0) {
                String lastName = vendorName.substring(0, start);
                String firstName = new String();
                if (start + VendorConstants.NAME_DELIM.length() <= vendorName.length()) {
                    firstName = vendorName.substring(start + VendorConstants.NAME_DELIM.length(), vendorName.length());
                }

                originalBo.setVendorFirstName((ObjectUtils.isNotNull(firstName) ? firstName.trim() : firstName));
                originalBo.setVendorLastName((ObjectUtils.isNotNull(lastName) ? lastName.trim() : lastName));
                originalBo.setVendorName(null);
            }
        }
        this.businessObject = originalBo;
    }

    /**
     * Sets a valid detail assigned id to a vendor if the vendor has not had a detail assigned id yet. If this is a new parent whose
     * header id is also null, this method will assign 0 as the detail assigned id. If this is a new division vendor, it will look
     * for the count of vendor details in the database whose vendor header id match with the vendor header id of this new division,
     * then look for the count of vendor details in the database, in a while loop, to find if a vendor detail with the same header
     * id and detail id as the count has existed. If a vendor with such criteria exists, this method will increment the count
     * by 1 and look up in the database again. If it does not exist, assign the count as the vendor detail id and change the
     * boolean flag to stop the loop, because we have already found the valid detail assigned id that we were looking for
     * 
     * @param vendorDetail VendorDetail The vendor whose detail assigned id we're trying to assign.
     */
    private void setDetailAssignedId(VendorDetail vendorDetail) {
        // If this is a new parent, let's set the detail id to 0.
        if (ObjectUtils.isNull(vendorDetail.getVendorHeaderGeneratedIdentifier())) {
            vendorDetail.setVendorDetailAssignedIdentifier(new Integer(0));
        }
        else {
            // Try to get the count of all the vendor whose header id is the same as this header id.
            Map criterias = new HashMap();
            criterias.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorDetail.getVendorHeaderGeneratedIdentifier());
            BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
            int count = boService.countMatching(VendorDetail.class, criterias);
            boolean validId = false;
            while (!validId) {
                criterias.put(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, count);
                int result = boService.countMatching(VendorDetail.class, criterias);
                if (result > 0) {
                    // increment the detail id by 1
                    count++;
                }
                else {
                    // count is a validId, so we'll use count as our vendor detail assigned id
                    validId = true;
                    vendorDetail.setVendorDetailAssignedIdentifier(new Integer(count));
                }
            }
        }
    }

    /**
     * Returns the locking representation of the vendor. If the vendor detail id is not null, call the super class
     * implementation of generateMaintenanceLocks which will set the locking key to be the header and detail ids. However, if the
     * detail id is null, that means this is a new vendor (parent or division) and we should ignore locking.
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        if (ObjectUtils.isNotNull(((VendorDetail) getBusinessObject()).getVendorDetailAssignedIdentifier())) {
            return super.generateMaintenanceLocks();
        }
        else {
            return new ArrayList();
        }
    }

    /**
     * Create a new division vendor if the user clicks on the "Create a new division" link. By default, the vendorParentIndicator is
     * set to true in the constructor of VendorDetail, but if we're creating a new division, it's not a parent, so we need to set
     * the vendorParentIndicator to false in this case.
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#setupNewFromExisting()
     */
    @Override
    public void setupNewFromExisting( MaintenanceDocument document, Map<String,String[]> parameters ) {
        super.setupNewFromExisting(document, parameters);
        ((VendorDetail) super.getBusinessObject()).setVendorParentIndicator(false);
        ((VendorDetail) super.getBusinessObject()).setActiveIndicator(true);

        setVendorCreateAndUpdateNote(VendorConstants.VendorCreateAndUpdateNotePrefixes.ADD);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#isRelationshipRefreshable(java.lang.Class, java.lang.String)
     */
    @Override
    protected boolean isRelationshipRefreshable(Class boClass, String relationshipName) {
        if (VendorDetail.class.isAssignableFrom(boClass) && VendorConstants.VENDOR_HEADER_ATTR.equals(relationshipName)) {
            return false;
        }
        return super.isRelationshipRefreshable(boClass, relationshipName);
    }

    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals("RequiresApproval")) return SpringContext.getBean(VendorService.class).shouldVendorRouteForApproval(this.documentNumber);
        return super.answerSplitNodeQuestion(nodeName);
    }
}

