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

package org.kuali.kfs.coa.businessobject;

import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.bo.PostalCode;
import org.kuali.rice.kns.bo.State;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.PostalCodeService;
import org.kuali.rice.kns.service.StateService;

/**
 * 
 */
public class PriorYearAccount extends PersistableBusinessObjectBase implements AccountIntf, Inactivateable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PriorYearAccount.class);

    private String chartOfAccountsCode;
    private String accountNumber;
    private String accountName;
    private boolean accountsFringesBnftIndicator;
    private Date accountRestrictedStatusDate;
    private String accountCityName;
    private String accountStateCode;
    private String accountStreetAddress;
    private String accountZipCode;
    private Date accountCreateDate;
    private Date accountEffectiveDate;
    private Date accountExpirationDate;
    private String acctIndirectCostRcvyTypeCd;
    private String acctCustomIndCstRcvyExclCd;
    private String financialIcrSeriesIdentifier;
    private boolean accountInFinancialProcessingIndicator;
    private String budgetRecordingLevelCode;
    private String accountSufficientFundsCode;
    private boolean pendingAcctSufficientFundsIndicator;
    private boolean extrnlFinEncumSufficntFndIndicator;
    private boolean intrnlFinEncumSufficntFndIndicator;
    private boolean finPreencumSufficientFundIndicator;
    private boolean financialObjectivePrsctrlIndicator;
    private String accountCfdaNumber;
    private boolean accountOffCampusIndicator;
    private boolean active;

    private String accountFiscalOfficerSystemIdentifier;
    private String accountsSupervisorySystemsIdentifier;
    private String accountManagerSystemIdentifier;
    private String organizationCode;
    private String accountTypeCode;
    private String accountPhysicalCampusCode;
    private String subFundGroupCode;
    private String financialHigherEdFunctionCd;
    private String accountRestrictedStatusCode;
    private String reportsToChartOfAccountsCode;
    private String reportsToAccountNumber;
    private String continuationFinChrtOfAcctCd;
    private String continuationAccountNumber;
    private String endowmentIncomeAcctFinCoaCd;
    private String endowmentIncomeAccountNumber;
    private String contractControlFinCoaCode;
    private String contractControlAccountNumber;
    private String incomeStreamFinancialCoaCode;
    private String incomeStreamAccountNumber;
    private String indirectCostRcvyFinCoaCode;
    private String indirectCostRecoveryAcctNbr;

    private Chart chartOfAccounts;
    private Organization organization;
    private AccountType accountType;
    private Campus accountPhysicalCampus;
    private State accountState;
    private SubFundGroup subFundGroup;
    private HigherEducationFunction financialHigherEdFunction;
    private RestrictedStatus accountRestrictedStatus;
    private Account reportsToAccount;
    private Account continuationAccount;
    private Account endowmentIncomeAccount;
    private Account contractControlAccount;
    private Account incomeStreamAccount;
    private Account indirectCostRecoveryAcct;
    private Person accountFiscalOfficerUser;
    private Person accountSupervisoryUser;
    private Person accountManagerUser;
    private PostalCode postalZipCode;
    private BudgetRecordingLevel budgetRecordingLevel;
    private SufficientFundsCode sufficientFundsCode;


    // Several kinds of Dummy Attributes for dividing sections on Inquiry page
    private String accountResponsibilitySectionBlank;
    private String accountResponsibilitySection;
    private String contractsAndGrantsSectionBlank;
    private String contractsAndGrantsSection;
    private String guidelinesAndPurposeSectionBlank;
    private String guidelinesAndPurposeSection;
    private String accountDescriptionSectionBlank;
    private String accountDescriptionSection;


    private AccountGuideline accountGuideline;
    private AccountDescription accountDescription;

    private List subAccounts;
    private Boolean forContractsAndGrants;

    /**
     * Default no-arg constructor.
     */
    public PriorYearAccount() {
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the accountName attribute.
     * 
     * @return Returns the accountName
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Sets the accountName attribute.
     * 
     * @param accountName The accountName to set.
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * Gets the _AccountsFringesBnftIndicator_ attribute.
     * 
     * @return Returns the _AccountsFringesBnftIndicator_
     */
    public boolean isAccountsFringesBnftIndicator() {
        return accountsFringesBnftIndicator;
    }

    /**
     * Sets the _AccountsFringesBnftIndicator_ attribute.
     * 
     * @param _AccountsFringesBnftIndicator_ The _AccountsFringesBnftIndicator_ to set.
     */
    public void setAccountsFringesBnftIndicator(boolean _AccountsFringesBnftIndicator_) {
        this.accountsFringesBnftIndicator = _AccountsFringesBnftIndicator_;
    }

    /**
     * Gets the accountRestrictedStatusDate attribute.
     * 
     * @return Returns the accountRestrictedStatusDate
     */
    public Date getAccountRestrictedStatusDate() {
        return accountRestrictedStatusDate;
    }

    /**
     * Sets the accountRestrictedStatusDate attribute.
     * 
     * @param accountRestrictedStatusDate The accountRestrictedStatusDate to set.
     */
    public void setAccountRestrictedStatusDate(Date accountRestrictedStatusDate) {
        this.accountRestrictedStatusDate = accountRestrictedStatusDate;
    }

    /**
     * Gets the accountCityName attribute.
     * 
     * @return Returns the accountCityName
     */
    public String getAccountCityName() {
        return accountCityName;
    }

    /**
     * Sets the accountCityName attribute.
     * 
     * @param accountCityName The accountCityName to set.
     */
    public void setAccountCityName(String accountCityName) {
        this.accountCityName = accountCityName;
    }

    /**
     * Gets the accountStateCode attribute.
     * 
     * @return Returns the accountStateCode
     */
    public String getAccountStateCode() {
        return accountStateCode;
    }

    /**
     * Sets the accountStateCode attribute.
     * 
     * @param accountStateCode The accountStateCode to set.
     */
    public void setAccountStateCode(String accountStateCode) {
        this.accountStateCode = accountStateCode;
    }

    /**
     * Gets the accountStreetAddress attribute.
     * 
     * @return Returns the accountStreetAddress
     */
    public String getAccountStreetAddress() {
        return accountStreetAddress;
    }

    /**
     * Sets the accountStreetAddress attribute.
     * 
     * @param accountStreetAddress The accountStreetAddress to set.
     */
    public void setAccountStreetAddress(String accountStreetAddress) {
        this.accountStreetAddress = accountStreetAddress;
    }

    /**
     * Gets the accountZipCode attribute.
     * 
     * @return Returns the accountZipCode
     */
    public String getAccountZipCode() {
        return accountZipCode;
    }

    /**
     * Sets the accountZipCode attribute.
     * 
     * @param accountZipCode The accountZipCode to set.
     */
    public void setAccountZipCode(String accountZipCode) {
        this.accountZipCode = accountZipCode;
    }

    /**
     * Gets the accountCreateDate attribute.
     * 
     * @return Returns the accountCreateDate
     */
    public Date getAccountCreateDate() {
        return accountCreateDate;
    }

    /**
     * Sets the accountCreateDate attribute.
     * 
     * @param accountCreateDate The accountCreateDate to set.
     */
    public void setAccountCreateDate(Date accountCreateDate) {
        this.accountCreateDate = accountCreateDate;
    }

    /**
     * Gets the accountEffectiveDate attribute.
     * 
     * @return Returns the accountEffectiveDate
     */
    public Date getAccountEffectiveDate() {
        return accountEffectiveDate;
    }

    /**
     * Sets the accountEffectiveDate attribute.
     * 
     * @param accountEffectiveDate The accountEffectiveDate to set.
     */
    public void setAccountEffectiveDate(Date accountEffectiveDate) {
        this.accountEffectiveDate = accountEffectiveDate;
    }

    /**
     * Gets the accountExpirationDate attribute.
     * 
     * @return Returns the accountExpirationDate
     */
    public Date getAccountExpirationDate() {
        return accountExpirationDate;
    }

    /**
     * Sets the accountExpirationDate attribute.
     * 
     * @param accountExpirationDate The accountExpirationDate to set.
     */
    public void setAccountExpirationDate(Date accountExpirationDate) {
        this.accountExpirationDate = accountExpirationDate;
    }

    /**
     * This method determines whether the account is expired or not. Note that if Expiration Date is the same as today, then this
     * will return false. It will only return true if the account expiration date is one day earlier than today or earlier. Note
     * that this logic ignores all time components when doing the comparison. It only does the before/after comparison based on date
     * values, not time-values.
     * 
     * @return true or false based on the logic outlined above
     */
    public boolean isExpired() {
        LOG.debug("entering isExpired()");
        // dont even bother trying to test if the accountExpirationDate is null
        if (this.accountExpirationDate == null) {
            return false;
        }

        return this.isExpired(SpringContext.getBean(DateTimeService.class).getCurrentCalendar());
    }

    /**
     * This method determines whether the account is expired or not. Note that if Expiration Date is the same date as testDate, then
     * this will return false. It will only return true if the account expiration date is one day earlier than testDate or earlier.
     * Note that this logic ignores all time components when doing the comparison. It only does the before/after comparison based on
     * date values, not time-values.
     * 
     * @param testDate - Calendar instance with the date to test the Account's Expiration Date against. This is most commonly set to
     *        today's date.
     * @return true or false based on the logic outlined above
     */
    public boolean isExpired(Calendar testDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("entering isExpired(" + testDate + ")");
        }

        // dont even bother trying to test if the accountExpirationDate is null
        if (this.accountExpirationDate == null) {
            return false;
        }

        // remove any time-components from the testDate
        testDate = DateUtils.truncate(testDate, Calendar.DAY_OF_MONTH);

        // get a calendar reference to the Account Expiration
        // date, and remove any time components
        Calendar acctDate = Calendar.getInstance();
        acctDate.setTime(this.accountExpirationDate);
        acctDate = DateUtils.truncate(acctDate, Calendar.DAY_OF_MONTH);

        // if the Account Expiration Date is before the testDate
        if (acctDate.before(testDate)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * This method determines whether the account is expired or not. Note that if Expiration Date is the same date as testDate, then
     * this will return false. It will only return true if the account expiration date is one day earlier than testDate or earlier.
     * Note that this logic ignores all time components when doing the comparison. It only does the before/after comparison based on
     * date values, not time-values.
     * 
     * @param testDate - java.util.Date instance with the date to test the Account's Expiration Date against. This is most commonly
     *        set to today's date.
     * @return true or false based on the logic outlined above
     */
    public boolean isExpired(Date testDate) {

        // dont even bother trying to test if the accountExpirationDate is null
        if (this.accountExpirationDate == null) {
            return false;
        }

        Calendar acctDate = Calendar.getInstance();
        acctDate.setTime(testDate);
        return isExpired(acctDate);
    }
    
    /**
     * Gets the acctIndirectCostRcvyTypeCd attribute.
     * 
     * @return Returns the acctIndirectCostRcvyTypeCd
     */
    public String getAcctIndirectCostRcvyTypeCd() {
        return acctIndirectCostRcvyTypeCd;
    }

    /**
     * Sets the acctIndirectCostRcvyTypeCd attribute.
     * 
     * @param acctIndirectCostRcvyTypeCd The acctIndirectCostRcvyTypeCd to set.
     */
    public void setAcctIndirectCostRcvyTypeCd(String acctIndirectCostRcvyTypeCd) {
        this.acctIndirectCostRcvyTypeCd = acctIndirectCostRcvyTypeCd;
    }

    /**
     * Gets the acctCustomIndCstRcvyExclCd attribute.
     * 
     * @return Returns the acctCustomIndCstRcvyExclCd
     */
    public String getAcctCustomIndCstRcvyExclCd() {
        return acctCustomIndCstRcvyExclCd;
    }

    /**
     * Sets the acctCustomIndCstRcvyExclCd attribute.
     * 
     * @param acctCustomIndCstRcvyExclCd The acctCustomIndCstRcvyExclCd to set.
     */
    public void setAcctCustomIndCstRcvyExclCd(String acctCustomIndCstRcvyExclCd) {
        this.acctCustomIndCstRcvyExclCd = acctCustomIndCstRcvyExclCd;
    }

    /**
     * Gets the financialIcrSeriesIdentifier attribute.
     * 
     * @return Returns the financialIcrSeriesIdentifier
     */
    public String getFinancialIcrSeriesIdentifier() {
        return financialIcrSeriesIdentifier;
    }

    /**
     * Sets the financialIcrSeriesIdentifier attribute.
     * 
     * @param financialIcrSeriesIdentifier The financialIcrSeriesIdentifier to set.
     */
    public void setFinancialIcrSeriesIdentifier(String financialIcrSeriesIdentifier) {
        this.financialIcrSeriesIdentifier = financialIcrSeriesIdentifier;
    }

    /**
     * Gets the accountInFinancialProcessingIndicator attribute.
     * 
     * @return Returns the accountInFinancialProcessingIndicator
     */
    public boolean getAccountInFinancialProcessingIndicator() {
        return accountInFinancialProcessingIndicator;
    }

    /**
     * Sets the accountInFinancialProcessingIndicator attribute.
     * 
     * @param accountInFinancialProcessingIndicator The accountInFinancialProcessingIndicator to set.
     */
    public void setAccountInFinancialProcessingIndicator(boolean accountInFinancialProcessingIndicator) {
        this.accountInFinancialProcessingIndicator = accountInFinancialProcessingIndicator;
    }

    /**
     * Gets the budgetRecordingLevelCode attribute.
     * 
     * @return Returns the budgetRecordingLevelCode
     */
    public String getBudgetRecordingLevelCode() {
        return budgetRecordingLevelCode;
    }

    /**
     * Sets the budgetRecordingLevelCode attribute.
     * 
     * @param budgetRecordingLevelCode The budgetRecordingLevelCode to set.
     */
    public void setBudgetRecordingLevelCode(String budgetRecordingLevelCode) {
        this.budgetRecordingLevelCode = budgetRecordingLevelCode;
    }

    /**
     * Gets the accountSufficientFundsCode attribute.
     * 
     * @return Returns the accountSufficientFundsCode
     */
    public String getAccountSufficientFundsCode() {
        return accountSufficientFundsCode;
    }

    /**
     * Sets the accountSufficientFundsCode attribute.
     * 
     * @param accountSufficientFundsCode The accountSufficientFundsCode to set.
     */
    public void setAccountSufficientFundsCode(String accountSufficientFundsCode) {
        this.accountSufficientFundsCode = accountSufficientFundsCode;
    }

    /**
     * Gets the pendingAcctSufficientFundsIndicator attribute.
     * 
     * @return Returns the pendingAcctSufficientFundsIndicator
     */
    public boolean isPendingAcctSufficientFundsIndicator() {
        return pendingAcctSufficientFundsIndicator;
    }

    /**
     * Sets the pendingAcctSufficientFundsIndicator attribute.
     * 
     * @param pendingAcctSufficientFundsIndicator The pendingAcctSufficientFundsIndicator to set.
     */
    public void setPendingAcctSufficientFundsIndicator(boolean pendingAcctSufficientFundsIndicator) {
        this.pendingAcctSufficientFundsIndicator = pendingAcctSufficientFundsIndicator;
    }

    /**
     * Gets the extrnlFinEncumSufficntFndIndicator attribute.
     * 
     * @return Returns the extrnlFinEncumSufficntFndIndicator
     */
    public boolean isExtrnlFinEncumSufficntFndIndicator() {
        return extrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Sets the extrnlFinEncumSufficntFndIndicator attribute.
     * 
     * @param extrnlFinEncumSufficntFndIndicator The extrnlFinEncumSufficntFndIndicator to set.
     */
    public void setExtrnlFinEncumSufficntFndIndicator(boolean extrnlFinEncumSufficntFndIndicator) {
        this.extrnlFinEncumSufficntFndIndicator = extrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Gets the intrnlFinEncumSufficntFndIndicator attribute.
     * 
     * @return Returns the intrnlFinEncumSufficntFndIndicator
     */
    public boolean isIntrnlFinEncumSufficntFndIndicator() {
        return intrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Sets the intrnlFinEncumSufficntFndIndicator attribute.
     * 
     * @param intrnlFinEncumSufficntFndIndicator The intrnlFinEncumSufficntFndIndicator to set.
     */
    public void setIntrnlFinEncumSufficntFndIndicator(boolean intrnlFinEncumSufficntFndIndicator) {
        this.intrnlFinEncumSufficntFndIndicator = intrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Gets the finPreencumSufficientFundIndicator attribute.
     * 
     * @return Returns the finPreencumSufficientFundIndicator
     */
    public boolean isFinPreencumSufficientFundIndicator() {
        return finPreencumSufficientFundIndicator;
    }

    /**
     * Sets the finPreencumSufficientFundIndicator attribute.
     * 
     * @param finPreencumSufficientFundIndicator The finPreencumSufficientFundIndicator to set.
     */
    public void setFinPreencumSufficientFundIndicator(boolean finPreencumSufficientFundIndicator) {
        this.finPreencumSufficientFundIndicator = finPreencumSufficientFundIndicator;
    }

    /**
     * Gets the _FinancialObjectivePrsctrlIndicator_ attribute.
     * 
     * @return Returns the _FinancialObjectivePrsctrlIndicator_
     */
    public boolean isFinancialObjectivePrsctrlIndicator() {
        return financialObjectivePrsctrlIndicator;
    }

    /**
     * Sets the _FinancialObjectivePrsctrlIndicator_ attribute.
     * 
     * @param _FinancialObjectivePrsctrlIndicator_ The _FinancialObjectivePrsctrlIndicator_ to set.
     */
    public void setFinancialObjectivePrsctrlIndicator(boolean _FinancialObjectivePrsctrlIndicator_) {
        this.financialObjectivePrsctrlIndicator = _FinancialObjectivePrsctrlIndicator_;
    }

    /**
     * Gets the accountCfdaNumber attribute.
     * 
     * @return Returns the accountCfdaNumber
     */
    public String getAccountCfdaNumber() {
        return accountCfdaNumber;
    }

    /**
     * Sets the accountCfdaNumber attribute.
     * 
     * @param accountCfdaNumber The accountCfdaNumber to set.
     */
    public void setAccountCfdaNumber(String accountCfdaNumber) {
        this.accountCfdaNumber = accountCfdaNumber;
    }

    /**
     * Gets the accountOffCampusIndicator attribute.
     * 
     * @return Returns the accountOffCampusIndicator
     */
    public boolean isAccountOffCampusIndicator() {
        return accountOffCampusIndicator;
    }

    /**
     * Sets the accountOffCampusIndicator attribute.
     * 
     * @param accountOffCampusIndicator The accountOffCampusIndicator to set.
     */
    public void setAccountOffCampusIndicator(boolean accountOffCampusIndicator) {
        this.accountOffCampusIndicator = accountOffCampusIndicator;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the accountType attribute.
     * 
     * @return Returns the accountType
     */
    public AccountType getAccountType() {
        return accountType;
    }

    /**
     * Sets the accountType attribute.
     * 
     * @param accountType The accountType to set.
     * @deprecated
     */
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    /**
     * Gets the accountPhysicalCampus attribute.
     * 
     * @return Returns the accountPhysicalCampus
     */
    public Campus getAccountPhysicalCampus() {
        return accountPhysicalCampus = (Campus) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Campus.class).retrieveExternalizableBusinessObjectIfNecessary(this, accountPhysicalCampus, "accountPhysicalCampus");
    }

    /**
     * Sets the accountPhysicalCampus attribute.
     * 
     * @param accountPhysicalCampus The accountPhysicalCampus to set.
     * @deprecated
     */
    public void setAccountPhysicalCampus(Campus accountPhysicalCampus) {
        this.accountPhysicalCampus = accountPhysicalCampus;
    }

    /**
     * Gets the accountState attribute
     * 
     * @return Returns the accountState
     */
    public State getAccountState() {
        accountState = SpringContext.getBean(StateService.class).getByPrimaryIdIfNecessary( accountStateCode, accountState);
        return accountState;
    }

    /**
     * Sets the accountState attribute
     * 
     * @param state
     * @deprecated
     */
    public void setAccountState(State state) {
        this.accountState = state;
    }

    /**
     * Gets the subFundGroup attribute.
     * 
     * @return Returns the subFundGroup
     */
    public SubFundGroup getSubFundGroup() {
        return subFundGroup;
    }

    /**
     * Sets the subFundGroup attribute.
     * 
     * @param subFundGroup The subFundGroup to set.
     * @deprecated
     */
    public void setSubFundGroup(SubFundGroup subFundGroup) {
        this.subFundGroup = subFundGroup;
    }

    /**
     * Gets the financialHigherEdFunction attribute.
     * 
     * @return Returns the financialHigherEdFunction
     */
    public HigherEducationFunction getFinancialHigherEdFunction() {
        return financialHigherEdFunction;
    }

    /**
     * Sets the financialHigherEdFunction attribute.
     * 
     * @param financialHigherEdFunction The financialHigherEdFunction to set.
     * @deprecated
     */
    public void setFinancialHigherEdFunction(HigherEducationFunction financialHigherEdFunction) {
        this.financialHigherEdFunction = financialHigherEdFunction;
    }

    /**
     * Gets the accountRestrictedStatus attribute.
     * 
     * @return Returns the accountRestrictedStatus
     */
    public RestrictedStatus getAccountRestrictedStatus() {
        return accountRestrictedStatus;
    }

    /**
     * Sets the accountRestrictedStatus attribute.
     * 
     * @param accountRestrictedStatus The accountRestrictedStatus to set.
     * @deprecated
     */
    public void setAccountRestrictedStatus(RestrictedStatus accountRestrictedStatus) {
        this.accountRestrictedStatus = accountRestrictedStatus;
    }

    /**
     * Gets the reportsToAccount attribute.
     * 
     * @return Returns the reportsToAccount
     */
    public Account getReportsToAccount() {
        return reportsToAccount;
    }

    /**
     * Sets the reportsToAccount attribute.
     * 
     * @param reportsToAccount The reportsToAccount to set.
     * @deprecated
     */
    public void setReportsToAccount(Account reportsToAccount) {
        this.reportsToAccount = reportsToAccount;
    }

    /**
     * Gets the endowmentIncomeAccount attribute.
     * 
     * @return Returns the endowmentIncomeAccount
     */
    public Account getEndowmentIncomeAccount() {
        return endowmentIncomeAccount;
    }

    /**
     * Sets the endowmentIncomeAccount attribute.
     * 
     * @param endowmentIncomeAccount The endowmentIncomeAccount to set.
     * @deprecated
     */
    public void setEndowmentIncomeAccount(Account endowmentIncomeAccount) {
        this.endowmentIncomeAccount = endowmentIncomeAccount;
    }

    /**
     * Gets the contractControlAccount attribute.
     * 
     * @return Returns the contractControlAccount
     */
    public Account getContractControlAccount() {
        return contractControlAccount;
    }

    /**
     * Sets the contractControlAccount attribute.
     * 
     * @param contractControlAccount The contractControlAccount to set.
     * @deprecated
     */
    public void setContractControlAccount(Account contractControlAccount) {
        this.contractControlAccount = contractControlAccount;
    }


    /**
     * Gets the incomeStreamAccount attribute.
     * 
     * @return Returns the incomeStreamAccount
     */
    public Account getIncomeStreamAccount() {
        return incomeStreamAccount;
    }

    /**
     * Sets the incomeStreamAccount attribute.
     * 
     * @param incomeStreamAccount The incomeStreamAccount to set.
     * @deprecated
     */
    public void setIncomeStreamAccount(Account incomeStreamAccount) {
        this.incomeStreamAccount = incomeStreamAccount;
    }

    /**
     * Gets the indirectCostRecoveryAcct attribute.
     * 
     * @return Returns the indirectCostRecoveryAcct
     */
    public Account getIndirectCostRecoveryAcct() {
        return indirectCostRecoveryAcct;
    }

    /**
     * Sets the indirectCostRecoveryAcct attribute.
     * 
     * @param indirectCostRecoveryAcct The indirectCostRecoveryAcct to set.
     * @deprecated
     */
    public void setIndirectCostRecoveryAcct(Account indirectCostRecoveryAcct) {
        this.indirectCostRecoveryAcct = indirectCostRecoveryAcct;
    }

    public Person getAccountFiscalOfficerUser() {
        accountFiscalOfficerUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(accountFiscalOfficerSystemIdentifier, accountFiscalOfficerUser);
        return accountFiscalOfficerUser;
    }


    /**
     * @param accountFiscalOfficerUser The accountFiscalOfficerUser to set.
     * @deprecated
     */
    public void setAccountFiscalOfficerUser(Person accountFiscalOfficerUser) {
        this.accountFiscalOfficerUser = accountFiscalOfficerUser;
    }

    public Person getAccountManagerUser() {
        accountManagerUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(accountManagerSystemIdentifier, accountManagerUser);
        return accountManagerUser;
    }

    /**
     * @param accountManagerUser The accountManagerUser to set.
     * @deprecated
     */
    public void setAccountManagerUser(Person accountManagerUser) {
        this.accountManagerUser = accountManagerUser;
    }


    public Person getAccountSupervisoryUser() {
        accountSupervisoryUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(accountsSupervisorySystemsIdentifier, accountSupervisoryUser);
        return accountSupervisoryUser;
    }


    /**
     * @param accountSupervisoryUser The accountSupervisoryUser to set.
     * @deprecated
     */
    public void setAccountSupervisoryUser(Person accountSupervisoryUser) {
        this.accountSupervisoryUser = accountSupervisoryUser;
    }


    /**
     * @return Returns the continuationAccount.
     */
    public Account getContinuationAccount() {
        return continuationAccount;
    }


    /**
     * @param continuationAccount The continuationAccount to set.
     * @deprecated
     */
    public void setContinuationAccount(Account continuationAccount) {
        this.continuationAccount = continuationAccount;
    }


    /**
     * @return Returns the accountGuideline.
     */
    public AccountGuideline getAccountGuideline() {
        return accountGuideline;
    }

    /**
     * @param accountGuideline The accountGuideline to set.
     * @deprecated
     */
    public void setAccountGuideline(AccountGuideline accountGuideline) {
        this.accountGuideline = accountGuideline;
    }


    /**
     * Gets the accountDescription attribute.
     * 
     * @return Returns the accountDescription.
     */
    public AccountDescription getAccountDescription() {
        return accountDescription;
    }

    /**
     * Sets the accountDescription attribute value.
     * 
     * @param accountDescription The accountDescription to set.
     */
    public void setAccountDescription(AccountDescription accountDescription) {
        this.accountDescription = accountDescription;
    }

    /**
     * @return Returns the subAccounts.
     */
    public List getSubAccounts() {
        return subAccounts;
    }


    /**
     * @param subAccounts The subAccounts to set.
     */
    public void setSubAccounts(List subAccounts) {
        this.subAccounts = subAccounts;
    }


    /**
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * @return Returns the accountFiscalOfficerSystemIdentifier.
     */
    public String getAccountFiscalOfficerSystemIdentifier() {
        return accountFiscalOfficerSystemIdentifier;
    }

    /**
     * @param accountFiscalOfficerSystemIdentifier The accountFiscalOfficerSystemIdentifier to set.
     */
    public void setAccountFiscalOfficerSystemIdentifier(String accountFiscalOfficerSystemIdentifier) {
        this.accountFiscalOfficerSystemIdentifier = accountFiscalOfficerSystemIdentifier;
    }

    /**
     * @return Returns the accountManagerSystemIdentifier.
     */
    public String getAccountManagerSystemIdentifier() {
        return accountManagerSystemIdentifier;
    }

    /**
     * @param accountManagerSystemIdentifier The accountManagerSystemIdentifier to set.
     */
    public void setAccountManagerSystemIdentifier(String accountManagerSystemIdentifier) {
        this.accountManagerSystemIdentifier = accountManagerSystemIdentifier;
    }

    /**
     * @return Returns the accountPhysicalCampusCode.
     */
    public String getAccountPhysicalCampusCode() {
        return accountPhysicalCampusCode;
    }

    /**
     * @param accountPhysicalCampusCode The accountPhysicalCampusCode to set.
     */
    public void setAccountPhysicalCampusCode(String accountPhysicalCampusCode) {
        this.accountPhysicalCampusCode = accountPhysicalCampusCode;
    }

    /**
     * @return Returns the accountRestrictedStatusCode.
     */
    public String getAccountRestrictedStatusCode() {
        return accountRestrictedStatusCode;
    }

    /**
     * @param accountRestrictedStatusCode The accountRestrictedStatusCode to set.
     */
    public void setAccountRestrictedStatusCode(String accountRestrictedStatusCode) {
        this.accountRestrictedStatusCode = accountRestrictedStatusCode;
    }

    /**
     * @return Returns the accountsSupervisorySystemsIdentifier.
     */
    public String getAccountsSupervisorySystemsIdentifier() {
        return accountsSupervisorySystemsIdentifier;
    }

    /**
     * @param accountsSupervisorySystemsIdentifier The accountsSupervisorySystemsIdentifier to set.
     */
    public void setAccountsSupervisorySystemsIdentifier(String accountsSupervisorySystemsIdentifier) {
        this.accountsSupervisorySystemsIdentifier = accountsSupervisorySystemsIdentifier;
    }

    /**
     * @return Returns the accountTypeCode.
     */
    public String getAccountTypeCode() {
        return accountTypeCode;
    }

    /**
     * @param accountTypeCode The accountTypeCode to set.
     */
    public void setAccountTypeCode(String accountTypeCode) {
        this.accountTypeCode = accountTypeCode;
    }

    /**
     * @return Returns the continuationAccountNumber.
     */
    public String getContinuationAccountNumber() {
        return continuationAccountNumber;
    }

    /**
     * @param continuationAccountNumber The continuationAccountNumber to set.
     */
    public void setContinuationAccountNumber(String continuationAccountNumber) {
        this.continuationAccountNumber = continuationAccountNumber;
    }

    /**
     * @return Returns the continuationFinChrtOfAcctCd.
     */
    public String getContinuationFinChrtOfAcctCd() {
        return continuationFinChrtOfAcctCd;
    }

    /**
     * @param continuationFinChrtOfAcctCd The continuationFinChrtOfAcctCd to set.
     */
    public void setContinuationFinChrtOfAcctCd(String continuationFinChrtOfAcctCd) {
        this.continuationFinChrtOfAcctCd = continuationFinChrtOfAcctCd;
    }

    /**
     * @return Returns the contractControlAccountNumber.
     */
    public String getContractControlAccountNumber() {
        return contractControlAccountNumber;
    }

    /**
     * @param contractControlAccountNumber The contractControlAccountNumber to set.
     */
    public void setContractControlAccountNumber(String contractControlAccountNumber) {
        this.contractControlAccountNumber = contractControlAccountNumber;
    }

    /**
     * @return Returns the contractControlFinCoaCode.
     */
    public String getContractControlFinCoaCode() {
        return contractControlFinCoaCode;
    }

    /**
     * @param contractControlFinCoaCode The contractControlFinCoaCode to set.
     */
    public void setContractControlFinCoaCode(String contractControlFinCoaCode) {
        this.contractControlFinCoaCode = contractControlFinCoaCode;
    }

    /**
     * @return Returns the endowmentIncomeAccountNumber.
     */
    public String getEndowmentIncomeAccountNumber() {
        return endowmentIncomeAccountNumber;
    }

    /**
     * @param endowmentIncomeAccountNumber The endowmentIncomeAccountNumber to set.
     */
    public void setEndowmentIncomeAccountNumber(String endowmentIncomeAccountNumber) {
        this.endowmentIncomeAccountNumber = endowmentIncomeAccountNumber;
    }

    /**
     * @return Returns the endowmentIncomeAcctFinCoaCd.
     */
    public String getEndowmentIncomeAcctFinCoaCd() {
        return endowmentIncomeAcctFinCoaCd;
    }

    /**
     * @param endowmentIncomeAcctFinCoaCd The endowmentIncomeAcctFinCoaCd to set.
     */
    public void setEndowmentIncomeAcctFinCoaCd(String endowmentIncomeAcctFinCoaCd) {
        this.endowmentIncomeAcctFinCoaCd = endowmentIncomeAcctFinCoaCd;
    }

    /**
     * @return Returns the financialHigherEdFunctionCd.
     */
    public String getFinancialHigherEdFunctionCd() {
        return financialHigherEdFunctionCd;
    }

    /**
     * @param financialHigherEdFunctionCd The financialHigherEdFunctionCd to set.
     */
    public void setFinancialHigherEdFunctionCd(String financialHigherEdFunctionCd) {
        this.financialHigherEdFunctionCd = financialHigherEdFunctionCd;
    }

    /**
     * @return Returns the incomeStreamAccountNumber.
     */
    public String getIncomeStreamAccountNumber() {
        return incomeStreamAccountNumber;
    }

    /**
     * @param incomeStreamAccountNumber The incomeStreamAccountNumber to set.
     */
    public void setIncomeStreamAccountNumber(String incomeStreamAccountNumber) {
        this.incomeStreamAccountNumber = incomeStreamAccountNumber;
    }

    /**
     * @return Returns the incomeStreamFinancialCoaCode.
     */
    public String getIncomeStreamFinancialCoaCode() {
        return incomeStreamFinancialCoaCode;
    }

    /**
     * @param incomeStreamFinancialCoaCode The incomeStreamFinancialCoaCode to set.
     */
    public void setIncomeStreamFinancialCoaCode(String incomeStreamFinancialCoaCode) {
        this.incomeStreamFinancialCoaCode = incomeStreamFinancialCoaCode;
    }

    /**
     * @return Returns the indirectCostRcvyFinCoaCode.
     */
    public String getIndirectCostRcvyFinCoaCode() {
        return indirectCostRcvyFinCoaCode;
    }

    /**
     * @param indirectCostRcvyFinCoaCode The indirectCostRcvyFinCoaCode to set.
     */
    public void setIndirectCostRcvyFinCoaCode(String indirectCostRcvyFinCoaCode) {
        this.indirectCostRcvyFinCoaCode = indirectCostRcvyFinCoaCode;
    }

    /**
     * @return Returns the indirectCostRecoveryAcctNbr.
     */
    public String getIndirectCostRecoveryAcctNbr() {
        return indirectCostRecoveryAcctNbr;
    }

    /**
     * @param indirectCostRecoveryAcctNbr The indirectCostRecoveryAcctNbr to set.
     */
    public void setIndirectCostRecoveryAcctNbr(String indirectCostRecoveryAcctNbr) {
        this.indirectCostRecoveryAcctNbr = indirectCostRecoveryAcctNbr;
    }

    /**
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * @return Returns the reportsToAccountNumber.
     */
    public String getReportsToAccountNumber() {
        return reportsToAccountNumber;
    }

    /**
     * @param reportsToAccountNumber The reportsToAccountNumber to set.
     */
    public void setReportsToAccountNumber(String reportsToAccountNumber) {
        this.reportsToAccountNumber = reportsToAccountNumber;
    }

    /**
     * @return Returns the reportsToChartOfAccountsCode.
     */
    public String getReportsToChartOfAccountsCode() {
        return reportsToChartOfAccountsCode;
    }

    /**
     * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
     */
    public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
        this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
    }

    /**
     * @return Returns the subFundGroupCode.
     */
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    /**
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
        forContractsAndGrants = null;
    }

    /**
     * Gets the postalZipCode attribute.
     * 
     * @return Returns the postalZipCode.
     */
    public PostalCode getPostalZipCode() {
        postalZipCode = SpringContext.getBean(PostalCodeService.class).getByPostalCodeInDefaultCountryIfNecessary(accountZipCode, postalZipCode);
        return postalZipCode;
    }

    /**
     * Sets the postalZipCode attribute value.
     * 
     * @param postalZipCode The postalZipCode to set.
     */
    public void setPostalZipCode(PostalCode postalZipCode) {
        this.postalZipCode = postalZipCode;
    }

    /**
     * Gets the budgetRecordingLevel attribute.
     * 
     * @return Returns the budgetRecordingLevel.
     */
    public BudgetRecordingLevel getBudgetRecordingLevel() {
        return budgetRecordingLevel;
    }

    /**
     * Sets the budgetRecordingLevel attribute value.
     * 
     * @param budgetRecordingLevel The budgetRecordingLevel to set.
     */
    public void setBudgetRecordingLevel(BudgetRecordingLevel budgetRecordingLevel) {
        this.budgetRecordingLevel = budgetRecordingLevel;
    }

    /**
     * Gets the sufficientFundsCode attribute.
     * 
     * @return Returns the sufficientFundsCode.
     */
    public SufficientFundsCode getSufficientFundsCode() {
        return sufficientFundsCode;
    }

    /**
     * Sets the sufficientFundsCode attribute value.
     * 
     * @param sufficientFundsCode The sufficientFundsCode to set.
     */
    public void setSufficientFundsCode(SufficientFundsCode sufficientFundsCode) {
        this.sufficientFundsCode = sufficientFundsCode;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chartCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);

        return m;
    }


    /**
     * Implementing equals since I need contains to behave reasonably in a hashed datastructure.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                Account other = (Account) obj;

                if (StringUtils.equals(this.getChartOfAccountsCode(), other.getChartOfAccountsCode())) {
                    if (StringUtils.equals(this.getAccountNumber(), other.getAccountNumber())) {
                        equal = true;
                    }
                }
            }
        }

        return equal;
    }

    /**
     * Calcluates hashCode based on current values of chartOfAccountsCode and accountNumber fields. Somewhat dangerous, since both
     * of those fields are mutable, but I don't expect people to be editing those values directly for Accounts stored in hashed
     * datastructures.
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String hashString = getChartOfAccountsCode() + "|" + getAccountNumber();

        return hashString.hashCode();
    }


    /**
     * Convenience method to make the primitive account fields from this Account easier to compare to the account fields from
     * another Account or an AccountingLine
     * 
     * @return String representing the account associated with this Accounting
     */
    public String getAccountKey() {
        String key = getChartOfAccountsCode() + ":" + getAccountNumber();
        return key;
    }

    /**
     * Gets the dummy attribute.
     * 
     * @return Returns the dummy.
     */

    /**
     * Gets the accountResponsibilitySection attribute.
     * 
     * @return Returns the accountResponsibilitySection.
     */
    public String getAccountResponsibilitySection() {
        return accountResponsibilitySection;
    }

    /**
     * Sets the accountResponsibilitySection attribute value.
     * 
     * @param accountResponsibilitySection The accountResponsibilitySection to set.
     */
    public void setAccountResponsibilitySection(String accountResponsibilitySection) {
        this.accountResponsibilitySection = accountResponsibilitySection;
    }

    /**
     * Gets the contractsAndGrantsSection attribute.
     * 
     * @return Returns the contractsAndGrantsSection.
     */
    public String getContractsAndGrantsSection() {
        return contractsAndGrantsSection;
    }

    /**
     * Sets the contractsAndGrantsSection attribute value.
     * 
     * @param contractsAndGrantsSection The contractsAndGrantsSection to set.
     */
    public void setContractsAndGrantsSection(String contractsAndGrantsSection) {
        this.contractsAndGrantsSection = contractsAndGrantsSection;
    }

    /**
     * Gets the accountDescriptionSection attribute.
     * 
     * @return Returns the accountDescriptionSection.
     */
    public String getAccountDescriptionSection() {
        return accountDescriptionSection;
    }

    /**
     * Sets the accountDescriptionSection attribute value.
     * 
     * @param accountDescriptionSection The accountDescriptionSection to set.
     */
    public void setAccountDescriptionSection(String accountDescriptionSection) {
        this.accountDescriptionSection = accountDescriptionSection;
    }

    /**
     * Gets the guidelinesAndPurposeSection attribute.
     * 
     * @return Returns the guidelinesAndPurposeSection.
     */
    public String getGuidelinesAndPurposeSection() {
        return guidelinesAndPurposeSection;
    }

    /**
     * Sets the guidelinesAndPurposeSection attribute value.
     * 
     * @param guidelinesAndPurposeSection The guidelinesAndPurposeSection to set.
     */
    public void setGuidelinesAndPurposeSection(String guidelinesAndPurposeSection) {
        this.guidelinesAndPurposeSection = guidelinesAndPurposeSection;
    }

    /**
     * Gets the accountResponsibilitySectionBlank attribute.
     * 
     * @return Returns the accountResponsibilitySectionBlank.
     */
    public String getAccountResponsibilitySectionBlank() {
        return accountResponsibilitySectionBlank;
    }

    /**
     * Gets the contractsAndGrantsSectionBlank attribute.
     * 
     * @return Returns the contractsAndGrantsSectionBlank.
     */
    public String getContractsAndGrantsSectionBlank() {
        return contractsAndGrantsSectionBlank;
    }

    /**
     * Gets the accountDescriptionSectionBlank attribute.
     * 
     * @return Returns the accountDescriptionSectionBlank.
     */
    public String getAccountDescriptionSectionBlank() {
        return accountDescriptionSectionBlank;
    }

    /**
     * Gets the guidelinesAndPurposeSectionBlank attribute.
     * 
     * @return Returns the guidelinesAndPurposeSectionBlank.
     */
    public String getGuidelinesAndPurposeSectionBlank() {
        return guidelinesAndPurposeSectionBlank;
    }

    /**
     * Gets the forContractsAndGrants attribute.
     * 
     * @return Returns the forContractsAndGrants.
     */
    public boolean isForContractsAndGrants() {
        if ( forContractsAndGrants == null ) {
            forContractsAndGrants = SpringContext.getBean(SubFundGroupService.class).isForContractsAndGrants(getSubFundGroup());
        }
        return forContractsAndGrants;
    }

    /**
     * @see org.kuali.kfs.coa.businessobject.AccountIntf#isClosed()
     */
    public boolean isClosed() {
        return !active;
    }
    
}
