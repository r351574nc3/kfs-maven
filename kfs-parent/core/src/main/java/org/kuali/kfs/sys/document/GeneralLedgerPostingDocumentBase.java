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
package org.kuali.kfs.sys.document;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.rule.event.ApproveDocumentEvent;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.rule.event.RouteDocumentEvent;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Base implementation for a general ledger posting document.
 */
public class GeneralLedgerPostingDocumentBase extends LedgerPostingDocumentBase implements GeneralLedgerPostingDocument {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerPostingDocumentBase.class);

    protected List<GeneralLedgerPendingEntry> generalLedgerPendingEntries;

    /**
     * Default constructor.
     */
    public GeneralLedgerPostingDocumentBase() {
        super();
        setGeneralLedgerPendingEntries(new ArrayList<GeneralLedgerPendingEntry>());
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocument#getGeneralLedgerPendingEntries()
     */
    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return generalLedgerPendingEntries;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocument#getGeneralLedgerPendingEntry(int)
     */
    public GeneralLedgerPendingEntry getGeneralLedgerPendingEntry(int index) {
        while (generalLedgerPendingEntries.size() <= index) {
            generalLedgerPendingEntries.add(new GeneralLedgerPendingEntry());
        }
        return generalLedgerPendingEntries.get(index);
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocument#setGeneralLedgerPendingEntries(java.util.List)
     */
    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> generalLedgerPendingEntries) {
        this.generalLedgerPendingEntries = generalLedgerPendingEntries;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocument#checkSufficientFunds()
     */
    public List<SufficientFundsItem> checkSufficientFunds() {
        LOG.debug("checkSufficientFunds() started");

        if (documentPerformsSufficientFundsCheck()) {
            SufficientFundsService sufficientFundsService = SpringContext.getBean(SufficientFundsService.class);
            return sufficientFundsService.checkSufficientFunds(this);
        }
        else {
            return new ArrayList<SufficientFundsItem>();
        }
    }

    /**
     * This method checks to see if SF checking should be done for this document. This was originally part of
     * SufficientFundsService.checkSufficientFunds() but was externalized so documents that need to override any of the SF methods
     * can still explicitly check this
     * 
     * @return
     */
    public boolean documentPerformsSufficientFundsCheck() {
        // check for reversing entries generated by an error correction.
        return StringUtils.isBlank(this.getDocumentHeader().getFinancialDocumentInErrorNumber());
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocument#getPendingLedgerEntriesForSufficientFundsChecking()
     */
    public List<GeneralLedgerPendingEntry> getPendingLedgerEntriesForSufficientFundsChecking() {
        return getGeneralLedgerPendingEntries();
    }

    /**
     * Override to call super and then iterate over all GLPEs and update the approved code appropriately.
     * 
     * @see Document#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            changeGeneralLedgerPendingEntriesApprovedStatusCode(); // update all glpes for doc and set their status to approved
        }
        else if (getDocumentHeader().getWorkflowDocument().stateIsCanceled() || getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            removeGeneralLedgerPendingEntries();
            if (this instanceof ElectronicPaymentClaiming) {
                ((ElectronicPaymentClaiming)this).declaimElectronicPaymentClaims();
            }
        }
    }

    /**
     * This method iterates over all of the GLPEs for a document and sets their approved status code to APPROVED "A".
     */
    protected void changeGeneralLedgerPendingEntriesApprovedStatusCode() {
        for (GeneralLedgerPendingEntry glpe : getGeneralLedgerPendingEntries()) {
            glpe.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
        }
    }

    /**
     * This method calls the service to remove all of the GLPE's associated with this document
     */
    protected void removeGeneralLedgerPendingEntries() {
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        glpeService.delete(getDocumentHeader().getDocumentNumber());
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();
        getGeneralLedgerPendingEntries().clear();
    }

    /**
     * @see org.kuali.rice.kns.document.TransactionalDocumentBase#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException {
        super.toErrorCorrection();
        getGeneralLedgerPendingEntries().clear();
    }

    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        super.prepareForSave(event);
        // TODO - add KFS wrappers of Rice Events to list
        if (event instanceof RouteDocumentEvent || event instanceof ApproveDocumentEvent) {
            // generate general ledger pending entries should be called prior to sufficient funds checking
            List<SufficientFundsItem> sfItems = checkSufficientFunds();
            if (!sfItems.isEmpty()) {
                for (SufficientFundsItem sfItem : sfItems) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.SufficientFunds.ERROR_INSUFFICIENT_FUNDS, new String[] { sfItem.getAccount().getChartOfAccountsCode(), sfItem.getAccount().getAccountNumber(), StringUtils.isNotBlank(sfItem.getSufficientFundsObjectCode()) ? sfItem.getSufficientFundsObjectCode() : KFSConstants.NOT_AVAILABLE_STRING, sfItem.getAccountSufficientFundsCode() });
                }
                throw new ValidationException("Insufficient Funds on this Document:");
            }
        }
    }
    
    /**
     * Adds a GeneralLedgerPendingEntry to this document's list of pending entries
     * @param pendingEntry a pending entry to add
     */
    public void addPendingEntry(GeneralLedgerPendingEntry pendingEntry) {
        generalLedgerPendingEntries.add(pendingEntry);
    }
    
    /**
     * This resets this document's list of general ledger pending etnries, though it does not delete those entries (however, the GeneralLedgerPendingEntryService will in most cases when this method is called).
     */
    public void clearAnyGeneralLedgerPendingEntries() {
        generalLedgerPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
    }
}