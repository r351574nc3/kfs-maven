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

import java.math.BigDecimal;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Purchase Order Retransmit Document
 */
public class PurchaseOrderRetransmitDocument extends PurchaseOrderDocument {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderRetransmitDocument.class);

    protected boolean shouldDisplayRetransmitTab;
    
    /**
     * Default constructor.
     */
    public PurchaseOrderRetransmitDocument() {
        super();
    }

    /**
     * General Ledger pending entries are not created for this document. Overriding this method so that entries are not created.
     * 
     * @see org.kuali.kfs.module.purap.document.PurchaseOrderDocument#customPrepareForSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void customPrepareForSave(KualiDocumentEvent event) {
        // do not set the accounts in sourceAccountingLines; this document should not create GL entries
    }

    /**
     * Adds up the total amount of the items selected by the user for retransmit, then return the amount.
     * 
     * @return KualiDecimal the total amount of the items selected by the user for retransmit.
     */
    public KualiDecimal getTotalDollarAmountForRetransmit() {
        // We should only add up the amount of the items that were selected for retransmit.
        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getItems()) {
            if (item.isItemSelectedForRetransmitIndicator()) {
                KualiDecimal totalAmount = item.getTotalAmount();
                KualiDecimal itemTotal = (totalAmount != null) ? totalAmount : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }

        return total;
    }
   
    public KualiDecimal getTotalPreTaxDollarAmountForRetransmit() {
        // We should only add up the amount of the items that were selected for retransmit.
        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getItems()) {
            if (item.isItemSelectedForRetransmitIndicator()) {
                KualiDecimal extendedPrice = item.getExtendedPrice();
                KualiDecimal itemTotal = (extendedPrice != null) ? extendedPrice : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }

        return total;
    }
    
    public KualiDecimal getTotalTaxDollarAmountForRetransmit() {
        // We should only add up the amount of the items that were selected for retransmit.
        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getItems()) {
            if (item.isItemSelectedForRetransmitIndicator()) {
                KualiDecimal taxAmount = item.getItemTaxAmount();
                KualiDecimal itemTotal = (taxAmount != null) ? taxAmount : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }

        return total;
    }
    
    @Override
    public List<Long> getWorkflowEngineDocumentIdsToLock() {
        return super.getWorkflowEngineDocumentIdsToLock();
    }

    /**
     * When Purchase Order Retransmit document has been Processed through Workflow, the PO status remains to "OPEN" and the last
     * transmit date is updated.
     * 
     * @see org.kuali.kfs.module.purap.document.PurchaseOrderDocument#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        // DOCUMENT PROCESSED
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForApprovedPODocuments(this);
            setPurchaseOrderLastTransmitTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
            SpringContext.getBean(PurapService.class).updateStatus(this, PurapConstants.PurchaseOrderStatuses.OPEN);
        }
        // DOCUMENT DISAPPROVED
        else if (getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForDisapprovedChangePODocuments(this);
        }
        // DOCUMENT CANCELED
        else if (getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForCancelledChangePODocuments(this);
        }

    }

    public boolean isShouldDisplayRetransmitTab() {
        return shouldDisplayRetransmitTab;
    }

    public void setShouldDisplayRetransmitTab(boolean shouldDisplayRetransmitTab) {
        this.shouldDisplayRetransmitTab = shouldDisplayRetransmitTab;
    }

}
