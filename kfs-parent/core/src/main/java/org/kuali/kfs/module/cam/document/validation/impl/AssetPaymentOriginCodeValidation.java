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
package org.kuali.kfs.module.cam.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.OriginationCodeService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This class validates asset payment document origin code
 */
public class AssetPaymentOriginCodeValidation extends GenericValidation {

    private OriginationCodeService originationCodeService;
    private DataDictionaryService dataDictionaryService;
    private AccountingLine accountingLineForValidation;

    /**
     * Validates origin code
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        // skip check if accounting line is from CAB
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) event.getDocument();
        if (assetPaymentDocument.isCapitalAssetBuilderOriginIndicator()) {
            return true;
        }
        
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) getAccountingLineForValidation();
        boolean result = true;
        if (!StringUtils.isBlank(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode())) {
            if (originationCodeService.getByPrimaryKey(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode()) == null) {
                String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(OriginationCode.class.getName()).getAttributeDefinition(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE).getLabel();
                GlobalVariables.getMessageMap().putError(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
                result = false;
            }
        }
        return result;
    }

    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    public void setAccountingLineForValidation(AccountingLine accountingLine) {
        this.accountingLineForValidation = accountingLine;
    }

    public OriginationCodeService getOriginationCodeService() {
        return originationCodeService;
    }

    public void setOriginationCodeService(OriginationCodeService originationCodeService) {
        this.originationCodeService = originationCodeService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }


}
