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
package org.kuali.kfs.module.cam.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.service.impl.KEWModuleService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Accounting line for the asset payment document.
 */
public class AssetPaymentDetail extends SourceAccountingLine {
    private static Logger LOG = Logger.getLogger(AssetPaymentDetail.class);

    private String expenditureFinancialSystemOriginationCode;
    private Date expenditureFinancialDocumentPostedDate;
    private boolean transferPaymentIndicator;

    private String expenditureFinancialDocumentNumber;
    private String expenditureFinancialDocumentTypeCode;
    private String postingPeriodCode;
    private String purchaseOrderNumber;
    private String requisitionNumber;
    private KualiDecimal amount;

    // bo references    
    private AccountingPeriod financialDocumentPostingPeriod;
    private DocumentTypeEBO expenditureFinancialSystemDocumentTypeCode;
    private OriginationCode expenditureFinancialSystemOrigination;



    /**
     * Default constructor.
     */
    public AssetPaymentDetail() {
        super();
    }

    public AssetPaymentDetail(AssetPayment assetPayment) {
        // populate AssetPaymentDetail with AssetPayment data
        this.setSequenceNumber(assetPayment.getPaymentSequenceNumber());
        this.setChartOfAccountsCode(assetPayment.getChartOfAccountsCode());
        this.setAccountNumber(assetPayment.getAccountNumber());
        this.setSubAccountNumber(assetPayment.getSubAccountNumber());
        this.setFinancialObjectCode(assetPayment.getFinancialObjectCode());
        this.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        this.setProjectCode(assetPayment.getProjectCode());
        this.setOrganizationReferenceId(assetPayment.getOrganizationReferenceId());
        this.setExpenditureFinancialSystemOriginationCode(assetPayment.getFinancialSystemOriginationCode());
        this.setExpenditureFinancialDocumentNumber(assetPayment.getDocumentNumber());
        this.setExpenditureFinancialDocumentTypeCode(assetPayment.getFinancialDocumentTypeCode());
        this.setPurchaseOrderNumber(assetPayment.getPurchaseOrderNumber());
        this.setRequisitionNumber(assetPayment.getRequisitionNumber());
        this.setExpenditureFinancialDocumentPostedDate(assetPayment.getFinancialDocumentPostingDate());
        this.setPostingYear(assetPayment.getFinancialDocumentPostingYear());
        this.setPostingPeriodCode(assetPayment.getFinancialDocumentPostingPeriodCode());
        this.setAmount(assetPayment.getAccountChargeAmount());
    }
    
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String,String> toStringMapper() {
        LinkedHashMap<String,String> m = new LinkedHashMap<String,String>();
        m.put("documentNumber", this.getDocumentNumber());
        m.put("sequenceNumber", this.getSequenceNumber() == null ? "" : this.getSequenceNumber().toString());
        m.put("chartOfAccountsCode",getChartOfAccountsCode());
        m.put("accountNumber",getAccountNumber());
        m.put("subAccountNumber",getSubAccountNumber());
        m.put("financialObjectCode",getFinancialObjectCode());
        m.put("financialSubObjectCode",getFinancialSubObjectCode());
        m.put("projectCode",getProjectCode());
        m.put("postingYear",this.getPostingYear().toString());
        return m;
    }
    
    /**
     * Create a key including the 
     * <li><b>expenditureFinancialDocumentNumber</b></li>
     * <li><b>expenditureFinancialDocumentTypeCode</b></li>
     * with accounting information for asset payment distribution
     * 
     * Make sure the full accounting line information is part of the key
     * chartOfAccount, accountNumber, subAccountNumber, objectCode, subObjectCode, projectCode 
     * 
     * @return
     */
    public String getAssetPaymentDetailKey() {
        LinkedHashMap<String,String> m = toStringMapper();
        m.put("expenditureFinancialDocumentTypeCode",this.getExpenditureFinancialDocumentTypeCode());
        m.put("expenditureFinancialDocumentNumber",this.getExpenditureFinancialDocumentNumber());
        return toStringBuilder(m);
    }

    public String getExpenditureFinancialSystemOriginationCode() {
        return expenditureFinancialSystemOriginationCode;
    }


    public void setExpenditureFinancialSystemOriginationCode(String expenditureFinancialSystemOriginationCode) {
        this.expenditureFinancialSystemOriginationCode = expenditureFinancialSystemOriginationCode;
    }


    public Date getExpenditureFinancialDocumentPostedDate() {
        return expenditureFinancialDocumentPostedDate;
    }


    public void setExpenditureFinancialDocumentPostedDate(Date expenditureFinancialDocumentPostedDate) {
        this.expenditureFinancialDocumentPostedDate = expenditureFinancialDocumentPostedDate;
    }


    public boolean isTransferPaymentIndicator() {
        return transferPaymentIndicator;
    }


    public void setTransferPaymentIndicator(boolean transferPaymentIndicator) {
        this.transferPaymentIndicator = transferPaymentIndicator;
    }


    public String getExpenditureFinancialDocumentNumber() {
        return expenditureFinancialDocumentNumber;
    }


    public void setExpenditureFinancialDocumentNumber(String expenditureFinancialDocumentNumber) {
        this.expenditureFinancialDocumentNumber = expenditureFinancialDocumentNumber;
    }


    public String getExpenditureFinancialDocumentTypeCode() {
        return expenditureFinancialDocumentTypeCode;
    }


    public void setExpenditureFinancialDocumentTypeCode(String expenditureFinancialDocumentTypeCode) {
        this.expenditureFinancialDocumentTypeCode = expenditureFinancialDocumentTypeCode;
    }
    

    public String getPostingPeriodCode() {
        return postingPeriodCode;
    }


    public void setPostingPeriodCode(String postingPeriodCode) {
        this.postingPeriodCode = postingPeriodCode;
    }


    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }


    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }


    public String getRequisitionNumber() {
        return requisitionNumber;
    }


    public void setRequisitionNumber(String requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }


    public AccountingPeriod getFinancialDocumentPostingPeriod() {
        return financialDocumentPostingPeriod;
    }


    public void setFinancialDocumentPostingPeriod(AccountingPeriod financialDocumentPostingPeriod) {
        this.financialDocumentPostingPeriod = financialDocumentPostingPeriod;
    }

    public DocumentTypeEBO getExpenditureFinancialSystemDocumentTypeCode() {
        return expenditureFinancialSystemDocumentTypeCode = SpringContext.getBean(KEWModuleService.class).retrieveExternalizableBusinessObjectIfNecessary(this, expenditureFinancialSystemDocumentTypeCode, "expenditureFinancialSystemDocumentTypeCode");
    }

    public OriginationCode getExpenditureFinancialSystemOrigination() {
        return expenditureFinancialSystemOrigination;
    }


    public void setExpenditureFinancialSystemOrigination(OriginationCode expenditureFinancialSystemOrigination) {
        this.expenditureFinancialSystemOrigination = expenditureFinancialSystemOrigination;
    }
    
    public KualiDecimal getAmount() {
        return amount;
    }


    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#getValuesMap()
     */
    @Override
    public Map getValuesMap() {
        Map simpleValues = super.getValuesMap();
        simpleValues.put(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, getExpenditureFinancialSystemOriginationCode());
        simpleValues.put(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE,getExpenditureFinancialDocumentPostedDate());

        return simpleValues;
    }    
}
