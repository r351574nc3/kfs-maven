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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.ExceptionUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class...
 */
public class IndirectCostAdjustmentAccountValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostAdjustmentAccountValidation.class);
    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        AccountingLine accountingLine = getAccountingLineForValidation();
        boolean isValid = true;
        if (isValid && accountingLine.isSourceAccountingLine()) {
            accountingLine.refreshReferenceObject("account");
            if (!ObjectUtils.isNull(accountingLine.getAccount())) {
                String icrAccount = accountingLine.getAccount().getIndirectCostRecoveryAcctNbr();
                isValid &= StringUtils.isNotBlank(icrAccount);
                if (!isValid) {
                    reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.IndirectCostAdjustment.ERROR_DOCUMENT_ICA_GRANT_INVALID_ACCOUNT, accountingLine.getAccountNumber());
                }
            }
        }
        return isValid;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Wrapper around global errorMap.put call, to allow better logging
     * 
     * @param propertyName
     * @param errorKey
     * @param errorParams
     */
    protected void reportError(String propertyName, String errorKey, String... errorParams) {
        LOG.debug("reportError(String, String, String) - start");

        GlobalVariables.getMessageMap().putError(propertyName, errorKey, errorParams);
        if (LOG.isDebugEnabled()) {
            LOG.debug("rule failure at " + ExceptionUtils.describeStackLevels(1, 2));
        }
    }
    
}
