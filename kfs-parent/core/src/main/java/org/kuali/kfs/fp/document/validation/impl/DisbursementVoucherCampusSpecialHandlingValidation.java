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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Validates that if a disbursement voucher had special handling turned off at the campus node, an extra note explaining that change has been added.
 */
public class DisbursementVoucherCampusSpecialHandlingValidation extends GenericValidation {
    private DisbursementVoucherDocument disbursementVoucherDocumentForValidation;
    private DocumentService documentService;
    private WorkflowInfo workflowInfo;
    
    public static final String DOCUMENT_EDITOR_ROLE_NAME = "Document Editor";

    /**
     * Carries out the validation
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        if (isAtNodeToCheck()) {
            final DisbursementVoucherDocument persistedDocument = getPersistedDisbursementVoucherDocument();
            if (isSpecialHandlingChanged(persistedDocument) && !isNoteAdded()) {
                result = false;
                GlobalVariables.getMessageMap().putError(AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX+"disbVchrSpecialHandlingCode", KFSKeyConstants.ERROR_DV_CAMPUS_TURNED_OFF_SPECIAL_HANDLING_WITHOUT_EXPLANATORY_NOTE, new String[] {});
            }
        }
        
        return result;
    }

    /**
     * Determines if the DisbursementVoucherDocumentForValidation is at the Campus route node 
     * @return true if the document is at the campus route node, false otherwise
     */
    protected boolean isAtNodeToCheck() {
        try {
            List<String> currentNodes = Arrays.asList(getDisbursementVoucherDocumentForValidation().getDocumentHeader().getWorkflowDocument().getNodeNames());
            return (!currentNodes.contains(DisbursementVoucherConstants.RouteLevelNames.PURCHASING));
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Workflow Exception while attempting to check route levels", we);
        }
    }
    
    /**
     * Retrieves from the persistence store the persisted version of the given document
     * @param document the document to find the persisted version of
     * @return the persisted version of that document
     */
    protected DisbursementVoucherDocument getPersistedDisbursementVoucherDocument() {
        try {
            return (DisbursementVoucherDocument)getDocumentService().getByDocumentHeaderId(getDisbursementVoucherDocumentForValidation().getDocumentNumber());
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Could not retrieve persisted version of document "+getDisbursementVoucherDocumentForValidation().getDocumentNumber()+" for Special Handling validation", we);
        }
    }
    
    /**
     * Determines if special handling was turned off from the DisbursementVoucherDocumentForValidation
     * @param persistedDocument the persisted version of the document
     * @return true if special handling was turned off, false otherwise
     */
    protected boolean isSpecialHandlingChanged(DisbursementVoucherDocument persistedDocument) {
        return persistedDocument.isDisbVchrSpecialHandlingCode() != getDisbursementVoucherDocumentForValidation().isDisbVchrSpecialHandlingCode();
    }
    
    /**
     * Determines if another note was added from the time the DisbursementVoucherDocumentForValidation was persisted
     * @param persistedDocument the persisted version of the document
     * @return true if an extra note was added, false otherwise
     */
    protected boolean isNoteAdded() {
       boolean foundNoteByCurrentApprover = false;
       int count = 0;
       final int noteCount = getDisbursementVoucherDocumentForValidation().getDocumentHeader().getBoNotes().size();
       while (!foundNoteByCurrentApprover && count < noteCount) {
           foundNoteByCurrentApprover |= noteAddedByApproverForCurrentNode(getDisbursementVoucherDocumentForValidation().getDocumentHeader().getBoNote(count));
           count += 1;
       }
       return foundNoteByCurrentApprover;
    }
    
    /**
     * Determines if the given note was added by the current approver
     * @param note the note to see added
     * @return true if the note was added by the current approver, false otherwise
     */
    protected boolean noteAddedByApproverForCurrentNode(Note note) {
        ActionRequestDTO[] actionRequests = null;
        try {
            actionRequests = getWorkflowInfo().getActionRequests(new Long(getDisbursementVoucherDocumentForValidation().getDocumentNumber()), getDisbursementVoucherDocumentForValidation().getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames(), note.getAuthorUniversalIdentifier());
        }
        catch (NumberFormatException nfe) {
            throw new RuntimeException("Could not convert Disbursement Voucher document number "+getDisbursementVoucherDocumentForValidation().getDocumentNumber()+" to long", nfe);
        }
        catch (WorkflowException we) {
            throw new RuntimeException("D'oh!  Workflow Exception!", we);
        }
        return actionRequests != null && actionRequests.length > 0;
    }
    
    /**
     * Determines the count of notes on the given document
     * @param dvDoc a document to find the count of notes on
     * @return the count of notes on the document
     */
    protected int getNoteCount(DisbursementVoucherDocument dvDoc) {
        return dvDoc.getDocumentHeader().getBoNotes().size();
    }

    /**
     * Gets the disbursementVoucherDocumentForValidation attribute. 
     * @return Returns the disbursementVoucherDocumentForValidation.
     */
    public DisbursementVoucherDocument getDisbursementVoucherDocumentForValidation() {
        return disbursementVoucherDocumentForValidation;
    }

    /**
     * Sets the disbursementVoucherDocumentForValidation attribute value.
     * @param disbursementVoucherDocumentForValidation The disbursementVoucherDocumentForValidation to set.
     */
    public void setDisbursementVoucherDocumentForValidation(DisbursementVoucherDocument disbursementVoucherDocumentForValidation) {
        this.disbursementVoucherDocumentForValidation = disbursementVoucherDocumentForValidation;
    }

    /**
     * Gets the workflowInfo attribute. 
     * @return Returns the workflowInfo.
     */
    public WorkflowInfo getWorkflowInfo() {
        if (workflowInfo == null) {
            workflowInfo = new WorkflowInfo();
        }
        return workflowInfo;
    }

    /**
     * Sets the workflowInfo attribute value.
     * @param workflowInfo The workflowInfo to set.
     */
    public void setWorkflowInfo(WorkflowInfo workflowInfo) {
        this.workflowInfo = workflowInfo;
    }

    /**
     * Gets the documentService attribute. 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
