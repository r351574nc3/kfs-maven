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
package org.kuali.kfs.module.purap.document.dataaccess.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.batch.service.PurapRunDateService;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.dataaccess.NegativePaymentRequestApprovalLimitDao;
import org.kuali.kfs.module.purap.document.dataaccess.PaymentRequestDao;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.exception.InfrastructureException;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TransactionalServiceUtils;

/**
 * OJB Implementation of PaymentRequestDao.
 */
public class PaymentRequestDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentRequestDao {
    private static Logger LOG = Logger.getLogger(PaymentRequestDaoOjb.class);

    private NegativePaymentRequestApprovalLimitDao negativePaymentRequestApprovalLimitDao;
    private DateTimeService dateTimeService;
    private PurapAccountingService purapAccountingService;
    private KualiConfigurationService kualiConfigurationService;
    private PurapRunDateService purapRunDateService;

    public void setNegativePaymentRequestApprovalLimitDao(NegativePaymentRequestApprovalLimitDao negativePaymentRequestApprovalLimitDao) {
        this.negativePaymentRequestApprovalLimitDao = negativePaymentRequestApprovalLimitDao;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setPurapRunDateService(PurapRunDateService purapRunDateService) {
        this.purapRunDateService = purapRunDateService;
    }
    

    /**
     * The special payments query should be this: select * from pur.ap_pmt_rqst_t where pmt_rqst_stat_cd in ('AUTO', 'DPTA') and
     * prcs_cmp_cd = ? and pmt_extrt_ts is NULL and pmt_hld_ind = 'N' and ( ( ( pmt_spcl_handlg_instrc_ln1_txt is not NULL or
     * pmt_spcl_handlg_instrc_ln2_txt is not NULL or pmt_spcl_handlg_instrc_ln3_txt is not NULL or pmt_att_ind = 'Y') and trunc
     * (pmt_rqst_pay_dt) <= trunc (sysdate)) or IMD_PMT_IND = 'Y')})
     * 
     * @see org.kuali.kfs.module.purap.document.dataaccess.PaymentRequestDao#getPaymentRequestsToExtract(boolean, java.lang.String)
     */
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtract(boolean onlySpecialPayments, String chartCode,
            Date onOrBeforePaymentRequestPayDate) {
        LOG.debug("getPaymentRequestsToExtract() started");

        Criteria criteria = new Criteria();
        if (chartCode != null) {
            criteria.addEqualTo("processingCampusCode", chartCode);
        }
        criteria.addIn("statusCode", Arrays.asList(PaymentRequestStatuses.STATUSES_ALLOWED_FOR_EXTRACTION));
        criteria.addIsNull("extractedTimestamp");
        criteria.addEqualTo("holdIndicator", Boolean.FALSE);

        if (onlySpecialPayments) {
            Criteria a = new Criteria();

            Criteria c1 = new Criteria();
            c1.addNotNull("specialHandlingInstructionLine1Text");
            Criteria c2 = new Criteria();
            c2.addNotNull("specialHandlingInstructionLine2Text");
            Criteria c3 = new Criteria();
            c3.addNotNull("specialHandlingInstructionLine3Text");
            Criteria c4 = new Criteria();
            c4.addEqualTo("paymentAttachmentIndicator", Boolean.TRUE);

            c1.addOrCriteria(c2);
            c1.addOrCriteria(c3);
            c1.addOrCriteria(c4);

            a.addAndCriteria(c1);
            a.addLessOrEqualThan("paymentRequestPayDate", onOrBeforePaymentRequestPayDate);

            Criteria c5 = new Criteria();
            c5.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);
            c5.addOrCriteria(a);

            criteria.addAndCriteria(a);
        }
        else {
            Criteria c1 = new Criteria();
            c1.addLessOrEqualThan("paymentRequestPayDate", onOrBeforePaymentRequestPayDate);

            Criteria c2 = new Criteria();
            c2.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);

            c1.addOrCriteria(c2);
            criteria.addAndCriteria(c1);
        }

        return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentRequestDocument.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.PaymentRequestDao#getImmediatePaymentRequestsToExtract(java.lang.String)
     */
    public Collection<PaymentRequestDocument> getImmediatePaymentRequestsToExtract(String chartCode) {
        LOG.debug("getImmediatePaymentRequestsToExtract() started");

        Criteria criteria = new Criteria();
        if (chartCode != null) {
            criteria.addEqualTo("processingCampusCode", chartCode);
        }

        criteria.addIn("statusCode", Arrays.asList(PaymentRequestStatuses.STATUSES_ALLOWED_FOR_EXTRACTION));
        criteria.addIsNull("extractedTimestamp");
        criteria.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);

        return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentRequestDocument.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.PaymentRequestDao#getPaymentRequestsToExtract(java.lang.String, java.lang.Integer,
     *      java.lang.Integer, java.lang.Integer, java.lang.Integer)
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtract(String campusCode, Integer paymentRequestIdentifier, Integer purchaseOrderIdentifier, Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier) {
        LOG.debug("getPaymentRequestsToExtract() started");

        List statuses = new ArrayList();
        statuses.add(PurapConstants.PaymentRequestStatuses.AUTO_APPROVED);
        statuses.add(PurapConstants.PaymentRequestStatuses.DEPARTMENT_APPROVED);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processingCampusCode", campusCode);
        criteria.addIn("statusCode", statuses);
        criteria.addIsNull("extractedTimestamp");
        criteria.addEqualTo("holdIndicator", Boolean.FALSE);

        Criteria c1 = new Criteria();
        c1.addLessOrEqualThan("paymentRequestPayDate", dateTimeService.getCurrentSqlDateMidnight());

        Criteria c2 = new Criteria();
        c2.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);

        c1.addOrCriteria(c2);
        criteria.addAndCriteria(c1);

        if (paymentRequestIdentifier != null) {
            criteria.addEqualTo("purapDocumentIdentifier", paymentRequestIdentifier);
        }
        if (purchaseOrderIdentifier != null) {
            criteria.addEqualTo("purchaseOrderIdentifier", purchaseOrderIdentifier);
        }
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorHeaderGeneratedIdentifier);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorDetailAssignedIdentifier);

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentRequestDocument.class, criteria));
    }

    /**
     * 
     * @see org.kuali.kfs.module.purap.document.dataaccess.PaymentRequestDao#getPaymentRequestsToExtractForVendor(java.lang.String, org.kuali.kfs.module.purap.util.VendorGroupingHelper)
     */
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtractForVendor(String campusCode, VendorGroupingHelper vendor, Date onOrBeforePaymentRequestPayDate) {
        LOG.debug("getPaymentRequestsToExtract() started");

        List statuses = new ArrayList();
        statuses.add(PurapConstants.PaymentRequestStatuses.AUTO_APPROVED);
        statuses.add(PurapConstants.PaymentRequestStatuses.DEPARTMENT_APPROVED);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processingCampusCode", campusCode);
        criteria.addIn("statusCode", statuses);
        criteria.addIsNull("extractedTimestamp");
        criteria.addEqualTo("holdIndicator", Boolean.FALSE);

        Criteria c1 = new Criteria();
        c1.addLessOrEqualThan("paymentRequestPayDate", onOrBeforePaymentRequestPayDate);

        Criteria c2 = new Criteria();
        c2.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);

        c1.addOrCriteria(c2);
        criteria.addAndCriteria(c1);

        criteria.addEqualTo( "vendorHeaderGeneratedIdentifier", vendor.getVendorHeaderGeneratedIdentifier() );
        criteria.addEqualTo( "vendorDetailAssignedIdentifier", vendor.getVendorDetailAssignedIdentifier() );
        criteria.addEqualTo( "vendorCountryCode", vendor.getVendorCountry() );
        criteria.addLike( "vendorPostalCode", vendor.getVendorPostalCode() + "%" );

        return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentRequestDocument.class, criteria));
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.PaymentRequestDao#getEligibleForAutoApproval()
     */
    public List<PaymentRequestDocument> getEligibleForAutoApproval() {
        Date todayAtMidnight = dateTimeService.getCurrentSqlDateMidnight();
        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan(PurapPropertyConstants.PAYMENT_REQUEST_PAY_DATE, todayAtMidnight);
        criteria.addNotEqualTo("holdIndicator", "Y");
        criteria.addNotEqualTo("paymentRequestedCancelIndicator", "Y");
        criteria.addIn("status", Arrays.asList(PurapConstants.PaymentRequestStatuses.PREQ_STATUSES_FOR_AUTO_APPROVE));

        Query query = new QueryByCriteria(PaymentRequestDocument.class, criteria);
        Iterator<PaymentRequestDocument> documents = (Iterator<PaymentRequestDocument>) getPersistenceBrokerTemplate().getIteratorByQuery(query);
        ArrayList<String> documentHeaderIds = new ArrayList<String>();
        while (documents.hasNext()) {
            PaymentRequestDocument document = (PaymentRequestDocument) documents.next();
            documentHeaderIds.add(document.getDocumentNumber());
        }

        if (documentHeaderIds.size() > 0) {
            try {
                return SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(PaymentRequestDocument.class, documentHeaderIds);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to retrieve paymentRequestDocuments", e);
            }
        }
        else {
            return null;
        }

    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.PaymentRequestDao#getDocumentNumberByPaymentRequestId(java.lang.Integer)
     */
    public String getDocumentNumberByPaymentRequestId(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        return getDocumentNumberOfPaymentRequestByCriteria(criteria);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.PaymentRequestDao#getDocumentNumbersByPurchaseOrderId(java.lang.Integer)
     */
    public List<String> getDocumentNumbersByPurchaseOrderId(Integer poPurApId) {
        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poPurApId);
        Iterator<Object[]> iter = getDocumentNumbersOfPaymentRequestByCriteria(criteria, false);
        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
        return returnList;
    }

    /**
     * Retrieves a document number for a payment request by user defined criteria.
     * 
     * @param criteria - list of criteria to use in the retrieve
     * @return - document number
     */
    protected String getDocumentNumberOfPaymentRequestByCriteria(Criteria criteria) {
        LOG.debug("getDocumentNumberOfPaymentRequestByCriteria() started");
        Iterator<Object[]> iter = getDocumentNumbersOfPaymentRequestByCriteria(criteria, false);
        if (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            if (iter.hasNext()) {
                // the iterator should have held only a single doc id of data but it holds 2 or more
                String errorMsg = "Expected single document number for given criteria but multiple (at least 2) were returned";
                LOG.error(errorMsg);
                TransactionalServiceUtils.exhaustIterator(iter);
                throw new RuntimeException();
            }
            // at this part of the code, we know there's no more elements in iterator
            return (String) cols[0];
        }
        return null;
    }

    /**
     * Retrieves a document number for a payment request by user defined criteria and sorts the values ascending if orderByAscending
     * parameter is true, descending otherwise.
     * 
     * @param criteria - list of criteria to use in the retrieve
     * @param orderByAscending - boolean to sort results ascending if true, descending otherwise
     * @return - Iterator of document numbers
     */
    protected Iterator<Object[]> getDocumentNumbersOfPaymentRequestByCriteria(Criteria criteria, boolean orderByAscending) {
        LOG.debug("getDocumentNumberOfPaymentRequestByCriteria() started");
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(PaymentRequestDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
    }

    /**
     * Retrieves a list of payment requests by user defined criteria.
     * 
     * @param qbc - query with criteria
     * @return - list of payment requests
     */
    protected List<PaymentRequestDocument> getPaymentRequestsByQueryByCriteria(QueryByCriteria qbc) {
        LOG.debug("getPaymentRequestsByQueryByCriteria() started");
        List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return l;
    }

    /**
     * Retrieves a list of payment requests with the given vendor id and invoice number.
     * 
     * @param vendorHeaderGeneratedId - header id of the vendor id
     * @param vendorDetailAssignedId - detail id of the vendor id
     * @param invoiceNumber - invoice number as entered by AP
     * @return - List of payment requests.
     */
    public List<PaymentRequestDocument> getActivePaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedId, Integer vendorDetailAssignedId, String invoiceNumber) {
        LOG.debug("getActivePaymentRequestsByVendorNumberInvoiceNumber() started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorHeaderGeneratedId);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorDetailAssignedId);
        criteria.addEqualTo("invoiceNumber", invoiceNumber);
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class, criteria);
        return this.getPaymentRequestsByQueryByCriteria(qbc);
    }

    /**
     * Retrieves a list of payment requests with the given vendor id and invoice number.
     * 
     * @param vendorHeaderGeneratedId - header id of the vendor id
     * @param vendorDetailAssignedId - detail id of the vendor id
     * @param invoiceNumber - invoice number as entered by AP
     * @return - List of payment requests.
     */
    public List<PaymentRequestDocument> getActivePaymentRequestsByVendorNumber(Integer vendorHeaderGeneratedId, Integer vendorDetailAssignedId) {
        LOG.debug("getActivePaymentRequestsByVendorNumber started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorHeaderGeneratedId);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorDetailAssignedId);
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class, criteria);
        return this.getPaymentRequestsByQueryByCriteria(qbc);
    }
    
    
    /**
     * @see org.kuali.kfs.module.purap.document.dataaccess.PaymentRequestDao#getActivePaymentRequestsByPOIdInvoiceAmountInvoiceDate(java.lang.Integer,
     *      org.kuali.rice.kns.util.KualiDecimal, java.sql.Date)
     */
    public List<PaymentRequestDocument> getActivePaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal vendorInvoiceAmount, Date invoiceDate) {
        LOG.debug("getActivePaymentRequestsByVendorNumberInvoiceNumber() started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("purchaseOrderIdentifier", poId);
        criteria.addEqualTo("vendorInvoiceAmount", vendorInvoiceAmount);
        criteria.addEqualTo("invoiceDate", invoiceDate);
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class, criteria);
        return this.getPaymentRequestsByQueryByCriteria(qbc);
    }

    public List<String> getActivePaymentRequestDocumentNumbersForPurchaseOrder(Integer purchaseOrderId){
        LOG.debug("getActivePaymentRequestsByVendorNumberInvoiceNumber() started");
                
        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, purchaseOrderId);
        criteria.addIn(PurapPropertyConstants.STATUS_CODE, Arrays.asList(PaymentRequestStatuses.STATUSES_POTENTIALLY_ACTIVE));
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class, criteria);
        
        Iterator<Object[]> iter = getDocumentNumbersOfPaymentRequestByCriteria(criteria, false);
        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
        return returnList;
    }
    
    public List<PaymentRequestDocument> getPaymentRequestInReceivingStatus() {
        Criteria criteria = new Criteria();
        criteria.addNotEqualTo("holdIndicator", "Y");
        criteria.addNotEqualTo("paymentRequestedCancelIndicator", "Y");
        criteria.addEqualTo("statusCode", PurapConstants.PaymentRequestStatuses.AWAITING_RECEIVING_REVIEW);

        Query query = new QueryByCriteria(PaymentRequestDocument.class, criteria);
        Iterator<PaymentRequestDocument> documents = (Iterator<PaymentRequestDocument>) getPersistenceBrokerTemplate().getIteratorByQuery(query);
        ArrayList<String> documentHeaderIds = new ArrayList<String>();
        while (documents.hasNext()) {
            PaymentRequestDocument document = (PaymentRequestDocument) documents.next();
            documentHeaderIds.add(document.getDocumentNumber());
        }

        if (documentHeaderIds.size() > 0) {
            try {
                return SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(PaymentRequestDocument.class, documentHeaderIds);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to retrieve paymentRequestDocuments", e);
            }
        }
        else {
            return null;
        }

    }
}
