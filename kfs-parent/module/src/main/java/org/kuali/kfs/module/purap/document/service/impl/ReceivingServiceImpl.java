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
package org.kuali.kfs.module.purap.document.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.CorrectionReceivingItem;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingView;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingItem;
import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.document.dataaccess.ReceivingDao;
import org.kuali.kfs.module.purap.document.service.LogicContainer;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedContinuePurapEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.exception.InfrastructureException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ReceivingServiceImpl implements ReceivingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReceivingServiceImpl.class);
    
    private PurchaseOrderService purchaseOrderService;
    private ReceivingDao receivingDao;
    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;
    private KualiConfigurationService configurationService;    
    private PurapService purapService;
    private NoteService noteService;

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setReceivingDao(ReceivingDao receivingDao) {
        this.receivingDao = receivingDao;
    }

    public void setDocumentService(DocumentService documentService){
        this.documentService = documentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService){
        this.workflowDocumentService = workflowDocumentService;
    }

    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * 
     * @see org.kuali.kfs.module.purap.document.service.ReceivingService#populateReceivingLineFromPurchaseOrder(org.kuali.kfs.module.purap.document.LineItemReceivingDocument)
     */
    public void populateReceivingLineFromPurchaseOrder(LineItemReceivingDocument rlDoc) {
        
        if(rlDoc == null){
            rlDoc = new LineItemReceivingDocument();
        }
                             
        //retrieve po by doc id
        PurchaseOrderDocument poDoc = null;
        poDoc = purchaseOrderService.getCurrentPurchaseOrder(rlDoc.getPurchaseOrderIdentifier());

        if(poDoc != null){
            rlDoc.populateReceivingLineFromPurchaseOrder(poDoc);
        }                
        
    }

    public void populateCorrectionReceivingFromReceivingLine(CorrectionReceivingDocument rcDoc) {
        
        if(rcDoc == null){
            rcDoc = new CorrectionReceivingDocument();
        }
                             
        //retrieve receiving line by doc id
        LineItemReceivingDocument rlDoc = rcDoc.getLineItemReceivingDocument();

        if(rlDoc != null){
            rcDoc.populateCorrectionReceivingFromReceivingLine(rlDoc);
        }                
        
    }

    /**
     * 
     * @see org.kuali.kfs.module.purap.document.service.ReceivingService#populateAndSaveLineItemReceivingDocument(org.kuali.kfs.module.purap.document.LineItemReceivingDocument)
     */
    public void populateAndSaveLineItemReceivingDocument(LineItemReceivingDocument rlDoc) throws WorkflowException {
        try {            
            documentService.saveDocument(rlDoc, AttributedContinuePurapEvent.class);
        }
        catch (WorkflowException we) {
            String errorMsg = "Error saving document # " + rlDoc.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            //LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.ReceivingService#populateCorrectionReceivingDocument(org.kuali.kfs.module.purap.document.CorrectionReceivingDocument)
     */
    public void populateCorrectionReceivingDocument(CorrectionReceivingDocument rcDoc)  {
            populateCorrectionReceivingFromReceivingLine(rcDoc);
    }

    /**
     * 
     * @see org.kuali.kfs.module.purap.document.service.ReceivingService#canCreateLineItemReceivingDocument(java.lang.Integer, java.lang.String)
     */
    public boolean canCreateLineItemReceivingDocument(Integer poId, String receivingDocumentNumber) throws RuntimeException {
        
        PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(poId);
        
        return canCreateLineItemReceivingDocument(po, receivingDocumentNumber);            
    }

    /**
     * 
     * @see org.kuali.kfs.module.purap.document.service.ReceivingService#canCreateLineItemReceivingDocument(org.kuali.kfs.module.purap.document.PurchaseOrderDocument)
     */
    public boolean canCreateLineItemReceivingDocument(PurchaseOrderDocument po) throws RuntimeException {
        return canCreateLineItemReceivingDocument(po, null);
    }
    
    protected boolean canCreateLineItemReceivingDocument(PurchaseOrderDocument po, String receivingDocumentNumber) {
        boolean canCreate = false;

        if (isPurchaseOrderValidForLineItemReceivingDocumentCreation(po) && 
            !isLineItemReceivingDocumentInProcessForPurchaseOrder(po.getPurapDocumentIdentifier(), receivingDocumentNumber) && 
            !isCorrectionReceivingDocumentInProcessForPurchaseOrder(po.getPurapDocumentIdentifier(), null)) {
            canCreate = true;
        }

        return canCreate;
    }

    public boolean isPurchaseOrderActiveForLineItemReceivingDocumentCreation(Integer poId){
        PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(poId);
        return isPurchaseOrderValidForLineItemReceivingDocumentCreation(po);
    }
    
    protected boolean isPurchaseOrderValidForLineItemReceivingDocumentCreation(PurchaseOrderDocument po){
        return po != null &&
               ObjectUtils.isNotNull(po.getPurapDocumentIdentifier()) && 
               po.isPurchaseOrderCurrentIndicator() && 
                   (PurchaseOrderStatuses.OPEN.equals(po.getStatusCode()) || 
                    PurchaseOrderStatuses.CLOSED.equals(po.getStatusCode()) || 
                    PurchaseOrderStatuses.PAYMENT_HOLD.equals(po.getStatusCode()));
    }
    
    public boolean canCreateCorrectionReceivingDocument(LineItemReceivingDocument rl) throws RuntimeException {
        return canCreateCorrectionReceivingDocument(rl, null);
    }

    public boolean canCreateCorrectionReceivingDocument(LineItemReceivingDocument rl, String receivingCorrectionDocNumber) throws RuntimeException {

        boolean canCreate = false;
        KualiWorkflowDocument workflowDocument = null;
        
        try{
            workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(rl.getDocumentNumber()), GlobalVariables.getUserSession().getPerson());
        }catch(WorkflowException we){
            throw new RuntimeException(we);
        }

        if( workflowDocument.stateIsFinal() &&
            !isCorrectionReceivingDocumentInProcessForReceivingLine(rl.getDocumentNumber(), receivingCorrectionDocNumber)){            
            canCreate = true;
        }
        
        return canCreate;
    }

    protected boolean isLineItemReceivingDocumentInProcessForPurchaseOrder(Integer poId, String receivingDocumentNumber) throws RuntimeException{
        return !getLineItemReceivingDocumentNumbersInProcessForPurchaseOrder(poId, receivingDocumentNumber).isEmpty();
    }

    public List<String> getLineItemReceivingDocumentNumbersInProcessForPurchaseOrder(Integer poId, 
                                                                                     String receivingDocumentNumber){
        
        List<String> inProcessDocNumbers = new ArrayList<String>();
        List<String> docNumbers = receivingDao.getDocumentNumbersByPurchaseOrderId(poId);
        KualiWorkflowDocument workflowDocument = null;
                
        for (String docNumber : docNumbers) {
        
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getPerson());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            if(!(workflowDocument.stateIsCanceled() ||
                 workflowDocument.stateIsException() ||
                 workflowDocument.stateIsFinal()) &&
                 docNumber.equals(receivingDocumentNumber) == false ){
                inProcessDocNumbers.add(docNumber);
            }
        }

        return inProcessDocNumbers;
    }
    
    public List<LineItemReceivingDocument> getLineItemReceivingDocumentsInFinalForPurchaseOrder(Integer poId) {

        List<String> finalDocNumbers = new ArrayList<String>();
        List<String> docNumbers = receivingDao.getDocumentNumbersByPurchaseOrderId(poId);
        KualiWorkflowDocument workflowDocument = null;

        for (String docNumber : docNumbers) {

            try {
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getPerson());
            }
            catch (WorkflowException we) {
                throw new RuntimeException(we);
            }

            if (workflowDocument.stateIsFinal()) {
                finalDocNumbers.add(docNumber);
            }
        }

        if (finalDocNumbers.size() > 0) {
            try {
                return SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(LineItemReceivingDocument.class, finalDocNumbers);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to retrieve LineItemReceivingDocuments", e);
            }
        }
        else {
            return null;
        }
        
    }
    
    protected boolean isCorrectionReceivingDocumentInProcessForPurchaseOrder(Integer poId, String receivingDocumentNumber) throws RuntimeException{
        return !getCorrectionReceivingDocumentNumbersInProcessForPurchaseOrder(poId, receivingDocumentNumber).isEmpty();
    }
    
    public List<String> getCorrectionReceivingDocumentNumbersInProcessForPurchaseOrder(Integer poId, 
                                                                                       String receivingDocumentNumber){
        
        boolean isInProcess = false;
        
        List<String> inProcessDocNumbers = new ArrayList<String>();
        List<String> docNumbers = receivingDao.getCorrectionReceivingDocumentNumbersByPurchaseOrderId(poId);
        KualiWorkflowDocument workflowDocument = null;
                
        for (String docNumber : docNumbers) {
        
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getPerson());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            if(!(workflowDocument.stateIsCanceled() ||
                 workflowDocument.stateIsException() ||
                 workflowDocument.stateIsFinal()) &&
                 docNumber.equals(receivingDocumentNumber) == false ){
                inProcessDocNumbers.add(docNumber);
            }
        }

        return inProcessDocNumbers;
    }
    
    
    protected boolean isCorrectionReceivingDocumentInProcessForReceivingLine(String receivingDocumentNumber, String receivingCorrectionDocNumber) throws RuntimeException{
        
        boolean isInProcess = false;
        
        List<String> docNumbers = receivingDao.getCorrectionReceivingDocumentNumbersByReceivingLineNumber(receivingDocumentNumber);
        KualiWorkflowDocument workflowDocument = null;
                
        for (String docNumber : docNumbers) {
        
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getPerson());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            if(!(workflowDocument.stateIsCanceled() ||
                 workflowDocument.stateIsException() ||
                 workflowDocument.stateIsFinal()) &&
                 docNumber.equals(receivingCorrectionDocNumber) == false ){
                     
                isInProcess = true;
                break;
            }
        }

        return isInProcess;
    }

    /**
     * 
     * @see org.kuali.kfs.module.purap.document.service.ReceivingService#receivingLineDuplicateMessages(org.kuali.kfs.module.purap.document.LineItemReceivingDocument)
     */
    public HashMap<String, String> receivingLineDuplicateMessages(LineItemReceivingDocument rlDoc) {
        HashMap<String, String> msgs;
        msgs = new HashMap<String, String>();
        Integer poId = rlDoc.getPurchaseOrderIdentifier();
        StringBuffer currentMessage = new StringBuffer("");
        List<String> docNumbers = null;
        
        //check vendor date for duplicates
        if( rlDoc.getShipmentReceivedDate() != null ){
            docNumbers = receivingDao.duplicateVendorDate(poId, rlDoc.getShipmentReceivedDate());
            if( hasDuplicateEntry(docNumbers) ){
                appendDuplicateMessage(currentMessage, PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_VENDOR_DATE, rlDoc.getPurchaseOrderIdentifier());                                
            }
        }
        
        //check packing slip number for duplicates
        if( !StringUtils.isEmpty(rlDoc.getShipmentPackingSlipNumber()) ){
            docNumbers = receivingDao.duplicatePackingSlipNumber(poId, rlDoc.getShipmentPackingSlipNumber());
            if( hasDuplicateEntry(docNumbers) ){
                appendDuplicateMessage(currentMessage, PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_PACKING_SLIP_NUMBER, rlDoc.getPurchaseOrderIdentifier());                                
            }
        }
        
        //check bill of lading number for duplicates
        if( !StringUtils.isEmpty(rlDoc.getShipmentBillOfLadingNumber()) ){
            docNumbers = receivingDao.duplicateBillOfLadingNumber(poId, rlDoc.getShipmentBillOfLadingNumber());
            if( hasDuplicateEntry(docNumbers) ){
                appendDuplicateMessage(currentMessage, PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_BILL_OF_LADING_NUMBER, rlDoc.getPurchaseOrderIdentifier());                
            }
        }
        
       //add message if one exists
       if(currentMessage.length() > 0){
           //add suffix
           appendDuplicateMessage(currentMessage, PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_SUFFIX, rlDoc.getPurchaseOrderIdentifier() );
           
           //add msg to map
           msgs.put(PurapConstants.LineItemReceivingDocumentStrings.DUPLICATE_RECEIVING_LINE_QUESTION, currentMessage.toString());
       }
       
       return msgs;
    }

    /**
     * Looks at a list of doc numbers, but only considers an entry duplicate
     * if the document is in a Final status.
     * 
     * @param docNumbers
     * @return
     */
    protected boolean hasDuplicateEntry(List<String> docNumbers){
        
        boolean isDuplicate = false;
        KualiWorkflowDocument workflowDocument = null;
        
        for (String docNumber : docNumbers) {
        
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getPerson());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            //if the doc number exists, and is in final status, consider this a dupe and return
            if(workflowDocument.stateIsFinal()){
                isDuplicate = true;
                break;
            }
        }
        
        return isDuplicate;

    }
    protected void appendDuplicateMessage(StringBuffer currentMessage, String duplicateMessageKey, Integer poId){
        
        //append prefix if this is first call
        if(currentMessage.length() == 0){
            String messageText = configurationService.getPropertyString(PurapKeyConstants.MESSAGE_DUPLICATE_RECEIVING_LINE_PREFIX);
            String prefix = MessageFormat.format(messageText, poId.toString() );
            
            currentMessage.append(prefix);
        }
        
        //append message
        currentMessage.append( configurationService.getPropertyString(duplicateMessageKey) );                
    }
    
    public void completeCorrectionReceivingDocument(ReceivingDocument correctionDocument){
       
        ReceivingDocument receivingDoc = ((CorrectionReceivingDocument)correctionDocument).getLineItemReceivingDocument();
        
        for (CorrectionReceivingItem correctionItem : (List<CorrectionReceivingItem>)correctionDocument.getItems()) {
            if(!StringUtils.equalsIgnoreCase(correctionItem.getItemType().getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE)) {

                LineItemReceivingItem recItem = (LineItemReceivingItem) receivingDoc.getItem(correctionItem.getItemLineNumber().intValue() - 1);
                PurchaseOrderItem poItem = (PurchaseOrderItem) receivingDoc.getPurchaseOrderDocument().getItem(correctionItem.getItemLineNumber().intValue() - 1);
                
                if(ObjectUtils.isNotNull(recItem)) {
                    recItem.setItemReceivedTotalQuantity(correctionItem.getItemReceivedTotalQuantity());
                    recItem.setItemReturnedTotalQuantity(correctionItem.getItemReturnedTotalQuantity());
                    recItem.setItemDamagedTotalQuantity(correctionItem.getItemDamagedTotalQuantity());
                }
            }
        }
        
    }
    
    /**
     * 
     * This method deletes unneeded items and updates the totals on the po and does any additional processing based on items i.e. FYI etc
     * @param receivingDocument receiving document
     */
    public void completeReceivingDocument(ReceivingDocument receivingDocument) {

        PurchaseOrderDocument poDoc = null;

        if (receivingDocument instanceof LineItemReceivingDocument){
            // delete unentered items
            purapService.deleteUnenteredItems(receivingDocument);
            poDoc = receivingDocument.getPurchaseOrderDocument();
        }else if (receivingDocument instanceof CorrectionReceivingDocument){
            CorrectionReceivingDocument correctionDocument = (CorrectionReceivingDocument)receivingDocument;
            poDoc = purchaseOrderService.getCurrentPurchaseOrder(correctionDocument.getLineItemReceivingDocument().getPurchaseOrderIdentifier());
        }
        
        updateReceivingTotalsOnPurchaseOrder(receivingDocument, poDoc);

        //TODO: custom doc specific service hook here for correction to do it's receiving doc update
        
        purapService.saveDocumentNoValidation(poDoc);

        sendFyiForItems(receivingDocument);
        
        spawnPoAmendmentForUnorderedItems(receivingDocument, poDoc);

        purapService.saveDocumentNoValidation(receivingDocument);
    }

    public void createNoteForReturnedAndDamagedItems(ReceivingDocument recDoc){
        
        for (ReceivingItem item : (List<ReceivingItem>)recDoc.getItems()){
            if(!StringUtils.equalsIgnoreCase(item.getItemType().getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE)) {
                if (item.getItemReturnedTotalQuantity() != null && item.getItemReturnedTotalQuantity().isGreaterThan(KualiDecimal.ZERO)){
                    try{
                        String noteString = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.MESSAGE_RECEIVING_LINEITEM_RETURN_NOTE_TEXT);
                        noteString = item.getItemReturnedTotalQuantity().intValue() + " " + noteString + " " + item.getItemLineNumber();
                        addNoteToReceivingDocument(recDoc, noteString);
                    }catch (Exception e){
                        String errorMsg = "Note Service Exception caught: " + e.getLocalizedMessage();
                        throw new RuntimeException(errorMsg, e);                    
                    }
                }
                
                if (item.getItemDamagedTotalQuantity() != null && item.getItemDamagedTotalQuantity().isGreaterThan(KualiDecimal.ZERO)){
                    try{
                        String noteString = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.MESSAGE_RECEIVING_LINEITEM_DAMAGE_NOTE_TEXT);
                        noteString = item.getItemDamagedTotalQuantity().intValue() + " " + noteString + " " + item.getItemLineNumber();
                        addNoteToReceivingDocument(recDoc, noteString);
                    }catch (Exception e){
                        String errorMsg = "Note Service Exception caught: " + e.getLocalizedMessage();
                        throw new RuntimeException(errorMsg, e);                    
                    }
                }
            }
        }
    }
    
    protected void updateReceivingTotalsOnPurchaseOrder(ReceivingDocument receivingDocument, PurchaseOrderDocument poDoc) {
        for (ReceivingItem receivingItem : (List<ReceivingItem>)receivingDocument.getItems()) {
            ItemType itemType = receivingItem.getItemType();
            if(!StringUtils.equalsIgnoreCase(itemType.getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE)) {
                //TODO: Chris - this method of getting the line out of po should be turned into a method that can get an item based on a combo or itemType and line
                PurchaseOrderItem poItem = (PurchaseOrderItem)poDoc.getItemByLineNumber(receivingItem.getItemLineNumber());
                
                if(ObjectUtils.isNotNull(poItem)) {
                    
                    KualiDecimal poItemReceivedTotal = poItem.getItemReceivedTotalQuantity();
                    
                    KualiDecimal receivingItemReceivedOriginal = receivingItem.getItemOriginalReceivedTotalQuantity();
                    /**
                     * FIXME: It's coming as null although we set the default value in the LineItemReceivingItem constructor - mpv
                     */
                    if (ObjectUtils.isNull(receivingItemReceivedOriginal)){
                        receivingItemReceivedOriginal = KualiDecimal.ZERO; 
                    }
                    KualiDecimal receivingItemReceived = receivingItem.getItemReceivedTotalQuantity(); 
                    KualiDecimal receivingItemTotalReceivedAdjested = receivingItemReceived.subtract(receivingItemReceivedOriginal); 
                    
                    if (ObjectUtils.isNull(poItemReceivedTotal)){
                        poItemReceivedTotal = KualiDecimal.ZERO;
                    }
                    KualiDecimal poItemReceivedTotalAdjusted = poItemReceivedTotal.add(receivingItemTotalReceivedAdjested); 
                    
                    KualiDecimal receivingItemReturnedOriginal = receivingItem.getItemOriginalReturnedTotalQuantity();
                    if (ObjectUtils.isNull(receivingItemReturnedOriginal)){
                        receivingItemReturnedOriginal = KualiDecimal.ZERO; 
                    }
                    
                    KualiDecimal receivingItemReturned = receivingItem.getItemReturnedTotalQuantity();
                    if (ObjectUtils.isNull(receivingItemReturned)){
                        receivingItemReturned = KualiDecimal.ZERO; 
                    }
                    
                    KualiDecimal receivingItemTotalReturnedAdjusted = receivingItemReturned.subtract(receivingItemReturnedOriginal); 
                    
                    poItemReceivedTotalAdjusted = poItemReceivedTotalAdjusted.subtract(receivingItemTotalReturnedAdjusted);
                    
                    poItem.setItemReceivedTotalQuantity(poItemReceivedTotalAdjusted);
                    
                    KualiDecimal poTotalDamaged = poItem.getItemDamagedTotalQuantity();
                    if (ObjectUtils.isNull(poTotalDamaged)){
                        poTotalDamaged = KualiDecimal.ZERO; 
                    }

                    KualiDecimal receivingItemTotalDamagedOriginal = receivingItem.getItemOriginalDamagedTotalQuantity();
                    if (ObjectUtils.isNull(receivingItemTotalDamagedOriginal)){
                        receivingItemTotalDamagedOriginal = KualiDecimal.ZERO; 
                    }
                    
                    KualiDecimal receivingItemTotalDamaged = receivingItem.getItemDamagedTotalQuantity();
                    if (ObjectUtils.isNull(receivingItemTotalDamaged)){
                        receivingItemTotalDamaged = KualiDecimal.ZERO; 
                    }
                    
                    KualiDecimal receivingItemTotalDamagedAdjusted = receivingItemTotalDamaged.subtract(receivingItemTotalDamagedOriginal);
                    
                    poItem.setItemDamagedTotalQuantity(poTotalDamaged.add(receivingItemTotalDamagedAdjusted));
                    
                }
            }
        }
    }
    
    /**
     * Spawns PO amendments for new unordered items on a receiving document.
     * 
     * @param receivingDocument
     * @param po
     */
    protected void spawnPoAmendmentForUnorderedItems(ReceivingDocument receivingDocument, PurchaseOrderDocument po){

        //if receiving line document
        if (receivingDocument instanceof LineItemReceivingDocument) {
            LineItemReceivingDocument rlDoc = (LineItemReceivingDocument)receivingDocument;
            
            //if a new item has been added spawn a purchase order amendment
            if( hasNewUnorderedItem((LineItemReceivingDocument)receivingDocument) ){
                String newSessionUserId = KFSConstants.SYSTEM_USER;
                try {                    
                    
                    LogicContainer logicToRun = new LogicContainer() {
                        public Object runLogic(Object[] objects) throws Exception {
                            LineItemReceivingDocument rlDoc = (LineItemReceivingDocument)objects[0];
                            String poDocNumber = (String)objects[1];
                            
                            //create a PO amendment
                            PurchaseOrderAmendmentDocument amendmentPo = (PurchaseOrderAmendmentDocument) purchaseOrderService.createAndSavePotentialChangeDocument(poDocNumber, PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, PurchaseOrderStatuses.AMENDMENT);

                            //add new lines to amendement
                            addUnorderedItemsToAmendment(amendmentPo, rlDoc);
                            
                            //route amendment
                            documentService.routeDocument(amendmentPo, null, null);

                            //add note to amendment po document
                            String note = "Purchase Order Amendment " + amendmentPo.getPurapDocumentIdentifier() + " (document id " + amendmentPo.getDocumentNumber() + ") created for new unordered line items due to Receiving (document id " + rlDoc.getDocumentNumber() + ")";
                            
                            Note noteObj = documentService.createNoteFromDocument(amendmentPo, note);
                            documentService.addNoteToDocument(amendmentPo, noteObj);
                            noteService.save(noteObj);

                            return null;
                        }
                    };
                    
                    purapService.performLogicWithFakedUserSession(newSessionUserId, logicToRun, new Object[] { rlDoc, po.getDocumentNumber() });
                }
                catch (WorkflowException e) {
                    String errorMsg = "Workflow Exception caught: " + e.getLocalizedMessage();
                    throw new RuntimeException(errorMsg, e);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }           
        }
    }
    
    /**
     * Checks the item list for newly added items.
     * 
     * @param rlDoc
     * @return
     */
    protected boolean hasNewUnorderedItem(LineItemReceivingDocument rlDoc){
        
        boolean itemAdded = false;
        
        for(LineItemReceivingItem rlItem: (List<LineItemReceivingItem>)rlDoc.getItems()){
            if( PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE.equals(rlItem.getItemTypeCode()) &&
                !StringUtils.isEmpty(rlItem.getItemReasonAddedCode()) ){
                itemAdded = true;
                break;
            }
        }
        
        return itemAdded;
    }
    
    /**
     * Adds an unordered item to a po amendment document.
     * 
     * @param amendment
     * @param rlDoc
     */
    protected void addUnorderedItemsToAmendment(PurchaseOrderAmendmentDocument amendment, LineItemReceivingDocument rlDoc){

        PurchaseOrderItem poi = null;
        
        for(LineItemReceivingItem rlItem: (List<LineItemReceivingItem>)rlDoc.getItems()){
            if( PurapConstants.ItemTypeCodes.ITEM_TYPE_UNORDERED_ITEM_CODE.equals(rlItem.getItemTypeCode()) &&
                !StringUtils.isEmpty(rlItem.getItemReasonAddedCode()) ){
                
                poi = createPoItemFromReceivingLine(rlItem);
                poi.setDocumentNumber( amendment.getDocumentNumber() );
                poi.refreshNonUpdateableReferences();
                amendment.addItem(poi);
            }
        }

    }
    
    /**
     * Creates a PO item from a receiving line item.
     * 
     * @param rlItem
     * @return
     */
    protected PurchaseOrderItem createPoItemFromReceivingLine(LineItemReceivingItem rlItem){
        
        PurchaseOrderItem poi = new PurchaseOrderItem();
                             
        poi.setItemActiveIndicator(true);
        poi.setItemTypeCode(rlItem.getItemTypeCode());                
        poi.setItemLineNumber(rlItem.getItemLineNumber());        
        poi.setItemCatalogNumber( rlItem.getItemCatalogNumber() );
        poi.setItemDescription( rlItem.getItemDescription() );

        if( rlItem.getItemReturnedTotalQuantity() == null){
            poi.setItemQuantity( rlItem.getItemReceivedTotalQuantity());
        }else{
            poi.setItemQuantity( rlItem.getItemReceivedTotalQuantity().subtract(rlItem.getItemReturnedTotalQuantity()) );
        }
        
        poi.setItemUnitOfMeasureCode( rlItem.getItemUnitOfMeasureCode() );
        poi.setItemUnitPrice(new BigDecimal(0));
        
        poi.setItemDamagedTotalQuantity( rlItem.getItemDamagedTotalQuantity() );
        poi.setItemReceivedTotalQuantity( rlItem.getItemReceivedTotalQuantity() );
        
        return poi;
    }
    
    /**
     * Creates a list of fiscal officers for new unordered items added to a purchase order.
     * 
     * @param po
     * @return
     */
    protected List<AdHocRoutePerson> createFyiFiscalOfficerList(ReceivingDocument recDoc){

        PurchaseOrderDocument po = recDoc.getPurchaseOrderDocument();
        List<AdHocRoutePerson> adHocRoutePersons = new ArrayList<AdHocRoutePerson>();
        Map fiscalOfficers = new HashMap();
        AdHocRoutePerson adHocRoutePerson = null;

        for(ReceivingItem recItem: (List<ReceivingItem>)recDoc.getItems()){
            //if this item has an item line number then it is coming from the po
            if (ObjectUtils.isNotNull(recItem.getItemLineNumber())) {
                PurchaseOrderItem poItem = (PurchaseOrderItem)po.getItemByLineNumber(recItem.getItemLineNumber());

                if(poItem.getItemQuantity().isLessThan(poItem.getItemReceivedTotalQuantity())||
                        recItem.getItemDamagedTotalQuantity().isGreaterThan(KualiDecimal.ZERO)) {

                    // loop through accounts and pull off fiscal officer
                    for(PurApAccountingLine account : poItem.getSourceAccountingLines()){

                        //check for dupes of fiscal officer
                        if( fiscalOfficers.containsKey(account.getAccount().getAccountFiscalOfficerUser().getPrincipalName()) == false ){

                            //add fiscal officer to list
                            fiscalOfficers.put(account.getAccount().getAccountFiscalOfficerUser().getPrincipalName(), account.getAccount().getAccountFiscalOfficerUser().getPrincipalName());

                            //create AdHocRoutePerson object and add to list
                            adHocRoutePerson = new AdHocRoutePerson();
                            adHocRoutePerson.setId(account.getAccount().getAccountFiscalOfficerUser().getPrincipalName());
                            adHocRoutePerson.setActionRequested(KFSConstants.WORKFLOW_FYI_REQUEST);
                            adHocRoutePersons.add(adHocRoutePerson);
                        }
                    }

                }

            }
        }

        return adHocRoutePersons;
    }
    /**
     * Sends an FYI to fiscal officers for new unordered items.
     * 
     * @param po
     */
    protected void sendFyiForItems(ReceivingDocument recDoc){

        List<AdHocRoutePerson> fyiList = createFyiFiscalOfficerList(recDoc);
        String annotation = "Notification of Item exceeded Quantity or Damaged" + "(document id " + recDoc.getDocumentNumber() + ")";
        String responsibilityNote = "Please Review";
        
        for(AdHocRoutePerson adHocPerson: fyiList){
            try{
                recDoc.appSpecificRouteDocumentToUser(
                        recDoc.getDocumentHeader().getWorkflowDocument(),
                        adHocPerson.getId(),
                        annotation,
                        responsibilityNote);
            }catch (WorkflowException e) {
                throw new RuntimeException("Error routing fyi for document with id " + recDoc.getDocumentNumber(), e);
            }

        }
    }    
    
    public void addNoteToReceivingDocument(ReceivingDocument receivingDocument, String note) throws Exception{
        Note noteObj = documentService.createNoteFromDocument(receivingDocument, note);
        documentService.addNoteToDocument(receivingDocument, noteObj);
        noteService.save(noteObj);        
    }
    
    public String getReceivingDeliveryCampusCode(PurchaseOrderDocument po){
        String deliveryCampusCode = "";
        String latestDocumentNumber = "";
                        
        List<LineItemReceivingView> rViews = null;
        KualiWorkflowDocument workflowDocument = null;
        Timestamp latestCreateDate = null;
        
        //get related views
        if(ObjectUtils.isNotNull(po.getRelatedViews()) ){       
            rViews = po.getRelatedViews().getRelatedLineItemReceivingViews();
        }
        
        //if not empty, then grab the latest receiving view
        if(ObjectUtils.isNotNull(rViews) && rViews.isEmpty() == false){                                    
            
            for(LineItemReceivingView rView : rViews){
                try{
                    workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(rView.getDocumentNumber()), GlobalVariables.getUserSession().getPerson());
                    
                    //if latest create date is null or the latest is before the current, current is newer
                    if( ObjectUtils.isNull(latestCreateDate) || latestCreateDate.before(workflowDocument.getCreateDate()) ){
                        latestCreateDate = workflowDocument.getCreateDate();
                        latestDocumentNumber = workflowDocument.getRouteHeaderId().toString();
                    }
                }catch(WorkflowException we){
                    throw new RuntimeException(we);
                }                
            }
            
            //if there is a create date, a latest workflow doc was found
            if( ObjectUtils.isNotNull(latestCreateDate)){
                try{                                    
                    LineItemReceivingDocument rlDoc = (LineItemReceivingDocument)documentService.getByDocumentHeaderId(latestDocumentNumber);                    
                    deliveryCampusCode = rlDoc.getDeliveryCampusCode();
                }catch(WorkflowException we){
                    throw new RuntimeException(we);
                }
            }
        }
                
        return deliveryCampusCode;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.service.ReceivingService#isLineItemReceivingDocumentGeneratedForPurchaseOrder(java.lang.Integer)
     */
    public boolean isLineItemReceivingDocumentGeneratedForPurchaseOrder(Integer poId) throws RuntimeException{
        
        boolean isGenerated = false;
        
        List<String> docNumbers = receivingDao.getDocumentNumbersByPurchaseOrderId(poId);
        KualiWorkflowDocument workflowDocument = null;
                
        for (String docNumber : docNumbers) {
        
            try{
                workflowDocument = workflowDocumentService.createWorkflowDocument(Long.valueOf(docNumber), GlobalVariables.getUserSession().getPerson());
            }catch(WorkflowException we){
                throw new RuntimeException(we);
            }
            
            if(workflowDocument.stateIsFinal()){                     
                isGenerated = true;
                break;
            }
        }

        return isGenerated;
    }

    public void approveReceivingDocsForPOAmendment(){
        List<LineItemReceivingDocument> docs = receivingDao.getReceivingDocumentsForPOAmendment();
        if (docs != null){
            for (LineItemReceivingDocument receivingDoc: docs) {
                if (StringUtils.equals(receivingDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getCurrentRouteNodeNames(),
                    PurapConstants.LineItemReceivingDocumentStrings.AWAITING_PO_OPEN_STATUS)){
                        approveReceivingDoc(receivingDoc);
                    }
            }
        }
        
    }
    
    protected void approveReceivingDoc(LineItemReceivingDocument receivingDoc){
        PurchaseOrderDocument poDoc = receivingDoc.getPurchaseOrderDocument();
        if (purchaseOrderService.canAmendPurchaseOrder(poDoc)){
            try{
                SpringContext.getBean(DocumentService.class).approveDocument(receivingDoc, "Approved by the batch job", null);
            }
            catch (WorkflowException e) {
                LOG.error("approveReceivingDoc() Error approving receiving document from awaiting PO open", e);
                throw new RuntimeException("Error approving receiving document from awaiting PO open", e);
            }
        }
    }
}

