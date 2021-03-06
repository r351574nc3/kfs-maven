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
package org.kuali.kfs.module.cam.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectSubType;
import org.kuali.kfs.integration.cam.CapitalAssetManagementAsset;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgency;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Asset extends PersistableBusinessObjectBase implements CapitalAssetManagementAsset {
    private Long capitalAssetNumber;
    private String capitalAssetDescription;
    private String capitalAssetTypeCode;
    private String conditionCode;
    private Date createDate;
    private Date receiveDate;
    private Date loanReturnDate;
    private Date loanDate;
    private Date expectedReturnDate;
    private String financialDocumentPostingPeriodCode;
    private Integer financialDocumentPostingYear;
    private String organizationOwnerAccountNumber;
    private String organizationOwnerChartOfAccountsCode;
    private String vendorName;
    private String acquisitionTypeCode;
    private KualiDecimal totalCostAmount;
    private KualiDecimal replacementAmount;
    private KualiDecimal salePrice;
    private KualiDecimal estimatedSellingPrice;
    private KualiDecimal salvageAmount;
    private String campusCode;
    private String buildingCode;
    private String buildingRoomNumber;
    private String buildingSubRoomNumber;
    private String retirementChartOfAccountsCode;
    private String retirementAccountNumber;
    private String retirementReasonCode;
    private String retirementPeriodCode;
    private Integer retirementFiscalYear;
    private String cashReceiptFinancialDocumentNumber;
    private String primaryDepreciationMethodCode;
    private Date estimatedFabricationCompletionDate;
    private KualiDecimal fabricationEstimatedTotalAmount;
    private String transferOfFundsFinancialDocumentNumber;
    private String inventoryStatusCode;
    private String campusTagNumber;
    private Timestamp lastInventoryDate;
    private String organizationInventoryName;
    private String oldTagNumber;
    private String manufacturerName;
    private String manufacturerModelNumber;
    private String serialNumber;
    private String representativeUniversalIdentifier;
    private String agencyNumber;
    private String campusPoliceDepartmentCaseNumber;
    private String inventoryScannedCode;
    private boolean active;
    private Date capitalAssetInServiceDate;
    private String governmentTagNumber;
    private String nationalStockNumber;
    private String landCountyName;
    private Integer landAcreageSize;
    private String landParcelNumber;
    private Date depreciationDate;
    private String financialObjectSubTypeCode;
    private Integer fabricationEstimatedRetentionYears;

    private AssetType capitalAssetType;
    private Account organizationOwnerAccount;
    private Chart organizationOwnerChartOfAccounts;
    private Campus campus;
    private Room buildingRoom;
    private Account retirementAccount;
    private Chart retirementChartOfAccounts;
    private AccountingPeriod financialDocumentPostingPeriod;
    private Building building;
    private AccountingPeriod retirementPeriod;
    private AssetRetirementReason retirementReason;
    private DocumentHeader cashReceiptFinancialDocument;
    private DocumentHeader transferOfFundsFinancialDocument;
    private AssetCondition condition;
    private AssetStatus inventoryStatus;
    private List<AssetPayment> assetPayments;
    private Person assetRepresentative;
    private Person borrowerPerson;
    private AssetOrganization assetOrganization;
    private String organizationTagNumber;
    private List<AssetRepairHistory> assetRepairHistory;
    private AssetWarranty assetWarranty;
    private List<AssetComponent> assetComponents;
    private List<AssetLocation> assetLocations;
    private List<AssetRetirementGlobalDetail> assetRetirementHistory;
    private AssetDepreciationMethod assetPrimaryDepreciationMethod;
    private List<AssetRetirementGlobal> retirementGlobals;
    private ObjectSubType financialObjectSubType;
    private AssetAcquisitionType acquisitionType;
    private ContractsAndGrantsAgency agency;


    // Non-persisted attributes:
    private KualiDecimal paymentTotalCost;
    private transient AssetGlobal separateHistory;
    private List<AssetRetirementGlobalDetail> mergeHistory;
    private KualiDecimal federalContribution;
    private AssetRetirementGlobalDetail retirementInfo;
    private AssetLocation offCampusLocation;
    private AssetLocation borrowerLocation;
    private AssetLocation borrowerStorageLocation;
    // calculated depreciation amounts
    private KualiDecimal accumulatedDepreciation;
    private KualiDecimal baseAmount;
    private KualiDecimal bookValue;
    private KualiDecimal prevYearDepreciation;
    private KualiDecimal yearToDateDepreciation;
    private KualiDecimal currentMonthDepreciation;
    private Date depreciationDateCopy;
    private transient Integer quantity;
    private String lookup;
    private String assetTransferDocumentLookup;
    private String assetMaintenanceDocumentLookup;
    private String assetFabricationDocumentLookup;
    private String assetCreateOrSeparateDocumentLookup;
    private String assetPaymentDocumentLookup;
    private String assetEquipmentLoanOrReturnDocumentLookup;
    private String assetLocationDocumentLookup;
    private String assetMergeOrRetirementDocumentLookup;
    private String camsComplexMaintenanceDocumentLookup;
    private boolean tagged;

    /**
     * Default constructor.
     */
    public Asset() {
        this.assetPayments = new TypedArrayList(AssetPayment.class);
        this.assetRepairHistory = new TypedArrayList(AssetRepairHistory.class);
        this.assetComponents = new TypedArrayList(AssetComponent.class);
        this.assetLocations = new TypedArrayList(AssetLocation.class);
        this.assetRetirementHistory = new TypedArrayList(AssetRetirementGlobalDetail.class);
        this.retirementGlobals = new TypedArrayList(AssetRetirementGlobal.class);
        this.mergeHistory = new TypedArrayList(AssetRetirementGlobalDetail.class);
    }

    /**
     * Constructs a Asset object. Includes logic to properly set fields depending if it's creating a new asset or separating an
     * asset
     * 
     * @param assetGlobal
     * @param assetGlobalDetail
     * @param separate if it's seprate an asset
     */
    public Asset(AssetGlobal assetGlobal, AssetGlobalDetail assetGlobalDetail, boolean separate) {
        this();

        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);

        this.setFinancialDocumentPostingYear(universityDateService.getCurrentUniversityDate().getUniversityFiscalYear());
        this.setFinancialDocumentPostingPeriodCode(universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
        this.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));

        this.setPrimaryDepreciationMethodCode(CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE);

        this.setInventoryStatusCode(assetGlobal.getInventoryStatusCode());
        this.setConditionCode(assetGlobal.getConditionCode());
        this.setAcquisitionTypeCode(assetGlobal.getAcquisitionTypeCode());
        this.setLandCountyName(assetGlobal.getLandCountyName());
        this.setLandAcreageSize(assetGlobal.getLandAcreageSize());
        this.setLandParcelNumber(assetGlobal.getLandParcelNumber());
        this.setVendorName(assetGlobal.getVendorName());
        this.setOrganizationOwnerAccountNumber(assetGlobal.getOrganizationOwnerAccountNumber());
        this.setOrganizationOwnerChartOfAccountsCode(assetGlobal.getOrganizationOwnerChartOfAccountsCode());
        this.setAgencyNumber(assetGlobal.getAgencyNumber());
        this.setCapitalAssetInServiceDate(assetGlobal.getCapitalAssetInServiceDate());
        this.setDepreciationDate(assetGlobal.getCapitalAssetDepreciationDate());
        this.setCreateDate(assetGlobal.getCreateDate());

        this.setCapitalAssetNumber(assetGlobalDetail.getCapitalAssetNumber());
        this.setCampusCode(assetGlobalDetail.getCampusCode());
        this.setBuildingCode(assetGlobalDetail.getBuildingCode());
        this.setBuildingRoomNumber(assetGlobalDetail.getBuildingRoomNumber());
        this.setBuildingSubRoomNumber(assetGlobalDetail.getBuildingSubRoomNumber());
        this.setSerialNumber(assetGlobalDetail.getSerialNumber());
        this.setOrganizationInventoryName(assetGlobalDetail.getOrganizationInventoryName());
        this.setGovernmentTagNumber(assetGlobalDetail.getGovernmentTagNumber());
        this.setCampusTagNumber(assetGlobalDetail.getCampusTagNumber());
        this.setNationalStockNumber(assetGlobalDetail.getNationalStockNumber());

        AssetOrganization assetOrganization = new AssetOrganization();
        assetOrganization.setCapitalAssetNumber(assetGlobalDetail.getCapitalAssetNumber());
        assetOrganization.setOrganizationAssetTypeIdentifier(assetGlobalDetail.getOrganizationAssetTypeIdentifier());
        this.setAssetOrganization(assetOrganization);

        this.setActive(true);

        if (separate) {
            this.setRepresentativeUniversalIdentifier(assetGlobalDetail.getRepresentativeUniversalIdentifier());
            this.setCapitalAssetTypeCode(assetGlobalDetail.getCapitalAssetTypeCode());
            this.setCapitalAssetDescription(assetGlobalDetail.getCapitalAssetDescription());
            this.setManufacturerName(assetGlobalDetail.getManufacturerName());
            this.setManufacturerModelNumber(assetGlobalDetail.getManufacturerModelNumber());

            this.assetOrganization.setOrganizationText(assetGlobalDetail.getOrganizationText());
        }
        else {
            this.setRepresentativeUniversalIdentifier(assetGlobalDetail.getRepresentativeUniversalIdentifier());
            this.setCapitalAssetTypeCode(assetGlobal.getCapitalAssetTypeCode());
            this.setCapitalAssetDescription(assetGlobal.getCapitalAssetDescription());
            this.setManufacturerName(assetGlobal.getManufacturerName());
            this.setManufacturerModelNumber(assetGlobal.getManufacturerModelNumber());

            this.assetOrganization.setOrganizationText(assetGlobal.getOrganizationText());
        }
    }

    public KualiDecimal getCurrentMonthDepreciation() {
        return currentMonthDepreciation;
    }

    public void setCurrentMonthDepreciation(KualiDecimal currentMonthDepreciation) {
        this.currentMonthDepreciation = currentMonthDepreciation;
    }

    public KualiDecimal getAccumulatedDepreciation() {

        // Calculates payment summary and depreciation summary based on available payment records
        // The value can be null due to it being used as a non-singleton on the AssetRetirementGlobal
        // page (a list of retired assets). If it were a singleton, each value would get overridden
        // by the next use on the same page
        if (this.accumulatedDepreciation == null) {
            PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
            paymentSummaryService.calculateAndSetPaymentSummary(this);
        }

        return accumulatedDepreciation;
    }

    public void setAccumulatedDepreciation(KualiDecimal accumulatedDepreciation) {
        this.accumulatedDepreciation = accumulatedDepreciation;
    }

    public KualiDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(KualiDecimal baseAmount) {
        this.baseAmount = baseAmount;
    }

    public KualiDecimal getBookValue() {
        // Calculates payment summary and depreciation summary based on available payment records
        // The value can be null due to it being used as a non-singleton on the AssetRetirementGlobal
        // page (a list of retired assets). If it were a singleton, each value would get overridden
        // by the next use on the same page
        if (this.bookValue == null) {
            PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
            paymentSummaryService.calculateAndSetPaymentSummary(this);
        }

        return bookValue;
    }

    public void setBookValue(KualiDecimal bookValue) {
        this.bookValue = bookValue;
    }

    public KualiDecimal getPrevYearDepreciation() {
        return prevYearDepreciation;
    }

    public void setPrevYearDepreciation(KualiDecimal prevYearDepreciation) {
        this.prevYearDepreciation = prevYearDepreciation;
    }

    public KualiDecimal getYearToDateDepreciation() {
        return yearToDateDepreciation;
    }

    public void setYearToDateDepreciation(KualiDecimal yearToDateDepreciation) {
        this.yearToDateDepreciation = yearToDateDepreciation;
    }


    /**
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute.
     * 
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }


    /**
     * Gets the capitalAssetDescription attribute
     * 
     * @return Returns the capitalAssetDescription
     */
    public String getCapitalAssetDescription() {
        return capitalAssetDescription;
    }

    /**
     * Sets the capitalAssetDescription attribute.
     * 
     * @param capitalAssetDescription The capitalAssetDescription to set.
     */
    public void setCapitalAssetDescription(String capitalAssetDescription) {
        this.capitalAssetDescription = capitalAssetDescription;
    }


    /**
     * Gets the capitalAssetTypeCode attribute.
     * 
     * @return Returns the capitalAssetTypeCode
     */
    public String getCapitalAssetTypeCode() {
        return capitalAssetTypeCode;
    }

    /**
     * Sets the capitalAssetTypeCode attribute.
     * 
     * @param capitalAssetTypeCode The capitalAssetTypeCode to set.
     */
    public void setCapitalAssetTypeCode(String capitalAssetTypeCode) {
        this.capitalAssetTypeCode = capitalAssetTypeCode;
    }


    /**
     * Gets the conditionCode attribute.
     * 
     * @return Returns the conditionCode
     */
    public String getConditionCode() {
        return conditionCode;
    }

    /**
     * Sets the conditionCode attribute.
     * 
     * @param conditionCode The conditionCode to set.
     */
    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }


    /**
     * Gets the createDate attribute.
     * 
     * @return Returns the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets the createDate attribute.
     * 
     * @param createDate The createDate to set.
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    /**
     * Gets the receiveDate attribute.
     * 
     * @return Returns the receiveDate
     */
    public Date getReceiveDate() {
        return receiveDate;
    }

    /**
     * Sets the receiveDate attribute.
     * 
     * @param receiveDate The receiveDate to set.
     */
    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }


    /**
     * Gets the loanReturnDate attribute.
     * 
     * @return Returns the loanReturnDate
     */
    public Date getLoanReturnDate() {
        return loanReturnDate;
    }

    /**
     * Sets the loanReturnDate attribute.
     * 
     * @param loanReturnDate The loanReturnDate to set.
     */
    public void setLoanReturnDate(Date loanReturnDate) {
        this.loanReturnDate = loanReturnDate;
    }


    /**
     * Gets the loanDate attribute.
     * 
     * @return Returns the loanDate
     */
    public Date getLoanDate() {
        return loanDate;
    }

    /**
     * Sets the loanDate attribute.
     * 
     * @param loanDate The loanDate to set.
     */
    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }


    /**
     * Gets the expectedReturnDate attribute.
     * 
     * @return Returns the expectedReturnDate
     */
    public Date getExpectedReturnDate() {
        return expectedReturnDate;
    }

    /**
     * Sets the expectedReturnDate attribute.
     * 
     * @param expectedReturnDate The expectedReturnDate to set.
     */
    public void setExpectedReturnDate(Date expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }


    /**
     * Gets the financialDocumentPostingPeriodCode attribute.
     * 
     * @return Returns the financialDocumentPostingPeriodCode
     */
    public String getFinancialDocumentPostingPeriodCode() {
        return financialDocumentPostingPeriodCode;
    }

    /**
     * Sets the financialDocumentPostingPeriodCode attribute.
     * 
     * @param financialDocumentPostingPeriodCode The financialDocumentPostingPeriodCode to set.
     */
    public void setFinancialDocumentPostingPeriodCode(String financialDocumentPostingPeriodCode) {
        this.financialDocumentPostingPeriodCode = financialDocumentPostingPeriodCode;
    }


    /**
     * Gets the financialDocumentPostingYear attribute.
     * 
     * @return Returns the financialDocumentPostingYear
     */
    public Integer getFinancialDocumentPostingYear() {
        return financialDocumentPostingYear;
    }

    /**
     * Sets the financialDocumentPostingYear attribute.
     * 
     * @param financialDocumentPostingYear The financialDocumentPostingYear to set.
     */
    public void setFinancialDocumentPostingYear(Integer financialDocumentPostingYear) {
        this.financialDocumentPostingYear = financialDocumentPostingYear;
    }


    /**
     * Gets the organizationOwnerAccountNumber attribute.
     * 
     * @return Returns the organizationOwnerAccountNumber
     */
    public String getOrganizationOwnerAccountNumber() {
        return organizationOwnerAccountNumber;
    }

    /**
     * Sets the organizationOwnerAccountNumber attribute.
     * 
     * @param organizationOwnerAccountNumber The organizationOwnerAccountNumber to set.
     */
    public void setOrganizationOwnerAccountNumber(String organizationOwnerAccountNumber) {
        this.organizationOwnerAccountNumber = organizationOwnerAccountNumber;
    }


    /**
     * Gets the organizationOwnerChartOfAccountsCode attribute.
     * 
     * @return Returns the organizationOwnerChartOfAccountsCode
     */
    public String getOrganizationOwnerChartOfAccountsCode() {
        return organizationOwnerChartOfAccountsCode;
    }

    /**
     * Sets the organizationOwnerChartOfAccountsCode attribute.
     * 
     * @param organizationOwnerChartOfAccountsCode The organizationOwnerChartOfAccountsCode to set.
     */
    public void setOrganizationOwnerChartOfAccountsCode(String organizationOwnerChartOfAccountsCode) {
        this.organizationOwnerChartOfAccountsCode = organizationOwnerChartOfAccountsCode;
    }


    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendorName attribute.
     * 
     * @param vendorName The vendorName to set.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }


    /**
     * Gets the acquisitionTypeCode attribute.
     * 
     * @return Returns the acquisitionTypeCode
     */
    public String getAcquisitionTypeCode() {
        return acquisitionTypeCode;
    }

    /**
     * Sets the acquisitionTypeCode attribute.
     * 
     * @param acquisitionTypeCode The acquisitionTypeCode to set.
     */
    public void setAcquisitionTypeCode(String acquisitionTypeCode) {
        this.acquisitionTypeCode = acquisitionTypeCode;
    }


    /**
     * Gets the totalCostAmount attribute.
     * 
     * @return Returns the totalCostAmount
     */
    public KualiDecimal getTotalCostAmount() {
        return totalCostAmount;
    }

    /**
     * Sets the totalCostAmount attribute.
     * 
     * @param totalCostAmount The totalCostAmount to set.
     */
    public void setTotalCostAmount(KualiDecimal totalCostAmount) {
        this.totalCostAmount = totalCostAmount;
    }


    /**
     * Gets the replacementAmount attribute.
     * 
     * @return Returns the replacementAmount
     */
    public KualiDecimal getReplacementAmount() {
        return replacementAmount;
    }

    /**
     * Sets the replacementAmount attribute.
     * 
     * @param replacementAmount The replacementAmount to set.
     */
    public void setReplacementAmount(KualiDecimal replacementAmount) {
        this.replacementAmount = replacementAmount;
    }


    /**
     * Gets the salePrice attribute.
     * 
     * @return Returns the salePrice
     */
    public KualiDecimal getSalePrice() {
        return salePrice;
    }

    /**
     * Sets the salePrice attribute.
     * 
     * @param salePrice The salePrice to set.
     */
    public void setSalePrice(KualiDecimal salePrice) {
        this.salePrice = salePrice;
    }


    /**
     * Gets the estimatedSellingPrice attribute.
     * 
     * @return Returns the estimatedSellingPrice
     */
    public KualiDecimal getEstimatedSellingPrice() {
        return estimatedSellingPrice;
    }

    /**
     * Sets the estimatedSellingPrice attribute.
     * 
     * @param estimatedSellingPrice The estimatedSellingPrice to set.
     */
    public void setEstimatedSellingPrice(KualiDecimal estimatedSellingPrice) {
        this.estimatedSellingPrice = estimatedSellingPrice;
    }


    /**
     * Gets the salvageAmount attribute.
     * 
     * @return Returns the salvageAmount
     */
    public KualiDecimal getSalvageAmount() {
        if (salvageAmount == null)salvageAmount=KualiDecimal.ZERO;
        return salvageAmount;
    }

    /**
     * Sets the salvageAmount attribute.
     * 
     * @param salvageAmount The salvageAmount to set.
     */
    public void setSalvageAmount(KualiDecimal salvageAmount) {
        this.salvageAmount = salvageAmount;
    }


    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute.
     * 
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }


    /**
     * Gets the buildingCode attribute.
     * 
     * @return Returns the buildingCode
     */
    public String getBuildingCode() {
        return buildingCode;
    }

    /**
     * Sets the buildingCode attribute.
     * 
     * @param buildingCode The buildingCode to set.
     */
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }


    /**
     * Gets the buildingRoomNumber attribute.
     * 
     * @return Returns the buildingRoomNumber
     */
    public String getBuildingRoomNumber() {
        return buildingRoomNumber;
    }

    /**
     * Sets the buildingRoomNumber attribute.
     * 
     * @param buildingRoomNumber The buildingRoomNumber to set.
     */
    public void setBuildingRoomNumber(String buildingRoomNumber) {
        this.buildingRoomNumber = buildingRoomNumber;
    }


    /**
     * Gets the buildingSubRoomNumber attribute.
     * 
     * @return Returns the buildingSubRoomNumber
     */
    public String getBuildingSubRoomNumber() {
        return buildingSubRoomNumber;
    }

    /**
     * Sets the buildingSubRoomNumber attribute.
     * 
     * @param buildingSubRoomNumber The buildingSubRoomNumber to set.
     */
    public void setBuildingSubRoomNumber(String buildingSubRoomNumber) {
        this.buildingSubRoomNumber = buildingSubRoomNumber;
    }


    /**
     * Gets the retirementChartOfAccountsCode attribute.
     * 
     * @return Returns the retirementChartOfAccountsCode
     */
    public String getRetirementChartOfAccountsCode() {
        return retirementChartOfAccountsCode;
    }

    /**
     * Sets the retirementChartOfAccountsCode attribute.
     * 
     * @param retirementChartOfAccountsCode The retirementChartOfAccountsCode to set.
     */
    public void setRetirementChartOfAccountsCode(String retirementChartOfAccountsCode) {
        this.retirementChartOfAccountsCode = retirementChartOfAccountsCode;
    }


    /**
     * Gets the retirementAccountNumber attribute.
     * 
     * @return Returns the retirementAccountNumber
     */
    public String getRetirementAccountNumber() {
        return retirementAccountNumber;
    }

    /**
     * Sets the retirementAccountNumber attribute.
     * 
     * @param retirementAccountNumber The retirementAccountNumber to set.
     */
    public void setRetirementAccountNumber(String retirementAccountNumber) {
        this.retirementAccountNumber = retirementAccountNumber;
    }


    /**
     * Gets the retirementReasonCode attribute.
     * 
     * @return Returns the retirementReasonCode
     */
    public String getRetirementReasonCode() {
        return retirementReasonCode;
    }

    /**
     * Sets the retirementReasonCode attribute.
     * 
     * @param retirementReasonCode The retirementReasonCode to set.
     */
    public void setRetirementReasonCode(String retirementReasonCode) {
        this.retirementReasonCode = retirementReasonCode;
    }


    /**
     * Gets the retirementPeriodCode attribute.
     * 
     * @return Returns the retirementPeriodCode
     */
    public String getRetirementPeriodCode() {
        return retirementPeriodCode;
    }

    /**
     * Sets the retirementPeriodCode attribute.
     * 
     * @param retirementPeriodCode The retirementPeriodCode to set.
     */
    public void setRetirementPeriodCode(String retirementPeriodCode) {
        this.retirementPeriodCode = retirementPeriodCode;
    }


    /**
     * Gets the retirementFiscalYear attribute.
     * 
     * @return Returns the retirementFiscalYear
     */
    public Integer getRetirementFiscalYear() {
        return retirementFiscalYear;
    }

    /**
     * Sets the retirementFiscalYear attribute.
     * 
     * @param retirementFiscalYear The retirementFiscalYear to set.
     */
    public void setRetirementFiscalYear(Integer retirementFiscalYear) {
        this.retirementFiscalYear = retirementFiscalYear;
    }

    /**
     * Gets the cashReceiptFinancialDocumentNumber attribute.
     * 
     * @return Returns the cashReceiptFinancialDocumentNumber
     */
    public String getCashReceiptFinancialDocumentNumber() {
        return cashReceiptFinancialDocumentNumber;
    }

    /**
     * Sets the cashReceiptFinancialDocumentNumber attribute.
     * 
     * @param cashReceiptFinancialDocumentNumber The cashReceiptFinancialDocumentNumber to set.
     */
    public void setCashReceiptFinancialDocumentNumber(String cashReceiptFinancialDocumentNumber) {
        this.cashReceiptFinancialDocumentNumber = cashReceiptFinancialDocumentNumber;
    }


    /**
     * Gets the primaryDepreciationMethodCode attribute.
     * 
     * @return Returns the primaryDepreciationMethodCode
     */
    public String getPrimaryDepreciationMethodCode() {
        return primaryDepreciationMethodCode;
    }

    /**
     * Sets the primaryDepreciationMethodCode attribute.
     * 
     * @param primaryDepreciationMethodCode The primaryDepreciationMethodCode to set.
     */
    public void setPrimaryDepreciationMethodCode(String primaryDepreciationMethodCode) {
        this.primaryDepreciationMethodCode = primaryDepreciationMethodCode;
    }


    /**
     * Gets the estimatedFabricationCompletionDate attribute.
     * 
     * @return Returns the estimatedFabricationCompletionDate
     */
    public Date getEstimatedFabricationCompletionDate() {
        return estimatedFabricationCompletionDate;
    }

    /**
     * Sets the estimatedFabricationCompletionDate attribute.
     * 
     * @param estimatedFabricationCompletionDate The estimatedFabricationCompletionDate to set.
     */
    public void setEstimatedFabricationCompletionDate(Date estimatedFabricationCompletionDate) {
        this.estimatedFabricationCompletionDate = estimatedFabricationCompletionDate;
    }


    /**
     * Gets the fabricationEstimatedTotalAmount attribute.
     * 
     * @return Returns the fabricationEstimatedTotalAmount
     */
    public KualiDecimal getFabricationEstimatedTotalAmount() {
        return fabricationEstimatedTotalAmount;
    }

    /**
     * Sets the fabricationEstimatedTotalAmount attribute.
     * 
     * @param fabricationEstimatedTotalAmount The fabricationEstimatedTotalAmount to set.
     */
    public void setFabricationEstimatedTotalAmount(KualiDecimal fabricationEstimatedTotalAmount) {
        this.fabricationEstimatedTotalAmount = fabricationEstimatedTotalAmount;
    }

    /**
     * Gets the transferOfFundsFinancialDocumentNumber attribute.
     * 
     * @return Returns the transferOfFundsFinancialDocumentNumber
     */
    public String getTransferOfFundsFinancialDocumentNumber() {
        return transferOfFundsFinancialDocumentNumber;
    }

    /**
     * Sets the transferOfFundsFinancialDocumentNumber attribute.
     * 
     * @param transferOfFundsFinancialDocumentNumber The transferOfFundsFinancialDocumentNumber to set.
     */
    public void setTransferOfFundsFinancialDocumentNumber(String transferOfFundsFinancialDocumentNumber) {
        this.transferOfFundsFinancialDocumentNumber = transferOfFundsFinancialDocumentNumber;
    }

    /**
     * Gets the inventoryStatusCode attribute.
     * 
     * @return Returns the inventoryStatusCode
     */
    public String getInventoryStatusCode() {
        return inventoryStatusCode;
    }

    /**
     * Sets the inventoryStatusCode attribute.
     * 
     * @param inventoryStatusCode The inventoryStatusCode to set.
     */
    public void setInventoryStatusCode(String inventoryStatusCode) {
        this.inventoryStatusCode = inventoryStatusCode;
    }

    /**
     * Gets the campusTagNumber attribute.
     * 
     * @return Returns the campusTagNumber
     */
    public String getCampusTagNumber() {
        return campusTagNumber;
    }

    /**
     * Sets the campusTagNumber attribute.
     * 
     * @param campusTagNumber The campusTagNumber to set.
     */
    public void setCampusTagNumber(String campusTagNumber) {
        this.campusTagNumber = campusTagNumber;
    }


    /**
     * Gets the lastInventoryDate attribute.
     * 
     * @return Returns the lastInventoryDate
     */
    public Timestamp getLastInventoryDate() {
        return lastInventoryDate;
    }

    /**
     * Sets the lastInventoryDate attribute.
     * 
     * @param lastInventoryDate The lastInventoryDate to set.
     */
    public void setLastInventoryDate(Timestamp lastInventoryDate) {
        this.lastInventoryDate = lastInventoryDate;
    }


    /**
     * Gets the organizationInventoryName attribute.
     * 
     * @return Returns the organizationInventoryName
     */
    public String getOrganizationInventoryName() {
        return organizationInventoryName;
    }

    /**
     * Sets the organizationInventoryName attribute.
     * 
     * @param organizationInventoryName The organizationInventoryName to set.
     */
    public void setOrganizationInventoryName(String organizationInventoryName) {
        this.organizationInventoryName = organizationInventoryName;
    }


    /**
     * Gets the oldTagNumber attribute.
     * 
     * @return Returns the oldTagNumber
     */
    public String getOldTagNumber() {
        return oldTagNumber;
    }

    /**
     * Sets the oldTagNumber attribute.
     * 
     * @param oldTagNumber The oldTagNumber to set.
     */
    public void setOldTagNumber(String oldTagNumber) {
        this.oldTagNumber = oldTagNumber;
    }


    /**
     * Gets the manufacturerName attribute.
     * 
     * @return Returns the manufacturerName
     */
    public String getManufacturerName() {
        return manufacturerName;
    }

    /**
     * Sets the manufacturerName attribute.
     * 
     * @param manufacturerName The manufacturerName to set.
     */
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }


    /**
     * Gets the manufacturerModelNumber attribute.
     * 
     * @return Returns the manufacturerModelNumber
     */
    public String getManufacturerModelNumber() {
        return manufacturerModelNumber;
    }

    /**
     * Sets the manufacturerModelNumber attribute.
     * 
     * @param manufacturerModelNumber The manufacturerModelNumber to set.
     */
    public void setManufacturerModelNumber(String manufacturerModelNumber) {
        this.manufacturerModelNumber = manufacturerModelNumber;
    }


    /**
     * Gets the serialNumber attribute.
     * 
     * @return Returns the serialNumber
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serialNumber attribute.
     * 
     * @param serialNumber The serialNumber to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }


    /**
     * Gets the representativeUniversalIdentifier attribute.
     * 
     * @return Returns the representativeUniversalIdentifier
     */
    public String getRepresentativeUniversalIdentifier() {
        return representativeUniversalIdentifier;
    }

    /**
     * Sets the representativeUniversalIdentifier attribute.
     * 
     * @param representativeUniversalIdentifier The representativeUniversalIdentifier to set.
     */
    public void setRepresentativeUniversalIdentifier(String representativeUniversalIdentifier) {
        this.representativeUniversalIdentifier = representativeUniversalIdentifier;
    }


    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }


    /**
     * Gets the campusPoliceDepartmentCaseNumber attribute.
     * 
     * @return Returns the campusPoliceDepartmentCaseNumber
     */
    public String getCampusPoliceDepartmentCaseNumber() {
        return campusPoliceDepartmentCaseNumber;
    }

    /**
     * Sets the campusPoliceDepartmentCaseNumber attribute.
     * 
     * @param campusPoliceDepartmentCaseNumber The campusPoliceDepartmentCaseNumber to set.
     */
    public void setCampusPoliceDepartmentCaseNumber(String campusPoliceDepartmentCaseNumber) {
        this.campusPoliceDepartmentCaseNumber = campusPoliceDepartmentCaseNumber;
    }


    /**
     * Gets the inventoryScannedCode attribute.
     * 
     * @return Returns the inventoryScannedCode
     */
    public String getInventoryScannedCode() {
        return inventoryScannedCode;
    }

    /**
     * Sets the inventoryScannedCode attribute.
     * 
     * @param inventoryScannedCode The inventoryScannedCode to set.
     */
    public void setInventoryScannedCode(String inventoryScannedCode) {
        this.inventoryScannedCode = inventoryScannedCode;
    }

    /**
     * Gets the capitalAssetInServiceDate attribute.
     * 
     * @return Returns the capitalAssetInServiceDate.
     */
    public Date getCapitalAssetInServiceDate() {
        return capitalAssetInServiceDate;
    }

    /**
     * Sets the capitalAssetInServiceDate attribute value.
     * 
     * @param capitalAssetInServiceDate The capitalAssetInServiceDate to set.
     */
    public void setCapitalAssetInServiceDate(Date capitalAssetInServiceDate) {
        this.capitalAssetInServiceDate = capitalAssetInServiceDate;
    }

    /**
     * Gets the governmentTagNumber attribute.
     * 
     * @return Returns the governmentTagNumber.
     */
    public String getGovernmentTagNumber() {
        return governmentTagNumber;
    }

    /**
     * Sets the governmentTagNumber attribute value.
     * 
     * @param governmentTagNumber The governmentTagNumber to set.
     */
    public void setGovernmentTagNumber(String governmentTagNumber) {
        this.governmentTagNumber = governmentTagNumber;
    }

    /**
     * Gets the nationalStockNumber attribute.
     * 
     * @return Returns the nationalStockNumber.
     */
    public String getNationalStockNumber() {
        return nationalStockNumber;
    }

    /**
     * Sets the nationalStockNumber attribute value.
     * 
     * @param nationalStockNumber The nationalStockNumber to set.
     */
    public void setNationalStockNumber(String nationalStockNumber) {
        this.nationalStockNumber = nationalStockNumber;
    }

    /**
     * Gets the landAcreageSize attribute.
     * 
     * @return Returns the landAcreageSize.
     */
    public Integer getLandAcreageSize() {
        return landAcreageSize;
    }

    /**
     * Sets the landAcreageSize attribute value.
     * 
     * @param landAcreageSize The landAcreageSize to set.
     */
    public void setLandAcreageSize(Integer landAcreageSize) {
        this.landAcreageSize = landAcreageSize;
    }

    /**
     * Gets the landCountyName attribute.
     * 
     * @return Returns the landCountyName.
     */
    public String getLandCountyName() {
        return landCountyName;
    }

    /**
     * Sets the landCountyName attribute value.
     * 
     * @param landCountyName The landCountyName to set.
     */
    public void setLandCountyName(String landCountyName) {
        this.landCountyName = landCountyName;
    }

    /**
     * Gets the landParcelNumber attribute.
     * 
     * @return Returns the landParcelNumber.
     */
    public String getLandParcelNumber() {
        return landParcelNumber;
    }

    /**
     * Sets the landParcelNumber attribute value.
     * 
     * @param landParcelNumber The landParcelNumber to set.
     */
    public void setLandParcelNumber(String landParcelNumber) {
        this.landParcelNumber = landParcelNumber;
    }

    /**
     * Gets the depreciationDate attribute.
     * 
     * @return Returns the depreciationDate.
     */
    public Date getDepreciationDate() {
        return depreciationDate;
    }

    /**
     * Sets the depreciationDate attribute value.
     * 
     * @param depreciationDate The depreciationDate to set.
     */
    public void setDepreciationDate(Date depreciationDate) {
        this.depreciationDate = depreciationDate;
    }

    /**
     * Gets the capitalAssetType attribute.
     * 
     * @return Returns the capitalAssetType
     */
    public AssetType getCapitalAssetType() {
        return capitalAssetType;
    }

    /**
     * Sets the capitalAssetType attribute.
     * 
     * @param capitalAssetType The capitalAssetType to set.
     * @deprecated
     */
    public void setCapitalAssetType(AssetType capitalAssetType) {
        this.capitalAssetType = capitalAssetType;
    }

    /**
     * Gets the organizationOwnerAccount attribute.
     * 
     * @return Returns the organizationOwnerAccount
     */
    public Account getOrganizationOwnerAccount() {
        return organizationOwnerAccount;
    }

    /**
     * Sets the organizationOwnerAccount attribute.
     * 
     * @param organizationOwnerAccount The organizationOwnerAccount to set.
     * @deprecated
     */
    public void setOrganizationOwnerAccount(Account organizationOwnerAccount) {
        this.organizationOwnerAccount = organizationOwnerAccount;
    }

    /**
     * Gets the organizationOwnerChartOfAccounts attribute.
     * 
     * @return Returns the organizationOwnerChartOfAccounts
     */
    public Chart getOrganizationOwnerChartOfAccounts() {
        return organizationOwnerChartOfAccounts;
    }

    /**
     * Sets the organizationOwnerChartOfAccounts attribute.
     * 
     * @param organizationOwnerChartOfAccounts The organizationOwnerChartOfAccounts to set.
     * @deprecated
     */
    public void setOrganizationOwnerChartOfAccounts(Chart organizationOwnerChartOfAccounts) {
        this.organizationOwnerChartOfAccounts = organizationOwnerChartOfAccounts;
    }

    /**
     * Gets the campus attribute.
     * 
     * @return Returns the campus
     */
    public Campus getCampus() {
        return campus = (Campus) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Campus.class).retrieveExternalizableBusinessObjectIfNecessary(this, campus, "campus");
    }

    /**
     * Sets the campus attribute.
     * 
     * @param campus The campus to set.
     * @deprecated
     */
    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    /**
     * Gets the buildingRoom attribute.
     * 
     * @return Returns the buildingRoom
     */
    public Room getBuildingRoom() {
        return buildingRoom;
    }

    /**
     * Sets the buildingRoom attribute.
     * 
     * @param buildingRoom The buildingRoom to set.
     * @deprecated
     */
    public void setBuildingRoom(Room buildingRoom) {
        this.buildingRoom = buildingRoom;
    }

    /**
     * Gets the retirementAccount attribute.
     * 
     * @return Returns the retirementAccount
     */
    public Account getRetirementAccount() {
        return retirementAccount;
    }

    /**
     * Sets the retirementAccount attribute.
     * 
     * @param retirementAccount The retirementAccount to set.
     * @deprecated
     */
    public void setRetirementAccount(Account retirementAccount) {
        this.retirementAccount = retirementAccount;
    }

    /**
     * Gets the retirementChartOfAccounts attribute.
     * 
     * @return Returns the retirementChartOfAccounts
     */
    public Chart getRetirementChartOfAccounts() {
        return retirementChartOfAccounts;
    }

    /**
     * Sets the retirementChartOfAccounts attribute.
     * 
     * @param retirementChartOfAccounts The retirementChartOfAccounts to set.
     * @deprecated
     */
    public void setRetirementChartOfAccounts(Chart retirementChartOfAccounts) {
        this.retirementChartOfAccounts = retirementChartOfAccounts;
    }

    /**
     * Gets the building attribute.
     * 
     * @return Returns the building.
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Sets the building attribute value.
     * 
     * @param building The building to set.
     * @deprecated
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * Gets the cashReceiptFinancialDocument attribute.
     * 
     * @return Returns the cashReceiptFinancialDocument.
     */
    public DocumentHeader getCashReceiptFinancialDocument() {
        return cashReceiptFinancialDocument;
    }

    /**
     * Sets the cashReceiptFinancialDocument attribute value.
     * 
     * @param cashReceiptFinancialDocument The cashReceiptFinancialDocument to set.
     * @deprecated
     */
    public void setCashReceiptFinancialDocument(DocumentHeader cashReceiptFinancialDocument) {
        this.cashReceiptFinancialDocument = cashReceiptFinancialDocument;
    }

    /**
     * Gets the retirementPeriod attribute.
     * 
     * @return Returns the retirementPeriod.
     */
    public AccountingPeriod getRetirementPeriod() {
        return retirementPeriod;
    }

    /**
     * Sets the retirementPeriod attribute value.
     * 
     * @param retirementPeriod The retirementPeriod to set.
     * @deprecated
     */
    public void setRetirementPeriod(AccountingPeriod retirementPeriod) {
        this.retirementPeriod = retirementPeriod;
    }

    /**
     * Gets the retirementReason attribute.
     * 
     * @return Returns the retirementReason.
     */
    public AssetRetirementReason getRetirementReason() {
        return retirementReason;
    }

    /**
     * Sets the retirementReason attribute value.
     * 
     * @param retirementReason The retirementReason to set.
     * @deprecated
     */
    public void setRetirementReason(AssetRetirementReason retirementReason) {
        this.retirementReason = retirementReason;
    }

    /**
     * Gets the transferOfFundsFinancialDocument attribute.
     * 
     * @return Returns the transferOfFundsFinancialDocument.
     */
    public DocumentHeader getTransferOfFundsFinancialDocument() {
        return transferOfFundsFinancialDocument;
    }

    /**
     * Sets the transferOfFundsFinancialDocument attribute value.
     * 
     * @param transferOfFundsFinancialDocument The transferOfFundsFinancialDocument to set.
     * @deprecated
     */
    public void setTransferOfFundsFinancialDocument(DocumentHeader transferOfFundsFinancialDocument) {
        this.transferOfFundsFinancialDocument = transferOfFundsFinancialDocument;
    }

    /**
     * Gets the financialDocumentPostingPeriod attribute.
     * 
     * @return Returns the financialDocumentPostingPeriod.
     */
    public AccountingPeriod getFinancialDocumentPostingPeriod() {
        return financialDocumentPostingPeriod;
    }

    /**
     * Sets the financialDocumentPostingPeriod attribute value.
     * 
     * @param financialDocumentPostingPeriod The financialDocumentPostingPeriod to set.
     * @deprecated
     */
    public void setFinancialDocumentPostingPeriod(AccountingPeriod financialDocumentPostingPeriod) {
        this.financialDocumentPostingPeriod = financialDocumentPostingPeriod;
    }

    /**
     * Gets the condition attribute.
     * 
     * @return Returns the condition.
     */
    public AssetCondition getCondition() {
        return condition;
    }

    /**
     * Sets the condition attribute value.
     * 
     * @param condition The condition to set.
     * @deprecated
     */
    public void setCondition(AssetCondition condition) {
        this.condition = condition;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        return m;
    }

    public List<AssetPayment> getAssetPayments() {
        return assetPayments;
    }

    public void setAssetPayments(List<AssetPayment> assetPayments) {
        this.assetPayments = assetPayments;
    }

    /**
     * Gets the inventoryStatus attribute.
     * 
     * @return Returns the inventoryStatus.
     */
    public AssetStatus getInventoryStatus() {
        return inventoryStatus;
    }

    /**
     * Sets the inventoryStatus attribute value.
     * 
     * @param inventoryStatus The inventoryStatus to set.
     */
    public void setInventoryStatus(AssetStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    /**
     * Gets the assetRepresentative attribute.
     * 
     * @return Returns the assetRepresentative.
     */
    public Person getAssetRepresentative() {
        assetRepresentative = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(representativeUniversalIdentifier, assetRepresentative);
        return assetRepresentative;
    }

    /**
     * Sets the assetRepresentative attribute value.
     * 
     * @deprecated
     * @param assetRepresentative The assetRepresentative to set.
     */
    public void setAssetRepresentative(Person assetRepresentative) {
        this.assetRepresentative = assetRepresentative;
    }


    /**
     * Gets the borrowerPerson attribute.
     * 
     * @return Returns the borrowerPerson.
     */
    public Person getBorrowerPerson() {
        if (ObjectUtils.isNotNull(borrowerLocation)) {
            borrowerPerson = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(borrowerLocation.getAssetLocationContactIdentifier(), borrowerPerson);
        }
        return borrowerPerson;
    }

    /**
     * Sets the borrowerPerson attribute value.
     * 
     * @param borrowerPerson The borrowerPerson to set.
     */
    public void setBorrowerPerson(Person borrowerPerson) {
        this.borrowerPerson = borrowerPerson;
    }

    /**
     * Gets the assetOrganization attribute.
     * 
     * @return Returns the assetOrganization.
     */
    public AssetOrganization getAssetOrganization() {
        return assetOrganization;
    }

    /**
     * Sets the assetOrganization attribute value.
     * 
     * @param assetOrganization The assetOrganization to set.
     */
    public void setAssetOrganization(AssetOrganization assetOrganization) {
        this.assetOrganization = assetOrganization;
    }

    /**
     * Gets the organizationTagNumber attribute.
     * 
     * @return Returns the organizationTagNumber.
     */
    public String getOrganizationTagNumber() {
        return organizationTagNumber;
    }

    /**
     * Sets the organizationTagNumber attribute value.
     * 
     * @param organizationTagNumber The organizationTagNumber to set.
     */
    public void setOrganizationTagNumber(String organizationTagNumber) {
        this.organizationTagNumber = organizationTagNumber;
    }

    public List<AssetRepairHistory> getAssetRepairHistory() {
        return assetRepairHistory;
    }

    public void setAssetRepairHistory(List<AssetRepairHistory> assetRepairHistory) {
        this.assetRepairHistory = assetRepairHistory;
    }

    public AssetWarranty getAssetWarranty() {
        return assetWarranty;
    }

    public void setAssetWarranty(AssetWarranty assetWarranty) {
        this.assetWarranty = assetWarranty;
    }

    /**
     * Gets the paymentTotalCost attribute.
     * 
     * @return Returns the paymentTotalCost.
     */
    public KualiDecimal getPaymentTotalCost() {
        return paymentTotalCost;
    }

    /**
     * Sets the paymentTotalCost attribute value.
     * 
     * @param paymentTotalCost The paymentTotalCost to set.
     */
    public void setPaymentTotalCost(KualiDecimal paymentTotalCost) {
        this.paymentTotalCost = paymentTotalCost;
    }

    public List<AssetComponent> getAssetComponents() {
        return assetComponents;
    }

    public void setAssetComponents(List<AssetComponent> assetComponents) {
        this.assetComponents = assetComponents;
    }

    /**
     * Gets the federalContribution attribute.
     * 
     * @return Returns the federalContribution.
     */
    public KualiDecimal getFederalContribution() {
        return federalContribution;
    }

    /**
     * Sets the federalContribution attribute value.
     * 
     * @param federalContribution The federalContribution to set.
     */
    public void setFederalContribution(KualiDecimal federalContribution) {
        this.federalContribution = federalContribution;
    }

    public AssetRetirementGlobalDetail getRetirementInfo() {
        return retirementInfo;
    }

    public void setRetirementInfo(AssetRetirementGlobalDetail retirementInfo) {
        this.retirementInfo = retirementInfo;
    }

    public List<AssetLocation> getAssetLocations() {
        return assetLocations;
    }

    public void setAssetLocations(List<AssetLocation> assetLocations) {
        this.assetLocations = assetLocations;
    }

    public AssetLocation getOffCampusLocation() {
        return offCampusLocation;
    }

    public void setOffCampusLocation(AssetLocation offCampusLocation) {
        this.offCampusLocation = offCampusLocation;
    }

    public List<AssetRetirementGlobalDetail> getAssetRetirementHistory() {
        return assetRetirementHistory;
    }

    public void setAssetRetirementHistory(List<AssetRetirementGlobalDetail> assetRetirementHistory) {
        this.assetRetirementHistory = assetRetirementHistory;
    }

    public AssetDepreciationMethod getAssetPrimaryDepreciationMethod() {
        return assetPrimaryDepreciationMethod;
    }

    public void setAssetPrimaryDepreciationMethod(AssetDepreciationMethod assetPrimaryDepreciationMethod) {
        this.assetPrimaryDepreciationMethod = assetPrimaryDepreciationMethod;
    }

    /**
     * Gets the fabricationEstimatedRetentionYears attribute.
     * 
     * @return Returns the fabricationEstimatedRetentionYears.
     */
    public Integer getFabricationEstimatedRetentionYears() {
        return fabricationEstimatedRetentionYears;
    }

    /**
     * Sets the fabricationEstimatedRetentionYears attribute value.
     * 
     * @param fabricationEstimatedRetentionYears The fabricationEstimatedRetentionYears to set.
     */
    public void setFabricationEstimatedRetentionYears(Integer fabricationEstimatedRetentionYears) {
        this.fabricationEstimatedRetentionYears = fabricationEstimatedRetentionYears;
    }


    public List<AssetRetirementGlobal> getRetirementGlobals() {
        return retirementGlobals;
    }

    public void setRetirementGlobals(List<AssetRetirementGlobal> retirementGlobals) {
        this.retirementGlobals = retirementGlobals;
    }

    public AssetGlobal getSeparateHistory() {
        return separateHistory;
    }

    public void setSeparateHistory(AssetGlobal separateHistory) {
        this.separateHistory = separateHistory;
    }

    public List<AssetRetirementGlobalDetail> getMergeHistory() {
        return mergeHistory;
    }

    public void setMergeHistory(List<AssetRetirementGlobalDetail> mergeHistory) {
        this.mergeHistory = mergeHistory;
    }

    /**
     * Gets the financialObjectSubTypeCode attribute.
     * 
     * @return Returns the financialObjectSubTypeCode.
     */
    public String getFinancialObjectSubTypeCode() {
        return financialObjectSubTypeCode;
    }

    /**
     * Sets the financialObjectSubTypeCode attribute value.
     * 
     * @param financialObjectSubTypeCode The financialObjectSubTypeCode to set.
     */
    public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
        this.financialObjectSubTypeCode = financialObjectSubTypeCode;
    }

    /**
     * Gets the financialObjectSubType attribute.
     * 
     * @return Returns the financialObjectSubType.
     */
    public ObjectSubType getFinancialObjectSubType() {
        return financialObjectSubType;
    }

    /**
     * Sets the financialObjectSubType attribute value.
     * 
     * @param financialObjectSubType The financialObjectSubType to set.
     * @deprecated
     */
    public void setFinancialObjectSubType(ObjectSubType financialObjectSubType) {
        this.financialObjectSubType = financialObjectSubType;
    }

    public Date getDepreciationDateCopy() {
        return depreciationDate;
    }

    /**
     * @param depreciationDateCopy
     * @deprecated
     */
    public void setDepreciationDateCopy(Date depreciationDateCopy) {
        this.depreciationDateCopy = depreciationDateCopy;
    }

    public AssetAcquisitionType getAcquisitionType() {
        return acquisitionType;
    }

    public void setAcquisitionType(AssetAcquisitionType acquisitionType) {
        this.acquisitionType = acquisitionType;
    }

    public ContractsAndGrantsAgency getAgency() {
        return agency = (ContractsAndGrantsAgency) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgency.class).retrieveExternalizableBusinessObjectIfNecessary(this, agency, "agency");
    }

    /**
     * Technically this is obsolete but necessary because MaintenanceDocumentBase.populateXmlDocumentContentsFromMaintainables has
     * the following hack:<br>
     * ObjectUtils.materializeAllSubObjects(oldBo); // hack to resolve XStream not dealing well with Proxies<br>
     * so as long as that is there we need this setter otherwise a NoSuchMethodException occurs.
     * 
     * @deprecated
     */
    public void setAgency(ContractsAndGrantsAgency agency) {
        this.agency = agency;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public boolean isTagged() {
        return tagged;
    }

    public void setTagged() {
        this.tagged = (StringUtils.isBlank(campusTagNumber)) ? false : true;
    }

    /**
     * Gets the borrowerLocation attribute.
     * 
     * @return Returns the borrowerLocation.
     */
    public AssetLocation getBorrowerLocation() {
        return borrowerLocation;
    }

    /**
     * Sets the borrowerLocation attribute value.
     * 
     * @param borrowerLocation The borrowerLocation to set.
     */
    public void setBorrowerLocation(AssetLocation borrowerLocation) {
        this.borrowerLocation = borrowerLocation;
    }

    /**
     * Gets the borrowerStorageLocation attribute.
     * 
     * @return Returns the borrowerStorageLocation.
     */
    public AssetLocation getBorrowerStorageLocation() {
        return borrowerStorageLocation;
    }

    /**
     * Sets the borrowerStorageLocation attribute value.
     * 
     * @param borrowerStorageLocation The borrowerStorageLocation to set.
     */
    public void setBorrowerStorageLocation(AssetLocation borrowerStorageLocation) {
        this.borrowerStorageLocation = borrowerStorageLocation;
    }


    /**
     * Return the link for payment lookup
     * 
     * @return
     */
    public String getLookup() {
        if (this.getCapitalAssetNumber() == null)
            return "";

        Properties params = new Properties();
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        params.put(KFSConstants.DOC_FORM_KEY, "88888888");
        params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        params.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, this.getCapitalAssetNumber().toString());
        params.put(KFSConstants.RETURN_LOCATION_PARAMETER, "portal.do");
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AssetPayment.class.getName());

        return UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);
    }

    /**
     * Build the properties collection for document lookup link. This link will be used for related document lookup.
     * 
     * @return
     */
    private Properties buildDocumentLookupLinkProperties() {
        Properties params = new Properties();
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        params.put(KFSConstants.DOC_FORM_KEY, "88888888");
        params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
        params.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, this.getCapitalAssetNumber().toString());
        params.put(KFSConstants.RETURN_LOCATION_PARAMETER, "portal.do");
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, DocSearchCriteriaDTO.class.getName());
        return params;
    }

    /**
     * return the link for asset transfer document lookup for given capital asset number.
     * 
     * @return
     */
    public String getAssetTransferDocumentLookup() {
        if (this.getCapitalAssetNumber() == null)
            return "";

        Properties params = buildDocumentLookupLinkProperties();
        params.put(KEWConstants.Sorting.SORT_DOC_TYPE_FULL_NAME, CamsConstants.DocumentTypeName.ASSET_TRANSFER);

        return UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);
    }

    /**
     * return the link for asset maintenance document lookup for given capital asset number.
     * 
     * @return
     */
    public String getAssetMaintenanceDocumentLookup() {
        if (this.getCapitalAssetNumber() == null)
            return "";

        Properties params = buildDocumentLookupLinkProperties();
        params.put(KEWConstants.Sorting.SORT_DOC_TYPE_FULL_NAME, CamsConstants.DocumentTypeName.ASSET_EDIT);

        return UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);
    }

    /**
     * return the link for asset fabrication document lookup for given capital asset number.
     * 
     * @return
     */
    public String getAssetFabricationDocumentLookup() {
        if (this.getCapitalAssetNumber() == null)
            return "";

        Properties params = buildDocumentLookupLinkProperties();
        params.put(KEWConstants.Sorting.SORT_DOC_TYPE_FULL_NAME, CamsConstants.DocumentTypeName.ASSET_FABRICATION);

        return UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);
    }

    /**
     * return the link for asset create or separate global document lookup for given capital asset number.
     * 
     * @return
     */
    public String getAssetCreateOrSeparateDocumentLookup() {
        if (this.getCapitalAssetNumber() == null)
            return "";

        Properties params = buildDocumentLookupLinkProperties();
        params.put(KEWConstants.Sorting.SORT_DOC_TYPE_FULL_NAME, CamsConstants.DocumentTypeName.ASSET_ADD_GLOBAL);

        return UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);
    }

    /**
     * return the link for asset payment document lookup for given capital asset number.
     * 
     * @return
     */
    public String getAssetPaymentDocumentLookup() {
        if (this.getCapitalAssetNumber() == null)
            return "";

        Properties params = buildDocumentLookupLinkProperties();
        params.put(KEWConstants.Sorting.SORT_DOC_TYPE_FULL_NAME, CamsConstants.DocumentTypeName.ASSET_PAYMENT);

        return UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);
    }

    /**
     * return the link for asset equipment loan or return document lookup for given capital asset number.
     * 
     * @return
     */
    public String getAssetEquipmentLoanOrReturnDocumentLookup() {
        if (this.getCapitalAssetNumber() == null)
            return "";

        Properties params = buildDocumentLookupLinkProperties();
        params.put(KEWConstants.Sorting.SORT_DOC_TYPE_FULL_NAME, CamsConstants.DocumentTypeName.ASSET_EQUIPMENT_LOAN_OR_RETURN);

        return UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);
    }

    /**
     * return the link for asset location global document lookup for given capital asset number.
     * 
     * @return
     */
    public String getAssetLocationDocumentLookup() {
        if (this.getCapitalAssetNumber() == null)
            return "";

        Properties params = buildDocumentLookupLinkProperties();
        params.put(KEWConstants.Sorting.SORT_DOC_TYPE_FULL_NAME, CamsConstants.DocumentTypeName.ASSET_LOCATION_GLOBAL);

        return UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);
    }

    /**
     * return the link for asset retirement or merge document lookup for given capital asset number.
     * 
     * @return
     */
    public String getAssetMergeOrRetirementDocumentLookup() {
        if (this.getCapitalAssetNumber() == null)
            return "";

        Properties params = buildDocumentLookupLinkProperties();
        params.put(KEWConstants.Sorting.SORT_DOC_TYPE_FULL_NAME, CamsConstants.DocumentTypeName.ASSET_RETIREMENT_GLOBAL);

        return UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);
    }


    /**
     * Gets the camsComplexMaintenanceDocumentLookup attribute.
     * 
     * @return Returns the camsComplexMaintenanceDocumentLookup.
     */
    public String getCamsComplexMaintenanceDocumentLookup() {
        if (this.getCapitalAssetNumber() == null)
            return "";

        Properties params = buildDocumentLookupLinkProperties();
        params.put(KEWConstants.Sorting.SORT_DOC_TYPE_FULL_NAME, CamsConstants.DocumentTypeName.COMPLEX_MAINTENANCE_DOC_BASE);

        return UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);
    }


    /**
     * override this method so we can remove the offcampus location
     * 
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List<List> managedLists = new ArrayList<List>();
        managedLists.add(getAssetLocations());
        return managedLists;
    }
}
