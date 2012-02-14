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

package org.kuali.kfs.module.bc.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * 
 */
public class BudgetConstructionAppointmentFundingLoad01Move extends PersistableBusinessObjectBase {

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String positionNumber;
    private String emplid;
    private String appointmentFundingDurationCode;
    private KualiInteger appointmentRequestedCsfAmount;
    private BigDecimal appointmentRequestedCsfFteQuantity;
    private BigDecimal appointmentRequestedCsfTimePercent;
    private KualiInteger appointmentTotalIntendedAmount;
    private BigDecimal appointmentTotalIntendedFteQuantity;
    private KualiInteger appointmentRequestedAmount;
    private BigDecimal appointmentRequestedTimePercent;
    private BigDecimal appointmentRequestedFteQuantity;
    private BigDecimal appointmentRequestedPayRate;
    private boolean appointmentFundingDeleteIndicator;
    private Integer appointmentFundingMonth;
    private String newChartOfAccountsCode;
    private String newAccountNumber;

    private ObjectCode financialObject;
    private BudgetConstructionSalaryFunding budgetConstructionSalaryFunding;
    private Chart chartOfAccounts;
    private Account account;
    private SubAccount subAccount;
    private SubObjectCode financialSubObject;
    private Chart newChartOfAccounts;
    private Account newAccount;

    /**
     * Default constructor.
     */
    public BudgetConstructionAppointmentFundingLoad01Move() {

    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
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
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }


    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }


    /**
     * Gets the appointmentFundingDurationCode attribute.
     * 
     * @return Returns the appointmentFundingDurationCode
     */
    public String getAppointmentFundingDurationCode() {
        return appointmentFundingDurationCode;
    }

    /**
     * Sets the appointmentFundingDurationCode attribute.
     * 
     * @param appointmentFundingDurationCode The appointmentFundingDurationCode to set.
     */
    public void setAppointmentFundingDurationCode(String appointmentFundingDurationCode) {
        this.appointmentFundingDurationCode = appointmentFundingDurationCode;
    }


    /**
     * Gets the appointmentRequestedCsfAmount attribute.
     * 
     * @return Returns the appointmentRequestedCsfAmount.
     */
    public KualiInteger getAppointmentRequestedCsfAmount() {
        return appointmentRequestedCsfAmount;
    }

    /**
     * Sets the appointmentRequestedCsfAmount attribute value.
     * 
     * @param appointmentRequestedCsfAmount The appointmentRequestedCsfAmount to set.
     */
    public void setAppointmentRequestedCsfAmount(KualiInteger appointmentRequestedCsfAmount) {
        this.appointmentRequestedCsfAmount = appointmentRequestedCsfAmount;
    }

    /**
     * Gets the appointmentRequestedCsfFteQuantity attribute.
     * 
     * @return Returns the appointmentRequestedCsfFteQuantity
     */
    public BigDecimal getAppointmentRequestedCsfFteQuantity() {
        return appointmentRequestedCsfFteQuantity;
    }

    /**
     * Sets the appointmentRequestedCsfFteQuantity attribute.
     * 
     * @param appointmentRequestedCsfFteQuantity The appointmentRequestedCsfFteQuantity to set.
     */
    public void setAppointmentRequestedCsfFteQuantity(BigDecimal appointmentRequestedCsfFteQuantity) {
        this.appointmentRequestedCsfFteQuantity = appointmentRequestedCsfFteQuantity;
    }


    /**
     * Gets the appointmentRequestedCsfTimePercent attribute.
     * 
     * @return Returns the appointmentRequestedCsfTimePercent
     */
    public BigDecimal getAppointmentRequestedCsfTimePercent() {
        return appointmentRequestedCsfTimePercent;
    }

    /**
     * Sets the appointmentRequestedCsfTimePercent attribute.
     * 
     * @param appointmentRequestedCsfTimePercent The appointmentRequestedCsfTimePercent to set.
     */
    public void setAppointmentRequestedCsfTimePercent(BigDecimal appointmentRequestedCsfTimePercent) {
        this.appointmentRequestedCsfTimePercent = appointmentRequestedCsfTimePercent;
    }


    /**
     * Gets the appointmentTotalIntendedAmount attribute.
     * 
     * @return Returns the appointmentTotalIntendedAmount.
     */
    public KualiInteger getAppointmentTotalIntendedAmount() {
        return appointmentTotalIntendedAmount;
    }

    /**
     * Sets the appointmentTotalIntendedAmount attribute value.
     * 
     * @param appointmentTotalIntendedAmount The appointmentTotalIntendedAmount to set.
     */
    public void setAppointmentTotalIntendedAmount(KualiInteger appointmentTotalIntendedAmount) {
        this.appointmentTotalIntendedAmount = appointmentTotalIntendedAmount;
    }

    /**
     * Gets the appointmentTotalIntendedFteQuantity attribute.
     * 
     * @return Returns the appointmentTotalIntendedFteQuantity
     */
    public BigDecimal getAppointmentTotalIntendedFteQuantity() {
        return appointmentTotalIntendedFteQuantity;
    }

    /**
     * Sets the appointmentTotalIntendedFteQuantity attribute.
     * 
     * @param appointmentTotalIntendedFteQuantity The appointmentTotalIntendedFteQuantity to set.
     */
    public void setAppointmentTotalIntendedFteQuantity(BigDecimal appointmentTotalIntendedFteQuantity) {
        this.appointmentTotalIntendedFteQuantity = appointmentTotalIntendedFteQuantity;
    }


    /**
     * Gets the appointmentRequestedAmount attribute.
     * 
     * @return Returns the appointmentRequestedAmount.
     */
    public KualiInteger getAppointmentRequestedAmount() {
        return appointmentRequestedAmount;
    }

    /**
     * Sets the appointmentRequestedAmount attribute value.
     * 
     * @param appointmentRequestedAmount The appointmentRequestedAmount to set.
     */
    public void setAppointmentRequestedAmount(KualiInteger appointmentRequestedAmount) {
        this.appointmentRequestedAmount = appointmentRequestedAmount;
    }

    /**
     * Gets the appointmentRequestedTimePercent attribute.
     * 
     * @return Returns the appointmentRequestedTimePercent
     */
    public BigDecimal getAppointmentRequestedTimePercent() {
        return appointmentRequestedTimePercent;
    }

    /**
     * Sets the appointmentRequestedTimePercent attribute.
     * 
     * @param appointmentRequestedTimePercent The appointmentRequestedTimePercent to set.
     */
    public void setAppointmentRequestedTimePercent(BigDecimal appointmentRequestedTimePercent) {
        this.appointmentRequestedTimePercent = appointmentRequestedTimePercent;
    }


    /**
     * Gets the appointmentRequestedFteQuantity attribute.
     * 
     * @return Returns the appointmentRequestedFteQuantity
     */
    public BigDecimal getAppointmentRequestedFteQuantity() {
        return appointmentRequestedFteQuantity;
    }

    /**
     * Sets the appointmentRequestedFteQuantity attribute.
     * 
     * @param appointmentRequestedFteQuantity The appointmentRequestedFteQuantity to set.
     */
    public void setAppointmentRequestedFteQuantity(BigDecimal appointmentRequestedFteQuantity) {
        this.appointmentRequestedFteQuantity = appointmentRequestedFteQuantity;
    }


    /**
     * Gets the appointmentRequestedPayRate attribute.
     * 
     * @return Returns the appointmentRequestedPayRate
     */
    public BigDecimal getAppointmentRequestedPayRate() {
        return appointmentRequestedPayRate;
    }

    /**
     * Sets the appointmentRequestedPayRate attribute.
     * 
     * @param appointmentRequestedPayRate The appointmentRequestedPayRate to set.
     */
    public void setAppointmentRequestedPayRate(BigDecimal appointmentRequestedPayRate) {
        this.appointmentRequestedPayRate = appointmentRequestedPayRate;
    }


    /**
     * Gets the appointmentFundingDeleteIndicator attribute.
     * 
     * @return Returns the appointmentFundingDeleteIndicator
     */
    public boolean isAppointmentFundingDeleteIndicator() {
        return appointmentFundingDeleteIndicator;
    }


    /**
     * Sets the appointmentFundingDeleteIndicator attribute.
     * 
     * @param appointmentFundingDeleteIndicator The appointmentFundingDeleteIndicator to set.
     */
    public void setAppointmentFundingDeleteIndicator(boolean appointmentFundingDeleteIndicator) {
        this.appointmentFundingDeleteIndicator = appointmentFundingDeleteIndicator;
    }


    /**
     * Gets the appointmentFundingMonth attribute.
     * 
     * @return Returns the appointmentFundingMonth
     */
    public Integer getAppointmentFundingMonth() {
        return appointmentFundingMonth;
    }

    /**
     * Sets the appointmentFundingMonth attribute.
     * 
     * @param appointmentFundingMonth The appointmentFundingMonth to set.
     */
    public void setAppointmentFundingMonth(Integer appointmentFundingMonth) {
        this.appointmentFundingMonth = appointmentFundingMonth;
    }

    /**
     * Gets the financialObject attribute.
     * 
     * @return Returns the financialObject
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute.
     * 
     * @param financialObject The financialObject to set.
     * @deprecated
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the budgetConstructionSalaryFunding attribute.
     * 
     * @return Returns the budgetConstructionSalaryFunding
     */
    public BudgetConstructionSalaryFunding getBudgetConstructionSalaryFunding() {
        return budgetConstructionSalaryFunding;
    }

    /**
     * Sets the budgetConstructionSalaryFunding attribute.
     * 
     * @param budgetConstructionSalaryFunding The budgetConstructionSalaryFunding to set.
     * @deprecated
     */
    public void setBudgetConstructionSalaryFunding(BudgetConstructionSalaryFunding budgetConstructionSalaryFunding) {
        this.budgetConstructionSalaryFunding = budgetConstructionSalaryFunding;
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
     * Gets the account attribute.
     * 
     * @return Returns the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the financialSubObject attribute.
     * 
     * @return Returns the financialSubObject.
     */
    public SubObjectCode getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute value.
     * 
     * @param financialSubObject The financialSubObject to set.
     * @deprecated
     */
    public void setFinancialSubObject(SubObjectCode financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    /**
     * Gets the subAccount attribute.
     * 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * 
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the newAccountNumber attribute.
     * 
     * @return Returns the newAccountNumber.
     */
    public String getNewAccountNumber() {
        return newAccountNumber;
    }

    /**
     * Sets the newAccountNumber attribute value.
     * 
     * @param newAccountNumber The newAccountNumber to set.
     */
    public void setNewAccountNumber(String newAccountNumber) {
        this.newAccountNumber = newAccountNumber;
    }

    /**
     * Gets the newChartOfAccountsCode attribute.
     * 
     * @return Returns the newChartOfAccountsCode.
     */
    public String getNewChartOfAccountsCode() {
        return newChartOfAccountsCode;
    }

    /**
     * Sets the newChartOfAccountsCode attribute value.
     * 
     * @param newChartOfAccountsCode The newChartOfAccountsCode to set.
     */
    public void setNewChartOfAccountsCode(String newChartOfAccountsCode) {
        this.newChartOfAccountsCode = newChartOfAccountsCode;
    }

    /**
     * Gets the newAccount attribute.
     * 
     * @return Returns the newAccount.
     */
    public Account getNewAccount() {
        return newAccount;
    }

    /**
     * Sets the newAccount attribute value.
     * 
     * @param newAccount The newAccount to set.
     * @deprecated
     */
    public void setNewAccount(Account newAccount) {
        this.newAccount = newAccount;
    }

    /**
     * Gets the newChartOfAccounts attribute.
     * 
     * @return Returns the newChartOfAccounts.
     */
    public Chart getNewChartOfAccounts() {
        return newChartOfAccounts;
    }

    /**
     * Sets the newChartOfAccounts attribute value.
     * 
     * @param newChartOfAccounts The newChartOfAccounts to set.
     * @deprecated
     */
    public void setNewChartOfAccounts(Chart newChartOfAccounts) {
        this.newChartOfAccounts = newChartOfAccounts;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        m.put("positionNumber", this.positionNumber);
        m.put("emplid", this.emplid);
        return m;
    }

}
