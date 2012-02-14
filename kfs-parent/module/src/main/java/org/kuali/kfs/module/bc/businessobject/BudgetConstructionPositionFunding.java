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

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionPositionFunding extends PersistableBusinessObjectBase {

    private String principalId;
    private String selectedOrganizationChartOfAccountsCode;
    private String selectedOrganizationCode;
    private String name;
    private String emplid;
    private String positionNumber;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;

    private Chart selectedOrganizationChartOfAccounts;
    private Organization selectedOrganization;
    private ObjectCode financialObject;
    private SubObjectCode financialSubObject;
    private Account account;
    private Chart chartOfAccounts;
    private SubAccount subAccount;
    private PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding;

    /**
     * Default constructor.
     */
    public BudgetConstructionPositionFunding() {

    }

    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * Gets the selectedOrganizationChartOfAccountsCode attribute.
     * 
     * @return Returns the selectedOrganizationChartOfAccountsCode
     */
    public String getSelectedOrganizationChartOfAccountsCode() {
        return selectedOrganizationChartOfAccountsCode;
    }

    /**
     * Sets the selectedOrganizationChartOfAccountsCode attribute.
     * 
     * @param selectedOrganizationChartOfAccountsCode The selectedOrganizationChartOfAccountsCode to set.
     */
    public void setSelectedOrganizationChartOfAccountsCode(String selectedOrganizationChartOfAccountsCode) {
        this.selectedOrganizationChartOfAccountsCode = selectedOrganizationChartOfAccountsCode;
    }


    /**
     * Gets the selectedOrganizationCode attribute.
     * 
     * @return Returns the selectedOrganizationCode
     */
    public String getSelectedOrganizationCode() {
        return selectedOrganizationCode;
    }

    /**
     * Sets the selectedOrganizationCode attribute.
     * 
     * @param selectedOrganizationCode The selectedOrganizationCode to set.
     */
    public void setSelectedOrganizationCode(String selectedOrganizationCode) {
        this.selectedOrganizationCode = selectedOrganizationCode;
    }


    /**
     * Gets the name attribute.
     * 
     * @return Returns the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name attribute.
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
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
     * Gets the selectedOrganizationChartOfAccounts attribute.
     * 
     * @return Returns the selectedOrganizationChartOfAccounts
     */
    public Chart getSelectedOrganizationChartOfAccounts() {
        return selectedOrganizationChartOfAccounts;
    }

    /**
     * Sets the selectedOrganizationChartOfAccounts attribute.
     * 
     * @param selectedOrganizationChartOfAccounts The selectedOrganizationChartOfAccounts to set.
     * @deprecated
     */
    public void setSelectedOrganizationChartOfAccounts(Chart selectedOrganizationChartOfAccounts) {
        this.selectedOrganizationChartOfAccounts = selectedOrganizationChartOfAccounts;
    }

    /**
     * Gets the selectedOrganization attribute.
     * 
     * @return Returns the selectedOrganization
     */
    public Organization getSelectedOrganization() {
        return selectedOrganization;
    }

    /**
     * Sets the selectedOrganization attribute.
     * 
     * @param selectedOrganization The selectedOrganization to set.
     * @deprecated
     */
    public void setSelectedOrganization(Organization selectedOrganization) {
        this.selectedOrganization = selectedOrganization;
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
     * Gets the pendingAppointmentFunding attribute.
     * 
     * @return Returns the pendingAppointmentFunding.
     */
    public PendingBudgetConstructionAppointmentFunding getPendingAppointmentFunding() {
        return pendingAppointmentFunding;
    }

    /**
     * Sets the pendingAppointmentFunding attribute value.
     * 
     * @param pendingAppointmentFunding The pendingAppointmentFunding to set.
     * @deprecated
     */
    public void setPendingAppointmentFunding(PendingBudgetConstructionAppointmentFunding pendingAppointmentFunding) {
        this.pendingAppointmentFunding = pendingAppointmentFunding;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        m.put("selectedOrganizationChartOfAccountsCode", this.selectedOrganizationChartOfAccountsCode);
        m.put("selectedOrganizationCode", this.selectedOrganizationCode);
        m.put("name", this.name);
        m.put("emplid", this.emplid);
        m.put("positionNumber", this.positionNumber);
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        return m;
    }

    public SubAccount getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    public SubObjectCode getFinancialSubObject() {
        return financialSubObject;
    }

    public void setFinancialSubObject(SubObjectCode financialSubObject) {
        this.financialSubObject = financialSubObject;
    }


}

