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

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.KualiPercent;

/**
 * Holds the single line benefits impact screen detail information for a particular request amount
 */
public class RequestBenefits extends TransientBusinessObjectBase {
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String financialObjectCode;
    private String financialObjectBenefitsTypeCode;
    private String financialObjectBenefitsTypeDescription;
    private KualiPercent positionFringeBenefitPercent;
    private String positionFringeBenefitObjectCode;
    private String positionFringeBenefitObjectCodeName;
    private KualiInteger fringeDetailAmount;
    private KualiInteger accountLineAnnualBalanceAmount;

    /**
     * Constructs a RequestBenefits.java.
     */
    public RequestBenefits() {
        super();
    }

    /**
     * Gets the universityFiscalYear attribute. 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the chartOfAccountsCode attribute. 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the financialObjectCode attribute. 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialObjectBenefitsTypeCode attribute. 
     * @return Returns the financialObjectBenefitsTypeCode.
     */
    public String getFinancialObjectBenefitsTypeCode() {
        return financialObjectBenefitsTypeCode;
    }

    /**
     * Sets the financialObjectBenefitsTypeCode attribute value.
     * @param financialObjectBenefitsTypeCode The financialObjectBenefitsTypeCode to set.
     */
    public void setFinancialObjectBenefitsTypeCode(String financialObjectBenefitsTypeCode) {
        this.financialObjectBenefitsTypeCode = financialObjectBenefitsTypeCode;
    }

    /**
     * Gets the financialObjectBenefitsTypeDescription attribute. 
     * @return Returns the financialObjectBenefitsTypeDescription.
     */
    public String getFinancialObjectBenefitsTypeDescription() {
        return financialObjectBenefitsTypeDescription;
    }

    /**
     * Sets the financialObjectBenefitsTypeDescription attribute value.
     * @param financialObjectBenefitsTypeDescription The financialObjectBenefitsTypeDescription to set.
     */
    public void setFinancialObjectBenefitsTypeDescription(String financialObjectBenefitsTypeDescription) {
        this.financialObjectBenefitsTypeDescription = financialObjectBenefitsTypeDescription;
    }

    /**
     * Gets the positionFringeBenefitPercent attribute. 
     * @return Returns the positionFringeBenefitPercent.
     */
    public KualiPercent getPositionFringeBenefitPercent() {
        return positionFringeBenefitPercent;
    }

    /**
     * Sets the positionFringeBenefitPercent attribute value.
     * @param positionFringeBenefitPercent The positionFringeBenefitPercent to set.
     */
    public void setPositionFringeBenefitPercent(KualiPercent positionFringeBenefitPercent) {
        this.positionFringeBenefitPercent = positionFringeBenefitPercent;
    }

    /**
     * Gets the positionFringeBenefitObjectCode attribute. 
     * @return Returns the positionFringeBenefitObjectCode.
     */
    public String getPositionFringeBenefitObjectCode() {
        return positionFringeBenefitObjectCode;
    }

    /**
     * Sets the positionFringeBenefitObjectCode attribute value.
     * @param positionFringeBenefitObjectCode The positionFringeBenefitObjectCode to set.
     */
    public void setPositionFringeBenefitObjectCode(String positionFringeBenefitObjectCode) {
        this.positionFringeBenefitObjectCode = positionFringeBenefitObjectCode;
    }

    /**
     * Gets the positionFringeBenefitObjectCodeName attribute. 
     * @return Returns the positionFringeBenefitObjectCodeName.
     */
    public String getPositionFringeBenefitObjectCodeName() {
        return positionFringeBenefitObjectCodeName;
    }

    /**
     * Sets the positionFringeBenefitObjectCodeName attribute value.
     * @param positionFringeBenefitObjectCodeName The positionFringeBenefitObjectCodeName to set.
     */
    public void setPositionFringeBenefitObjectCodeName(String positionFringeBenefitObjectCodeName) {
        this.positionFringeBenefitObjectCodeName = positionFringeBenefitObjectCodeName;
    }

    /**
     * Gets the fringeDetailAmount attribute. 
     * @return Returns the fringeDetailAmount.
     */
    public KualiInteger getFringeDetailAmount() {
        return fringeDetailAmount;
    }

    /**
     * Sets the fringeDetailAmount attribute value.
     * @param fringeDetailAmount The fringeDetailAmount to set.
     */
    public void setFringeDetailAmount(KualiInteger fringeDetailAmount) {
        this.fringeDetailAmount = fringeDetailAmount;
    }

    /**
     * Gets the accountLineAnnualBalanceAmount attribute. 
     * @return Returns the accountLineAnnualBalanceAmount.
     */
    public KualiInteger getAccountLineAnnualBalanceAmount() {
        return accountLineAnnualBalanceAmount;
    }

    /**
     * Sets the accountLineAnnualBalanceAmount attribute value.
     * @param accountLineAnnualBalanceAmount The accountLineAnnualBalanceAmount to set.
     */
    public void setAccountLineAnnualBalanceAmount(KualiInteger accountLineAnnualBalanceAmount) {
        this.accountLineAnnualBalanceAmount = accountLineAnnualBalanceAmount;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();

        if (this.universityFiscalYear != null) {
            m.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialObjectBenefitsTypeCode", this.financialObjectBenefitsTypeCode);

        return m;
    }

}
