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
package org.kuali.kfs.sys.businessobject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.fp.businessobject.SalesTax;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.service.impl.KEWModuleService;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This is the generic class which contains all the elements on a typical line of accounting elements. These are all the accounting
 * items necessary to create a pending entry to the G/L. All transaction documents will use this business object inherently.
 */
public abstract class AccountingLineBase extends PersistableBusinessObjectBase implements Serializable, AccountingLine, GeneralLedgerPendingEntrySourceDetail {
    private static Logger LOG = Logger.getLogger(AccountingLineBase.class);

    private String documentNumber;
    private Integer sequenceNumber; // relative to the grouping of acctng lines
    private Integer postingYear;
    private KualiDecimal amount;
    private String referenceOriginCode;
    private String referenceNumber;
    private String referenceTypeCode;
    private String overrideCode = AccountingLineOverride.CODE.NONE;
    private boolean accountExpiredOverride; // for the UI, persisted in overrideCode
    private boolean accountExpiredOverrideNeeded; // for the UI, not persisted
    private boolean nonFringeAccountOverride; // for the UI, persisted in overrideCode
    private boolean nonFringeAccountOverrideNeeded; // for the UI, not persisted
    private boolean objectBudgetOverride;
    private boolean objectBudgetOverrideNeeded;
    private String organizationReferenceId;
    private String debitCreditCode; // should only be set by the Journal Voucher or Auxiliary Voucher document
    private String encumbranceUpdateCode; // should only be set by the Journal Voucher document
    protected String financialDocumentLineTypeCode;
    protected String financialDocumentLineDescription;
    protected boolean salesTaxRequired;

    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private String subAccountNumber;
    private String financialSubObjectCode;
    private String projectCode;
    private String balanceTypeCode;

    // bo references
    private Chart chart;
    private Account account;
    private ObjectCode objectCode;
    private SubAccount subAccount;
    private SubObjectCode subObjectCode;
    private ProjectCode project;
    private BalanceType balanceTyp;
    private OriginationCode referenceOrigin;
    private DocumentTypeEBO referenceFinancialSystemDocumentTypeCode;
    private SalesTax salesTax;

    /**
     * This constructor sets up empty instances for the dependent objects.
     */
    public AccountingLineBase() {
        setAmount(KualiDecimal.ZERO);
        chart = new Chart();
        account = new Account();
        objectCode = new ObjectCode();
        subAccount = new SubAccount();
        subObjectCode = new SubObjectCode();
        project = new ProjectCode();

        balanceTyp = new BalanceType();
        // salesTax = new SalesTax();
        salesTaxRequired = false;
    }


    /**
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * @param chart The chartOfAccountsCode to set.
     * @deprecated
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @return Returns the amount.
     */
    public KualiDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount The amount to set.
     */
    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return Returns the balanceTyp.
     */
    public BalanceType getBalanceTyp() {
        return balanceTyp;
    }

    /**
     * @param balanceTyp The balanceTyp to set.
     * @deprecated
     */
    public void setBalanceTyp(BalanceType balanceTyp) {
        this.balanceTyp = balanceTyp;
    }

    /**
     * @return Returns the objectCode.
     */
    public ObjectCode getObjectCode() {
        return objectCode;
    }

    /**
     * @param objectCode The objectCode to set.
     * @deprecated
     */
    public void setObjectCode(ObjectCode objectCode) {
        this.objectCode = objectCode;
    }

    /**
     * @return Returns the referenceOriginCode.
     */
    public String getReferenceOriginCode() {
        return referenceOriginCode;
    }

    /**
     * @param originCode The referenceOriginCode to set.
     */
    public void setReferenceOriginCode(String originCode) {
        this.referenceOriginCode = originCode;
    }

    /**
     * This method returns the object related to referenceOriginCode
     * 
     * @return referenceOrigin
     */
    public OriginationCode getReferenceOrigin() {
        return referenceOrigin;
    }

    /**
     * This method sets the referenceOrigin object, this is only to be used by OJB
     * 
     * @param referenceOrigin
     * @deprecated
     */
    public void setReferenceOrigin(OriginationCode referenceOrigin) {
        this.referenceOrigin = referenceOrigin;
    }

    /**
     * Gets the referenceFinancialSystemDocumentTypeCode attribute. 
     * @return Returns the referenceFinancialSystemDocumentTypeCode.
     */
    public DocumentTypeEBO getReferenceFinancialSystemDocumentTypeCode() {
        return referenceFinancialSystemDocumentTypeCode = SpringContext.getBean(KEWModuleService.class).retrieveExternalizableBusinessObjectIfNecessary(this, referenceFinancialSystemDocumentTypeCode, "referenceFinancialSystemDocumentTypeCode");
    }

    /**
     * @return Returns the organizationReferenceId.
     */
    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    /**
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    /**
     * @return Returns the overrideCode.
     */
    public String getOverrideCode() {
        return overrideCode;
    }

    /**
     * @param overrideCode The overrideCode to set.
     */
    public void setOverrideCode(String overrideCode) {
        this.overrideCode = overrideCode;
    }

    /**
     * @return Returns the postingYear.
     */
    public Integer getPostingYear() {
        if (postingYear == null) {
            postingYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        }
        return postingYear;
    }

    /**
     * @param postingYear The postingYear to set.
     */
    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }

    /**
     * @return Returns the projectCode.
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * @return Returns the referenceNumber.
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * @param referenceNumber The referenceNumber to set.
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * @return Returns the referenceTypeCode.
     */
    public String getReferenceTypeCode() {
        return referenceTypeCode;
    }

    /**
     * @param referenceTypeCode The referenceTypeCode to set.
     */
    public void setReferenceTypeCode(String referenceTypeCode) {
        this.referenceTypeCode = referenceTypeCode;
    }

    /**
     * @return Returns the sequenceNumber.
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @param sequenceNumber The sequenceNumber to set.
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * @return Returns the subObjectCode.
     */
    public SubObjectCode getSubObjectCode() {
        return subObjectCode;
    }

    /**
     * @param subObjectCode The subObjectCode to set.
     * @deprecated
     */
    public void setSubObjectCode(SubObjectCode subObjectCode) {
        this.subObjectCode = subObjectCode;
    }


    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#getSalesTax()
     */
    public SalesTax getSalesTax() {
        return salesTax;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#setSalesTax(org.kuali.kfs.fp.businessobject.SalesTax)
     * @deprecated
     */
    public void setSalesTax(SalesTax salesTax) {
        this.salesTax = salesTax;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#isSalesTaxRequired()
     */
    public boolean isSalesTaxRequired() {
        return salesTaxRequired;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#setSalesTaxRequired(boolean)
     */
    public void setSalesTaxRequired(boolean salesTaxRequired) {
        this.salesTaxRequired = salesTaxRequired;
    }


    /**
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * This method retrieves the debit/credit code for the accounting line. This method will only return a not null value for a
     * Journal Voucher document.
     * 
     * @return A String code.
     */
    public String getDebitCreditCode() {
        return debitCreditCode;
    }

    /**
     * This method sets the debit/credit code for the accounting line. This method should only be used for a Journal Voucher
     * document.
     * 
     * @param debitCreditCode
     */
    public void setDebitCreditCode(String debitCreditCode) {
        this.debitCreditCode = debitCreditCode;
    }

    /**
     * This method retrieves the encumbrance update code for the accounting line. This method will only return a not null value for
     * a Journal Voucher document.
     * 
     * @return A String code.
     */
    public String getEncumbranceUpdateCode() {
        return encumbranceUpdateCode;
    }

    /**
     * This method sets the debit/credit code for the accounting line. This method should only be used for a Journal Voucher
     * document.
     * 
     * @param encumbranceUpdateCode
     */
    public void setEncumbranceUpdateCode(String encumbranceUpdateCode) {
        this.encumbranceUpdateCode = encumbranceUpdateCode;
    }

    /**
     * This method retrieves the ObjectType for the accounting line. This method will only return a not null value for a Journal
     * Voucher document.
     * 
     * @return An ObjectType instance.
     */
    public ObjectType getObjectType() {
        if ( getObjectTypeCode() != null ) {
            return objectCode.getFinancialObjectType();
        }
        return null;
    }

    /**
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        // if accounts can't cross charts, set chart code whenever account number is set
        SpringContext.getBean(AccountService.class).populateAccountingLineChartIfNeeded(this);
    }

    /**
     * @return Returns the balanceTypeCode.
     */
    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }

    /**
     * @param balanceTypeCode The balanceTypeCode to set.
     */
    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
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
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * @return Returns the objectTypeCode.
     */
    public String getObjectTypeCode() {
        if ( ObjectUtils.isNull(objectCode)
                || !StringUtils.equals(getFinancialObjectCode(), objectCode.getFinancialObjectCode())
                || !StringUtils.equals(getChartOfAccountsCode(), objectCode.getChartOfAccountsCode())
                || !getPostingYear().equals(objectCode.getUniversityFiscalYear() )
                        ) {
            refreshReferenceObject("objectCode");
        }
       
        if (!ObjectUtils.isNull(objectCode)) {
            return objectCode.getFinancialObjectTypeCode();
        }
        return null;
    }

    /**
     * @return Returns the financialDocumentLineTypeCode.
     */
    public String getFinancialDocumentLineTypeCode() {
        return financialDocumentLineTypeCode;
    }

    /**
     * @param financialDocumentLineTypeCode The financialDocumentLineTypeCode to set.
     */
    public void setFinancialDocumentLineTypeCode(String financialDocumentLineTypeCode) {
        this.financialDocumentLineTypeCode = financialDocumentLineTypeCode;
    }

    /**
     * @return Returns the project.
     */
    public ProjectCode getProject() {
        return project;
    }

    /**
     * @param project The project to set.
     * @deprecated
     */
    public void setProject(ProjectCode project) {
        this.project = project;
    }

    /**
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * @return Returns the financialDocumentLineDescription.
     */
    public String getFinancialDocumentLineDescription() {
        return financialDocumentLineDescription;
    }

    /**
     * @param financialDocumentLineDescription The financialDocumentLineDescription to set.
     */
    public void setFinancialDocumentLineDescription(String financialDocumentLineDescription) {
        this.financialDocumentLineDescription = financialDocumentLineDescription;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

        m.put("sequenceNumber", sequenceNumber);
        m.put("postingYear", postingYear);
        m.put("amount", amount);
        m.put("debitCreditCode", debitCreditCode);
        m.put("encumbranceUpdateCode", encumbranceUpdateCode);
        m.put("financialDocumentLineDescription", financialDocumentLineDescription);

        m.put("chart", getChartOfAccountsCode());
        m.put("account", getAccountNumber());
        m.put("objectCode", getFinancialObjectCode());
        m.put("subAccount", getSubAccountNumber());
        m.put("subObjectCode", getFinancialSubObjectCode());
        m.put("projectCode", getProjectCode());
        m.put("balanceTyp", getBalanceTypeCode());

        m.put("orgRefId", getOrganizationReferenceId());

        return m;
    }

    /**
     * @see org.kuali.rice.kns.bo.AccountingLine#isSourceAccountingLine()
     */
    public boolean isSourceAccountingLine() {
        return (this instanceof SourceAccountingLine);
    }

    /**
     * @see org.kuali.rice.kns.bo.AccountingLine#isTargetAccountingLine()
     */
    public boolean isTargetAccountingLine() {
        return (this instanceof TargetAccountingLine);
    }


    /**
     * @see org.kuali.rice.kns.bo.AccountingLine#getAccountKey()
     */
    public String getAccountKey() {
        String key = getChartOfAccountsCode() + ":" + getAccountNumber();
        return key;
    }


    /**
     * @see org.kuali.rice.kns.bo.AccountingLine#copyFrom(org.kuali.rice.kns.bo.AccountingLine)
     */
    public void copyFrom(AccountingLine other) {
        if (other == null) {
            throw new IllegalArgumentException("invalid (null) other");
        }

        if (this != other) {
            // primitive fields
            setSequenceNumber(other.getSequenceNumber());
            setDocumentNumber(other.getDocumentNumber());
            setPostingYear(other.getPostingYear());
            setAmount(other.getAmount());
            setReferenceOriginCode(other.getReferenceOriginCode());
            setReferenceNumber(other.getReferenceNumber());
            setReferenceTypeCode(other.getReferenceTypeCode());
            setOverrideCode(other.getOverrideCode());
            setOrganizationReferenceId(other.getOrganizationReferenceId());
            setDebitCreditCode(other.getDebitCreditCode());
            setEncumbranceUpdateCode(other.getEncumbranceUpdateCode());
            setFinancialDocumentLineTypeCode(other.getFinancialDocumentLineTypeCode());
            setFinancialDocumentLineDescription(other.getFinancialDocumentLineDescription());
            setAccountExpiredOverride(other.getAccountExpiredOverride());
            setAccountExpiredOverrideNeeded(other.getAccountExpiredOverrideNeeded());
            setObjectBudgetOverride(other.isObjectBudgetOverride());
            setObjectBudgetOverrideNeeded(other.isObjectBudgetOverrideNeeded());

            // foreign keys
            setChartOfAccountsCode(other.getChartOfAccountsCode());
            setAccountNumber(other.getAccountNumber());
            setFinancialObjectCode(other.getFinancialObjectCode());
            setSubAccountNumber(other.getSubAccountNumber());
            setFinancialSubObjectCode(other.getFinancialSubObjectCode());
            setProjectCode(other.getProjectCode());
            setBalanceTypeCode(other.getBalanceTypeCode());

            // sales tax
            if (ObjectUtils.isNotNull(other.getSalesTax())) {
                SalesTax salesTax = getSalesTax();
                SalesTax origSalesTax = other.getSalesTax();
                if (salesTax != null) {
                    salesTax.setAccountNumber(origSalesTax.getAccountNumber());
                    salesTax.setChartOfAccountsCode(origSalesTax.getChartOfAccountsCode());
                    salesTax.setFinancialDocumentGrossSalesAmount(origSalesTax.getFinancialDocumentGrossSalesAmount());
                    salesTax.setFinancialDocumentTaxableSalesAmount(origSalesTax.getFinancialDocumentTaxableSalesAmount());
                    salesTax.setFinancialDocumentSaleDate(origSalesTax.getFinancialDocumentSaleDate());

                    // primary keys
                    salesTax.setDocumentNumber(other.getDocumentNumber());
                    salesTax.setFinancialDocumentLineNumber(other.getSequenceNumber());
                    salesTax.setFinancialDocumentLineTypeCode(other.getFinancialDocumentLineTypeCode());
                }
                else {
                    salesTax = origSalesTax;
                }
            }

            // object references
            setChart(other.getChart());
            setAccount(other.getAccount());
            setObjectCode(other.getObjectCode());
            setSubAccount(other.getSubAccount());
            setSubObjectCode(other.getSubObjectCode());
            setProject(other.getProject());
            setBalanceTyp(other.getBalanceTyp());
        }
    }


    /**
     * @see org.kuali.rice.kns.bo.AccountingLine#isLike(org.kuali.rice.kns.bo.AccountingLine)
     */
    public boolean isLike(AccountingLine other) {
        boolean isLike = false;

        if (other != null) {
            if (other == this) {
                isLike = true;
            }
            else {
                Map thisValues = this.getValuesMap();
                Map otherValues = other.getValuesMap();

                isLike = thisValues.equals(otherValues);

                if (!isLike && LOG.isDebugEnabled()) {
                    StringBuffer inequalities = new StringBuffer();
                    boolean first = true;

                    for (Iterator i = thisValues.keySet().iterator(); i.hasNext();) {
                        String key = (String) i.next();

                        Object thisValue = thisValues.get(key);
                        Object otherValue = otherValues.get(key);
                        if (!org.apache.commons.lang.ObjectUtils.equals(thisValue, otherValue)) {
                            inequalities.append(key + "(" + thisValue + " != " + otherValue + ")");

                            if (first) {
                                first = false;
                            }
                            else {
                                inequalities.append(",");
                            }
                        }
                    }

                    LOG.debug("inequalities: " + inequalities);
                }
            }
        }

        return isLike;
    }

    /**
     * @see AccountingLine#getAccountExpiredOverride()
     */
    public boolean getAccountExpiredOverride() {
        return accountExpiredOverride;
    }

    /**
     * @see AccountingLine#setAccountExpiredOverride(boolean)
     */
    public void setAccountExpiredOverride(boolean b) {
        accountExpiredOverride = b;
    }

    /**
     * @see AccountingLine#getAccountExpiredOverrideNeeded()
     */
    public boolean getAccountExpiredOverrideNeeded() {
        return accountExpiredOverrideNeeded;
    }

    /**
     * @see AccountingLine#setAccountExpiredOverrideNeeded(boolean)
     */
    public void setAccountExpiredOverrideNeeded(boolean b) {
        accountExpiredOverrideNeeded = b;
    }

    /**
     * @return Returns the objectBudgetOverride.
     */
    public boolean isObjectBudgetOverride() {
        return objectBudgetOverride;
    }

    /**
     * @param objectBudgetOverride The objectBudgetOverride to set.
     */
    public void setObjectBudgetOverride(boolean objectBudgetOverride) {
        this.objectBudgetOverride = objectBudgetOverride;
    }

    /**
     * @return Returns the objectBudgetOverrideNeeded.
     */
    public boolean isObjectBudgetOverrideNeeded() {
        return objectBudgetOverrideNeeded;
    }

    /**
     * @param objectBudgetOverrideNeeded The objectBudgetOverrideNeeded to set.
     */
    public void setObjectBudgetOverrideNeeded(boolean objectBudgetOverrideNeeded) {
        this.objectBudgetOverrideNeeded = objectBudgetOverrideNeeded;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#isNonFringeAccountOverride()
     */
    public boolean getNonFringeAccountOverride() {
        return nonFringeAccountOverride;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#setNonFringeAccountOverride(boolean)
     */
    public void setNonFringeAccountOverride(boolean nonFringeAccountOverride) {
        this.nonFringeAccountOverride = nonFringeAccountOverride;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#isNonFringeAccountOverrideNeeded()
     */
    public boolean getNonFringeAccountOverrideNeeded() {
        return nonFringeAccountOverrideNeeded;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLine#setNonFringeAccountOverrideNeeded(boolean)
     */
    public void setNonFringeAccountOverrideNeeded(boolean nonFringeAccountOverrideNeeded) {
        this.nonFringeAccountOverrideNeeded = nonFringeAccountOverrideNeeded;
    }

    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     * 
     * @return Map
     */
    public Map getValuesMap() {
        Map simpleValues = new HashMap();

        simpleValues.put("sequenceNumber", getSequenceNumber());
        simpleValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        simpleValues.put("postingYear", getPostingYear());
        simpleValues.put("amount", getAmount());
        simpleValues.put("referenceOriginCode", getReferenceOriginCode());
        simpleValues.put("referenceNumber", getReferenceNumber());
        simpleValues.put("referenceTypeCode", getReferenceTypeCode());
        simpleValues.put("overrideCode", getOverrideCode());
        // The override booleans are not in the map because they should not cause isLike() to fail and generate update events.
        simpleValues.put("organizationReferenceId", getOrganizationReferenceId());
        simpleValues.put("debitCreditCode", getDebitCreditCode());
        simpleValues.put("encumbranceUpdateCode", getEncumbranceUpdateCode());
        simpleValues.put("financialDocumentLineTypeCode", getFinancialDocumentLineTypeCode());
        simpleValues.put("financialDocumentLineDescription", getFinancialDocumentLineDescription());

        simpleValues.put("chartOfAccountsCode", getChartOfAccountsCode());
        simpleValues.put("accountNumber", getAccountNumber());
        simpleValues.put("financialObjectCode", getFinancialObjectCode());
        simpleValues.put("subAccountNumber", getSubAccountNumber());
        simpleValues.put("financialSubObjectCode", getFinancialSubObjectCode());
        simpleValues.put("projectCode", getProjectCode());
        simpleValues.put("balanceTypeCode", getBalanceTypeCode());
        simpleValues.put("objectTypeCode", getObjectTypeCode());

        return simpleValues;
    }

    /**
     * Override needed for PURAP GL entry creation (hjs) - please do not add "amount" to this method
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof AccountingLine)) {
            return false;
        }
        AccountingLine accountingLine = (AccountingLine) obj;
        return new EqualsBuilder().append(this.chartOfAccountsCode, accountingLine.getChartOfAccountsCode()).append(this.accountNumber, accountingLine.getAccountNumber()).append(this.subAccountNumber, accountingLine.getSubAccountNumber()).append(this.financialObjectCode, accountingLine.getFinancialObjectCode()).append(this.financialSubObjectCode, accountingLine.getFinancialSubObjectCode()).append(this.projectCode, accountingLine.getProjectCode()).append(this.organizationReferenceId, accountingLine.getOrganizationReferenceId()).isEquals();
    }

    /**
     * Override needed for PURAP GL entry creation (hjs) - please do not add "amount" to this method
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(37, 41).append(this.chartOfAccountsCode).append(this.accountNumber).append(this.subAccountNumber).append(this.financialObjectCode).append(this.financialSubObjectCode).append(this.projectCode).append(this.organizationReferenceId).toHashCode();
    }

}
