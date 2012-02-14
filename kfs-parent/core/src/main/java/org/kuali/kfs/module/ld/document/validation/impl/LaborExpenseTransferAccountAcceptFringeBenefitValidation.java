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
package org.kuali.kfs.module.ld.document.validation.impl;

import static org.kuali.kfs.sys.businessobject.AccountingLineOverride.CODE.EXPIRED_ACCOUNT;
import static org.kuali.kfs.sys.businessobject.AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED;
import static org.kuali.kfs.sys.businessobject.AccountingLineOverride.CODE.NON_FRINGE_ACCOUNT_USED;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ld.LaborConstants ;
import org.kuali.kfs.module.ld.LaborKeyConstants; 
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * verify if the accounts in target accounting lines accept fringe benefits
 * 
 * @param accountingLine the given accounting line
 * @return true if the accounts in the target accounting lines accept fringe benefits; otherwise, false
 */
public class LaborExpenseTransferAccountAcceptFringeBenefitValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    
    /**
     * Validates that an accounting line whether the expired account in the target accounting line 
     * can be used.
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        AccountingLine accountingLine = getAccountingLineForValidation();
          
        // verify if the accounts in target accounting lines accept fringe benefits
        if (!isAccountAcceptFringeBenefit(accountingLine)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_ACCOUNT_NOT_ACCEPT_FRINGES, accountingLine.getAccount().getReportsToChartOfAccountsCode(), accountingLine.getAccount().getReportsToAccountNumber());
            return false;
        }
                        
        return result;
    }

    /**
     * Determines whether the account in the target line accepts fringe benefits.
     * 
     * @param accountingLine the line to check
     * @return true if the accounts in the target accounting lines accept fringe benefits; otherwise, false
     */
    protected boolean isAccountAcceptFringeBenefit(AccountingLine accountingLine) {
        boolean acceptsFringeBenefits = true;

 //       accountingLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        Account account = accountingLine.getAccount();
        if (ObjectUtils.isNotNull(account) && !account.isAccountsFringesBnftIndicator()) {
            String overrideCode = accountingLine.getOverrideCode();
            boolean canNonFringeAccountUsed = NON_FRINGE_ACCOUNT_USED.equals(overrideCode);
            canNonFringeAccountUsed = canNonFringeAccountUsed || EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED.equals(overrideCode);

            if (!canNonFringeAccountUsed) {
                acceptsFringeBenefits = false;
            }
        }

        return acceptsFringeBenefits;
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
}
