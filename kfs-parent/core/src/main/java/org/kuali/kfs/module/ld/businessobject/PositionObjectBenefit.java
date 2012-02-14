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

package org.kuali.kfs.module.ld.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.ld.LaborLedgerBenefitsCalculation;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectBenefit;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * Labor business object for Modeling of Position Object Benefit
 */
public class PositionObjectBenefit extends PersistableBusinessObjectBase implements LaborLedgerPositionObjectBenefit, Inactivateable {
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String financialObjectCode;
    private String financialObjectBenefitsTypeCode;
    private Chart chartOfAccounts;
    private boolean active;
  
    private ObjectCode financialObject;
    private transient SystemOptions universityFiscal;
    private BenefitsCalculation benefitsCalculation;
    private BenefitsType financialObjectBenefitsType;
    private LaborObject laborObject;

    /**
     * Default constructor.
     */
    public PositionObjectBenefit() {

    }

    /**
     * Gets the universityFiscalYear
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the chartOfAccountsCode
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the financialObjectCode
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialObjectBenefitsTypeCode
     * 
     * @return Returns the financialObjectBenefitsTypeCode
     */
    public String getFinancialObjectBenefitsTypeCode() {
        return financialObjectBenefitsTypeCode;
    }

    /**
     * Sets the financialObjectBenefitsTypeCode
     * 
     * @param financialObjectBenefitsTypeCode The financialObjectBenefitsTypeCode to set.
     */
    public void setFinancialObjectBenefitsTypeCode(String financialObjectBenefitsTypeCode) {
        this.financialObjectBenefitsTypeCode = financialObjectBenefitsTypeCode;
    }

    /**
     * Gets the financialObject
     * 
     * @return Returns the financialObject
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject
     * 
     * @param financialObject The financialObject to set.
     */
    @Deprecated
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the chartOfAccounts
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the universityFiscal
     * 
     * @return Returns the universityFiscal.
     */
    public SystemOptions getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal
     * 
     * @param universityFiscal The universityFiscal to set.
     */
    @Deprecated
    public void setUniversityFiscal(SystemOptions universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

    /**
     * Gets the benefitsCalculation
     * 
     * @return Returns the benefitsCalculation.
     */
    public BenefitsCalculation getBenefitsCalculation() {
        return benefitsCalculation;
    }

    /**
     * Sets the benefitsCalculation
     * 
     * @param benefitsCalculation The benefitsCalculation to set.
     */
    @Deprecated
    public void setBenefitsCalculation(BenefitsCalculation benefitsCalculation) {
        this.benefitsCalculation = benefitsCalculation;
    }

    /**
     * Gets the financialObjectBenefitsType
     * 
     * @return financialObjectBenefitsType
     */
    public BenefitsType getFinancialObjectBenefitsType() {
        return financialObjectBenefitsType;
    }

    /**
     * Sets financialObjectBenefitsType
     * 
     * @param financialObjectBenefitsType The financialObjectBenefitsType to be set
     */
    @Deprecated
    public void setFinancialObjectBenefitsType(BenefitsType financialObjectBenefitsType) {
        this.financialObjectBenefitsType = financialObjectBenefitsType;
    }

    /**
     * Gets the laborObject
     * 
     * @return Returns the laborObject.
     */
    public LaborObject getLaborObject() {
        return laborObject;
    }

    /**
     * Sets the laborObject
     * 
     * @param laborObject The laborObject to set.
     */
    @Deprecated
    public void setLaborObject(LaborObject laborObject) {
        this.laborObject = laborObject;
    }
    
    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * This method (a hack by any other name...) returns a string so that an Labor Object Code Benefits can have a link to view its own
     * inquiry page after a look up
     * 
     * @return the String "View Labor Object Code Benefits"
     */
    public String getLaborObjectCodeBenefitsViewer() {
        return "View Labor Object Code Benefits";
    }
    
    /**
     * construct the key list of the business object.
     * 
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialObjectBenefitsTypeCode", this.financialObjectBenefitsTypeCode);

        return m;
    }

    /**
     * @see org.kuali.kfs.integration.businessobject.LaborLedgerPositionObjectBenefit#getLaborLedgerBenefitsCalculation()
     */
    public LaborLedgerBenefitsCalculation getLaborLedgerBenefitsCalculation() {
        return this.getBenefitsCalculation();
    }

    /**
     * @see org.kuali.kfs.integration.businessobject.LaborLedgerPositionObjectBenefit#setLaborLedgerBenefitsCalculation(org.kuali.kfs.integration.businessobject.LaborLedgerBenefitsCalculation)
     */
    public void setLaborLedgerBenefitsCalculation(LaborLedgerBenefitsCalculation laborLedgerBenefitsCalculation) {
        benefitsCalculation = (BenefitsCalculation)laborLedgerBenefitsCalculation;
    }
}
