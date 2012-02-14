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

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.kns.service.KualiConfigurationService;

public class CustomerCreditMemoDocumentPreRules extends PromptBeforeValidationBase {

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {
        boolean preRulesOK = conditionallyAskQuestion(document);
        return preRulesOK;
    }
    
    /**
     * This method checks if there is at least one discount applied to the invoice and generates yes/no question
     * 
     * @param document the maintenance document
     * @return
     */
    protected boolean conditionallyAskQuestion(Document document) {
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;
        boolean shouldAskQuestion = customerCreditMemoDocument.getInvoice().hasAtLeastOneDiscount();
        if (shouldAskQuestion) {
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(ArKeyConstants.WARNING_CUSTOMER_CREDIT_MEMO_DOCUMENT_INVOICE_HAS_DISCOUNT);
            boolean confirm = super.askOrAnalyzeYesNoQuestion(ArConstants.CustomerCreditMemoConstants.GENERATE_CUSTOMER_CREDIT_MEMO_DOCUMENT_QUESTION_ID, questionText);
            if (!confirm) {
                super.abortRulesCheck();
            }
        }
        return true;
    }

}
