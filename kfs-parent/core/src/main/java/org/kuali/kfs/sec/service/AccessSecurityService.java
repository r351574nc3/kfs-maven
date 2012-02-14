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
package org.kuali.kfs.sec.service;

import java.util.List;

import org.kuali.kfs.sec.businessobject.AccessSecurityRestrictionInfo;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.bo.BusinessObject;


/**
 * Exposes methods to apply access security restrictions to business objects from the various framework points (lookups, inquiries,
 * document accounting lines)
 */
public interface AccessSecurityService {

    /**
     * Retrieves any setup security permissions (with lookup template) for the given person and evaluates against List of business
     * objects. Any instances not passing validation are removed from given list.
     * 
     * @param results List of business object instances with data to check
     * @param person Person to apply security for
     */
    public void applySecurityRestrictionsForLookup(List<? extends BusinessObject> results, Person person);

    /**
     * Retrieves any setup security permissions (with gl inquiry template) for the given person and evaluates against List of
     * business objects. Any instances not passing validation are removed from given list.
     * 
     * @param results List of business object instances with data to check
     * @param person Person to apply security for
     */
    public void applySecurityRestrictionsForGLInquiry(List<? extends BusinessObject> results, Person person);

    /**
     * Retrieves any setup security permissions (with ld inquiry template) for the given person and evaluates against List of
     * business objects. Any instances not passing validation are removed from given list.
     * 
     * @param results List of business object instances with data to check
     * @param person Person to apply security for
     */
    public void applySecurityRestrictionsForLaborInquiry(List<? extends BusinessObject> results, Person person);

    /**
     * Retrieves any setup security permissions for the given person and evaluates against List of business objects. Any instances
     * not passing validation are removed from given list.
     * 
     * @param results List of business object instances with data to check
     * @param person Person to apply security for
     * @param templateId KIM template id for permissions to check
     * @param additionalPermissionDetails Any additional details that should be matched on when retrieving permissions
     */
    public void applySecurityRestrictions(List<? extends BusinessObject> results, Person person, String templateId, AttributeSet additionalPermissionDetails);

    /**
     * Retrieves any access security permissions that are assigned to the user and applicable for the given business object, then
     * evaluates permissions against the business object instance
     * 
     * @param businessObject BusinessObject instance to check access permissions against
     * @param person Person to retrieve access permissions for
     * @param restrictionInfo Object providing information on a restriction if one is found
     * @return boolean true if all access permissions pass (or none are found), false if at least one access permission fails
     */
    public boolean checkSecurityRestrictionsForBusinessObject(BusinessObject businessObject, Person person, AccessSecurityRestrictionInfo restrictionInfo);

    /**
     * Checks any view access security permissions setup for the user and for accounting lines of the given document type
     * 
     * @param document AccountingDocument that contains the line to be validated, doc type of instance is used for retrieving
     *        permissions
     * @param accountingLine AccountingLine instance with values to check
     * @param person the user who we are checking access for
     * @return boolean true if user has view access for the accounting line, false otherwise
     */
    public boolean canViewDocumentAccountingLine(AccountingDocument document, AccountingLine accountingLine, Person person);

    /**
     * Checks any edit access security permissions setup for the user and for accounting lines of the given document type
     * 
     * @param document AccountingDocument instance that contains the line to be validated, doc type of instance is used for
     *        retrieving permissions
     * @param accountingLine AccountingLine instance with values to check
     * @param person the user who we are checking access for
     * @return boolean true if user has edit access for the accounting line, false otherwise
     */
    public boolean canEditDocumentAccountingLine(AccountingDocument document, AccountingLine accountingLine, Person person);

    /**
     * Checks any edit access security permissions setup for the user and for accounting lines of the given document type
     * 
     * @param document AccountingDocument instance that contains the line to be validated, doc type of instance is used for
     *        retrieving permissions
     * @param accountingLine AccountingLine instance with values to check
     * @param person the user who we are checking access for
     * @param restrictionInfo Object providing information on a restriction if one is found
     * @return boolean true if user has edit access for the accounting line, false otherwise
     */
    public boolean canEditDocumentAccountingLine(AccountingDocument document, AccountingLine accountingLine, Person person, AccessSecurityRestrictionInfo restrictionInfo);

    /**
     * Checks view access on all accounting lines contained on the document for given user
     * 
     * @param document AccountingDocument instance with accounting lines to check, doc type of instance is used for retrieving
     *        permissions
     * @param person the user who we are checking access for
     * @param restrictionInfo Object providing information on a restriction if one is found
     * @return boolean true if the user has view access for all accounting lines on the document, false if access is denied on one
     *         or more lines
     */
    public boolean canViewDocument(AccountingDocument document, Person person, AccessSecurityRestrictionInfo restrictionInfo);

    /**
     * Checks edit access on all accounting lines contained on the document for given user
     * 
     * @param document AccountingDocument instance with accounting lines to check, doc type of instance is used for retrieving
     *        permissions
     * @param person the user who we are checking access for
     * @return boolean true if the user has edit access for all accounting lines on the document, false if access is denied on one
     *         or more lines
     */
    public boolean canEditDocument(AccountingDocument document, Person person);

    /**
     * Checks access is allowed to view document notes based on the document's accounting lines
     * 
     * @param document AccountingDocument instance with accounting lines to check, doc type of instance is used for retrieving
     *        permissions
     * @param person the user who we are checking access for
     * @return boolean true if the user has permission to view the notes/attachments, false otherwise
     */
    public boolean canViewDocumentNotesAttachments(AccountingDocument document, Person person);

    /**
     * Gets the View Document With Field Values template ID.
     * 
     * @return the View Document With Field Values template ID
     */
    public String getViewDocumentWithFieldValueTemplateId();

    /**
     * Gets the View Accounting Line With Field Value Template Id.
     * 
     * @return the View Accounting Line With Field Value Template Id
     */
    public String getViewAccountingLineWithFieldValueTemplateId();

    /**
     * Gets the View Notes Attachments With Field Value Template Id.
     * 
     * @return the View Notes Attachments With Field Value Template Id
     */
    public String getViewNotesAttachmentsWithFieldValueTemplateId();

    /**
     * Gets the Edit Document With Field Value Template Id.
     * 
     * @return the Edit Document With Field Value Template Id
     */
    public String getEditDocumentWithFieldValueTemplateId();

    /**
     * Gets the Edit Accounting Line With Field Value Template Id.
     * 
     * @return the Edit Accounting Line With Field Value Template Id
     */
    public String getEditAccountingLineWithFieldValueTemplateId();

    /**
     * Gets the Lookup With Field Value Template Id.
     * 
     * @return the Lookup With Field Value Template Id
     */
    public String getLookupWithFieldValueTemplateId();

    /**
     * Gets the Inquiry With Field Value Template Id.
     * 
     * @return the InquiryWithFieldValueTemplateId
     */
    public String getInquiryWithFieldValueTemplateId();


}
