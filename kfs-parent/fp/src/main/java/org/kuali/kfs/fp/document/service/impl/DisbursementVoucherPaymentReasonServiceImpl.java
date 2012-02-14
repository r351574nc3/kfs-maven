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
package org.kuali.kfs.fp.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.businessobject.PaymentReasonCode;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.MessageList;

/**
 * implementing the service methods defined in DisbursementVoucherPaymentReasonService
 * 
 * @see DisbursementVoucherPaymentReasonService
 */
public class DisbursementVoucherPaymentReasonServiceImpl implements DisbursementVoucherPaymentReasonService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherPaymentReasonServiceImpl.class);

    public ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private DisbursementVoucherPayeeService disbursementVoucherPayeeService;

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isPayeeQualifiedForPayment(org.kuali.kfs.fp.businessobject.DisbursementPayee,
     *      java.lang.String)
     */
    public boolean isPayeeQualifiedForPayment(DisbursementPayee payee, String paymentReasonCode) {
        List<String> payeeTypeCodes = this.getPayeeTypesByPaymentReason(paymentReasonCode);
        return this.isPayeeQualifiedForPayment(payee, paymentReasonCode, payeeTypeCodes);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isPayeeQualifiedForPayment(org.kuali.kfs.fp.businessobject.DisbursementPayee,
     *      java.lang.String, java.util.List)
     */
    public boolean isPayeeQualifiedForPayment(DisbursementPayee payee, String paymentReasonCode, List<String> payeeTypeCodes) {
        if (payeeTypeCodes == null || payeeTypeCodes.isEmpty()) {
            return false;
        }

        String payeeTypeCode = payee.getPayeeTypeCode();
        if (!payeeTypeCodes.contains(payeeTypeCode)) {
            return false;
        }
        
        if (disbursementVoucherPayeeService.isVendor(payee)) {
            List<String> vendorOwnershipTypeCodes = this.getVendorOwnershipTypesByPaymentReason(paymentReasonCode);
            
            if (vendorOwnershipTypeCodes != null && !vendorOwnershipTypeCodes.isEmpty()) {                
                String vendorOwnershipTypeCodeOfPayee = disbursementVoucherPayeeService.getVendorOwnershipTypeCode(payee);
                return vendorOwnershipTypeCodes.contains(vendorOwnershipTypeCodeOfPayee);
            } 
        }

        if (this.isPrepaidTravelPaymentReason(paymentReasonCode)) {
            boolean isActiveVendorEmployee = payee.isActive();
            isActiveVendorEmployee &= disbursementVoucherPayeeService.isVendor(payee);
            isActiveVendorEmployee &= disbursementVoucherPayeeService.isEmployee(payee);

            // Active vendor employees cannot be paid for prepaid travel
            return !isActiveVendorEmployee;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isMovingPaymentReason(java.lang.String)
     */
    public boolean isMovingPaymentReason(String paymentReasonCode) {
        String typeParameterName = DisbursementVoucherConstants.MOVING_PAYMENT_REASONS_PARM_NM;
        return this.isPaymentReasonOfType(typeParameterName, paymentReasonCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isPrepaidTravelPaymentReason(java.lang.String)
     */
    public boolean isPrepaidTravelPaymentReason(String paymentReasonCode) {
        String typeParameterName = DisbursementVoucherConstants.PREPAID_TRAVEL_PAYMENT_REASONS_PARM_NM;
        return this.isPaymentReasonOfType(typeParameterName, paymentReasonCode);
    }
    
    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isNonEmployeeTravelPaymentReason(java.lang.String)
     */
    public boolean isNonEmployeeTravelPaymentReason(String paymentReasonCode) {
        String typeParameterName = DisbursementVoucherConstants.NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM;
        return this.isPaymentReasonOfType(typeParameterName, paymentReasonCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isResearchPaymentReason(java.lang.String)
     */
    public boolean isResearchPaymentReason(String paymentReasonCode) {
        String typeParameterName = DisbursementVoucherConstants.RESEARCH_PAYMENT_REASONS_PARM_NM;
        return this.isPaymentReasonOfType(typeParameterName, paymentReasonCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isRevolvingFundPaymentReason(java.lang.String)
     */
    public boolean isRevolvingFundPaymentReason(String paymentReasonCode) {
        String typeParameterName = DisbursementVoucherConstants.REVOLVING_FUND_PAYMENT_REASONS_PARM_NM;
        return this.isPaymentReasonOfType(typeParameterName, paymentReasonCode);
    }
    
    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isDecedentCompensationPaymentReason(java.lang.String)
     */
    public boolean isDecedentCompensationPaymentReason(String paymentReasonCode) {
        String typeParameterName = DisbursementVoucherConstants.DECEDENT_COMPENSATION_PAYMENT_REASONS_PARM_NM;
        return this.isPaymentReasonOfType(typeParameterName, paymentReasonCode);
    }    

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isPaymentReasonOfType(java.lang.String,
     *      java.lang.String)
     */
    public boolean isPaymentReasonOfType(String typeParameterName, String paymentReasonCode) {
        return parameterService.getParameterEvaluator(DisbursementVoucherDocument.class, typeParameterName, paymentReasonCode).evaluationSucceeds();
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#getReserchNonVendorPayLimit()
     */
    public String getReserchNonVendorPayLimit() {
        return parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT_PARM_NM);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#getPayeeTypesByPaymentReason(java.lang.String)
     */
    public List<String> getPayeeTypesByPaymentReason(String paymentReasonCode) {
        return parameterService.getParameterValues(DisbursementVoucherDocument.class, DisbursementVoucherConstants.VALID_PAYEE_TYPES_BY_PAYMENT_REASON_PARM, paymentReasonCode);
    }
    
    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#getVendorOwnershipTypesByPaymentReason(java.lang.String)
     */
    public List<String> getVendorOwnershipTypesByPaymentReason(String paymentReasonCode) {
        return parameterService.getParameterValues(DisbursementVoucherDocument.class, DisbursementVoucherConstants.VALID_VENDOR_OWNERSHIP_TYPES_BY_PAYMENT_REASON, paymentReasonCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#getPaymentReasonByPrimaryId(java.lang.String)
     */
    public PaymentReasonCode getPaymentReasonByPrimaryId(String paymentReasonCode) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.CODE, paymentReasonCode);

        return (PaymentReasonCode) businessObjectService.findByPrimaryKey(PaymentReasonCode.class, primaryKeys);
    }
    
    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isTaxReviewRequired(java.lang.String)
     */
    public boolean isTaxReviewRequired(String paymentReasonCode) {
        String parameterName = DisbursementVoucherConstants.PAYMENT_REASONS_REQUIRING_TAX_REVIEW_PARM_NM;
        List<String> values = parameterService.getParameterValues(DisbursementVoucherDocument.class, parameterName);
        
        return values != null && values.contains(paymentReasonCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#postPaymentReasonCodeUsage(java.lang.String,
     *      org.kuali.rice.kns.util.MessageList)
     */
    public void postPaymentReasonCodeUsage(String paymentReasonCode, MessageList messageList) {
        List<String> payeeTypeCodes = this.getPayeeTypesByPaymentReason(paymentReasonCode);
        if (payeeTypeCodes == null || payeeTypeCodes.isEmpty()) {
            return;
        }

        String descriptivePayeeTypes = this.getDescriptivePayeeTypesAsString(payeeTypeCodes);
        String descriptivePaymentReason = this.getPaymentReasonByPrimaryId(paymentReasonCode).getCodeAndDescription();
        if (payeeTypeCodes.size() > 1) {
            String messageKey = KFSKeyConstants.WARNING_DV_PAYMENT_REASON_VALID_FOR_MULTIPLE_PAYEE_TYPES;
            messageList.add(messageKey, descriptivePaymentReason, descriptivePayeeTypes);
        }
        else if (payeeTypeCodes.size() == 1) {
            String messageKey = KFSKeyConstants.WARNING_DV_PAYMENT_REASON_VALID_FOR_SINGEL_PAYEE_TYPE;
            messageList.add(messageKey, descriptivePaymentReason, descriptivePayeeTypes);
        }

        if (this.isResearchPaymentReason(paymentReasonCode)) {
            String payLimit = this.getReserchNonVendorPayLimit();
            String messageKey = KFSKeyConstants.WARNING_DV_REASERCH_PAYMENT_REASON;

            List<String> vendorTypeCodes = new ArrayList<String>();
            vendorTypeCodes.addAll(payeeTypeCodes);
            vendorTypeCodes.remove(DisbursementVoucherConstants.DV_PAYEE_TYPE_EMPLOYEE);
            String vendorTypes = this.getDescriptivePayeeTypesAsString(vendorTypeCodes);

            messageList.add(messageKey, descriptivePaymentReason, descriptivePayeeTypes, vendorTypes, payLimit);
        }

        if (this.isMovingPaymentReason(paymentReasonCode)) {
            List<String> individualOwnerShipTypeCodes = parameterService.getParameterValues(DisbursementVoucherDocument.class, DisbursementVoucherConstants.INDIVIDUAL_OWNERSHIP_TYPES_PARM_NM);
            String ownerShipTypeAsString = this.convertListToString(individualOwnerShipTypeCodes);

            String messageKey = KFSKeyConstants.WARNING_DV_MOVING_PAYMENT_REASON;
            messageList.add(messageKey, ownerShipTypeAsString);
        }

        if (this.isPrepaidTravelPaymentReason(paymentReasonCode)) {
            String messageKey = KFSKeyConstants.WARNING_DV_PREPAID_TRAVEL_PAYMENT_REASON;
            messageList.add(messageKey, descriptivePaymentReason, descriptivePayeeTypes);
        }
    }

    // get and concatenate the descriptive payee types of the given codes
    protected String getDescriptivePayeeTypesAsString(List<String> payeeTypeCodes) {
        List<String> payeeTypeDescriptions = new ArrayList<String>();

        for (String payeeTypeCode : payeeTypeCodes) {
            String description = SpringContext.getBean(DisbursementVoucherPayeeService.class).getPayeeTypeDescription(payeeTypeCode);
            payeeTypeDescriptions.add(description);
        }

        return this.convertListToString(payeeTypeDescriptions);
    }

    protected String convertListToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return StringUtils.EMPTY;
        }

        String oneSpace = " ";
        StringBuilder listAsString = new StringBuilder();
        for (int index = 0; index < list.size(); index++) {
            String emlement = list.get(index);

            if (index == 0) {
                listAsString.append(emlement);
            }
            else if (index < list.size() - 1) {
                listAsString.append(KFSConstants.COMMA).append(oneSpace).append(emlement);
            }
            else if (index == list.size() - 1) {
                listAsString.append(oneSpace).append(KFSConstants.AND).append(oneSpace).append(emlement);
            }
        }

        return listAsString.toString();
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the disbursementVoucherPayeeService attribute value.
     * 
     * @param disbursementVoucherPayeeService The disbursementVoucherPayeeService to set.
     */
    public void setDisbursementVoucherPayeeService(DisbursementVoucherPayeeService disbursementVoucherPayeeService) {
        this.disbursementVoucherPayeeService = disbursementVoucherPayeeService;
    }
}
