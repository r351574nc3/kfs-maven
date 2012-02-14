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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.DeleteAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.UpdateAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class PurchaseOrderAmendmentAccountingLineAccessibleValidation extends PurchasingAccountsPayableAccountingLineAccessibleValidation {

    private PurapService purapService;
    
    /**
     * Validates that the given accounting line is accessible for editing by the current user.
     * <strong>This method expects a document as the first parameter and an accounting line as the second</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {

        if( purapService.isDocumentStoppedInRouteNode((PurchasingAccountsPayableDocument)event.getDocument(), "New Unordered Items") ){
            //DO NOTHING: do not check that user owns acct lines; at this level, they can edit all accounts on PO amendment
            return true;
        } else if (event.getDocument().getDocumentHeader().getWorkflowDocument().isAdHocRequested()) {
            return true;
        } else {
            return super.validate(event);
        }
    }

    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }
 
}

