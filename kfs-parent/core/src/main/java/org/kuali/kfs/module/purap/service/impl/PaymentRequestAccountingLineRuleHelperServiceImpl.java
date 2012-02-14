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
package org.kuali.kfs.module.purap.service.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class PaymentRequestAccountingLineRuleHelperServiceImpl extends PurapAccountingLineRuleHelperServiceImpl {

    /**
     * @see org.kuali.kfs.module.purap.service.impl.AccountsPayableAccountingLineRuleHelperServiceImpl#hasRequiredOverrides(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     * For payment request we must throw an error after AP approval for C&G accounts that are expired more than 90 days.
     */
    @Override
    public boolean hasRequiredOverrides(AccountingLine line, String overrideCode) {
        boolean hasOverrides = true;
        
        Account account = SpringContext.getBean(AccountService.class).getByPrimaryId(line.getChartOfAccountsCode(), line.getAccountNumber());
        String docStatus = getDocument().getStatusCode();
                
        //if account exists
        if(ObjectUtils.isNotNull(account)){
            //after AP approval
            if(PaymentRequestStatuses.AWAITING_SUB_ACCT_MGR_REVIEW.equals(docStatus) ||
                PaymentRequestStatuses.AWAITING_FISCAL_REVIEW.equals(docStatus) || 
                PaymentRequestStatuses.AWAITING_ORG_REVIEW.equals(docStatus) || 
                PaymentRequestStatuses.AWAITING_TAX_REVIEW.equals(docStatus) ||
                PaymentRequestStatuses.DEPARTMENT_APPROVED.equals(docStatus) || 
                PaymentRequestStatuses.AUTO_APPROVED.equals(docStatus) ){

                String expirationExtensionDays = SpringContext.getBean(ParameterService.class).getParameterValue(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET);
                int expirationExtensionDaysInt = 3 * 30; // default to 90 days (approximately 3 months)

                if (ObjectUtils.isNotNull(expirationExtensionDays) && expirationExtensionDays.trim().length() > 0) {

                    expirationExtensionDaysInt = new Integer(expirationExtensionDays).intValue();
                }

                //if account is expired, c&g and past 90 days, add error
                if(account.isExpired() && account.isForContractsAndGrants() && (SpringContext.getBean(DateTimeService.class).dateDiff(account.getAccountExpirationDate(), SpringContext.getBean(DateTimeService.class).getCurrentDate(), true) > expirationExtensionDaysInt)){
                    GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, PurapKeyConstants.ERROR_ITEM_ACCOUNT_EXPIRED_REPLACE, account.getAccountNumber());
                    hasOverrides = false;
                }
            }
        }else{
            //account not valid, shouldn't happen but just in case
            hasOverrides = false;
        }
        return hasOverrides;        
    }


    public boolean isValidObjectCode(ObjectCode objectCode, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(objectCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        
        Integer universityFiscalYear = ((PaymentRequestDocument)getDocument()).getPostingYearPriorOrCurrent();
        ObjectCode objectCodeForValidation = (SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(universityFiscalYear, objectCode.getChartOfAccountsCode(), objectCode.getFinancialObjectCode()));

        // check active status
        if (!objectCodeForValidation.isFinancialObjectActiveCode()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }

        return true;
    }

  

    public boolean isValidSubObjectCode(SubObjectCode subObjectCode, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getSubObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subObjectCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        
        Integer universityFiscalYear = ((PaymentRequestDocument)getDocument()).getPostingYearPriorOrCurrent();
        SubObjectCode subObjectCodeForValidation = (SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(universityFiscalYear, subObjectCode.getChartOfAccountsCode(), subObjectCode.getAccountNumber(), subObjectCode.getFinancialObjectCode(), subObjectCode.getFinancialSubObjectCode()));

 
        // check active flag
        if (!subObjectCodeForValidation.isActive()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }

        return true;
    }
    
}
