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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.NegativePaymentRequestApprovalLimit;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Business rules for the NegativePaymentRequestApprovalLimit maintenance document
 */
public class NegativePaymentRequestApprovalLimitRule extends MaintenanceDocumentRuleBase {

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomApproveDocumentBusinessRules(document);
        final NegativePaymentRequestApprovalLimit limit = (NegativePaymentRequestApprovalLimit)getNewBo();
        result &= checkExclusiveOrganizationCodeAndAccountNumber(limit);
        return result;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);
        final NegativePaymentRequestApprovalLimit limit = (NegativePaymentRequestApprovalLimit)getNewBo();
        result &= checkExclusiveOrganizationCodeAndAccountNumber(limit);
        return result;
    }
    
    /**
     * Checks that organization code and account number aren't both specified on a new NegativePaymentRequestApprovalLimit
     * @param limit the NegativePaymentRequestApprovalLimit to check
     * @return true if the rule passed, false otherwise (with error message added)
     */
    protected boolean checkExclusiveOrganizationCodeAndAccountNumber(NegativePaymentRequestApprovalLimit limit) {
        if (!StringUtils.isBlank(limit.getOrganizationCode()) && !StringUtils.isBlank(limit.getAccountNumber())) {
            putFieldError(KFSPropertyConstants.ORGANIZATION_CODE, PurapKeyConstants.ERROR_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT_ORG_AND_ACCOUNT_EXCLUSIVE, new String[] {});
            putFieldError(KFSPropertyConstants.ACCOUNT_NUMBER, PurapKeyConstants.ERROR_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT_ORG_AND_ACCOUNT_EXCLUSIVE, new String[] {});
            return false;
        }
        return true;
    }

}
