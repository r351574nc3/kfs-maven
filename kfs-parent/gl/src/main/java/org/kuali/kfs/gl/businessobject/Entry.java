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

package org.kuali.kfs.gl.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.service.impl.KEWModuleService;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Represents a G/L entry
 * 
 */
public class Entry extends PersistableBusinessObjectBase implements Transaction {
    static final long serialVersionUID = -24983129882357448L;

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private String universityFiscalPeriodCode;
    private String financialDocumentTypeCode;
    private String financialSystemOriginationCode;
    private String documentNumber;
    private Integer transactionLedgerEntrySequenceNumber;
    private String transactionLedgerEntryDescription;
    private KualiDecimal transactionLedgerEntryAmount;
    private String transactionDebitCreditCode;
    private Date transactionDate;
    private String organizationDocumentNumber;
    private String projectCode;
    private String organizationReferenceId;
    private String referenceFinancialDocumentTypeCode;
    private String referenceFinancialSystemOriginationCode;
    private String referenceFinancialDocumentNumber;
    private Date financialDocumentReversalDate;
    private String transactionEncumbranceUpdateCode;
    private Date transactionPostingDate;
    private Timestamp transactionDateTimeStamp;

    // bo references
    private Account account;
    private SubAccount subAccount;
    private BalanceType balanceType;
    private Chart chart;
    private ObjectCode financialObject;
    private SubObjectCode financialSubObject;
    private ObjectType objectType;
    private ProjectCode project;
    private DocumentTypeEBO financialSystemDocumentTypeCode;
    private DocumentTypeEBO referenceFinancialSystemDocumentTypeCode;
    private UniversityDate universityDate;
    private SystemOptions option;
    private AccountingPeriod accountingPeriod;
    private UniversityDate reversalDate;
    private OriginationCode originationCode;
    private OriginationCode referenceOriginationCode;

    private TransientBalanceInquiryAttributes dummyBusinessObject;

    /**
     * Default constructor.
     */
    public Entry() {
    }

    public Entry(Transaction t) {
        super();

        setUniversityFiscalYear(t.getUniversityFiscalYear());
        setChartOfAccountsCode(t.getChartOfAccountsCode());
        setAccountNumber(t.getAccountNumber());
        setSubAccountNumber(t.getSubAccountNumber());
        setFinancialObjectCode(t.getFinancialObjectCode());
        setFinancialSubObjectCode(t.getFinancialSubObjectCode());
        setFinancialBalanceTypeCode(t.getFinancialBalanceTypeCode());
        setFinancialObjectTypeCode(t.getFinancialObjectTypeCode());
        setUniversityFiscalPeriodCode(t.getUniversityFiscalPeriodCode());
        setFinancialDocumentTypeCode(t.getFinancialDocumentTypeCode());
        setFinancialSystemOriginationCode(t.getFinancialSystemOriginationCode());
        setDocumentNumber(t.getDocumentNumber());
        setTransactionLedgerEntrySequenceNumber(t.getTransactionLedgerEntrySequenceNumber());
        setTransactionLedgerEntryDescription(t.getTransactionLedgerEntryDescription());
        setTransactionLedgerEntryAmount(t.getTransactionLedgerEntryAmount());
        setTransactionDebitCreditCode(t.getTransactionDebitCreditCode());
        setTransactionDate(t.getTransactionDate());
        setOrganizationDocumentNumber(t.getOrganizationDocumentNumber());
        setProjectCode(t.getProjectCode());
        setOrganizationReferenceId(t.getOrganizationReferenceId());
        setReferenceFinancialDocumentTypeCode(t.getReferenceFinancialDocumentTypeCode());
        setReferenceFinancialSystemOriginationCode(t.getReferenceFinancialSystemOriginationCode());
        setReferenceFinancialDocumentNumber(t.getReferenceFinancialDocumentNumber());
        setFinancialDocumentReversalDate(t.getFinancialDocumentReversalDate());
        setTransactionEncumbranceUpdateCode(t.getTransactionEncumbranceUpdateCode());
        
        Timestamp now = SpringContext.getBean(DateTimeService.class).getCurrentTimestamp();
        setTransactionDateTimeStamp(now);
    }

    public Entry(Transaction t, java.util.Date postDate) {
        super();
        this.dummyBusinessObject = new TransientBalanceInquiryAttributes();

        setUniversityFiscalYear(t.getUniversityFiscalYear());
        setChartOfAccountsCode(t.getChartOfAccountsCode());
        setAccountNumber(t.getAccountNumber());
        setSubAccountNumber(t.getSubAccountNumber());
        setFinancialObjectCode(t.getFinancialObjectCode());
        setFinancialSubObjectCode(t.getFinancialSubObjectCode());
        setFinancialBalanceTypeCode(t.getFinancialBalanceTypeCode());
        setFinancialObjectTypeCode(t.getFinancialObjectTypeCode());
        setUniversityFiscalPeriodCode(t.getUniversityFiscalPeriodCode());
        setFinancialDocumentTypeCode(t.getFinancialDocumentTypeCode());
        setFinancialSystemOriginationCode(t.getFinancialSystemOriginationCode());
        setDocumentNumber(t.getDocumentNumber());
        setTransactionLedgerEntrySequenceNumber(t.getTransactionLedgerEntrySequenceNumber());
        setTransactionLedgerEntryDescription(t.getTransactionLedgerEntryDescription());
        setTransactionLedgerEntryAmount(t.getTransactionLedgerEntryAmount());
        setTransactionDebitCreditCode(t.getTransactionDebitCreditCode());
        setTransactionDate(t.getTransactionDate());
        setOrganizationDocumentNumber(t.getOrganizationDocumentNumber());
        setProjectCode(t.getProjectCode());
        setOrganizationReferenceId(t.getOrganizationReferenceId());
        setReferenceFinancialDocumentTypeCode(t.getReferenceFinancialDocumentTypeCode());
        setReferenceFinancialSystemOriginationCode(t.getReferenceFinancialSystemOriginationCode());
        setReferenceFinancialDocumentNumber(t.getReferenceFinancialDocumentNumber());
        setFinancialDocumentReversalDate(t.getFinancialDocumentReversalDate());
        setTransactionEncumbranceUpdateCode(t.getTransactionEncumbranceUpdateCode());
        if (postDate != null) {
            setTransactionPostingDate(new Date(postDate.getTime()));
        }

        Timestamp now = SpringContext.getBean(DateTimeService.class).getCurrentTimestamp();
        setTransactionDateTimeStamp(now);
    }

    public OriginationCode getOriginationCode() {
        return originationCode;
    }

    public void setOriginationCode(OriginationCode originationCode) {
        this.originationCode = originationCode;
    }

    public OriginationCode getReferenceOriginationCode() {
        return referenceOriginationCode;
    }

    public void setReferenceOriginationCode(OriginationCode referenceOriginationCode) {
        this.referenceOriginationCode = referenceOriginationCode;
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
     * Gets the financialSystemDocumentTypeCode attribute. 
     * 
     * @return Returns the financialSystemDocumentTypeCode.
     */
    public DocumentTypeEBO getFinancialSystemDocumentTypeCode() {
        return financialSystemDocumentTypeCode = SpringContext.getBean(KEWModuleService.class).retrieveExternalizableBusinessObjectIfNecessary(this, financialSystemDocumentTypeCode, "financialSystemDocumentTypeCode");
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
     * Gets the financialBalanceTypeCode attribute.
     * 
     * @return Returns the financialBalanceTypeCode
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute.
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the universityFiscalPeriodCode attribute.
     * 
     * @return Returns the universityFiscalPeriodCode
     */
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    /**
     * Sets the universityFiscalPeriodCode attribute.
     * 
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    /**
     * Gets the financialDocumentTypeCode attribute.
     * 
     * @return Returns the financialDocumentTypeCode
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * Gets the financialSystemOriginationCode attribute.
     * 
     * @return Returns the financialSystemOriginationCode
     */
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * Sets the financialSystemOriginationCode attribute.
     * 
     * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
     */
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the transactionLedgerEntrySequenceNumber attribute.
     * 
     * @return Returns the transactionLedgerEntrySequenceNumber
     */
    public Integer getTransactionLedgerEntrySequenceNumber() {
        return transactionLedgerEntrySequenceNumber;
    }

    /**
     * Sets the transactionLedgerEntrySequenceNumber attribute.
     * 
     * @param transactionLedgerEntrySequenceNumber The transactionLedgerEntrySequenceNumber to set.
     */
    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
    }

    /**
     * Gets the transactionLedgerEntryDescription attribute.
     * 
     * @return Returns the transactionLedgerEntryDescription
     */
    public String getTransactionLedgerEntryDescription() {
        return transactionLedgerEntryDescription;
    }

    /**
     * Sets the transactionLedgerEntryDescription attribute.
     * 
     * @param transactionLedgerEntryDescription The transactionLedgerEntryDescription to set.
     */
    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
        this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
    }

    /**
     * Gets the transactionLedgerEntryAmount attribute.
     * 
     * @return Returns the transactionLedgerEntryAmount
     */
    public KualiDecimal getTransactionLedgerEntryAmount() {
        return transactionLedgerEntryAmount;
    }

    /**
     * Sets the transactionLedgerEntryAmount attribute.
     * 
     * @param transactionLedgerEntryAmount The transactionLedgerEntryAmount to set.
     */
    public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
        this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
    }

    /**
     * Gets the transactionDebitCreditCode attribute.
     * 
     * @return Returns the transactionDebitCreditCode
     */
    public String getTransactionDebitCreditCode() {
        if (transactionDebitCreditCode == null) 
            return " "; 
        else 
            return transactionDebitCreditCode;
    }

    /**
     * Sets the transactionDebitCreditCode attribute.
     * 
     * @param transactionDebitCreditCode The transactionDebitCreditCode to set.
     */
    public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
        this.transactionDebitCreditCode = transactionDebitCreditCode;
    }

    /**
     * Gets the transactionDate attribute.
     * 
     * @return Returns the transactionDate
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the transactionDate attribute.
     * 
     * @param transactionDate The transactionDate to set.
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets the organizationDocumentNumber attribute.
     * 
     * @return Returns the organizationDocumentNumber
     */
    public String getOrganizationDocumentNumber() {
        return organizationDocumentNumber;
    }

    /**
     * Sets the organizationDocumentNumber attribute.
     * 
     * @param organizationDocumentNumber The organizationDocumentNumber to set.
     */
    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

    /**
     * Gets the projectCode attribute.
     * 
     * @return Returns the projectCode
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * Sets the projectCode attribute.
     * 
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * Gets the organizationReferenceId attribute.
     * 
     * @return Returns the organizationReferenceId
     */
    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    /**
     * Sets the organizationReferenceId attribute.
     * 
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    /**
     * Gets the referenceFinancialDocumentTypeCode attribute.
     * 
     * @return Returns the referenceFinancialDocumentTypeCode
     */
    public String getReferenceFinancialDocumentTypeCode() {
        return referenceFinancialDocumentTypeCode;
    }

    /**
     * Sets the referenceFinancialDocumentTypeCode attribute.
     * 
     * @param referenceFinancialDocumentTypeCode The referenceFinancialDocumentTypeCode to set.
     */
    public void setReferenceFinancialDocumentTypeCode(String referenceFinancialDocumentTypeCode) {
        this.referenceFinancialDocumentTypeCode = referenceFinancialDocumentTypeCode;
    }

    /**
     * Gets the referenceFinancialSystemOriginationCode attribute.
     * 
     * @return Returns the referenceFinancialSystemOriginationCode
     */
    public String getReferenceFinancialSystemOriginationCode() {
        return referenceFinancialSystemOriginationCode;
    }

    /**
     * Sets the referenceFinancialSystemOriginationCode attribute.
     * 
     * @param referenceFinancialSystemOriginationCode The referenceFinancialSystemOriginationCode to set.
     */
    public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
        this.referenceFinancialSystemOriginationCode = referenceFinancialSystemOriginationCode;
    }

    /**
     * Gets the referenceFinancialDocumentNumber attribute.
     * 
     * @return Returns the referenceFinancialDocumentNumber
     */
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    /**
     * Sets the referenceFinancialDocumentNumber attribute.
     * 
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }

    /**
     * Gets the financialDocumentReversalDate attribute.
     * 
     * @return Returns the financialDocumentReversalDate
     */
    public Date getFinancialDocumentReversalDate() {
        return financialDocumentReversalDate;
    }

    /**
     * Sets the financialDocumentReversalDate attribute.
     * 
     * @param financialDocumentReversalDate The financialDocumentReversalDate to set.
     */
    public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
        this.financialDocumentReversalDate = financialDocumentReversalDate;
    }

    /**
     * Gets the transactionEncumbranceUpdateCode attribute.
     * 
     * @return Returns the transactionEncumbranceUpdateCode
     */
    public String getTransactionEncumbranceUpdateCode() {
        return transactionEncumbranceUpdateCode;
    }

    /**
     * Sets the transactionEncumbranceUpdateCode attribute.
     * 
     * @param transactionEncumbranceUpdateCode The transactionEncumbranceUpdateCode to set.
     */
    public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
        this.transactionEncumbranceUpdateCode = transactionEncumbranceUpdateCode;
    }

    /**
     * Gets the transactionPostingDate attribute.
     * 
     * @return Returns the transactionPostingDate
     */
    public Date getTransactionPostingDate() {
        return transactionPostingDate;
    }

    /**
     * Sets the transactionPostingDate attribute.
     * 
     * @param transactionPostingDate The transactionPostingDate to set.
     */
    public void setTransactionPostingDate(Date transactionPostingDate) {
        this.transactionPostingDate = transactionPostingDate;
    }

    /**
     * Gets the transactionDateTimeStamp attribute.
     * 
     * @return Returns the transactionDateTimeStamp
     */
    public Timestamp getTransactionDateTimeStamp() {
        return transactionDateTimeStamp;
    }

    /**
     * Sets the transactionDateTimeStamp attribute.
     * 
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     */
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
        this.transactionDateTimeStamp = transactionDateTimeStamp;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }

    public BalanceType getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(BalanceType balanceType) {
        this.balanceType = balanceType;
    }

    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }

    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    public SubObjectCode getFinancialSubObject() {
        return financialSubObject;
    }

    public void setFinancialSubObject(SubObjectCode financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public SystemOptions getOption() {
        return option;
    }

    public void setOption(SystemOptions option) {
        this.option = option;
    }

    public ProjectCode getProject() {
        return project;
    }

    public void setProject(ProjectCode project) {
        this.project = project;
    }

    public UniversityDate getReversalDate() {
        return reversalDate;
    }

    public void setReversalDate(UniversityDate reversalDate) {
        this.reversalDate = reversalDate;
    }

    public SubAccount getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    public UniversityDate getUniversityDate() {
        return universityDate;
    }

    public void setUniversityDate(UniversityDate universityDate) {
        this.universityDate = universityDate;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("universityFiscalYear", this.universityFiscalYear.toString());
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        m.put("financialBalanceTypeCode", this.financialBalanceTypeCode);
        m.put("financialObjectTypeCode", this.financialObjectTypeCode);
        m.put("universityFiscalPeriodCode", this.universityFiscalPeriodCode);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("financialSystemOriginationCode", this.financialSystemOriginationCode);
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("transactionLedgerEntrySequenceNumber", this.transactionLedgerEntrySequenceNumber.toString());
        return m;
    }

    /**
     * Gets the dummyBusinessObject attribute.
     * 
     * @return Returns the dummyBusinessObject.
     */
    public TransientBalanceInquiryAttributes getDummyBusinessObject() {
        return dummyBusinessObject;
    }

    /**
     * Sets the dummyBusinessObject attribute value.
     * 
     * @param dummyBusinessObject The dummyBusinessObject to set.
     */
    public void setDummyBusinessObject(TransientBalanceInquiryAttributes dummyBusinessObject) {
        this.dummyBusinessObject = dummyBusinessObject;
    }

    /**
     * Gets the referenceFinancialSystemDocumentTypeCode attribute. 
     * 
     * @return Returns the referenceFinancialSystemDocumentTypeCode.
     */
    public DocumentTypeEBO getReferenceFinancialSystemDocumentTypeCode() {
        return referenceFinancialSystemDocumentTypeCode = SpringContext.getBean(KEWModuleService.class).retrieveExternalizableBusinessObjectIfNecessary(this, referenceFinancialSystemDocumentTypeCode, "referenceFinancialSystemDocumentTypeCode");
    }

}
