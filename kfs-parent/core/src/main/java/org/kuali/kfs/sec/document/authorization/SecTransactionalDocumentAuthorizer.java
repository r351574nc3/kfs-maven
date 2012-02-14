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
package org.kuali.kfs.sec.document.authorization;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.SecKeyConstants;
import org.kuali.kfs.sec.businessobject.AccessSecurityRestrictionInfo;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * TransactionDocumentAuthorizer that wraps access security checks around another TransactionDocumentAuthorizer configured for the document type
 */
public class SecTransactionalDocumentAuthorizer implements TransactionalDocumentAuthorizer {
    protected TransactionalDocumentAuthorizer documentAuthorizer;

    public Set<String> getEditModes(Document document, Person user, Set<String> editModes) {
        return documentAuthorizer.getEditModes(document, user, editModes);
    }

    public boolean canAddNoteAttachment(Document document, String attachmentTypeCode, Person user) {
        return documentAuthorizer.canAddNoteAttachment(document, attachmentTypeCode, user);
    }

    public boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode, String createdBySelfOnly, Person user) {
        return documentAuthorizer.canDeleteNoteAttachment(document, attachmentTypeCode, createdBySelfOnly, user);
    }

    public boolean canInitiate(String documentTypeName, Person user) {
        return documentAuthorizer.canInitiate(documentTypeName, user);
    }

    /**
     * If user has open permission then does further checks to verify there are no access security restriction setup that prevents the user from opening the document
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canOpen(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
     */
    public boolean canOpen(Document document, Person user) {
        AccessSecurityService securityService = SpringContext.getBean(AccessSecurityService.class);

        boolean canOpen = documentAuthorizer.canOpen(document, user);
        if (canOpen) {
            AccessSecurityRestrictionInfo restrictionInfo = new AccessSecurityRestrictionInfo();
            canOpen = securityService.canViewDocument((AccountingDocument) document, user, restrictionInfo);
            if (!canOpen) {
                GlobalVariables.getUserSession().addObject(SecConstants.OPEN_DOCUMENT_SECURITY_ACCESS_DENIED_ERROR_KEY, restrictionInfo);
            }
        }

        return canOpen;
    }

    public boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode) {
        return documentAuthorizer.canReceiveAdHoc(document, user, actionRequestCode);
    }

    public boolean canSendAdHocRequests(Document document, String actionRequestCd, Person user) {
        return documentAuthorizer.canSendAdHocRequests(document, actionRequestCd, user);
    }

    /**
     * If user has permission to view notes/attachments then does further checks to verify there are no access security restriction setup that prevents the user from viewing the
     * notes/attachments
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#canViewNoteAttachment(org.kuali.rice.kns.document.Document, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean canViewNoteAttachment(Document document, String attachmentTypeCode, Person user) {
        AccessSecurityService securityService = SpringContext.getBean(AccessSecurityService.class);

        boolean canView = documentAuthorizer.canViewNoteAttachment(document, attachmentTypeCode, user);
        if (canView) {
            canView = securityService.canViewDocumentNotesAttachments((AccountingDocument) document, user);

            if (!canView) {
                GlobalVariables.getMessageMap().putInfo(KFSConstants.GLOBAL_ERRORS, SecKeyConstants.MESSAGE_DOCUMENT_NOTES_RESTRICTED, (String) null);
            }
        }

        return canView;
    }

    /**
     * If there are line restrictions and the initiator override flag is turned on, we need to disable the copy and error correct buttons since those would result in documents
     * displaying the restricted lines
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getDocumentActions(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person, java.util.Set)
     */
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActions) {
        Set<String> documentActionsToReturn = documentAuthorizer.getDocumentActions(document, user, documentActions);

        AccessSecurityService securityService = SpringContext.getBean(AccessSecurityService.class);

        boolean alwaysAllowInitiatorAccess = SpringContext.getBean(ParameterService.class).getIndicatorParameter(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_INITIATOR_LINE_ACCESS_IND);
        if (alwaysAllowInitiatorAccess) {
            // determine if any lines are view restricted
            boolean hasViewRestrictions = false;

            AccountingDocument accountingDocument = (AccountingDocument) document;
            for (Iterator iterator = accountingDocument.getSourceAccountingLines().iterator(); iterator.hasNext();) {
                AccountingLine line = (AccountingLine) iterator.next();
                if (!securityService.canViewDocumentAccountingLine(accountingDocument, line, user)) {
                    hasViewRestrictions = true;
                    break;
                }
            }

            if (!hasViewRestrictions) {
                for (Iterator iterator = accountingDocument.getTargetAccountingLines().iterator(); iterator.hasNext();) {
                    AccountingLine line = (AccountingLine) iterator.next();
                    if (!securityService.canViewDocumentAccountingLine(accountingDocument, line, user)) {
                        hasViewRestrictions = true;
                        break;
                    }
                }
            }

            // if we have restrictions then disable copy and error correction
            if (hasViewRestrictions) {
                if (documentActionsToReturn.contains(KNSConstants.KUALI_ACTION_CAN_COPY)) {
                    documentActionsToReturn.remove(KNSConstants.KUALI_ACTION_CAN_COPY);
                    GlobalVariables.getMessageMap().putInfo(KFSConstants.GLOBAL_ERRORS, SecKeyConstants.MESSAGE_DOCUMENT_COPY_RESTRICTED, (String) null);
                }

                if (documentActionsToReturn.contains(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT)) {
                    documentActionsToReturn.remove(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT);
                    GlobalVariables.getMessageMap().putInfo(KFSConstants.GLOBAL_ERRORS, SecKeyConstants.MESSAGE_DOCUMENT_ERROR_CORRECT_RESTRICTED, (String) null);
                }
            }
        }

        return documentActionsToReturn;
    }

    public Map<String, String> getCollectionItemPermissionDetails(BusinessObject collectionItemBusinessObject) {
        return documentAuthorizer.getCollectionItemPermissionDetails(collectionItemBusinessObject);
    }

    public Map<String, String> getCollectionItemRoleQualifications(BusinessObject collectionItemBusinessObject) {
        return documentAuthorizer.getCollectionItemRoleQualifications(collectionItemBusinessObject);
    }

    public boolean isAuthorized(BusinessObject businessObject, String namespaceCode, String permissionName, String principalId) {
        return documentAuthorizer.isAuthorized(businessObject, namespaceCode, permissionName, principalId);
    }

    public boolean isAuthorized(BusinessObject businessObject, String namespaceCode, String permissionName, String principalId, Map<String, String> additionalPermissionDetails, Map<String, String> additionalRoleQualifiers) {
        return documentAuthorizer.isAuthorized(businessObject, namespaceCode, permissionName, principalId, additionalPermissionDetails, additionalRoleQualifiers);
    }

    public boolean isAuthorizedByTemplate(BusinessObject businessObject, String namespaceCode, String permissionTemplateName, String principalId) {
        return documentAuthorizer.isAuthorizedByTemplate(businessObject, namespaceCode, permissionTemplateName, principalId);
    }

    public boolean isAuthorizedByTemplate(BusinessObject businessObject, String namespaceCode, String permissionTemplateName, String principalId, Map<String, String> additionalPermissionDetails, Map<String, String> additionalRoleQualifiers) {
        return documentAuthorizer.isAuthorizedByTemplate(businessObject, namespaceCode, permissionTemplateName, principalId, additionalPermissionDetails, additionalRoleQualifiers);
    }

    /**
     * Sets the documentAuthorizer attribute value.
     * 
     * @param documentAuthorizer The documentAuthorizer to set.
     */
    public void setDocumentAuthorizer(TransactionalDocumentAuthorizer documentAuthorizer) {
        this.documentAuthorizer = documentAuthorizer;
    }

}
