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
package org.kuali.kfs.sys.document.authorization;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Base class for all FinancialSystemDocumentPresentationControllers.
 */
public class FinancialSystemTransactionalDocumentPresentationControllerBase extends TransactionalDocumentPresentationControllerBase implements FinancialSystemTransactionalDocumentPresentationController {
    private static Log LOG = LogFactory.getLog(FinancialSystemTransactionalDocumentPresentationControllerBase.class);
            
    /**
     * Makes sure that the given document implements error correction, that error correction is turned on for the document in the
     * data dictionary, and that the document is in a workflow state that allows error correction.
     * 
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationController#canErrorCorrect(org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument)
     */
    public boolean canErrorCorrect(FinancialSystemTransactionalDocument document) {
        if (!(document instanceof Correctable)) {
            return false;
        }
        
        if (!this.canCopy(document)) {
            return false;
        }

        DataDictionary dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
        FinancialSystemTransactionalDocumentEntry documentEntry = (FinancialSystemTransactionalDocumentEntry) (dataDictionary.getDocumentEntry(document.getClass().getName()));

        if (!documentEntry.getAllowsErrorCorrection()) {
            return false;
        }

        if (document.getDocumentHeader().getCorrectedByDocumentId() != null) {
            return false;
        }

        if (document.getDocumentHeader().getFinancialDocumentInErrorNumber() != null) {
            return false;
        }

        final KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
           
        return (workflowDocument.stateIsApproved() || workflowDocument.stateIsProcessed() || workflowDocument.stateIsFinal());
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#getDocumentActions(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getDocumentActions(Document document) {
        Set<String> documentActions = super.getDocumentActions(document);
        
        if (document instanceof FinancialSystemTransactionalDocument) {
            if (canErrorCorrect((FinancialSystemTransactionalDocument) document)) {
                documentActions.add(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT);
            }
            
            if (canHaveBankEntry(document)) {
                documentActions.add(KFSConstants.KFS_ACTION_CAN_EDIT_BANK);
            }
        }
        
        return documentActions;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        if (document instanceof AmountTotaling) {
            editModes.add(KFSConstants.AMOUNT_TOTALING_EDITING_MODE);
        }
        
        if (this.canHaveBankEntry(document)) {
            editModes.add(KFSConstants.BANK_ENTRY_VIEWABLE_EDITING_MODE);
        }

        return editModes;
    }

    // check if bank entry should be viewable for the given document
    protected boolean canHaveBankEntry(Document document) {        
        boolean bankSpecificationEnabled = getBankService().isBankSpecificationEnabled();
        
        if (bankSpecificationEnabled) {            
            String documentTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();

            ParameterEvaluator evaluator = getParameterService().getParameterEvaluator(Bank.class, KFSParameterKeyConstants.BANK_CODE_DOCUMENT_TYPES, documentTypeName);
            return evaluator.evaluationSucceeds();
        }

        return false;
    }
    
    private static BankService bankService;
    protected BankService getBankService() {
        if ( bankService == null ) {
            bankService = SpringContext.getBean(BankService.class);
        }
        return bankService;
    }
}
