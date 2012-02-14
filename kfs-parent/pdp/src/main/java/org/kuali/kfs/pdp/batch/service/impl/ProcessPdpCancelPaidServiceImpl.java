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
package org.kuali.kfs.pdp.batch.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.batch.service.DisbursementVoucherExtractService;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.batch.service.ProcessPdpCancelPaidService;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.service.PaymentDetailService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ProcessPdpCancelPaidService
 */
@Transactional
public class ProcessPdpCancelPaidServiceImpl implements ProcessPdpCancelPaidService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessPdpCancelPaidServiceImpl.class);

    private PaymentGroupService paymentGroupService;
    private PaymentDetailService paymentDetailService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private PurchasingAccountsPayableModuleService purchasingAccountsPayableModuleService;
    private DisbursementVoucherExtractService dvExtractService;

    /**
     * @see org.kuali.kfs.module.purap.service.ProcessPdpCancelPaidService#processPdpCancels()
     */
    public void processPdpCancels() {
        LOG.debug("processPdpCancels() started");

        Date processDate = dateTimeService.getCurrentSqlDate();

        String organization = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_ORG_CODE);
        String purapSubUnit = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_SUB_UNIT_CODE);
        String dvSubUnit = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DvPdpExtractGroup.DV_PDP_SBUNT_CODE);

        List<String> subUnits = new ArrayList<String>();
        subUnits.add(purapSubUnit);
        subUnits.add(dvSubUnit);

        Iterator<PaymentDetail> details = paymentDetailService.getUnprocessedCancelledDetails(organization, subUnits);
        while (details.hasNext()) {
            PaymentDetail paymentDetail = details.next();

            String documentTypeCode = paymentDetail.getFinancialDocumentTypeCode();
            String documentNumber = paymentDetail.getCustPaymentDocNbr();

            boolean primaryCancel = paymentDetail.getPrimaryCancelledPayment();
            boolean disbursedPayment = PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT.equals(paymentDetail.getPaymentGroup().getPaymentStatusCode());

            if(purchasingAccountsPayableModuleService.isPurchasingBatchDocument(documentTypeCode)) {
                purchasingAccountsPayableModuleService.handlePurchasingBatchCancels(documentNumber, documentTypeCode, primaryCancel, disbursedPayment);
            }
            else if (DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH.equals(documentTypeCode)) {
                DisbursementVoucherDocument dv = dvExtractService.getDocumentById(documentNumber);
                if (dv != null) {
                    if (disbursedPayment || primaryCancel) {
                        dvExtractService.cancelExtractedDisbursementVoucher(dv, processDate);
                    } else {
                        dvExtractService.resetExtractedDisbursementVoucher(dv, processDate);
                    }
                }
            }
            else {
                LOG.warn("processPdpCancels() Unknown document type (" + documentTypeCode + ") for document ID: " + documentNumber);
                continue;
            }

            paymentGroupService.processCancelledGroup(paymentDetail.getPaymentGroup(), processDate);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.service.ProcessPdpCancelPaidService#processPdpPaids()
     */
    public void processPdpPaids() {
        LOG.debug("processPdpPaids() started");

        Date processDate = dateTimeService.getCurrentSqlDate();

        String organization = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_ORG_CODE);
        String purapSubUnit = parameterService.getParameterValue(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_SUB_UNIT_CODE);
        String dvSubUnit = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DvPdpExtractGroup.DV_PDP_SBUNT_CODE);

        List<String> subUnits = new ArrayList<String>();
        subUnits.add(purapSubUnit);
        subUnits.add(dvSubUnit);

        Iterator<PaymentDetail> details = paymentDetailService.getUnprocessedPaidDetails(organization, subUnits);
        while (details.hasNext()) {
            PaymentDetail paymentDetail = details.next();

            String documentTypeCode = paymentDetail.getFinancialDocumentTypeCode();
            String documentNumber = paymentDetail.getCustPaymentDocNbr();

            if(purchasingAccountsPayableModuleService.isPurchasingBatchDocument(documentTypeCode)) {
                purchasingAccountsPayableModuleService.handlePurchasingBatchPaids(documentNumber, documentTypeCode, processDate);
            }
            else if (documentTypeCode.equals(DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH)) {
                DisbursementVoucherDocument dv = dvExtractService.getDocumentById(documentNumber);
                dvExtractService.markDisbursementVoucherAsPaid(dv, processDate);
            }
            else {
                LOG.warn("processPdpPaids() Unknown document type (" + documentTypeCode + ") for document ID: " + documentNumber);
                continue;
            }

            paymentGroupService.processPaidGroup(paymentDetail.getPaymentGroup(), processDate);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.service.ProcessPdpCancelPaidService#processPdpCancelsAndPaids()
     */
    public void processPdpCancelsAndPaids() {
        LOG.debug("processPdpCancelsAndPaids() started");

        processPdpCancels();
        processPdpPaids();
    }

    public void setPaymentDetailService(PaymentDetailService paymentDetailService) {
        this.paymentDetailService = paymentDetailService;
    }

    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setPurchasingAccountsPayableModuleService(PurchasingAccountsPayableModuleService purchasingAccountsPayableModuleService) {
        this.purchasingAccountsPayableModuleService = purchasingAccountsPayableModuleService;
    }

    public void setDateTimeService(DateTimeService dts) {
        this.dateTimeService = dts;
    }

    /**
     * Sets the dvExtractService attribute value.
     * @param dvExtractService The dvExtractService to set.
     */
    public void setDvExtractService(DisbursementVoucherExtractService dvExtractService) {
        this.dvExtractService = dvExtractService;
    }
    
}
