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

package org.kuali.kfs.fp.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.ProcurementCardHolder;
import org.kuali.kfs.fp.businessobject.ProcurementCardSourceAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransactionDetail;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.rule.event.SaveDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This is the Procurement Card Document Class. The procurement cards distributes expenses from clearing accounts. It is a two-sided
 * document, but only target lines are displayed because source lines cannot be changed. Transaction, Card, and Vendor information
 * are associated with the document to help better distribute the expense.
 */
public class ProcurementCardDocument extends AccountingDocumentBase implements AmountTotaling, CapitalAssetEditable {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardDocument.class);

    protected ProcurementCardHolder procurementCardHolder;

    protected List transactionEntries;

    protected transient CapitalAssetInformation capitalAssetInformation;
    protected transient CapitalAssetManagementModuleService capitalAssetManagementModuleService;

    /**
     * Default constructor.
     */
    public ProcurementCardDocument() {
        super();
        transactionEntries = new TypedArrayList(ProcurementCardTransactionDetail.class);
        // Save Capital Asset Information for PCard document when created.
        this.capitalAssetInformation = new CapitalAssetInformation();
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List<List> managedLists = super.buildListOfDeletionAwareLists();
        if (ObjectUtils.isNotNull(capitalAssetInformation) && ObjectUtils.isNotNull(capitalAssetInformation.getCapitalAssetInformationDetails())) {
            managedLists.add(capitalAssetInformation.getCapitalAssetInformationDetails());
        }
        return managedLists;
    }

    /**
     * @return Returns the transactionEntries.
     */
    public List getTransactionEntries() {
        return transactionEntries;
    }

    /**
     * @param transactionEntries The transactionEntries to set.
     */
    public void setTransactionEntries(List transactionEntries) {
        this.transactionEntries = transactionEntries;
    }

    /**
     * Gets the procurementCardHolder attribute.
     * 
     * @return Returns the procurementCardHolder.
     */
    public ProcurementCardHolder getProcurementCardHolder() {
        return procurementCardHolder;
    }

    /**
     * Sets the procurementCardHolder attribute value.
     * 
     * @param procurementCardHolder The procurementCardHolder to set.
     */
    public void setProcurementCardHolder(ProcurementCardHolder procurementCardHolder) {
        this.procurementCardHolder = procurementCardHolder;
    }

    /**
     * Removes the target accounting line at the given index from the transaction detail entry.
     * 
     * @param index
     */
    public void removeTargetAccountingLine(int index) {
        ProcurementCardTargetAccountingLine line = (ProcurementCardTargetAccountingLine) getTargetAccountingLines().get(index);

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            if (transactionEntry.getFinancialDocumentTransactionLineNumber().equals(line.getFinancialDocumentTransactionLineNumber())) {
                transactionEntry.getTargetAccountingLines().remove(line);
            }
        }
    }

    /**
     * Override to set the accounting line in the transaction detail object.
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocument#addSourceAccountingLine(SourceAccountingLine)
     */
    @Override
    public void addSourceAccountingLine(SourceAccountingLine sourceLine) {
        ProcurementCardSourceAccountingLine line = (ProcurementCardSourceAccountingLine) sourceLine;

        line.setSequenceNumber(this.getNextSourceLineNumber());

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            if (transactionEntry.getFinancialDocumentTransactionLineNumber().equals(line.getFinancialDocumentTransactionLineNumber())) {
                transactionEntry.getSourceAccountingLines().add(line);
            }
        }

        this.nextSourceLineNumber = new Integer(this.getNextSourceLineNumber().intValue() + 1);
    }

    /**
     * Override to set the accounting line in the transaction detail object.
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocument#addTargetAccountingLine(TargetAccountingLine)
     */
    @Override
    public void addTargetAccountingLine(TargetAccountingLine targetLine) {
        ProcurementCardTargetAccountingLine line = (ProcurementCardTargetAccountingLine) targetLine;

        line.setSequenceNumber(this.getNextTargetLineNumber());

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            if (transactionEntry.getFinancialDocumentTransactionLineNumber().equals(line.getFinancialDocumentTransactionLineNumber())) {
                transactionEntry.getTargetAccountingLines().add(line);
            }
        }

        this.nextTargetLineNumber = new Integer(this.getNextTargetLineNumber().intValue() + 1);
    }

    /**
     * Override to get source accounting lines out of transactions
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLines()
     */
    @Override
    public List getSourceAccountingLines() {
        List sourceAccountingLines = new ArrayList();

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            for (Iterator iterator = transactionEntry.getSourceAccountingLines().iterator(); iterator.hasNext();) {
                SourceAccountingLine sourceLine = (SourceAccountingLine) iterator.next();
                sourceAccountingLines.add(sourceLine);
            }
        }

        return sourceAccountingLines;
    }

    /**
     * Override to get target accounting lines out of transactions
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLines()
     */
    @Override
    public List getTargetAccountingLines() {
        List targetAccountingLines = new ArrayList();

        for (Iterator iter = transactionEntries.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            for (Iterator iterator = transactionEntry.getTargetAccountingLines().iterator(); iterator.hasNext();) {
                TargetAccountingLine targetLine = (TargetAccountingLine) iterator.next();
                targetAccountingLines.add(targetLine);
            }
        }

        return targetAccountingLines;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
        return ProcurementCardSourceAccountingLine.class;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTargetAccountingLineClass()
     */
    @Override
    public Class getTargetAccountingLineClass() {
        return ProcurementCardTargetAccountingLine.class;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        // Updating for rice-1.0.0 api changes. doRouteStatusChange() went away, so
        // that functionality needs to be a part of doRouteStatusChange now.
        // handleRouteStatusChange did not happen on a save
        if (!KEWConstants.ACTION_TAKEN_SAVED_CD.equals(statusChangeEvent.getDocumentEventCode())) {
            this.getCapitalAssetManagementModuleService().deleteDocumentAssetLocks(this);
        }
    }

    /**
     * On procurement card documents, positive source amounts are credits, negative source amounts are debits.
     * 
     * @param transactionalDocument The document the accounting line being checked is located in.
     * @param accountingLine The accounting line being analyzed.
     * @return True if the accounting line given is a debit accounting line, false otherwise.
     * @throws Throws an IllegalStateException if one of the following rules are violated: the accounting line amount is zero or the
     *         accounting line is not an expense or income accounting line.
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isDebit(FinancialDocument,
     *      org.kuali.rice.kns.bo.AccountingLine)
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase.IsDebitUtils#isDebitConsideringSection(AccountingDocumentRuleBase,
     *      AccountingDocument, AccountingLine)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) throws IllegalStateException {
        // disallow error correction
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        isDebitUtils.disallowErrorCorrectionDocumentCheck(this);
        return isDebitUtils.isDebitConsideringSection(this, (AccountingLine) postable);
    }

    /**
     * Gets the capitalAssetInformation attribute.
     * 
     * @return Returns the capitalAssetInformation.
     */
    public CapitalAssetInformation getCapitalAssetInformation() {
        return ObjectUtils.isNull(capitalAssetInformation) ? null : capitalAssetInformation;
    }

    /**
     * Sets the capitalAssetInformation attribute value.
     * 
     * @param capitalAssetInformation The capitalAssetInformation to set.
     */
    @Deprecated
    public void setCapitalAssetInformation(CapitalAssetInformation capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#postProcessSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);
        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(this.getClass());
            this.getCapitalAssetManagementModuleService().generateCapitalAssetLock(this, documentTypeName);
        }
    }

    /**
     * @return CapitalAssetManagementModuleService
     */
    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        if (capitalAssetManagementModuleService == null) {
            capitalAssetManagementModuleService = SpringContext.getBean(CapitalAssetManagementModuleService.class);
        }
        return capitalAssetManagementModuleService;
    }

}
