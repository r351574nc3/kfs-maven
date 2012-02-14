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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import java.math.BigDecimal;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class CustomerCreditMemoDetailQuantityAndAmountValidation extends GenericValidation {

    protected KualiDecimal getAllowedQtyDeviation() {
        return new KualiDecimal("0.10");
    }

    private CustomerCreditMemoDetail customerCreditMemoDetail;
    
    public boolean validate(AttributedDocumentEvent event) {

        KualiDecimal creditAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
        BigDecimal quantity = customerCreditMemoDetail.getCreditMemoItemQuantity();
        BigDecimal unitPrice = customerCreditMemoDetail.getCustomerInvoiceDetail().getInvoiceItemUnitPrice();
        boolean validValue;
        
        if (ObjectUtils.isNotNull(quantity) && ObjectUtils.isNotNull(creditAmount)) {

            //  determine the expected exact total credit memo quantity, based on actual credit amount entered
            KualiDecimal creditQuantity = new KualiDecimal(customerCreditMemoDetail.getCreditMemoItemQuantity());
            KualiDecimal expectedCreditQuantity = creditAmount.divide(new KualiDecimal(unitPrice), true);
        
            //  determine the deviation percentage that the actual creditQuantity has from expectedCreditQuantity
            KualiDecimal deviationPercentage = expectedCreditQuantity.subtract(creditQuantity).abs().divide(expectedCreditQuantity);
        
            // only allow a certain deviation of creditQuantity from the expectedCreditQuantity 
            validValue = (deviationPercentage.isLessEqual(getAllowedQtyDeviation()));
            if (!validValue){
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT);
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_TOTAL_AMOUNT, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT);
            }
        }
        return true;
    }    
    
    public CustomerCreditMemoDetail getCustomerCreditMemoDetail() {
        return customerCreditMemoDetail;
    }

    public void setCustomerCreditMemoDetail(CustomerCreditMemoDetail customerCreditMemoDetail) {
        this.customerCreditMemoDetail = customerCreditMemoDetail;
    }



}
