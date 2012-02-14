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

package org.kuali.kfs.module.purap.document;

import static org.kuali.kfs.sys.KFSConstants.GL_DEBIT_CODE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.service.PurapGeneralLedgerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;

/**
 * Purchase Order Amendment Document
 */
public class PurchaseOrderAmendmentDocument extends PurchaseOrderDocument {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderAmendmentDocument.class);

    boolean newUnorderedItem; //Used for routing
    String receivingDeliveryCampusCode; //Used for routing
    
    /**
     * Default constructor.
     */
    public PurchaseOrderAmendmentDocument() {
        super();
    }

    
    @Override
    public List<Long> getWorkflowEngineDocumentIdsToLock() {
        return super.getWorkflowEngineDocumentIdsToLock();
    }

    /**
     * When Purchase Order Amendment document has been Processed through Workflow, the general ledger entries are created and the PO
     * status remains "OPEN".
     * 
     * @see org.kuali.kfs.module.purap.document.PurchaseOrderDocument#doRouteStatusChange()
     */
   @Override
	public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        // DOCUMENT PROCESSED
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            // generate GL entries
            SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesApproveAmendPurchaseOrder(this);

            // update indicators
            SpringContext.getBean(PurchaseOrderService.class).completePurchaseOrderAmendment(this);

            // update vendor commodity code by automatically spawning vendor maintenance document
            SpringContext.getBean(PurchaseOrderService.class).updateVendorCommodityCode(this);
            
            // set purap status
            SpringContext.getBean(PurapService.class).updateStatus(this, PurapConstants.PurchaseOrderStatuses.OPEN);

        }
        // DOCUMENT DISAPPROVED
        else if (getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForDisapprovedChangePODocuments(this);
            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);
        }
        // DOCUMENT CANCELED
        else if (getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForCancelledChangePODocuments(this);
            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);
        }
   }

   /**
    * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.document.AccountingDocument,
    *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
    */
   @Override
   public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
       super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);

       SpringContext.getBean(PurapGeneralLedgerService.class).customizeGeneralLedgerPendingEntry(this, (AccountingLine)postable, explicitEntry, getPurapDocumentIdentifier(), GL_DEBIT_CODE, PurapDocTypeCodes.PO_DOCUMENT, true);

       // don't think i should have to override this, but default isn't getting the right PO doc
       explicitEntry.setFinancialDocumentTypeCode(PurapDocTypeCodes.PO_AMENDMENT_DOCUMENT);
       explicitEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
   }

   @Override
   public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
       List<GeneralLedgerPendingEntrySourceDetail> accountingLines = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
       if (getGlOnlySourceAccountingLines() != null) {
           Iterator iter = getGlOnlySourceAccountingLines().iterator();
           while (iter.hasNext()) {
               accountingLines.add((GeneralLedgerPendingEntrySourceDetail) iter.next());
           }
       }
       return accountingLines;
   }

   
   @Override
   public void populateDocumentForRouting() {
       newUnorderedItem = SpringContext.getBean(PurchaseOrderService.class).hasNewUnorderedItem(this);
       receivingDeliveryCampusCode = SpringContext.getBean(ReceivingService.class).getReceivingDeliveryCampusCode(this);
       super.populateDocumentForRouting();
   }

    public boolean isNewUnorderedItem() {
        return newUnorderedItem;
    }
    
    public void setNewUnorderedItem(boolean newUnorderedItem) {
        this.newUnorderedItem = newUnorderedItem;
    }

    public String getReceivingDeliveryCampusCode() {
        return receivingDeliveryCampusCode;
    }

    public void setReceivingDeliveryCampusCode(String receivingDeliveryCampusCode) {
        this.receivingDeliveryCampusCode = receivingDeliveryCampusCode;
    }
    
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(PurapWorkflowConstants.HAS_NEW_UNORDERED_ITEMS)) return isNewUnorderedItem();
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \""+nodeName+"\"");
    } 

}
