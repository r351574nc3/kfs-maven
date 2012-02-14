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
package org.kuali.kfs.module.cam.document.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetGlpeSourceDetail;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.gl.CamsGeneralLedgerPendingEntrySourceBase;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.document.service.AssetObjectCodeService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.module.cam.util.AssetSeparatePaymentDistributor;
import org.kuali.kfs.module.cam.util.KualiDecimalUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants.CAPITAL_ASSETS_BATCH;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class AssetGlobalServiceImpl implements AssetGlobalService {

    protected enum AmountCategory {

        PAYMENT {
            void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType) {
                postable.setPayment(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetGlobal.LINE_DESCRIPTION_PAYMENT);
                postable.setFinancialObjectCode(assetPaymentDetail.getFinancialObjectCode());
                postable.setObjectCode(assetPaymentDetail.getObjectCode());
            };

        },
        PAYMENT_OFFSET {
            void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType) {
                postable.setPaymentOffset(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetGlobal.LINE_DESCRIPTION_PAYMENT_OFFSET);
                postable.setFinancialObjectCode(acquisitionType.getIncomeAssetObjectCode());
                postable.setObjectCode(SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(assetPaymentDetail.getPostingYear(), assetPaymentDetail.getChartOfAccountsCode(), acquisitionType.getIncomeAssetObjectCode()));
            };

        };

        abstract void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType);
    }

    private ParameterService parameterService;
    private AssetService assetService;
    private UniversityDateService universityDateService;
    private AssetObjectCodeService assetObjectCodeService;
    private BusinessObjectService businessObjectService;
    private AssetPaymentService assetPaymentService;
    private PaymentSummaryService paymentSummaryService;

    private static final Logger LOG = Logger.getLogger(AssetGlobalServiceImpl.class);

    /**
     * Creates an instance of AssetGlpeSourceDetail depending on the source flag
     * 
     * @param universityDateService University Date Service
     * @param assetPayment Payment record for which postable is created
     * @param isSource Indicates if postable is for source organization
     * @return GL Postable source detail
     */
    protected AssetGlpeSourceDetail createAssetGlpePostable(AssetGlobal document, AssetPaymentDetail assetPaymentDetail, AmountCategory amountCategory) {
        LOG.debug("Start - createAssetGlpePostable (" + document.getDocumentNumber() + "-" + assetPaymentDetail.getAccountNumber() + ")");
        AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();

        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT);
        postable.setAccount(assetPaymentDetail.getAccount());

        postable.setAmount(assetPaymentDetail.getAmount());
        postable.setAccountNumber(assetPaymentDetail.getAccountNumber());
        postable.setBalanceTypeCode(CamsConstants.Postable.GL_BALANCE_TYPE_CODE_AC);
        postable.setChartOfAccountsCode(assetPaymentDetail.getChartOfAccountsCode());
        postable.setDocumentNumber(document.getDocumentNumber());
        postable.setFinancialSubObjectCode(assetPaymentDetail.getFinancialSubObjectCode());
        postable.setPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        postable.setProjectCode(assetPaymentDetail.getProjectCode());
        postable.setSubAccountNumber(assetPaymentDetail.getSubAccountNumber());
        postable.setOrganizationReferenceId(assetPaymentDetail.getOrganizationReferenceId());

        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);
        ObjectCodeService objectCodeService = (ObjectCodeService) SpringContext.getBean(ObjectCodeService.class);
        ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(assetPaymentDetail.getChartOfAccountsCode(), assetPaymentDetail.getFinancialObjectCode());

        AssetObjectCode assetObjectCode = getAssetObjectCodeService().findAssetObjectCode(assetPaymentDetail.getChartOfAccountsCode(), objectCode.getFinancialObjectSubTypeCode());

        OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getUniversityDateService().getCurrentFiscalYear(), assetPaymentDetail.getChartOfAccountsCode(), CamsConstants.AssetTransfer.DOCUMENT_TYPE_CODE, CamsConstants.Postable.GL_BALANCE_TYPE_CODE_AC);
        document.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE);
        amountCategory.setParams(postable, assetPaymentDetail, assetObjectCode, offsetDefinition, document.getAcquisitionType());

        LOG.debug("End - createAssetGlpePostable(" + document.getDocumentNumber() + "-" + assetPaymentDetail.getAccountNumber() + "-" + ")");
        return postable;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#createGLPostables(org.kuali.module.cams.document.AssetGlobal)
     */
    public void createGLPostables(AssetGlobal assetGlobal, CamsGeneralLedgerPendingEntrySourceBase assetGlobalGlPoster) {
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();

        for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
            if (isPaymentFinancialObjectActive(assetPaymentDetail)) {
                KualiDecimal accountChargeAmount = assetPaymentDetail.getAmount();
                if (accountChargeAmount != null && !accountChargeAmount.isZero()) {
                    assetGlobalGlPoster.getGeneralLedgerPendingEntrySourceDetails().add(createAssetGlpePostable(assetGlobal, assetPaymentDetail, AmountCategory.PAYMENT));
                    assetGlobalGlPoster.getGeneralLedgerPendingEntrySourceDetails().add(createAssetGlpePostable(assetGlobal, assetPaymentDetail, AmountCategory.PAYMENT_OFFSET));
                }
            }
        }
    }


    public ParameterService getParameterService() {
        return parameterService;
    }


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    /**
     * Gets the assetObjectCodeService attribute.
     * 
     * @return Returns the assetObjectCodeService.
     */
    public AssetObjectCodeService getAssetObjectCodeService() {
        return assetObjectCodeService;
    }


    /**
     * Sets the assetObjectCodeService attribute value.
     * 
     * @param assetObjectCodeService The assetObjectCodeService to set.
     */
    public void setAssetObjectCodeService(AssetObjectCodeService assetObjectCodeService) {
        this.assetObjectCodeService = assetObjectCodeService;
    }


    /**
     * Gets the assetPaymentService attribute.
     * 
     * @return Returns the assetPaymentService.
     */
    public AssetPaymentService getAssetPaymentService() {
        return assetPaymentService;
    }


    /**
     * Sets the assetPaymentService attribute value.
     * 
     * @param assetPaymentService The assetPaymentService to set.
     */
    public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
        this.assetPaymentService = assetPaymentService;
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
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
     * Gets the universityDateService attribute.
     * 
     * @return Returns the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }


    /**
     * Sets the universityDateService attribute value.
     * 
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * We need to calculate asset total amount from the sum of its payments. Otherwise, the asset total amount could mismatch with
     * the sum of payments.
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#totalPaymentByAsset(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public KualiDecimal totalPaymentByAsset(AssetGlobal assetGlobal, boolean lastEntry) {
        KualiDecimal assetTotalAmount = KualiDecimal.ZERO;
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();
        int numberOfTotalAsset = assetGlobal.getAssetGlobalDetails().size();
        if (numberOfTotalAsset > 0) {
            for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
                KualiDecimal assetPaymentUnitCost = assetPaymentDetail.getAmount().divide(new KualiDecimal(numberOfTotalAsset));
                if (lastEntry) {
                    assetPaymentUnitCost = assetPaymentDetail.getAmount().subtract(assetPaymentUnitCost.multiply(new KualiDecimal(numberOfTotalAsset - 1)));
                }
                assetTotalAmount = assetTotalAmount.add(assetPaymentUnitCost);
            }
        }

        return assetTotalAmount;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#existsInGroup(java.lang.String, java.lang.String)
     */
    public boolean existsInGroup(String groupName, String memberName) {
        if (StringUtils.isBlank(groupName) || StringUtils.isBlank(memberName)) {
            return false;
        }
        return Arrays.asList(groupName.split(";")).contains(memberName);
    }

    protected boolean isPaymentFinancialObjectActive(AssetPaymentDetail assetPayment) {
        ObjectCode financialObjectCode = new ObjectCode();
        financialObjectCode.setUniversityFiscalYear(getUniversityDateService().getCurrentFiscalYear());
        financialObjectCode.setChartOfAccountsCode(assetPayment.getChartOfAccountsCode());
        financialObjectCode.setFinancialObjectCode(assetPayment.getFinancialObjectCode());
        financialObjectCode = (ObjectCode) getBusinessObjectService().retrieve(financialObjectCode);
        if (ObjectUtils.isNotNull(financialObjectCode)) {
            return financialObjectCode.isActive();
        }
        return false;
    }

    /**
     * Gets the assetService attribute.
     * 
     * @return Returns the assetService.
     */
    public AssetService getAssetService() {
        return assetService;
    }

    /**
     * Sets the assetService attribute value.
     * 
     * @param assetService The assetService to set.
     */
    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#isAssetSeparate(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public boolean isAssetSeparate(AssetGlobal assetGlobal) {
        if (ObjectUtils.isNotNull(assetGlobal.getFinancialDocumentTypeCode()) && assetGlobal.getFinancialDocumentTypeCode().equals(CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE)) {
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#isAssetSeparateByPayment(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public boolean isAssetSeparateByPayment(AssetGlobal assetGlobal) {
        if (this.isAssetSeparate(assetGlobal) && ObjectUtils.isNotNull(assetGlobal.getSeparateSourcePaymentSequenceNumber())) {
            return true;
        }
        return false;
    }

    /**
     * Add and return the total amount for all unique assets created.
     * 
     * @param assetGlobal
     * @return Returns the total separate source amount
     */
    public KualiDecimal getUniqueAssetsTotalAmount(AssetGlobal assetGlobal) {
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        // add new asset location
        for (AssetGlobalDetail assetSharedDetail : assetGlobal.getAssetSharedDetails()) {
            // existing
            for (AssetGlobalDetail assetGlobalUniqueDetail : assetSharedDetail.getAssetGlobalUniqueDetails()) {
                KualiDecimal separateSourceAmount = assetGlobalUniqueDetail.getSeparateSourceAmount();
                if (separateSourceAmount != null) {
                    totalAmount = totalAmount.add(separateSourceAmount);
                }
            }
        }
        return totalAmount;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#getCreateNewAssets(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public List<PersistableBusinessObject> getCreateNewAssets(AssetGlobal assetGlobal) {
        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();

        // create new assets with inner loop handling payments
        Iterator<AssetGlobalDetail> assetGlobalDetailsIterator = assetGlobal.getAssetGlobalDetails().iterator();
        for (int assetGlobalDetailsIndex = 0; assetGlobalDetailsIterator.hasNext(); assetGlobalDetailsIndex++) {
            AssetGlobalDetail assetGlobalDetail = (AssetGlobalDetail) assetGlobalDetailsIterator.next();

            // Create the asset with most fields set as required
            Asset asset = setupAsset(assetGlobal, assetGlobalDetail, false);

            // track total cost of all payments for this assetGlobalDetail
            KualiDecimal paymentsAccountChargeAmount = new KualiDecimal(0);

            // take care of all the payments for this asset
            for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
                AssetPayment assetPayment = setupCreateNewAssetPayment(assetGlobalDetail.getCapitalAssetNumber(), assetGlobal.getAcquisitionTypeCode(), assetGlobal.getAssetGlobalDetails().size(), assetGlobalDetailsIndex, assetPaymentDetail);

                paymentsAccountChargeAmount = paymentsAccountChargeAmount.add(assetPayment.getAccountChargeAmount());

                asset.getAssetPayments().add(assetPayment);
            }

            // set the amount generically. Note for separate this should equal assetGlobalDetail.getSeparateSourceAmount()
            asset.setTotalCostAmount(paymentsAccountChargeAmount);

            persistables.add(asset);
        }

        return persistables;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#getSeparateAssets(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public List<PersistableBusinessObject> getSeparateAssets(AssetGlobal assetGlobal) {

        // set the source asset amounts properly
        Asset separateSourceCapitalAsset = assetGlobal.getSeparateSourceCapitalAsset();
        List<AssetPayment> sourcePayments = new ArrayList<AssetPayment>();

        for (AssetPayment assetPayment : separateSourceCapitalAsset.getAssetPayments()) {
            if (!this.isAssetSeparateByPayment(assetGlobal)) {
                sourcePayments.add(assetPayment);
            }
            else if (assetPayment.getPaymentSequenceNumber().equals(assetGlobal.getSeparateSourcePaymentSequenceNumber())) {
                // If this is separate by payment, then only add the payment that we are interested in
                sourcePayments.add(assetPayment);
                break;
            }
        }

        List<Asset> newAssets = new ArrayList<Asset>();
        // create new assets with inner loop handling payments
        for (AssetGlobalDetail assetGlobalDetail : assetGlobal.getAssetGlobalDetails()) {
            newAssets.add(setupAsset(assetGlobal, assetGlobalDetail, true));
        }
        // adjust source asset amounts
        KualiDecimalUtils kualiDecimalUtils = new KualiDecimalUtils();
        double separateRatio = 1 - (assetGlobal.getSeparateSourceTotalAmount().doubleValue() / assetGlobal.getSeparateSourceCapitalAsset().getTotalCostAmount().doubleValue());
        separateSourceCapitalAsset.setSalvageAmount(kualiDecimalUtils.safeMultiply(assetGlobal.getSeparateSourceCapitalAsset().getSalvageAmount(), separateRatio));
        separateSourceCapitalAsset.setReplacementAmount(kualiDecimalUtils.safeMultiply(assetGlobal.getSeparateSourceCapitalAsset().getReplacementAmount(), separateRatio));
        separateSourceCapitalAsset.setFabricationEstimatedTotalAmount(kualiDecimalUtils.safeMultiply(assetGlobal.getSeparateSourceCapitalAsset().getFabricationEstimatedTotalAmount(), separateRatio));

        Integer maxSequenceNumber = SpringContext.getBean(AssetPaymentService.class).getMaxSequenceNumber(separateSourceCapitalAsset.getCapitalAssetNumber());
        // Add to the save list
        AssetSeparatePaymentDistributor distributor = new AssetSeparatePaymentDistributor(separateSourceCapitalAsset, sourcePayments, maxSequenceNumber, assetGlobal, newAssets);
        distributor.distribute();
        // re-compute the source total cost amount after split
        separateSourceCapitalAsset.setTotalCostAmount(getPaymentSummaryService().calculatePaymentTotalCost(separateSourceCapitalAsset));
        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();
        persistables.add(separateSourceCapitalAsset);
        persistables.addAll(newAssets);
        return persistables;
    }

    /**
     * Creates a new Asset appropriate for either create new or separate. This does not create the payments for this asset.
     * 
     * @param assetGlobal containing data for the asset to be created
     * @param assetGlobalDetail containing data for the asset to be created
     * @param separate indicating whether this is a separate and asset or not
     * @return set of assets appropriate for this creation without payments
     */
    protected Asset setupAsset(AssetGlobal assetGlobal, AssetGlobalDetail assetGlobalDetail, boolean separate) {
        Asset asset = new Asset(assetGlobal, assetGlobalDetail, separate);
        KualiDecimalUtils kualiDecimalUtils = new KualiDecimalUtils();

        // set financialObjectSubTypeCode per first payment entry if one exists
        if (!assetGlobal.getAssetPaymentDetails().isEmpty() && ObjectUtils.isNotNull(assetGlobal.getAssetPaymentDetails().get(0).getObjectCode())) {
            ObjectCodeService objectCodeService = (ObjectCodeService) SpringContext.getBean(ObjectCodeService.class);
            AssetPaymentDetail assetPaymentDetail = assetGlobal.getAssetPaymentDetails().get(0);
            
            ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(assetPaymentDetail.getChartOfAccountsCode(), assetPaymentDetail.getFinancialObjectCode());

            asset.setFinancialObjectSubTypeCode(objectCode.getFinancialObjectSubTypeCode());
        }

        // create off campus location for each asset only if it was filled in
        boolean offCampus = StringUtils.isNotBlank(assetGlobalDetail.getOffCampusName()) || StringUtils.isNotBlank(assetGlobalDetail.getOffCampusAddress()) || StringUtils.isNotBlank(assetGlobalDetail.getOffCampusCityName()) || StringUtils.isNotBlank(assetGlobalDetail.getOffCampusStateCode()) || StringUtils.isNotBlank(assetGlobalDetail.getOffCampusZipCode()) || StringUtils.isNotBlank(assetGlobalDetail.getOffCampusCountryCode());
        if (offCampus) {
            setupAssetLocationOffCampus(assetGlobalDetail, asset);
        }

        // set specific values for new assets if document is Asset Separate
        if (separate) {
            double separateRatio = assetGlobalDetail.getSeparateSourceAmount().doubleValue() / assetGlobal.getSeparateSourceCapitalAsset().getTotalCostAmount().doubleValue();
            asset.setSalvageAmount(kualiDecimalUtils.safeMultiply(assetGlobal.getSeparateSourceCapitalAsset().getSalvageAmount(), separateRatio));
            asset.setReplacementAmount(kualiDecimalUtils.safeMultiply(assetGlobal.getSeparateSourceCapitalAsset().getReplacementAmount(), separateRatio));
            asset.setFabricationEstimatedTotalAmount(kualiDecimalUtils.safeMultiply(assetGlobal.getSeparateSourceCapitalAsset().getFabricationEstimatedTotalAmount(), separateRatio));
            Date lastInventoryDate = assetGlobal.getLastInventoryDate();
            if (lastInventoryDate != null) {
                asset.setLastInventoryDate(new Timestamp(lastInventoryDate.getTime()));
            }
        }

        return asset;
    }

    /**
     * Off campus location appropriately set for this asset.
     * 
     * @param assetGlobalDetail containing data for the location
     * @param asset object that location is set in
     */
    protected void setupAssetLocationOffCampus(AssetGlobalDetail assetGlobalDetail, Asset asset) {
        // We are not checking if it already exists since on a new asset it can't
        AssetLocation offCampusAssetLocation = new AssetLocation();
        offCampusAssetLocation.setCapitalAssetNumber(asset.getCapitalAssetNumber());
        offCampusAssetLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.OFF_CAMPUS);
        asset.getAssetLocations().add(offCampusAssetLocation);

        // Set the location fields either way
        offCampusAssetLocation.setAssetLocationContactName(assetGlobalDetail.getOffCampusName());
        offCampusAssetLocation.setAssetLocationContactIdentifier(assetGlobalDetail.getRepresentativeUniversalIdentifier());
        offCampusAssetLocation.setAssetLocationInstitutionName(assetGlobalDetail.getAssetRepresentative().getPrimaryDepartmentCode());
        offCampusAssetLocation.setAssetLocationStreetAddress(assetGlobalDetail.getOffCampusAddress());
        offCampusAssetLocation.setAssetLocationCityName(assetGlobalDetail.getOffCampusCityName());
        offCampusAssetLocation.setAssetLocationStateCode(assetGlobalDetail.getOffCampusStateCode());
        offCampusAssetLocation.setAssetLocationCountryCode(assetGlobalDetail.getOffCampusCountryCode());
        offCampusAssetLocation.setAssetLocationZipCode(assetGlobalDetail.getOffCampusZipCode());

        // There is no phone number field on Asset Global... odd...
        offCampusAssetLocation.setAssetLocationPhoneNumber(null);
    }

    /**
     * Creates a payment for an asset in create new mode.
     * 
     * @param capitalAssetNumber to use for the payment
     * @param acquisitionTypeCode for logic in determining how dates are to be set
     * @param assetGlobalDetailsSize for logic in determining depreciation amounts (if asset is depreciable)
     * @param assetGlobalDetailsIndex for logic in determining depreciation amounts (if asset is depreciable)
     * @param assetPaymentDetail containing data for the payment
     * @return payment for an asset in create new
     */
    protected AssetPayment setupCreateNewAssetPayment(Long capitalAssetNumber, String acquisitionTypeCode, int assetGlobalDetailsSize, int assetGlobalDetailsIndex, AssetPaymentDetail assetPaymentDetail) {
        AssetPayment assetPayment = new AssetPayment(assetPaymentDetail, acquisitionTypeCode);
        assetPayment.setCapitalAssetNumber(capitalAssetNumber);
        assetPayment.setPaymentSequenceNumber(assetPaymentDetail.getSequenceNumber());
        assetPayment.setTransferPaymentCode(CamsConstants.AssetPayment.TRANSFER_PAYMENT_CODE_N);
        // Running this every time of the loop is inefficient, could be put into HashMap
        KualiDecimalUtils kualiDecimalService = new KualiDecimalUtils(assetPaymentDetail.getAmount(), CamsConstants.CURRENCY_USD);
        KualiDecimal[] amountBuckets = kualiDecimalService.allocateByQuantity(assetGlobalDetailsSize);

        assetPayment.setAccountChargeAmount(amountBuckets[assetGlobalDetailsIndex]);
        ObjectCodeService objectCodeService = (ObjectCodeService) SpringContext.getBean(ObjectCodeService.class);
        ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(assetPayment.getChartOfAccountsCode(), assetPayment.getFinancialObjectCode());

        boolean isDepreciablePayment = ObjectUtils.isNotNull(assetPaymentDetail.getObjectCode()) && !Arrays.asList(parameterService.getParameterValue(CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES).split(";")).contains(objectCode.getFinancialObjectSubTypeCode());
        if (isDepreciablePayment) {
            assetPayment.setPrimaryDepreciationBaseAmount(amountBuckets[assetGlobalDetailsIndex]);
        }
        else {
            assetPayment.setPrimaryDepreciationBaseAmount(KualiDecimal.ZERO);
        }

        return assetPayment;
    }


    /**
     * Gets the paymentSummaryService attribute.
     * 
     * @return Returns the paymentSummaryService.
     */
    public PaymentSummaryService getPaymentSummaryService() {
        return paymentSummaryService;
    }

    /**
     * Sets the paymentSummaryService attribute value.
     * 
     * @param paymentSummaryService The paymentSummaryService to set.
     */
    public void setPaymentSummaryService(PaymentSummaryService paymentSummaryService) {
        this.paymentSummaryService = paymentSummaryService;
    }
    
    /**
     * @return the parameter value for the new acquisition type code
     */
    public String getNewAcquisitionTypeCode() {
        return getParameterService().getParameterValue(AssetGlobal.class, 
                CamsConstants.AssetGlobal.NEW_ACQUISITION_CODE_PARAM); 
    }
    /**
     * @return the parameter value for the capital object acquisition code group
     */
    public String getCapitalObjectAcquisitionCodeGroup() {
        return getParameterService().getParameterValue(AssetGlobal.class, 
                CamsConstants.AssetGlobal.CAPITAL_OBJECT_ACQUISITION_CODE_PARAM);  
    }
    /**
     * @return the parameter value for the not new acquisition code group 
     */
    public String getNonNewAcquisitionCodeGroup() {
        return getParameterService().getParameterValue(AssetGlobal.class, 
                CamsConstants.AssetGlobal.NON_NEW_ACQUISITION_GROUP_PARAM);         
    }


}
