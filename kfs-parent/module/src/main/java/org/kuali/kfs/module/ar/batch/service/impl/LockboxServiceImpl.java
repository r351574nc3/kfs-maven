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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.service.LockboxService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.Lockbox;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.dataaccess.LockboxDao;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.docsearch.service.SearchableAttributeProcessingService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

/**
 *  
 * Lockbox Iterators are sorted by processedInvoiceDate and batchSequenceNumber. 
 * Potentially there could be many batches on the same date. 
 * For each set of records with the same processedInvoiceDate and batchSequenceNumber, 
 * there will be one Cash-Control document. Each record within this set will create one Application document.
 * 
 */

@Transactional
public class LockboxServiceImpl implements LockboxService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(LockboxServiceImpl.class);;

    private PersonService<Person> personService;
    private DocumentService documentService;
    private SystemInformationService systemInformationService;
    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    private CashControlDocumentService cashControlDocumentService;
    private PaymentApplicationDocumentService payAppDocService;
    private DataDictionaryService dataDictionaryService;
    private DateTimeService dateTimeService;
    private BusinessObjectService boService;
    
    private LockboxDao lockboxDao;
    private String reportsDirectory;
    
    public boolean processLockbox() throws WorkflowException {

        //  create the pdf doc
        com.lowagie.text.Document pdfdoc = getPdfDoc();
        
        //  this giant try/catch is to make sure that something gets written to the 
        // report.  please dont use it for specific exception handling, rather nest 
        // new try/catch handlers inside this.
        try {
            
        Iterator<Lockbox> itr = lockboxDao.getAllLockboxes();
        Lockbox ctrlLockbox = new Lockbox();
        CashControlDocument cashControlDocument = null;
        boolean anyRecordsFound = false;
        while (itr.hasNext()) {
            anyRecordsFound = true;
            Lockbox lockbox = (Lockbox)itr.next();
            LOG.info("LOCKBOX: '" + lockbox.getLockboxNumber() + "'");

            //  retrieve the processingOrg (system information) for this lockbox number
            SystemInformation sysInfo = systemInformationService.getByLockboxNumberForCurrentFiscalYear(lockbox.getLockboxNumber());
            String initiator = sysInfo.getFinancialDocumentInitiatorIdentifier();
            LOG.info("   using SystemInformation: '" + sysInfo.toString() + "'");
            LOG.info("   using Financial Document Initiator ID: '" + initiator + "'");
            
            //  puke if the initiator stored in the systemInformation table is no good
            Person person = getPersonService().getPerson(initiator);
            if (person == null) {
                LOG.warn("   could not find [" + initiator + "] when searching by PrincipalID, so trying to find as a PrincipalName.");
                person = getPersonService().getPersonByPrincipalName(initiator);
                if (person == null) {
                    LOG.error("Financial Document Initiator ID [" + initiator + "] specified in SystemInformation [" + sysInfo.toString() + "] for Lockbox Number " + lockbox.getLockboxNumber() + " is not present in the system as either a PrincipalID or a PrincipalName.");
                    throw new RuntimeException("Financial Document Initiator ID [" + initiator + "] specified in SystemInformation [" + sysInfo.toString() + "] for Lockbox Number " + lockbox.getLockboxNumber() + " is not present in the system as either a PrincipalID or a PrincipalName.");
                }
                else {
                    LOG.info("   found [" + initiator + "] in the system as a PrincipalName.");
                }
            }
            else {
                LOG.info("   found [" + initiator + "] in the system as a PrincipalID.");
            }
            
            //  masquerade as the person indicated in the systemInformation
            GlobalVariables.clear();
            GlobalVariables.setUserSession(new UserSession(person.getPrincipalName()));

            if (lockbox.compareTo(ctrlLockbox) != 0) {
                // If we made it in here, then we have hit a different batchSequenceNumber and processedInvoiceDate.
                // When this is the case, we create a new cashcontroldocument and start tacking subsequent lockboxes on 
                // to the current cashcontroldocument as cashcontroldetails.
                LOG.info("New Lockbox batch");

                //  we're creating a new cashcontrol, so if we have an old one, we need to route it
                if (cashControlDocument != null) {
                    LOG.info("   routing cash control document.");
                    try {
                        documentService.routeDocument(cashControlDocument, "Routed by Lockbox Batch process.", null);
                    }
                    catch (Exception e) {
                        LOG.error("A Exception was thrown while trying to route the CashControl document.", e);
                        throw new RuntimeException("A Exception was thrown while trying to route the CashControl document.", e);
                    }
                }

                //  create a new CashControl document
                LOG.info("Creating new CashControl document for invoice: " + lockbox.getFinancialDocumentReferenceInvoiceNumber() + ".");
                try {
                    cashControlDocument = (CashControlDocument)documentService.getNewDocument(KFSConstants.FinancialDocumentTypeCodes.CASH_CONTROL);
                }
                catch (Exception e) {
                    LOG.error("A Exception was thrown while trying to initiate a new CashControl document.", e);
                    throw new RuntimeException("A Exception was thrown while trying to initiate a new CashControl document.", e);
                }
                LOG.info("   CashControl documentNumber == '" + cashControlDocument.getDocumentNumber() + "'");
                
                //  write the batch group header to the report
                writeBatchGroupSectionTitle(pdfdoc, lockbox.getBatchSequenceNumber().toString(), lockbox.getProcessedInvoiceDate(), 
                        cashControlDocument.getDocumentNumber());
                
                cashControlDocument.setCustomerPaymentMediumCode(lockbox.getCustomerPaymentMediumCode());
                if(ObjectUtils.isNotNull(lockbox.getBankCode())) {
                    String bankCode = lockbox.getBankCode();
                    cashControlDocument.setBankCode(bankCode);
                } 
                cashControlDocument.getDocumentHeader().setDocumentDescription(ArConstants.LOCKBOX_DOCUMENT_DESCRIPTION + lockbox.getLockboxNumber());

                //  setup the AR header for this CashControl doc
                LOG.info("   creating AR header for customer: [" + lockbox.getCustomerNumber() + "] and ProcessingOrg: " + sysInfo.getProcessingChartOfAccountCode() + "-" + sysInfo.getProcessingOrganizationCode() + ".");
                AccountsReceivableDocumentHeader arDocHeader;
                try {
                    arDocHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeader(
                            sysInfo.getProcessingChartOfAccountCode(), sysInfo.getProcessingOrganizationCode());
                }
                catch (Exception e) {
                    LOG.error("An Exception was thrown while trying to create a new AccountsReceivableDocumentHeader for the current user: '" + person.getPrincipalName() + "'.", e);
                    throw new RuntimeException("An Exception was thrown while trying to create a new AccountsReceivableDocumentHeader for the current user: '" + person.getPrincipalName() + "'.", e);
                }
                arDocHeader.setDocumentNumber(cashControlDocument.getDocumentNumber());
                arDocHeader.setCustomerNumber(lockbox.getCustomerNumber());
                cashControlDocument.setAccountsReceivableDocumentHeader(arDocHeader);
            } 
            // set our control lockbox as the current lockbox and create details.
            ctrlLockbox = lockbox;
            
            //  write the lockbox detail line to the report
            writeLockboxRecordLine(pdfdoc, lockbox.getLockboxNumber(), lockbox.getCustomerNumber(), lockbox.getFinancialDocumentReferenceInvoiceNumber(), 
                    lockbox.getInvoicePaidOrAppliedAmount(), lockbox.getCustomerPaymentMediumCode(), lockbox.getBankCode());

            //  skip zero-dollar-amount lockboxes
            if (lockbox.getInvoicePaidOrAppliedAmount().isZero()) {
                LOG.warn("   lockbox has a zero dollar amount, so we're skipping it.");
                writeSummaryDetailLine(pdfdoc, "ZERO-DOLLAR LOCKBOX - NO FURTHER PROCESSING");
                deleteProcessedLockboxEntry(lockbox);
                continue;
            }
            if (lockbox.getInvoicePaidOrAppliedAmount().isLessThan(KualiDecimal.ZERO)) {
                LOG.warn("   lockbox has a negative dollar amount, so we're skipping it.");
                writeCashControlDetailLine(pdfdoc, lockbox.getInvoicePaidOrAppliedAmount(), "SKIPPED");
                writeSummaryDetailLine(pdfdoc, "NEGATIVE-DOLLAR LOCKBOX - NO FURTHER PROCESSING - LOCKBOX ENTRY NOT DELETED");
                continue;
            }
            
            //  create a new cashcontrol detail
            CashControlDetail detail = new CashControlDetail();
            detail.setCustomerNumber(lockbox.getCustomerNumber());
            detail.setFinancialDocumentLineAmount(lockbox.getInvoicePaidOrAppliedAmount());
            detail.setCustomerPaymentDate(lockbox.getProcessedInvoiceDate());
            detail.setCustomerPaymentDescription("Lockbox Remittance  " +lockbox.getFinancialDocumentReferenceInvoiceNumber());

            //  add it to the document
            LOG.info("   creating detail for $" + lockbox.getInvoicePaidOrAppliedAmount() + " with invoiceDate: " + lockbox.getProcessedInvoiceDate());
            try {
                cashControlDocumentService.addNewCashControlDetail(ArConstants.LOCKBOX_DOCUMENT_DESCRIPTION, cashControlDocument, detail);
            }
            catch (Exception e) {
                LOG.error("A Exception was thrown while trying to create a new CashControl detail.", e);
                throw new RuntimeException("A Exception was thrown while trying to create a new CashControl detail.", e);
            }

            //  retrieve the docNumber of the generated payapp
            String payAppDocNumber = detail.getReferenceFinancialDocumentNumber();
            LOG.info("   new PayAppDoc was created: " + payAppDocNumber + ".");
            
            String invoiceNumber = lockbox.getFinancialDocumentReferenceInvoiceNumber();
            LOG.info("   lockbox references invoice number [" + invoiceNumber + "].");
            
            //  before release 3, during dev, sometimes invoice numbers we got from the functional 
            // testing dataset were old FIS style, and not compatible with KFS
            boolean invoiceNumberNotParsable = false;
            if (StringUtils.isBlank(invoiceNumber)) {
                invoiceNumberNotParsable = true;
            }
            else {
                try {
                    Integer.parseInt(invoiceNumber);
                } catch (Exception e) {
                    invoiceNumberNotParsable = true;
                }
            }
            
            //  if thats the case, dont even bother looking for an invoice, just save the CashControl
            if (invoiceNumberNotParsable) {
                LOG.info("   invoice number [" + invoiceNumber + "] isnt in expected KFS format, so cannot load the original invoice.");
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVALID_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                try {
                    documentService.saveDocument(cashControlDocument);
                }
                catch (Exception e) {
                    LOG.error("A Exception was thrown while trying to save the CashControl document.", e);
                    throw new RuntimeException("A Exception was thrown while trying to save the CashControl document.", e);
                }
                
                //  write the detail and payapp lines to the report
                routePayAppWithoutBusinessRules(payAppDocNumber, "CREATED & SAVED by Lockbox batch");
                writeCashControlDetailLine(pdfdoc, detail.getFinancialDocumentLineAmount(), detail.getCustomerPaymentDescription());
                writePayAppLine(pdfdoc, detail.getReferenceFinancialDocumentNumber(), "CREATED & SAVED");
                writeSummaryDetailLine(pdfdoc, "INVOICE NUMBER NOT PARSEABLE");
                //  delete the lockbox now we're done with it
                deleteProcessedLockboxEntry(lockbox);
                continue;
            }
            
            //  check to see if the invoice indicated exists, and if not, then save the CashControl and move on
            if (!documentService.documentExists(invoiceNumber)) {
                LOG.info("   invoice number [" + invoiceNumber + "] does not exist in system, so cannot load the original invoice.");
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVALID_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                try {
                    documentService.saveDocument(cashControlDocument);
                }
                catch (Exception e) {
                    LOG.error("A Exception was thrown while trying to save the CashControl document.", e);
                    throw new RuntimeException("A Exception was thrown while trying to save the CashControl document.", e);
                }
                routePayAppWithoutBusinessRules(payAppDocNumber, "CREATED & SAVED by Lockbox batch");
                writeCashControlDetailLine(pdfdoc, detail.getFinancialDocumentLineAmount(), detail.getCustomerPaymentDescription());
                writePayAppLine(pdfdoc, detail.getReferenceFinancialDocumentNumber(), "CREATED & SAVED");
                writeSummaryDetailLine(pdfdoc, "INVOICE DOESNT EXIST");
                //  delete the lockbox now we're done with it
                deleteProcessedLockboxEntry(lockbox);
                continue;
            }

            //  load up the specified invoice from the lockbox
            LOG.info("   loading invoice number [" + invoiceNumber + "].");
            CustomerInvoiceDocument customerInvoiceDocument;
            try {
                customerInvoiceDocument = (CustomerInvoiceDocument)documentService.getByDocumentHeaderId(invoiceNumber);
            }
            catch (Exception e) {
                LOG.error("A Exception was thrown while trying to load invoice #" + invoiceNumber + ".", e);
                throw new RuntimeException("A Exception was thrown while trying to load invoice #" + invoiceNumber + ".", e);
            }
            
            //  if the invoice is already closed, then just save the CashControl and move on
            writeInvoiceDetailLine(pdfdoc, invoiceNumber, customerInvoiceDocument.isOpenInvoiceIndicator(), 
                    customerInvoiceDocument.getCustomer().getCustomerNumber(), customerInvoiceDocument.getOpenAmount());
            if (!customerInvoiceDocument.isOpenInvoiceIndicator()) {
                LOG.info("   invoice is already closed, so saving CashControl doc and moving on.");
                detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_CLOSED_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
                try {
                    documentService.saveDocument(cashControlDocument);
                }
                catch (Exception e) {
                    LOG.error("A Exception was thrown while trying to save the CashControl document.", e);
                    throw new RuntimeException("A Exception was thrown while trying to save the CashControl document.", e);
                }
                routePayAppWithoutBusinessRules(payAppDocNumber, "CREATED & SAVED by Lockbox batch");
                writeCashControlDetailLine(pdfdoc, detail.getFinancialDocumentLineAmount(), detail.getCustomerPaymentDescription());
                writePayAppLine(pdfdoc, detail.getReferenceFinancialDocumentNumber(), "CREATED & SAVED");
                writeSummaryDetailLine(pdfdoc, "INVOICE ALREADY CLOSED");
                deleteProcessedLockboxEntry(lockbox);
                continue;
            }
            
            boolean autoApprove = customerInvoiceDocument.getOpenAmount().equals(lockbox.getInvoicePaidOrAppliedAmount());
            String annotation = "CREATED & SAVED";
            
            //  if the lockbox amount matches the invoice amount, then create, save and approve a PayApp, and then 
            // mark the invoice
            if (autoApprove){
                LOG.info("   lockbox amount matches invoice total document amount [" + customerInvoiceDocument.getTotalDollarAmount() + "].");
                annotation = "CREATED, SAVED, and BLANKET APPROVED";
                
                //  load up the PayApp document that was created
                LOG.info("   loading the generated PayApp [" + payAppDocNumber + "], so we can route or approve it.");
                PaymentApplicationDocument payAppDoc;
                try {
                    payAppDoc = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(payAppDocNumber);
                }
                catch (Exception e) {
                    LOG.error("A Exception was thrown while trying to load PayApp #" + payAppDocNumber + ".", e);
                    throw new RuntimeException("A Exception was thrown while trying to load PayApp #" + payAppDocNumber + ".", e);
                }

                //  create paidapplieds on the PayApp doc for all the Invoice details
                LOG.info("   attempting to create paidApplieds on the PayAppDoc for every detail on the invoice.");
                payAppDoc = payAppDocService.createInvoicePaidAppliedsForEntireInvoiceDocument(customerInvoiceDocument, payAppDoc);
                LOG.info("   PayAppDoc has TotalApplied of " + payAppDoc.getTotalApplied() + " for a Control Balance of " + payAppDoc.getTotalFromControl() + ".");
                
                //  Save and approve the payapp doc
                LOG.info("   attempting to blanketApprove the PayApp Doc.");
                try {
                    
                    documentService.blanketApproveDocument(payAppDoc, "Automatically approved by Lockbox batch job.", null);
                }
                catch (Exception e) {
                    LOG.error("A Exception was thrown while trying to blanketApprove PayAppDoc #" + payAppDoc.getDocumentNumber() + ".", e);
                    throw new RuntimeException("A Exception was thrown while trying to blanketApprove PayAppDoc #" + payAppDoc.getDocumentNumber() + ".", e);
                }

                //  write the report details 
                writeCashControlDetailLine(pdfdoc, detail.getFinancialDocumentLineAmount(), detail.getCustomerPaymentDescription());
                writePayAppLine(pdfdoc, detail.getReferenceFinancialDocumentNumber(), annotation);
                writeSummaryDetailLine(pdfdoc, "LOCKBOX AMOUNT MATCHES INVOICE OPEN AMOUNT");
            }
            else {
                LOG.info("   lockbox amount does NOT match invoice total document amount [" + customerInvoiceDocument.getTotalDollarAmount() + "].");
                routePayAppWithoutBusinessRules(payAppDocNumber, "CREATED & SAVED by Lockbox batch");

                //  write the report details 
                writeCashControlDetailLine(pdfdoc, detail.getFinancialDocumentLineAmount(), detail.getCustomerPaymentDescription());
                writePayAppLine(pdfdoc, detail.getReferenceFinancialDocumentNumber(), annotation);
                if (lockbox.getInvoicePaidOrAppliedAmount().isLessThan(customerInvoiceDocument.getOpenAmount())) {
                    writeSummaryDetailLine(pdfdoc, "LOCKBOX UNDERPAID INVOICE");
                }
                else {
                    writeSummaryDetailLine(pdfdoc, "LOCKBOX OVERPAID INVOICE");
                }
            }
            
            //  save the cashcontrol, which saves any changes to the details
            detail.setCustomerPaymentDescription(ArConstants.LOCKBOX_REMITTANCE_FOR_INVOICE_NUMBER +lockbox.getFinancialDocumentReferenceInvoiceNumber());
            LOG.info("   saving cash control document.");
            try {
                documentService.saveDocument(cashControlDocument);
            }
            catch (Exception e) {
                LOG.error("A Exception was thrown while trying to save the CashControl document.", e);
                throw new RuntimeException("A Exception was thrown while trying to save the CashControl document.", e);
            }
            
            //  delete the lockbox now we're done with it
            deleteProcessedLockboxEntry(lockbox);
        }
        
        //  if we have a cashControlDocument here, then it needs to be routed, its the last one
        if (cashControlDocument != null) {
            LOG.info("   routing cash control document.");
            try {
                documentService.routeDocument(cashControlDocument, "Routed by Lockbox Batch process.", null);
            }
            catch (Exception e) {
                LOG.error("A Exception was thrown while trying to route the CashControl document.", e);
                throw new RuntimeException("A Exception was thrown while trying to route the CashControl document.", e);
            }
        }

        //  if no records were found, write something useful to the report
        if (!anyRecordsFound) {
            writeDetailLine(pdfdoc, "NO LOCKBOX RECORDS WERE FOUND");
        }
        
        //  this annoying all-encompassing try/catch is here to make sure that the report gets 
        // written.  without it, if anything goes wrong, the report will end up a zero-byte document.
        }
        catch (Exception e) {
            writeDetailLine(pdfdoc, "AN EXCEPTION OCCURRED:");
            writeDetailLine(pdfdoc, "");
            writeDetailLine(pdfdoc, e.getMessage());
            writeDetailLine(pdfdoc, "");
            writeExceptionStackTrace(pdfdoc, e);
        }
        
        //  spool the report
        pdfdoc.close();
        
        return true;

    }

    protected void routePayAppWithoutBusinessRules(String payAppDocNumber, String annotation) {

        //  load up the PayApp document that was created
        LOG.info("   loading the generated PayApp [" + payAppDocNumber + "], so we can route or approve it.");
        PaymentApplicationDocument payAppDoc;
        try {
            payAppDoc = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(payAppDocNumber);
        }
        catch (Exception e) {
            LOG.error("A Exception was thrown while trying to load PayApp #" + payAppDocNumber + ".", e);
            throw new RuntimeException("A Exception was thrown while trying to load PayApp #" + payAppDocNumber + ".", e);
        }

        //  route without business rules 
        LOG.info("   attempting to route without business rules the PayApp Doc.");
        try {
           payAppDoc.getDocumentHeader().getWorkflowDocument().routeDocument(annotation);
            final SearchableAttributeProcessingService searchableAttributeProcessingService = SpringContext.getBean(SearchableAttributeProcessingService.class);
            searchableAttributeProcessingService.indexDocument(new Long(payAppDoc.getDocumentNumber()));
        }
        catch (Exception e) {
            LOG.error("A Exception was thrown while trying to route (without business rules) PayAppDoc #" + payAppDoc.getDocumentNumber() + ".", e);
            throw new RuntimeException("A Exception was thrown while trying to route (without business rules) PayAppDoc #" + payAppDoc.getDocumentNumber() + ".", e);
        }
    }
    
    protected void deleteProcessedLockboxEntry(Lockbox lockboxEntry) {
        Map<String,Object> pkMap = new HashMap<String,Object>();
        pkMap.put("invoiceSequenceNumber", lockboxEntry.getInvoiceSequenceNumber());
        boService.deleteMatching(Lockbox.class, pkMap);
    }
    
    protected com.lowagie.text.Document getPdfDoc() {
        
        String reportDropFolder = reportsDirectory + "/" + ArConstants.Lockbox.LOCKBOX_REPORT_SUBFOLDER + "/";
        String fileName = ArConstants.Lockbox.BATCH_REPORT_BASENAME + "_" +  
            new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(dateTimeService.getCurrentDate()) + ".pdf";
       
        //  setup the writer
        File reportFile = new File(reportDropFolder + fileName);
        FileOutputStream fileOutStream;
        try {
            fileOutStream = new FileOutputStream(reportFile);
        }
        catch (IOException e) {
            LOG.error("IOException thrown when trying to open the FileOutputStream.", e);
            throw new RuntimeException("IOException thrown when trying to open the FileOutputStream.", e);
        }
        BufferedOutputStream buffOutStream = new BufferedOutputStream(fileOutStream);
        
        com.lowagie.text.Document pdfdoc = new com.lowagie.text.Document(PageSize.LETTER, 54, 54, 72, 72);
        try {
            PdfWriter.getInstance(pdfdoc, buffOutStream);
        }
        catch (DocumentException e) {
            LOG.error("iText DocumentException thrown when trying to start a new instance of the PdfWriter.", e);
            throw new RuntimeException("iText DocumentException thrown when trying to start a new instance of the PdfWriter.", e);
        }
        
        pdfdoc.open();
        
        return pdfdoc;
    }

    protected String rightPad(String valToPad, int sizeToPadTo) {
        return rightPad(valToPad, sizeToPadTo, " ");
    }
    
    protected String rightPad(String valToPad, int sizeToPadTo, String padChar) {
        if (StringUtils.isBlank(valToPad)) {
            return StringUtils.repeat(padChar, sizeToPadTo);
        }
        if (valToPad.length() >= sizeToPadTo) {
            return valToPad;
        }
        return valToPad + StringUtils.repeat(padChar, sizeToPadTo - valToPad.length());
    }
    
    protected void writeBatchGroupSectionTitle(com.lowagie.text.Document pdfDoc, String batchSeqNbr, java.sql.Date procInvDt, String cashControlDocNumber) {
        Font font = FontFactory.getFont(FontFactory.COURIER, 10, Font.BOLD);
        
        String lineText = "CASHCTL " + rightPad(cashControlDocNumber, 12) + " " + 
                            "BATCH GROUP: " + rightPad(batchSeqNbr, 5) + " " + 
                            rightPad((procInvDt == null ? "NONE" : procInvDt.toString()), 35);
        
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(com.lowagie.text.Element.ALIGN_LEFT);
        Chunk chunk = new Chunk(lineText, font);
        chunk.setBackground(Color.LIGHT_GRAY, 5, 5, 5, 5);
        paragraph.add(chunk);
        
        //  blank line
        paragraph.add(new Chunk("", font));
        
        try {
            pdfDoc.add(paragraph);
        }
        catch (DocumentException e) {
            LOG.error("iText DocumentException thrown when trying to write content.", e);
            throw new RuntimeException("iText DocumentException thrown when trying to write content.", e);
        }
    }
    
    protected void writeLockboxRecordLine(com.lowagie.text.Document pdfDoc, String lockboxNumber, String customerNumber, String invoiceNumber, 
            KualiDecimal invoiceTotalAmount, String paymentMediumCode, String bankCode) {
        
        writeDetailLine(pdfDoc, StringUtils.repeat("-", 100));
        
        StringBuilder sb = new StringBuilder();
        sb.append("   ");                                                        // 3:   1 - 3
        sb.append("LOCKBOX: " + rightPad(lockboxNumber, 10) + " ");              // 20:  4 - 23    
        sb.append("CUST: " + rightPad(customerNumber, 9) + " ");                 // 15:  24 - 38 
        sb.append("INV: " + rightPad(invoiceNumber, 10) + " ");                  // 16:  39 - 55 
        sb.append(StringUtils.repeat(" ", 28));                                  // 28:  56 - 83 
        sb.append("AMT: " + rightPad(invoiceTotalAmount.toString(), 11) + " ");  // 17:  84 - 100
        
        writeDetailLine(pdfDoc, sb.toString());
    }
    
    protected void writeInvoiceDetailLine(com.lowagie.text.Document pdfDoc, String invoiceNumber, boolean open, String customerNumber, KualiDecimal openAmount) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("   ");                                                        // 3:   1 - 3
        sb.append("INVOICE: " + rightPad(invoiceNumber, 10) + " ");              // 20:  4 - 23    
        sb.append("CUST: " + rightPad(customerNumber, 9) + " ");                 // 15:  24 - 38 
        if (open) {
            sb.append(rightPad("OPEN", 16) + " ");                               // 16:  39 - 55 
        }
        else {
            sb.append(rightPad("CLOSED", 16) + " ");                             // 16:  39 - 55 
        }
        sb.append(StringUtils.repeat(" ", 22));                                  // 28:  56 - 83 
        sb.append("OPEN AMT: " + rightPad(openAmount.toString(), 11) + " ");  // 17:  84 - 100
        
        writeDetailLine(pdfDoc, sb.toString());
    }
    
    protected void writeCashControlDetailLine(com.lowagie.text.Document pdfDoc, KualiDecimal amount, String description) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("   ");                                                        // 3:   1 - 3
        sb.append("CASHCTL DTL: " + rightPad(description, 66) + " ");            // 80:  4 - 83    
        sb.append("AMT: " + rightPad(amount.toString(), 11) + " ");              // 17:  84 - 100
        
        writeDetailLine(pdfDoc, sb.toString());
    }
    
    protected void writeSummaryDetailLine(com.lowagie.text.Document pdfDoc, String summary) {
        writeDetailLine(pdfDoc, "   " + summary);
    }
    
    protected void writePayAppLine(com.lowagie.text.Document pdfDoc, String payAppDocNbr, String description) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("   ");                                                    // 3:   1 - 3
        sb.append("PAYAPP DOC NBR: " + rightPad(payAppDocNbr, 12) + " ");    // 29:  4 - 32    
        sb.append("ACTION: " + description);                                 // 40:  33 - 72
        
        writeDetailLine(pdfDoc, sb.toString());
    }
    
    protected void writeExceptionStackTrace(com.lowagie.text.Document pdfDoc, Exception e) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(outStream, true);
        
        e.printStackTrace(printWriter);
        printWriter.flush();
        writeDetailLine(pdfDoc, outStream.toString());
    }
    
    protected void writeDetailLine(com.lowagie.text.Document pdfDoc, String detailLineText) {
        Font font = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);
        
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(com.lowagie.text.Element.ALIGN_LEFT);
        paragraph.add(new Chunk(detailLineText, font));

        try {
            pdfDoc.add(paragraph);
        }
        catch (DocumentException e) {
            LOG.error("iText DocumentException thrown when trying to write content.", e);
            throw new RuntimeException("iText DocumentException thrown when trying to write content.", e);
        }
    }
    
    public Long getMaxLockboxSequenceNumber() {
        return lockboxDao.getMaxLockboxSequenceNumber();
    }
    
    public LockboxDao getLockboxDao() {
        return lockboxDao;
    }

    public void setLockboxDao(LockboxDao lockboxDao) {
        this.lockboxDao = lockboxDao;
    }

    public SystemInformationService getSystemInformationService() {
        return systemInformationService;
    }

    public void setSystemInformationService(SystemInformationService systemInformationService) {
        this.systemInformationService = systemInformationService;
    }

    public AccountsReceivableDocumentHeaderService getAccountsReceivableDocumentHeaderService() {
        return accountsReceivableDocumentHeaderService;
    }

    public void setAccountsReceivableDocumentHeaderService(AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService) {
        this.accountsReceivableDocumentHeaderService = accountsReceivableDocumentHeaderService;
    }

    public void setPaymentApplicationDocumentService(PaymentApplicationDocumentService paymentApplicationDocumentService) {
        this.payAppDocService = paymentApplicationDocumentService;
    }

    /**
     * @return Returns the personService.
     */
    protected PersonService<Person> getPersonService() {
        if(personService==null)
            personService = SpringContext.getBean(PersonService.class);
        return personService;
    }

    /**
     * Gets the documentService attribute. 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public CashControlDocumentService getCashControlDocumentService() {
        return cashControlDocumentService;
    }

    public void setCashControlDocumentService(CashControlDocumentService cashControlDocumentService) {
        this.cashControlDocumentService = cashControlDocumentService;
    }

    public void setReportsDirectory(String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    public void setBoService(BusinessObjectService boService) {
        this.boService = boService;
    }

}
