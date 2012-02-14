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

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kim.bo.Person;

public class CustomerInvoiceRecurrenceInitiatorValidation extends GenericValidation {
    
    private CustomerInvoiceDocument customerInvoiceDocument;

    public boolean validate(AttributedDocumentEvent event) {

        // short circuit if no recurrence object at all
        if (ObjectUtils.isNull(customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails())) {
            return true;
        }
        
        if (customerInvoiceDocument.getNoRecurrenceDataFlag())
            return true;
        
        String initiatorUserIdentifier = customerInvoiceDocument.getCustomerInvoiceRecurrenceDetails().getDocumentInitiatorUserIdentifier();
        if (ObjectUtils.isNull(initiatorUserIdentifier)) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DOCUMENT_RECURRENCE_INITIATOR, ArKeyConstants.ERROR_INVOICE_RECURRENCE_INITIATOR_IS_REQUIRED);
            return false;
        }

        DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer("INVR");
        Person person = org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPerson(initiatorUserIdentifier);
        if (person == null) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DOCUMENT_RECURRENCE_INITIATOR, ArKeyConstants.ERROR_INVOICE_RECURRENCE_INITIATOR_DOES_NOT_EXIST);
            return false;
        }
        if (!documentAuthorizer.canInitiate("INVR", person)) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DOCUMENT_RECURRENCE_INITIATOR, ArKeyConstants.ERROR_INVOICE_RECURRENCE_INITIATOR_IS_NOT_AUTHORIZED);
            return false;
        }
        return true;
    }

    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }
}