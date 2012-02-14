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
package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Purap Accounting Line Base Business Object.
 */
public abstract class PurApAccountingLineBase extends SourceAccountingLine implements PurApAccountingLine, Comparable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApAccountingLineBase.class);
    protected Integer accountIdentifier;
    private Integer itemIdentifier;
    private BigDecimal accountLinePercent;
    private String postingPeriodCode;  //stored in DB only for PREQ and CM Account History
    private KualiDecimal alternateAmountForGLEntryCreation; // not stored in DB; needed for disencumbrances and such
    
    private PurApItem purapItem;

    public Integer getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(Integer requisitionAccountIdentifier) {
        this.accountIdentifier = requisitionAccountIdentifier;
    }

    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(Integer requisitionItemIdentifier) {
        this.itemIdentifier = requisitionItemIdentifier;
    }

    public BigDecimal getAccountLinePercent() {
        return accountLinePercent;
    }

    public void setAccountLinePercent(BigDecimal accountLinePercent) {
        this.accountLinePercent = accountLinePercent;
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApAccountingLine#isEmpty()
     */
    public boolean isEmpty() {
        return !(StringUtils.isNotEmpty(getAccountNumber()) || StringUtils.isNotEmpty(getChartOfAccountsCode()) || StringUtils.isNotEmpty(getFinancialObjectCode()) || StringUtils.isNotEmpty(getFinancialSubObjectCode()) || StringUtils.isNotEmpty(getOrganizationReferenceId()) || StringUtils.isNotEmpty(getProjectCode()) || StringUtils.isNotEmpty(getSubAccountNumber()) || ObjectUtils.isNotNull(getAccountLinePercent()));
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApAccountingLine#createBlankAmountsCopy()
     */
    public PurApAccountingLine createBlankAmountsCopy() {
        PurApAccountingLine newAccount = (PurApAccountingLine) ObjectUtils.deepCopy(this);
        newAccount.setAccountLinePercent(BigDecimal.ZERO);
        newAccount.setAmount(KualiDecimal.ZERO);
        return newAccount;
    }


    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApAccountingLine#accountStringsAreEqual(org.kuali.kfs.sys.businessobject.SourceAccountingLine)
     */
    public boolean accountStringsAreEqual(SourceAccountingLine accountingLine) {
        if (accountingLine == null) {
            return false;
        }
        return new EqualsBuilder().append(getChartOfAccountsCode(), accountingLine.getChartOfAccountsCode()).append(getAccountNumber(), accountingLine.getAccountNumber()).append(getSubAccountNumber(), accountingLine.getSubAccountNumber()).append(getFinancialObjectCode(), accountingLine.getFinancialObjectCode()).append(getFinancialSubObjectCode(), accountingLine.getFinancialSubObjectCode()).append(getProjectCode(), accountingLine.getProjectCode()).append(getOrganizationReferenceId(), accountingLine.getOrganizationReferenceId())
                .isEquals();
    }

    public boolean accountStringsAreEqual(PurApAccountingLine accountingLine) {
        return accountStringsAreEqual((SourceAccountingLine) accountingLine);

    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApAccountingLine#generateSourceAccountingLine()
     */
    public SourceAccountingLine generateSourceAccountingLine() {
        // the fields here should probably match method 'accountStringsAreEqual' above
        SourceAccountingLine sourceLine = new SourceAccountingLine();
        sourceLine.setChartOfAccountsCode(getChartOfAccountsCode());
        sourceLine.setAccountNumber(getAccountNumber());
        sourceLine.setSubAccountNumber(getSubAccountNumber());
        sourceLine.setFinancialObjectCode(getFinancialObjectCode());
        sourceLine.setFinancialSubObjectCode(getFinancialSubObjectCode());
        sourceLine.setProjectCode(getProjectCode());
        sourceLine.setOrganizationReferenceId(getOrganizationReferenceId());
        sourceLine.setAmount(getAmount());
        return sourceLine;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chart", getChartOfAccountsCode());
        m.put("account", getAccountNumber());
        m.put("objectCode", getFinancialObjectCode());
        m.put("subAccount", getSubAccountNumber());
        m.put("subObjectCode", getFinancialSubObjectCode());
        m.put("projectCode", getProjectCode());
        m.put("orgRefId", getOrganizationReferenceId());

        return m;
    }

    public int compareTo(Object arg0) {
        if (arg0 instanceof PurApAccountingLine) {
            PurApAccountingLine account = (PurApAccountingLine) arg0;
            return this.getString().compareTo(account.getString());
        }
        return -1;
    }

    public String getString() {
        return getChartOfAccountsCode() + "~" + getAccountNumber() + "~" + getSubAccountNumber() + "~" + getFinancialObjectCode() + "~" + getFinancialSubObjectCode() + "~" + getProjectCode() + "~" + getOrganizationReferenceId();
    }

    public KualiDecimal getAlternateAmountForGLEntryCreation() {
        return alternateAmountForGLEntryCreation;
    }

    public void setAlternateAmountForGLEntryCreation(KualiDecimal alternateAmount) {
        this.alternateAmountForGLEntryCreation = alternateAmount;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#getSequenceNumber()
     */
    @Override
    public Integer getSequenceNumber() {
        return this.getAccountIdentifier();
    }
    
    protected void copyFrom(PurApAccountingLine other) {
        
        super.copyFrom(other);

        setAccountLinePercent(other.getAccountLinePercent());      
        setAlternateAmountForGLEntryCreation(other.getAlternateAmountForGLEntryCreation());            
 
    }
 
    @Override
    public void refreshNonUpdateableReferences() {
        //hold onto item reference if there without itemId
        PurApItem item = null;
        PurApItem tempItem = getPurapItem();
        if(tempItem != null &&
           tempItem.getItemIdentifier() != null) {
            item = tempItem;
        }
        super.refreshNonUpdateableReferences();
        if(ObjectUtils.isNotNull(item)) {
            this.setPurapItem(item);
        }
    }

    public <T extends PurApItem> T getPurapItem() {
        return (T) purapItem;
    }
    
    /**
     * Sets the requisitionItem attribute.
     * @deprecated
     * @param item
     */
    public void setPurapItem(PurApItem item) {
        purapItem = item;
    }

    public String getPostingPeriodCode() {
        return postingPeriodCode;
    }

    public void setPostingPeriodCode(String postingPeriodCode) {
        this.postingPeriodCode = postingPeriodCode;
    }

    /**
     * Overridden to use purap doc identifier, rather than document number
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#getValuesMap()
     */
    @Override
    public Map getValuesMap() {
        Map valuesMap = super.getValuesMap();
        // remove document number
        valuesMap.remove(KFSPropertyConstants.DOCUMENT_NUMBER);
        return valuesMap;
    }

}
