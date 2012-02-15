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
package org.kuali.kfs.module.external.kc.document.validation.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsUnit;
import org.kuali.kfs.integration.cg.dto.AccountCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AccountParametersDTO;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.service.impl.AccountCreationServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.KfsMaintenanceDocumentRuleBase;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Business rule(s) applicable to AccountMaintenance documents.
 */
public class AccountAutoCreateDefaultsRule extends org.kuali.kfs.coa.document.validation.impl.AccountRule {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountAutoCreateDefaultsRule.class);
    protected static ParameterService parameterService;

    protected AccountService accountService;
    protected ContractsAndGrantsModuleService contractsAndGrantsModuleService;

    protected AccountAutoCreateDefaults oldAccountAutoCreateDefaults;
    protected AccountAutoCreateDefaults newAccountAutoCreateDefaults;

    public AccountAutoCreateDefaultsRule() {

        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes
        // to spring-managed with these services injected by Spring at some later date.
        // When this happens, just remove these calls to the setters with
        // SpringContext, and configure the bean defs for spring.
        this.setContractsAndGrantsModuleService(SpringContext.getBean(ContractsAndGrantsModuleService.class));
        accountService = SpringContext.getBean(AccountService.class);
    }

    /**
     * This method sets the convenience objects like newAccountAutoCreateDefaults and oldAccountAutoCreateDefaults, so you have
     * short and easy handles to the new and old objects contained in the maintenance document. It also calls the
     * BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys, if available.
     */
    public void setupConvenienceObjects() {

        // setup oldAccountAutoCreateDefaults convenience objects, make sure all possible sub-objects are populated
        oldAccountAutoCreateDefaults = (AccountAutoCreateDefaults) super.getOldBo();

        // setup newAccountAutoCreateDefaults convenience objects, make sure all possible sub-objects are populated
        newAccountAutoCreateDefaults = (AccountAutoCreateDefaults) super.getNewBo();
    }

    /**
     * This method calls the route rules but does not fail if any of them fail (this only happens on routing)
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomSaveDocumentBusinessRules called");
        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }

    /**
     * This method calls the following rules: checkAccountGuidelinesValidation checkEmptyValues checkGeneralRules checkCloseAccount
     * checkContractsAndGrants checkExpirationDate checkFundGroup checkSubFundGroup checkFiscalOfficerIsValidKualiUser this rule
     * will fail on routing
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        // default to success
        boolean success = true;

        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();
        
        success &= checkEmptyValues(document);
        success &= checkGeneralRules(document);        
        success &= checkContractsAndGrants(document);                        
        success &= checkIncomeStreamAccountRule();        
        success &= checkRequiredKcUnit(newAccountAutoCreateDefaults, document.isNew());
        
        return success;
    }
 
    protected boolean checkEmptyValues(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkEmptyValues called");

        boolean success = true;

        // this set confirms that all fields which are grouped (ie, foreign keys of a reference
        // object), must either be none filled out, or all filled out.
        success &= checkForPartiallyFilledOutReferenceForeignKeys("continuationAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("incomeStreamAccount");
        
        success &= checkForPartiallyFilledOutReferenceForeignKeys("reportsToAccount");        
        success &= checkForPartiallyFilledOutReferenceForeignKeys("indirectCostRecoveryAcct");

        return success;
    }

    protected boolean checkGeneralRules(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkGeneralRules called");
        Person fiscalOfficer = newAccountAutoCreateDefaults.getAccountFiscalOfficerUser();
        Person accountManager = newAccountAutoCreateDefaults.getAccountManagerUser();
        Person accountSupervisor = newAccountAutoCreateDefaults.getAccountSupervisoryUser();

        boolean success = true;
        
        // check FringeBenefit account rules
        success &= checkFringeBenefitAccountRule();

        if (ObjectUtils.isNotNull(fiscalOfficer) && !getDocumentHelperService().getDocumentAuthorizer(maintenanceDocument).isAuthorized(maintenanceDocument, KFSConstants.ParameterNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER, fiscalOfficer.getPrincipalId())) {
            super.putFieldError("accountFiscalOfficerUser.principalName", KFSKeyConstants.ERROR_USER_MISSING_PERMISSION, new String[] {fiscalOfficer.getName(), KFSConstants.ParameterNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER});
            success = false;
        }
        if (ObjectUtils.isNotNull(accountSupervisor) && !getDocumentHelperService().getDocumentAuthorizer(maintenanceDocument).isAuthorized(maintenanceDocument, KFSConstants.ParameterNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_ACCOUNT_SUPERVISOR, accountSupervisor.getPrincipalId())) {
            super.putFieldError("accountSupervisoryUser.principalName", KFSKeyConstants.ERROR_USER_MISSING_PERMISSION, new String[] {accountSupervisor.getName(), KFSConstants.ParameterNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_ACCOUNT_SUPERVISOR});
            success = false;
        }
        if (ObjectUtils.isNotNull(accountManager) && !getDocumentHelperService().getDocumentAuthorizer(maintenanceDocument).isAuthorized(maintenanceDocument, KFSConstants.ParameterNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_ACCOUNT_MANAGER, accountManager.getPrincipalId())) {
            super.putFieldError("accountManagerUser.principalName", KFSKeyConstants.ERROR_USER_MISSING_PERMISSION, new String[] {accountManager.getName(), KFSConstants.ParameterNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_ACCOUNT_MANAGER});
            success = false;
        }

        // the supervisor cannot be the same as the fiscal officer or account manager.
        if (isSupervisorSameAsFiscalOfficer()) {
            success &= false;
            putFieldError("accountsSupervisorySystemsIdentifier", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_BE_FISCAL_OFFICER);
        }
        if (isSupervisorSameAsManager()) {
            success &= false;
            putFieldError("accountManagerSystemIdentifier", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_BE_ACCT_MGR);
        }

        // disallow continuation account being expired
        if (isContinuationAccountExpired()) {
            success &= false;
            putFieldError("continuationAccountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_EXPIRED_CONTINUATION);
        }

        return success;
    }

    protected boolean checkFringeBenefitAccountRule() {

        boolean result = true;

        // if this account is selected as a Fringe Benefit Account, then we have nothing
        // to test, so exit
        if (newAccountAutoCreateDefaults.isAccountsFringesBnftIndicator()) {
            return true;
        }

        // if fringe benefit is not selected ... continue processing

        // fringe benefit account number is required
        if (StringUtils.isBlank(newAccountAutoCreateDefaults.getReportsToAccountNumber())) {
            putFieldError("reportsToAccountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE);
            result &= false;
        }

        // fringe benefit chart of accounts code is required
        if (StringUtils.isBlank(newAccountAutoCreateDefaults.getReportsToChartOfAccountsCode())) {
            putFieldError("reportsToChartOfAccountsCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE);
            result &= false;
        }

        // if either of the fringe benefit account fields are not present, then we're done
        if (result == false) {
            return result;
        }

        // attempt to load the fringe benefit account
        Account fringeBenefitAccount = accountService.getByPrimaryId(newAccountAutoCreateDefaults.getReportsToChartOfAccountsCode(), newAccountAutoCreateDefaults.getReportsToAccountNumber());

        // fringe benefit account must exist
        if (fringeBenefitAccount == null) {
            putFieldError("reportsToAccountNumber", KFSKeyConstants.ERROR_EXISTENCE, getFieldLabel(Account.class, "reportsToAccountNumber"));
            return false;
        }

        // fringe benefit account must be active
        if (!fringeBenefitAccount.isActive()) {
            putFieldError("reportsToAccountNumber", KFSKeyConstants.ERROR_INACTIVE, getFieldLabel(Account.class, "reportsToAccountNumber"));
            result &= false;
        }

        // make sure the fringe benefit account specified is set to fringe benefits = Y
        if (!fringeBenefitAccount.isAccountsFringesBnftIndicator()) {
            putFieldError("reportsToAccountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_MUST_BE_FLAGGED_FRINGEBENEFIT, fringeBenefitAccount.getChartOfAccountsCode() + "-" + fringeBenefitAccount.getAccountNumber());
            result &= false;
        }

        return result;
    }

    protected boolean isSupervisorSameAsFiscalOfficer() {
        return areTwoUsersTheSame(newAccountAutoCreateDefaults.getAccountSupervisoryUser(), newAccountAutoCreateDefaults.getAccountFiscalOfficerUser());
    }

    protected boolean isSupervisorSameAsManager() {
        return areTwoUsersTheSame(newAccountAutoCreateDefaults.getAccountSupervisoryUser(), newAccountAutoCreateDefaults.getAccountManagerUser());
    }

    protected boolean isContinuationAccountExpired() {

        boolean result = false;

        String chartCode = newAccountAutoCreateDefaults.getContinuationFinChrtOfAcctCd();
        String accountNumber = newAccountAutoCreateDefaults.getContinuationAccountNumber();

        // if either chartCode or accountNumber is not entered, then we
        // can't continue, so exit
        if (StringUtils.isBlank(chartCode) || StringUtils.isBlank(accountNumber)) {
            return result;
        }

        // attempt to retrieve the continuation account from the DB
        Account continuation = accountService.getByPrimaryId(chartCode, accountNumber);

        // if the object doesn't exist, then we can't continue, so exit
        if (ObjectUtils.isNull(continuation)) {
            return result;
        }

        // at this point, we have a valid continuation account, so we just need to
        // know whether its expired or not
        result = continuation.isExpired();

        return result;
    }

    protected boolean checkContractsAndGrants(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkContractsAndGrants called");

        boolean success = true;

        // Certain C&G fields are required if the Account belongs to the CG Fund Group
        success &= checkCgRequiredFields();

        // Income Stream account is required if this account is CG fund group,
        // or GF (general fund) fund group (with some exceptions)
        success &= checkIncomeStreamValid();
        
        // check if the new account has a valid responsibility id
        if (!ObjectUtils.isNull(newAccountAutoCreateDefaults)) {
            Account account = new Account();
            account.setContractsAndGrantsAccountResponsibilityId(newAccountAutoCreateDefaults.getContractsAndGrantsAccountResponsibilityId());
            final boolean hasValidAccountResponsibility = contractsAndGrantsModuleService.hasValidAccountReponsiblityIdIfNotNull(account);
            if (!hasValidAccountResponsibility) {
                success &= hasValidAccountResponsibility;
                putFieldError("contractsAndGrantsAccountResponsibilityId", KFSKeyConstants.ERROR_DOCUMENT_ACCTMAINT_INVALID_CG_RESPONSIBILITY , new String[] { newAccountAutoCreateDefaults.getContractsAndGrantsAccountResponsibilityId().toString(), newAccountAutoCreateDefaults.getChartOfAccountsCode(), "" });
            }
        }

        return success;
    }

    protected boolean checkCgRequiredFields() {

        boolean result = true;

        // Certain C&G fields are required if the Account belongs to the CG Fund Group
        if (ObjectUtils.isNotNull(newAccountAutoCreateDefaults.getSubFundGroup())) {
            if (getSubFundGroupService().isForContractsAndGrants(newAccountAutoCreateDefaults.getSubFundGroup())) {

                result &= checkEmptyBOField("indirectCostRcvyFinCoaCode", newAccountAutoCreateDefaults.getIndirectCostRcvyFinCoaCode(), replaceTokens(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_CHART_CODE_CANNOT_BE_EMPTY));
                result &= checkEmptyBOField("indirectCostRecoveryAcctNbr", newAccountAutoCreateDefaults.getIndirectCostRecoveryAcctNbr(), replaceTokens(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_CANNOT_BE_EMPTY));                
            }
            else {
                // this is not a C&G fund group. So users should not fill in any fields in the C&G tab.
                result &= checkCGFieldNotFilledIn("indirectCostRcvyFinCoaCode");
                result &= checkCGFieldNotFilledIn("indirectCostRecoveryAcctNbr");
            }
        }
        return result;
    }

    protected boolean checkIncomeStreamValid() {
        // if the subFundGroup object is null, we can't test, so exit
        if (ObjectUtils.isNull(newAccountAutoCreateDefaults.getSubFundGroup())) {
            return true;
        }
        String subFundGroupCode = newAccountAutoCreateDefaults.getSubFundGroupCode().trim();
        String fundGroupCode = newAccountAutoCreateDefaults.getSubFundGroup().getFundGroupCode().trim();
        boolean valid = true;
        if (getParameterService().getParameterEvaluator(Account.class, KFSConstants.ChartApcParms.INCOME_STREAM_ACCOUNT_REQUIRING_FUND_GROUPS, fundGroupCode).evaluationSucceeds()) {
            if (getParameterService().getParameterEvaluator(Account.class, KFSConstants.ChartApcParms.INCOME_STREAM_ACCOUNT_REQUIRING_SUB_FUND_GROUPS, subFundGroupCode).evaluationSucceeds()) {
                if (StringUtils.isBlank(newAccountAutoCreateDefaults.getIncomeStreamFinancialCoaCode())) {
                    putFieldError(KFSPropertyConstants.INCOME_STREAM_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_INCOME_STREAM_ACCT_COA_CANNOT_BE_EMPTY, new String[] { getDdService().getAttributeLabel(FundGroup.class, KFSConstants.FUND_GROUP_CODE_PROPERTY_NAME), fundGroupCode, getDdService().getAttributeLabel(SubFundGroup.class, KFSConstants.SUB_FUND_GROUP_CODE_PROPERTY_NAME), subFundGroupCode });
                    valid = false;
                } 
                if (StringUtils.isBlank(newAccountAutoCreateDefaults.getIncomeStreamAccountNumber())) {
                    putFieldError(KFSPropertyConstants.INCOME_STREAM_ACCOUNT_NUMBER, KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_INCOME_STREAM_ACCT_NBR_CANNOT_BE_EMPTY, new String[] { getDdService().getAttributeLabel(FundGroup.class, KFSConstants.FUND_GROUP_CODE_PROPERTY_NAME), fundGroupCode, getDdService().getAttributeLabel(SubFundGroup.class, KFSConstants.SUB_FUND_GROUP_CODE_PROPERTY_NAME), subFundGroupCode});
                    valid = false;
                }
            }
        }
        return valid;
    }

    protected boolean checkCGFieldNotFilledIn(String propertyName) {
        boolean success = true;
        Object value = ObjectUtils.getPropertyValue(newAccountAutoCreateDefaults, propertyName);
        if ((value instanceof String && !StringUtils.isBlank(value.toString())) || (value != null)) {
            success = false;
            putFieldError(propertyName, KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_CG_FIELDS_FILLED_FOR_NON_CG_ACCOUNT, new String[] { newAccountAutoCreateDefaults.getSubFundGroupCode() });
        }

        return success;
    }

    protected boolean checkIncomeStreamAccountRule() {
        // KFSMI-4877: if fund group is in system parameter values then income stream account number must exist.
        if ( ObjectUtils.isNotNull(newAccountAutoCreateDefaults.getSubFundGroup()) && StringUtils.isNotBlank(newAccountAutoCreateDefaults.getSubFundGroup().getFundGroupCode())) {
            if (ObjectUtils.isNull(newAccountAutoCreateDefaults.getIncomeStreamAccount())) {
                String incomeStreamRequiringFundGroupCode = SpringContext.getBean(ParameterService.class).getParameterValue(Account.class, KFSConstants.ChartApcParms.INCOME_STREAM_ACCOUNT_REQUIRING_FUND_GROUPS);
                if (StringUtils.containsIgnoreCase(newAccountAutoCreateDefaults.getSubFundGroup().getFundGroupCode(), incomeStreamRequiringFundGroupCode)) {
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_DOCUMENT_BA_NO_INCOME_STREAM_ACCOUNT, "");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method checks to make sure that the kcUnit field exists and is entered correctly
     * 
     * @param newAccountAutoCreateDefaults
     * @return true/false
     */
    protected boolean checkRequiredKcUnit(AccountAutoCreateDefaults newAccountAutoCreateDefaults, boolean isNew) {

        boolean result = true;
        try {
            ContractsAndGrantsUnit unitDTO = newAccountAutoCreateDefaults.getUnitDTO();
            unitDTO = (ContractsAndGrantsUnit) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsUnit.class).retrieveExternalizableBusinessObjectIfNecessary(newAccountAutoCreateDefaults, unitDTO, "unitDTO");
            if (unitDTO == null) {
                putFieldError(KcConstants.AccountCreationDefaults.KcUnit, ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND, newAccountAutoCreateDefaults.getKcUnit());
                result &= false;
            }
            // in the case of new accounts check if KcUnit exists already in accountAutoCreateDefaults table - if so reject
            if (isNew) {
                // check for new copy and new conditions
                String kcUnit = newAccountAutoCreateDefaults.getKcUnit();
                if ((kcUnit != null)) {
                    HashMap<String, String> map = new HashMap<String,String>();
                
                    map.put(KcConstants.AccountCreationDefaults.KcUnit, kcUnit);
                    Collection <AccountAutoCreateDefaults> accountAutoCreateDefaultList = boService.findMatching(AccountAutoCreateDefaults.class, map);
                    if (accountAutoCreateDefaultList == null || (! accountAutoCreateDefaultList.isEmpty())) {
                        putFieldError(KcConstants.AccountCreationDefaults.KcUnit, ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_ACCOUNT_ALREADY_DEFINED, newAccountAutoCreateDefaults.getKcUnit());
                        result &= false;
                    }
                }
            }
            return result;
        }
        catch (Exception ex) {
            putFieldError(KcConstants.AccountCreationDefaults.KcUnit, ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND, newAccountAutoCreateDefaults.getKcUnit());
            return false;
        }
    }


    /**
     * Sets the contractsAndGrantsModuleService attribute value.
     * 
     * @param contractsAndGrantsModuleService The contractsAndGrantsModuleService to set.
     */
    public void setContractsAndGrantsModuleService(ContractsAndGrantsModuleService contractsAndGrantsModuleService) {
        this.contractsAndGrantsModuleService = contractsAndGrantsModuleService;
    }


    public ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }
    
}
