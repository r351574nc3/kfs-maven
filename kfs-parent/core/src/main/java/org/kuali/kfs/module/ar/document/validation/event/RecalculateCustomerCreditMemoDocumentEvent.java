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
package org.kuali.kfs.module.ar.document.validation.event;

import org.kuali.kfs.module.ar.document.validation.RecalculateCustomerCreditMemoDocumentRule;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.rule.BusinessRule;
import org.kuali.rice.kns.rule.event.KualiDocumentEventBase;

public class RecalculateCustomerCreditMemoDocumentEvent extends KualiDocumentEventBase {
    
    protected boolean printErrMsgFlag;

    public RecalculateCustomerCreditMemoDocumentEvent(String errorPathPrefix, Document document, boolean printErrMsgFlag) {
        super("Recalculating customer credit memo document " + getDocumentId(document), errorPathPrefix,document);
        this.printErrMsgFlag = printErrMsgFlag;
    }    
    
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return RecalculateCustomerCreditMemoDocumentRule.class;
    }

    @SuppressWarnings("unchecked")
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((RecalculateCustomerCreditMemoDocumentRule) rule).processRecalculateCustomerCreditMemoDocumentRules((TransactionalDocument)document,printErrMsgFlag);
    }

}
