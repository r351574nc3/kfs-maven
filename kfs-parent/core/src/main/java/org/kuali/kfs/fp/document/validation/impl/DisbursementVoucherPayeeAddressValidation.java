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
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.util.ObjectUtils;

public class DisbursementVoucherPayeeAddressValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherPayeeAddressValidation.class);

    private AccountingDocument accountingDocumentForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");
        boolean isValid = true;

        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

        MessageMap errors = GlobalVariables.getMessageMap();
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);

        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        
        String stateCode = payeeDetail.getDisbVchrPayeeStateCode();
        if (StringUtils.isNotBlank(stateCode) && ObjectUtils.isNull(payeeDetail.getDisbVchrPayeeState())) {
            String label = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_STATE_CODE);
            String propertyPath = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_STATE_CODE;
            errors.putError(propertyPath, KFSKeyConstants.ERROR_EXISTENCE, label);
            isValid = false;
        }

        String zipCode = payeeDetail.getDisbVchrPayeeZipCode();
        if (StringUtils.isNotBlank(zipCode) && ObjectUtils.isNull(payeeDetail.getDisbVchrPayeePostalZipCode())) {
            String label = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ZIP_CODE);
            String propertyPath = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_ZIP_CODE;
            errors.putError(propertyPath, KFSKeyConstants.ERROR_EXISTENCE, label);
            isValid = false;
        }

        String countryCode = payeeDetail.getDisbVchrPayeeCountryCode();
        if (StringUtils.isNotBlank(countryCode) && ObjectUtils.isNull(payeeDetail.getDisbVchrPayeeCountry())) {
            String label = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_COUNTRY_CODE);
            String propertyPath = KFSPropertyConstants.DV_PAYEE_DETAIL + "." + KFSPropertyConstants.DISB_VCHR_PAYEE_COUNTRY_CODE;
            errors.putError(propertyPath, KFSKeyConstants.ERROR_EXISTENCE, label);
            isValid = false;
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        return isValid;
    }


    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }


    /**
     * Sets the accountingDocumentForValidation attribute value.
     * 
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
