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
package org.kuali.kfs.module.cam.batch.service.impl;

import static org.kuali.kfs.sys.KFSConstants.BALANCE_TYPE_ACTUAL;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.batch.AssetPaymentInfo;
import org.kuali.kfs.module.cam.batch.service.AssetDepreciationService;
import org.kuali.kfs.module.cam.batch.service.ReportService;
import org.kuali.kfs.module.cam.businessobject.AssetDepreciationTransaction;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.document.AssetDepreciationDocument;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciableAssetsDao;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is a service that calculates the depreciation amount for each asset that has a eligible asset payment.
 * <p>
 * When an error occurs running this process, a pdf file will be created with the error message. However, this doesn't mean that
 * this process automatically leaves all the records as they were right before running the program. If the process fails, is
 * suggested to do the following before trying to run the process again: a.)Delete gl pending entry depreciation entries: DELETE
 * FROM GL_PENDING_ENTRY_T WHERE FDOC_TYP_CD = 'DEPR' b.)Substract from the accumulated depreciation amount the depreciation
 * calculated for the fiscal month that was ran, and then reset the depreciation amount field for the fiscal month that was ran. ex:
 * Assuming that the fiscal month = 8 then: UPDATE CM_AST_PAYMENT_T SET AST_ACUM_DEPR1_AMT = AST_ACUM_DEPR1_AMT -
 * AST_PRD8_DEPR1_AMT, AST_PRD8_DEPR1_AMT=0
 */
@Transactional
public class AssetDepreciationServiceImpl implements AssetDepreciationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetDepreciationServiceImpl.class);
    private ParameterService parameterService;
    private ReportService reportService;
    private DateTimeService dateTimeService;
    private DepreciableAssetsDao depreciableAssetsDao;
    private KualiConfigurationService kualiConfigurationService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private BusinessObjectService businessObjectService;
    private UniversityDateDao universityDateDao;
    private WorkflowDocumentService workflowDocumentService;
    private DataDictionaryService dataDictionaryService;
    private Integer fiscalYear;
    private Integer fiscalMonth;
    private String errorMsg = "";
    private List<String> documentNos = new ArrayList<String>();
    private DepreciationBatchDao depreciationBatchDao;

    /**
     * @see org.kuali.kfs.module.cam.batch.service.AssetDepreciationService#runDepreciation()
     */
    public void runDepreciation() {
        List<String[]> reportLog = new ArrayList<String[]>();
        boolean hasErrors = false;
        Calendar depreciationDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();
        String depreciationDateParameter = null;
        DateFormat dateFormat = new SimpleDateFormat(CamsConstants.DateFormats.YEAR_MONTH_DAY);

        try {
            LOG.info("*******" + CamsConstants.Depreciation.DEPRECIATION_BATCH + " HAS BEGUN *******");

            /*
             * Getting the system parameter "DEPRECIATION_DATE" When this parameter is used to determine which fiscal month and year
             * is going to be depreciated If blank then the system will take the system date to determine the fiscal period
             */
            if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_RUN_DATE_PARAMETER)) {
                depreciationDateParameter = ((List<String>) parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_RUN_DATE_PARAMETER)).get(0).trim();
            }
            else {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.DEPRECIATION_DATE_PARAMETER_NOT_FOUND));
            }
            // This validates the system parameter depreciation_date has a valid format of YYYY-MM-DD.
            if (depreciationDateParameter != null && !depreciationDateParameter.trim().equals("")) {
                try {
                    depreciationDate.setTime(dateFormat.parse(depreciationDateParameter));
                }
                catch (ParseException e) {
                    throw new IllegalArgumentException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.INVALID_DEPRECIATION_DATE_FORMAT));
                }
            }
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation run date: " + depreciationDateParameter);

            UniversityDate universityDate = (UniversityDate) businessObjectService.findBySinglePrimaryKey(UniversityDate.class, new java.sql.Date(depreciationDate.getTimeInMillis()));
            if (universityDate == null) {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
            }

            this.fiscalYear = universityDate.getUniversityFiscalYear();
            this.fiscalMonth = new Integer(universityDate.getUniversityFiscalAccountingPeriod());
            // If the depreciation date is not = to the system date then, the depreciation process cannot run.
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Fiscal Year = " + this.fiscalYear + " & Fiscal Period=" + this.fiscalMonth);
            reportLog.addAll(depreciableAssetsDao.generateStatistics(true, null, fiscalYear, fiscalMonth, depreciationDate));
            // update if fiscal period is 12
            depreciationBatchDao.updateAssetsCreatedInLastFiscalPeriod(fiscalMonth, fiscalYear);
            // Retrieving eligible asset payment details
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting list of asset payments eligible for depreciation.");
            Collection<AssetPaymentInfo> depreciableAssetsCollection = depreciationBatchDao.getListOfDepreciableAssetPaymentInfo(this.fiscalYear, this.fiscalMonth, depreciationDate);
            // if we have assets eligible for depreciation then, calculate depreciation and create glpe's transactions
            if (depreciableAssetsCollection != null && !depreciableAssetsCollection.isEmpty()) {
                SortedMap<String, AssetDepreciationTransaction> depreciationTransactions = this.calculateDepreciation(depreciableAssetsCollection, depreciationDate);
                processGeneralLedgerPendingEntry(depreciationTransactions);
            }
            else {
                throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.NO_ELIGIBLE_FOR_DEPRECIATION_ASSETS_FOUND));
            }
        }
        catch (Exception e) {
            LOG.info("Error occurred");
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "**************************************************************************");
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "AN ERROR HAS OCCURRED! - ERROR: " + e.getMessage());
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "**************************************************************************");
            hasErrors = true;
            this.errorMsg = "Depreciation process ran unsucessfuly.\nReason:" + e.getMessage();
        }
        finally {
            if (!hasErrors) {
                reportLog.addAll(depreciableAssetsDao.generateStatistics(false, this.documentNos, fiscalYear, fiscalMonth, depreciationDate));
            }
            // the report will be generated only when there is an error or when the log has something.
            if (!reportLog.isEmpty() || !errorMsg.trim().equals(""))
                reportService.generateDepreciationReport(reportLog, errorMsg, depreciationDateParameter);

            LOG.info("*******" + CamsConstants.Depreciation.DEPRECIATION_BATCH + " HAS ENDED *******");
        }
    }

    /**
     * This method calculates the depreciation of each asset payment, creates the depreciation transactions that will be stored in
     * the general ledger pending entry table
     * 
     * @param depreciableAssetsCollection asset payments eligible for depreciation
     * @return SortedMap with a list of depreciation transactions
     */
    protected SortedMap<String, AssetDepreciationTransaction> calculateDepreciation(Collection<AssetPaymentInfo> depreciableAssetsCollection, Calendar depreciationDate) {
        LOG.debug("calculateDepreciation() - start");

        List<String> organizationPlantFundObjectSubType = new ArrayList<String>();
        List<String> campusPlantFundObjectSubType = new ArrayList<String>();
        SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary = new TreeMap<String, AssetDepreciationTransaction>();
        double monthsElapsed = 0d;
        double assetLifeInMonths = 0d;
        KualiDecimal accumulatedDepreciationAmount = KualiDecimal.ZERO;
        Calendar assetDepreciationDate = Calendar.getInstance();

        try {
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting the parameters for the plant fund object sub types.");
            // Getting system parameters needed.
            if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES)) {
                organizationPlantFundObjectSubType = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES);
            }
            if (parameterService.parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES)) {
                campusPlantFundObjectSubType = parameterService.getParameterValues(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES);
            }
            // Initializing the asset payment table.
            depreciationBatchDao.resetPeriodValuesWhenFirstFiscalPeriod(fiscalMonth);
            LOG.debug("getBaseAmountOfAssets(Collection<AssetPayment> depreciableAssetsCollection) - Started.");
            // Invoking method that will calculate the base amount for each asset payment transactions, which could be more than 1
            // per asset.
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Calculating the base amount for each asset.");
            Map<Long, KualiDecimal> salvageValueAssetDeprAmounts = depreciationBatchDao.getPrimaryDepreciationBaseAmountForSV();
            // Retrieving the object asset codes.
            Map<String, AssetObjectCode> assetObjectCodeMap = buildChartObjectToCapitalizationObjectMap();
            Map<String, ObjectCode> capitalizationObjectCodes = new HashMap<String, ObjectCode>();

            // Reading asset payments
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Reading collection with eligible asset payment details.");
            int counter = 0;
            List<AssetPaymentInfo> saveList = new ArrayList<AssetPaymentInfo>();
            for (AssetPaymentInfo assetPaymentInfo : depreciableAssetsCollection) {
                AssetObjectCode assetObjectCode = assetObjectCodeMap.get(assetPaymentInfo.getChartOfAccountsCode() + "-" + assetPaymentInfo.getFinancialObjectCode());
                if (assetObjectCode == null) {
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset object code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetPaymentInfo.getFinancialObjectCode());
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }
                ObjectCode accumulatedDepreciationFinancialObject = getDepreciationObjectCode(capitalizationObjectCodes, assetPaymentInfo, assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                ObjectCode depreciationExpenseFinancialObject = getDepreciationObjectCode(capitalizationObjectCodes, assetPaymentInfo, assetObjectCode.getDepreciationExpenseFinancialObjectCode());

                if (ObjectUtils.isNull(accumulatedDepreciationFinancialObject)) {
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Accumulated Depreciation Financial Object Code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }

                if (ObjectUtils.isNull(depreciationExpenseFinancialObject)) {
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation Expense Financial Object Code not found for " + fiscalYear + "-" + assetPaymentInfo.getChartOfAccountsCode() + "-" + assetObjectCode.getDepreciationExpenseFinancialObjectCode());
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }
                Long assetNumber = assetPaymentInfo.getCapitalAssetNumber();
                assetDepreciationDate.setTime(assetPaymentInfo.getDepreciationDate());
                accumulatedDepreciationAmount = KualiDecimal.ZERO;
                KualiDecimal deprAmountSum = salvageValueAssetDeprAmounts.get(assetNumber);
                // Calculating the life of the asset in months.
                assetLifeInMonths = assetPaymentInfo.getDepreciableLifeLimit() * 12;
                // Calculating the months elapsed for the asset using the depreciation date and the asset service date.
                monthsElapsed = (depreciationDate.get(Calendar.MONTH) - assetDepreciationDate.get(Calendar.MONTH) + (depreciationDate.get(Calendar.YEAR) - assetDepreciationDate.get(Calendar.YEAR)) * 12) + 1;

                // **************************************************************************************************************
                // CALCULATING ACCUMULATED DEPRECIATION BASED ON FORMULA FOR SINGLE LINE AND SALVAGE VALUE DEPRECIATION METHODS.
                // **************************************************************************************************************
                KualiDecimal primaryDepreciationBaseAmount = assetPaymentInfo.getPrimaryDepreciationBaseAmount();
                if (primaryDepreciationBaseAmount == null)
                    assetPaymentInfo.setPrimaryDepreciationBaseAmount(KualiDecimal.ZERO);

                if (assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount() == null)
                    assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(KualiDecimal.ZERO);

                // If the months elapsed >= to the life of the asset (in months) then, the accumulated depreciation should be:
                if (monthsElapsed >= assetLifeInMonths) {
                    if (CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode())) {
                        accumulatedDepreciationAmount = primaryDepreciationBaseAmount;
                    }
                    else if (CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode()) && deprAmountSum != null && deprAmountSum.isNonZero()) {
                        accumulatedDepreciationAmount = primaryDepreciationBaseAmount.subtract((primaryDepreciationBaseAmount.divide(deprAmountSum)).multiply(assetPaymentInfo.getSalvageAmount()));
                    }
                } // If the month elapse < to the life of the asset (in months) then....
                else {
                    if (CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode())) {
                        accumulatedDepreciationAmount = new KualiDecimal((monthsElapsed / assetLifeInMonths) * primaryDepreciationBaseAmount.doubleValue());
                    }
                    else if (CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE.equals(assetPaymentInfo.getPrimaryDepreciationMethodCode()) && deprAmountSum != null && deprAmountSum.isNonZero()) {
                        accumulatedDepreciationAmount = new KualiDecimal((monthsElapsed / assetLifeInMonths) * (primaryDepreciationBaseAmount.subtract((primaryDepreciationBaseAmount.divide(deprAmountSum)).multiply(assetPaymentInfo.getSalvageAmount()))).doubleValue());
                    }
                }
                // Calculating in process fiscal month depreciation amount
                KualiDecimal transactionAmount = accumulatedDepreciationAmount.subtract(assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount());

                String transactionType = KFSConstants.GL_DEBIT_CODE;
                if (transactionAmount.isNegative()) {
                    transactionType = KFSConstants.GL_CREDIT_CODE;
                }
                String plantAccount = "";
                String plantCOA = "";

                // getting the right Plant Fund Chart code & Plant Fund Account
                if (organizationPlantFundObjectSubType.contains(assetPaymentInfo.getFinancialObjectSubTypeCode())) {
                    plantAccount = assetPaymentInfo.getOrganizationPlantAccountNumber();
                    plantCOA = assetPaymentInfo.getOrganizationPlantChartCode();
                }
                else if (campusPlantFundObjectSubType.contains(assetPaymentInfo.getFinancialObjectSubTypeCode())) {
                    plantAccount = assetPaymentInfo.getCampusPlantAccountNumber();
                    plantCOA = assetPaymentInfo.getCampusPlantChartCode();
                }
                if (StringUtils.isBlank(plantCOA) || StringUtils.isBlank(plantAccount)) {
                    // skip the payment
                    LOG.error(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Plant COA is " + plantCOA + " and plant account is " + plantAccount + " for Financial Object SubType Code = " + assetPaymentInfo.getFinancialObjectSubTypeCode() + " so Asset payment is not included in depreciation " + assetPaymentInfo.getCapitalAssetNumber() + " - " + assetPaymentInfo.getPaymentSequenceNumber());
                    continue;
                }
                LOG.info("Asset#: " + assetNumber + " - Payment sequence#:" + assetPaymentInfo.getPaymentSequenceNumber() + " - Asset Depreciation date:" + assetDepreciationDate + " - Life:" + assetLifeInMonths + " - Depreciation base amt:" + primaryDepreciationBaseAmount + " - Accumulated depreciation:" + assetPaymentInfo.getAccumulatedPrimaryDepreciationAmount() + " - Month Elapsed:" + monthsElapsed + " - Calculated accum depreciation:" + accumulatedDepreciationAmount + " - Depreciation amount:" + transactionAmount.toString() + " - Depreciation Method:" + assetPaymentInfo.getPrimaryDepreciationMethodCode());
                assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(accumulatedDepreciationAmount);
                assetPaymentInfo.setTransactionAmount(transactionAmount);
                counter++;
                saveList.add(assetPaymentInfo);
                // Saving depreciation amount in the asset payment table
                if (counter % 1000 == 0) {
                    getDepreciationBatchDao().updateAssetPayments(saveList, fiscalMonth);
                    saveList.clear();
                }
                // if the asset has a depreciation amount <> 0 then, create its debit and credit entries.
                if (transactionAmount.isNonZero()) {
                    this.populateDepreciationTransaction(assetPaymentInfo, transactionType, plantCOA, plantAccount, depreciationExpenseFinancialObject, depreciationTransactionSummary);
                    transactionType = (transactionType.equals(KFSConstants.GL_DEBIT_CODE) ? KFSConstants.GL_CREDIT_CODE : KFSConstants.GL_DEBIT_CODE);
                    this.populateDepreciationTransaction(assetPaymentInfo, transactionType, plantCOA, plantAccount, accumulatedDepreciationFinancialObject, depreciationTransactionSummary);
                }
            }
            getDepreciationBatchDao().updateAssetPayments(saveList, fiscalMonth);
            saveList.clear();
            return depreciationTransactionSummary;
        }
        catch (Exception e) {
            LOG.error("Error occurred", e);
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.ERROR_WHEN_CALCULATING_DEPRECIATION) + " :" + e.getMessage());
        }
    }


    /**
     * This method stores in a collection of business objects the depreciation transaction that later on will be passed to the
     * processGeneralLedgerPendingEntry method in order to store the records in gl pending entry table
     * 
     * @param assetPayment asset payment
     * @param transactionType which can be [C]redit or [D]ebit
     * @param plantCOA plant fund char of account code
     * @param plantAccount plant fund char of account code
     * @param financialObject char of account object code linked to the payment
     * @param depreciationTransactionSummary
     * @return none
     */
    protected void populateDepreciationTransaction(AssetPaymentInfo assetPayment, String transactionType, String plantCOA, String plantAccount, ObjectCode deprObjectCode, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) {
        LOG.debug("populateDepreciationTransaction(AssetDepreciationTransaction depreciationTransaction, AssetPayment assetPayment, String transactionType, KualiDecimal transactionAmount, String plantCOA, String plantAccount, String accumulatedDepreciationFinancialObjectCode, String depreciationExpenseFinancialObjectCode, ObjectCode financialObject, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) -  started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "populateDepreciationTransaction(): populating AssetDepreciationTransaction pojo - Asset#:" + assetPayment.getCapitalAssetNumber());
        AssetDepreciationTransaction depreciationTransaction = new AssetDepreciationTransaction();
        depreciationTransaction.setCapitalAssetNumber(assetPayment.getCapitalAssetNumber());
        depreciationTransaction.setChartOfAccountsCode(plantCOA);
        depreciationTransaction.setAccountNumber(plantAccount);
        depreciationTransaction.setSubAccountNumber(assetPayment.getSubAccountNumber());
        depreciationTransaction.setFinancialObjectCode(deprObjectCode.getFinancialObjectCode());
        depreciationTransaction.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        depreciationTransaction.setFinancialObjectTypeCode(deprObjectCode.getFinancialObjectTypeCode());
        depreciationTransaction.setTransactionType(transactionType);
        depreciationTransaction.setProjectCode(assetPayment.getProjectCode());
        depreciationTransaction.setTransactionAmount(assetPayment.getTransactionAmount());
        depreciationTransaction.setTransactionLedgerEntryDescription(CamsConstants.Depreciation.TRANSACTION_DESCRIPTION + assetPayment.getCapitalAssetNumber());

        String sKey = depreciationTransaction.getKey();

        // Grouping the asset transactions by asset#, accounts, sub account, object, transaction type (C/D), etc. in order to
        // only have one credit and one credit by group.
        if (depreciationTransactionSummary.containsKey(sKey)) {
            depreciationTransaction = depreciationTransactionSummary.get(sKey);
            depreciationTransaction.setTransactionAmount(depreciationTransaction.getTransactionAmount().add(assetPayment.getTransactionAmount()));
        }
        else {
            depreciationTransactionSummary.put(sKey, depreciationTransaction);
        }
        LOG.debug("populateDepreciationTransaction(AssetDepreciationTransaction depreciationTransaction, AssetPayment assetPayment, String transactionType, KualiDecimal transactionAmount, String plantCOA, String plantAccount, String accumulatedDepreciationFinancialObjectCode, String depreciationExpenseFinancialObjectCode, ObjectCode financialObject, SortedMap<String, AssetDepreciationTransaction> depreciationTransactionSummary) -  ended");
    }

    /**
     * This method stores the depreciation transactions in the general pending entry table and creates a new documentHeader entry.
     * <p>
     * 
     * @param trans SortedMap with the transactions
     * @return none
     */
    protected void processGeneralLedgerPendingEntry(SortedMap<String, AssetDepreciationTransaction> trans) {
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");

        String financialSystemDocumentTypeCodeCode;
        try {

            String documentNumber = createNewDepreciationDocument();
            financialSystemDocumentTypeCodeCode = CamsConstants.DocumentTypeName.ASSET_DEPRECIATION;
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Depreciation Document Type Code: " + financialSystemDocumentTypeCodeCode);

            Timestamp transactionTimestamp = new Timestamp(dateTimeService.getCurrentDate().getTime());

            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
            List<GeneralLedgerPendingEntry> saveList = new ArrayList<GeneralLedgerPendingEntry>();
            int counter = 0;

            for (AssetDepreciationTransaction t : trans.values()) {
                if (t.getTransactionAmount().isNonZero()) {
                    counter++;
                    LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Creating GLPE entries for asset:" + t.getCapitalAssetNumber());
                    GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
                    explicitEntry.setFinancialSystemOriginationCode(KFSConstants.ORIGIN_CODE_KUALI);
                    explicitEntry.setDocumentNumber(documentNumber);
                    explicitEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
                    sequenceHelper.increment();
                    explicitEntry.setChartOfAccountsCode(t.getChartOfAccountsCode());
                    explicitEntry.setAccountNumber(t.getAccountNumber());
                    explicitEntry.setSubAccountNumber(t.getSubAccountNumber());
                    explicitEntry.setFinancialObjectCode(t.getFinancialObjectCode());
                    explicitEntry.setFinancialSubObjectCode(t.getFinancialSubObjectCode());
                    explicitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
                    explicitEntry.setFinancialObjectTypeCode(t.getFinancialObjectTypeCode());
                    explicitEntry.setUniversityFiscalYear(this.fiscalYear);
                    explicitEntry.setUniversityFiscalPeriodCode(StringUtils.leftPad(this.fiscalMonth.toString().trim(), 2, "0"));
                    explicitEntry.setTransactionLedgerEntryDescription(t.getTransactionLedgerEntryDescription());
                    explicitEntry.setTransactionLedgerEntryAmount(t.getTransactionAmount().abs());
                    explicitEntry.setTransactionDebitCreditCode(t.getTransactionType());
                    explicitEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
                    explicitEntry.setFinancialDocumentTypeCode(financialSystemDocumentTypeCodeCode);
                    explicitEntry.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
                    explicitEntry.setVersionNumber(new Long(1));
                    explicitEntry.setTransactionEntryProcessedTs(new java.sql.Timestamp(transactionTimestamp.getTime()));
                    // this.generalLedgerPendingEntryService.save(explicitEntry);
                    saveList.add(explicitEntry);
                    if (counter % 1000 == 0) {
                        // save here
                        getDepreciationBatchDao().savePendingGLEntries(saveList);
                        saveList.clear();
                    }
                    if (sequenceHelper.getSequenceCounter() == 99999) {
                        // create new document and sequence is reset
                        documentNumber = createNewDepreciationDocument();
                        sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
                    }
                }
            }
            // save last list
            getDepreciationBatchDao().savePendingGLEntries(saveList);
            saveList.clear();

        }
        catch (Exception e) {
            LOG.error("Error occurred", e);
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(CamsKeyConstants.Depreciation.ERROR_WHEN_UPDATING_GL_PENDING_ENTRY_TABLE) + " :" + e.getMessage());
        }
        LOG.debug("populateExplicitGeneralLedgerPendingEntry(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - end");
    }


    protected String createNewDepreciationDocument() throws WorkflowException {
        KualiWorkflowDocument workflowDocument = workflowDocumentService.createWorkflowDocument(CamsConstants.DocumentTypeName.ASSET_DEPRECIATION, GlobalVariables.getUserSession().getPerson());
        // **************************************************************************************************
        // Create a new document header object
        // **************************************************************************************************
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Creating document header entry.");

        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setWorkflowDocument(workflowDocument);
        documentHeader.setDocumentNumber(workflowDocument.getRouteHeaderId().toString());
        documentHeader.setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        documentHeader.setExplanation(CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
        documentHeader.setDocumentDescription(CamsConstants.Depreciation.DOCUMENT_DESCRIPTION);
        documentHeader.setFinancialDocumentTotalAmount(KualiDecimal.ZERO);

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Saving document header entry.");
        this.businessObjectService.save(documentHeader);
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Document Header entry was saved successfully.");
        // **************************************************************************************************

        String documentNumber = documentHeader.getDocumentNumber();
        this.documentNos.add(documentNumber);
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Document Number Created: " + documentNumber);
        return documentNumber;
    }


    /**
     * Depreciation object code is returned from cache or from DB
     * 
     * @param capitalizationObjectCodes collection cache
     * @param assetPaymentInfo
     * @param capitalizationFinancialObjectCode
     * @return
     */
    protected ObjectCode getDepreciationObjectCode(Map<String, ObjectCode> capObjectCodesCache, AssetPaymentInfo assetPaymentInfo, String capitalizationFinancialObjectCode) {
        ObjectCode deprObjCode = null;
        String key = assetPaymentInfo.getChartOfAccountsCode() + "-" + capitalizationFinancialObjectCode;
        if ((deprObjCode = capObjectCodesCache.get(key)) == null) {
            deprObjCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(fiscalYear, assetPaymentInfo.getChartOfAccountsCode(), capitalizationFinancialObjectCode);
            if (ObjectUtils.isNotNull(deprObjCode)) {
                capObjectCodesCache.put(key, deprObjCode);
            }
        }
        return deprObjCode;
    }

    /**
     * Builds map between object code to corresponding asset object code
     * 
     * @return Map
     */
    protected Map<String, AssetObjectCode> buildChartObjectToCapitalizationObjectMap() {
        Map<String, AssetObjectCode> assetObjectCodeMap = new HashMap<String, AssetObjectCode>();
        Collection<AssetObjectCode> assetObjectCodes = depreciableAssetsDao.getAssetObjectCodes(fiscalYear);

        for (AssetObjectCode assetObjectCode : assetObjectCodes) {
            List<ObjectCode> objectCodes = assetObjectCode.getObjectCode();
            for (ObjectCode objectCode : objectCodes) {
                String key = objectCode.getChartOfAccountsCode() + "-" + objectCode.getFinancialObjectCode();
                if (!assetObjectCodeMap.containsKey(key)) {
                    assetObjectCodeMap.put(key, assetObjectCode);
                }
            }
        }
        return assetObjectCodeMap;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setDepreciableAssetsDao(DepreciableAssetsDao depreciableAssetsDao) {
        this.depreciableAssetsDao = depreciableAssetsDao;
    }

    public void setCamsReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setUniversityDateDao(UniversityDateDao universityDateDao) {
        this.universityDateDao = universityDateDao;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }


    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the depreciationBatchDao attribute.
     * 
     * @return Returns the depreciationBatchDao.
     */
    public DepreciationBatchDao getDepreciationBatchDao() {
        return depreciationBatchDao;
    }

    /**
     * Sets the depreciationBatchDao attribute value.
     * 
     * @param depreciationBatchDao The depreciationBatchDao to set.
     */
    public void setDepreciationBatchDao(DepreciationBatchDao depreciationBatchDao) {
        this.depreciationBatchDao = depreciationBatchDao;
    }
}
