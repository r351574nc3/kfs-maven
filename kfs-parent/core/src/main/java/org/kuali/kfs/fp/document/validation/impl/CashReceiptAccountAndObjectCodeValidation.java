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

import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_ACCT_OBJ_CD;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Validation that checks the sales tax account/object code combination on accounting lines of the cash receipt
 */
public class CashReceiptAccountAndObjectCodeValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    private ParameterService parameterService;

    /**
     * This method processes the accounting line to make sure if a sales tax account is used the right object code is used with it
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = true;
        // not evaluating, just want to retrieve the evaluator to get the values of the parameter
        // get the object code and account
        String objCd = getAccountingLineForValidation().getFinancialObjectCode();
        String account = getAccountingLineForValidation().getAccountNumber();
        if (!StringUtils.isEmpty(objCd) && !StringUtils.isEmpty(account)) {
            String[] params = getParameterService().getParameterValues(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, APPLICATION_PARAMETER.SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES).toArray(new String[] {});
            boolean acctsMatched = false;
            for (int i = 0; i < params.length; i++) {
                String paramAcct = params[i].split(":")[0];
                if (account.equalsIgnoreCase(paramAcct)) {
                    acctsMatched = true;
                }
            }
            if (acctsMatched) {
                String compare = account + ":" + objCd;
                ParameterEvaluator evaluator = getParameterService().getParameterEvaluator(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, APPLICATION_PARAMETER.SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES, compare);
                if (!evaluator.evaluationSucceeds()) {
                    isValid = false;
                }
            }

        }
        if (!isValid) {
            GlobalVariables.getMessageMap().putError("accountNumber", ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_ACCT_OBJ_CD, account, objCd);
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
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
