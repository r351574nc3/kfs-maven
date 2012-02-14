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
package org.kuali.kfs.module.bc.document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCConstants.AccountSalarySettingOnlyCause;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountReports;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.service.BudgetParameterService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.TypedArrayList;

public class BudgetConstructionDocument extends FinancialSystemTransactionalDocumentBase {

    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionDocument.class);

    protected Integer universityFiscalYear;
    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String subAccountNumber;
    protected Integer organizationLevelCode;
    protected String organizationLevelChartOfAccountsCode;
    protected String organizationLevelOrganizationCode;
    protected String budgetLockUserIdentifier;
    protected String budgetTransactionLockUserIdentifier;

    protected Chart chartOfAccounts;
    protected Account account;
    protected SubAccount subAccount;
    protected Person budgetLockUser;
    protected Person budgetTransactionLockUser;
    protected Organization organizationLevelOrganization;
    protected BudgetConstructionAccountReports budgetConstructionAccountReports;

    protected List pendingBudgetConstructionGeneralLedgerRevenueLines;
    protected List pendingBudgetConstructionGeneralLedgerExpenditureLines;

    protected Integer previousUniversityFiscalYear;

    // revenue and expenditure line totals
    protected KualiInteger revenueAccountLineAnnualBalanceAmountTotal;
    protected KualiInteger revenueFinancialBeginningBalanceLineAmountTotal;
    protected KualiDecimal revenuePercentChangeTotal;
    protected KualiInteger expenditureAccountLineAnnualBalanceAmountTotal;
    protected KualiInteger expenditureFinancialBeginningBalanceLineAmountTotal;
    protected KualiDecimal expenditurePercentChangeTotal;

    // benefits calculation state flags
    // these are set when a change is detected in the request and the line
    // is involved in the benefits calculation - ie exists in
    protected boolean isBenefitsCalcNeeded;
    protected boolean isMonthlyBenefitsCalcNeeded;

    protected boolean isSalarySettingOnly;
    protected AccountSalarySettingOnlyCause accountSalarySettingOnlyCause;
    protected boolean containsTwoPlug = false;
    protected boolean budgetableDocument = false;

    // This property supports a hack to indicate to the rules framework
    // the user is performing an action (save) that forces rules check on nonZero request amounts
    // while still allowing the user to do salary setting cleanup when a document becomes not budgetable
    protected boolean cleanupModeActionForceCheck = false;

    public BudgetConstructionDocument() {
        super();
        // setPendingBudgetConstructionGeneralLedgerExpenditureLines(new ArrayList());
        // setPendingBudgetConstructionGeneralLedgerRevenueLines(new ArrayList());
        setPendingBudgetConstructionGeneralLedgerExpenditureLines(new TypedArrayList(PendingBudgetConstructionGeneralLedger.class));
        setPendingBudgetConstructionGeneralLedgerRevenueLines(new TypedArrayList(PendingBudgetConstructionGeneralLedger.class));
        zeroTotals();
    }

    /**
     * This zeros revenue and expenditure totals displayed on the BC document screen
     */
    public void zeroTotals() {

        revenueAccountLineAnnualBalanceAmountTotal = new KualiInteger(BigDecimal.ZERO);
        revenueFinancialBeginningBalanceLineAmountTotal = new KualiInteger(BigDecimal.ZERO);
        revenuePercentChangeTotal = new KualiDecimal(0);
        expenditureAccountLineAnnualBalanceAmountTotal = new KualiInteger(BigDecimal.ZERO);
        expenditureFinancialBeginningBalanceLineAmountTotal = new KualiInteger(BigDecimal.ZERO);
        expenditurePercentChangeTotal = new KualiDecimal(0);
    }

    /**
     * move stuff from constructor to here so as to get out of fred's way initiateDocument would be called from
     * BudgetConstructionAction
     */
    public void initiateDocument() {


        Map fieldValues = new HashMap();
        // fieldValues.put("UNIV_FISCAL_YR", new Integer(2008));
        // fieldValues.put("FIN_COA_CD", "BA");
        // fieldValues.put("ACCOUNT_NBR", "6044906");
        // fieldValues.put("SUB_ACCT_NBR", "-----");
        // fieldValues.put("UNIV_FISCAL_YR", budgetConstructionHeader.getUniversityFiscalYear());
        // fieldValues.put("FIN_COA_CD", budgetConstructionHeader.getChartOfAccountsCode());
        // fieldValues.put("ACCOUNT_NBR", budgetConstructionHeader.getAccountNumber());
        // fieldValues.put("SUB_ACCT_NBR", budgetConstructionHeader.getSubAccountNumber());
        fieldValues.put("UNIV_FISCAL_YR", getUniversityFiscalYear());
        fieldValues.put("FIN_COA_CD", getChartOfAccountsCode());
        fieldValues.put("ACCOUNT_NBR", getAccountNumber());
        fieldValues.put("SUB_ACCT_NBR", getSubAccountNumber());

        // this needs to do query FIN_OBJ_TYP_CD IN ('IN','IC','IN') or equivalent
        fieldValues.put("FIN_OBJ_TYP_CD", "IN");

        pendingBudgetConstructionGeneralLedgerRevenueLines = (ArrayList) SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(PendingBudgetConstructionGeneralLedger.class, fieldValues, "FIN_OBJECT_CD", true);
        if (LOG.isDebugEnabled()) {
            LOG.debug("pendingBudgetConstructionGeneralLedgerRevenue is: " + pendingBudgetConstructionGeneralLedgerRevenueLines);
        }

        // this needs to do query FIN_OBJ_TYP_CD IN ('EE','ES','EX') or equivalent
        fieldValues.remove("FIN_OBJ_TYP_CD");
        fieldValues.put("FIN_OBJ_TYP_CD", "EX");

        pendingBudgetConstructionGeneralLedgerExpenditureLines = (ArrayList) SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(PendingBudgetConstructionGeneralLedger.class, fieldValues, "FIN_OBJECT_CD", true);
        if (LOG.isDebugEnabled()) {
            LOG.debug("pendingBudgetConstructionGeneralLedgerExpenditure is: " + pendingBudgetConstructionGeneralLedgerExpenditureLines);
        }
        // Iterator<PendingBudgetConstructionGeneralLedger> iter =
        // pendingBudgetConstructionGeneralLedgerExpenditureLines.iterator();
        // while (iter.hasNext()){
        // iter.next().refreshReferenceObject("budgetConstructionMonthly");
        // }

    }

    /**
     * This adds a revenue or expenditure line to the appropriate list
     * 
     * @param isRevenue
     * @param line
     */
    public int addPBGLLine(PendingBudgetConstructionGeneralLedger line, boolean isRevenue) {
        int insertPoint = 0;
        ListIterator pbglLines;
        if (isRevenue) {
            pbglLines = this.getPendingBudgetConstructionGeneralLedgerRevenueLines().listIterator();
        }
        else {
            pbglLines = this.getPendingBudgetConstructionGeneralLedgerExpenditureLines().listIterator();
        }
        while (pbglLines.hasNext()) {
            PendingBudgetConstructionGeneralLedger pbglLine = (PendingBudgetConstructionGeneralLedger) pbglLines.next();
            if (pbglLine.getFinancialObjectCode().compareToIgnoreCase(line.getFinancialObjectCode()) < 0) {
                insertPoint++;
            }
            else {
                if (pbglLine.getFinancialObjectCode().compareToIgnoreCase(line.getFinancialObjectCode()) > 0) {
                    break;
                }
                else {
                    if ((pbglLine.getFinancialObjectCode().compareToIgnoreCase(line.getFinancialObjectCode()) == 0) && (pbglLine.getFinancialSubObjectCode().compareToIgnoreCase(line.getFinancialSubObjectCode()) < 0)) {
                        insertPoint++;
                    }
                    else {
                        break;
                    }
                }
            }
        }
        if (isRevenue) {
            this.pendingBudgetConstructionGeneralLedgerRevenueLines.add(insertPoint, line);
        }
        else {
            this.pendingBudgetConstructionGeneralLedgerExpenditureLines.add(insertPoint, line);
        }
        return insertPoint;

    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
        setPreviousUniversityFiscalYear(universityFiscalYear - 1);
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    /**
     * Gets the organizationLevelCode attribute.
     * 
     * @return Returns the organizationLevelCode
     */
    public Integer getOrganizationLevelCode() {
        return organizationLevelCode;
    }

    /**
     * Sets the organizationLevelCode attribute.
     * 
     * @param organizationLevelCode The organizationLevelCode to set.
     */
    public void setOrganizationLevelCode(Integer organizationLevelCode) {
        this.organizationLevelCode = organizationLevelCode;
    }


    /**
     * Gets the organizationLevelChartOfAccountsCode attribute.
     * 
     * @return Returns the organizationLevelChartOfAccountsCode
     */
    public String getOrganizationLevelChartOfAccountsCode() {
        return organizationLevelChartOfAccountsCode;
    }

    /**
     * Sets the organizationLevelChartOfAccountsCode attribute.
     * 
     * @param organizationLevelChartOfAccountsCode The organizationLevelChartOfAccountsCode to set.
     */
    public void setOrganizationLevelChartOfAccountsCode(String organizationLevelChartOfAccountsCode) {
        this.organizationLevelChartOfAccountsCode = organizationLevelChartOfAccountsCode;
    }


    /**
     * Gets the organizationLevelOrganizationCode attribute.
     * 
     * @return Returns the organizationLevelOrganizationCode
     */
    public String getOrganizationLevelOrganizationCode() {
        return organizationLevelOrganizationCode;
    }

    /**
     * Sets the organizationLevelOrganizationCode attribute.
     * 
     * @param organizationLevelOrganizationCode The organizationLevelOrganizationCode to set.
     */
    public void setOrganizationLevelOrganizationCode(String organizationLevelOrganizationCode) {
        this.organizationLevelOrganizationCode = organizationLevelOrganizationCode;
    }


    /**
     * Gets the budgetLockUserIdentifier attribute.
     * 
     * @return Returns the budgetLockUserIdentifier
     */
    public String getBudgetLockUserIdentifier() {
        return budgetLockUserIdentifier;
    }

    /**
     * Sets the budgetLockUserIdentifier attribute.
     * 
     * @param budgetLockUserIdentifier The budgetLockUserIdentifier to set.
     */
    public void setBudgetLockUserIdentifier(String budgetLockUserIdentifier) {
        this.budgetLockUserIdentifier = budgetLockUserIdentifier;
    }


    /**
     * Gets the budgetTransactionLockUserIdentifier attribute.
     * 
     * @return Returns the budgetTransactionLockUserIdentifier
     */
    public String getBudgetTransactionLockUserIdentifier() {
        return budgetTransactionLockUserIdentifier;
    }

    /**
     * Sets the budgetTransactionLockUserIdentifier attribute.
     * 
     * @param budgetTransactionLockUserIdentifier The budgetTransactionLockUserIdentifier to set.
     */
    public void setBudgetTransactionLockUserIdentifier(String budgetTransactionLockUserIdentifier) {
        this.budgetTransactionLockUserIdentifier = budgetTransactionLockUserIdentifier;
    }


    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    public Person getBudgetLockUser() {
        budgetLockUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(budgetLockUserIdentifier, budgetLockUser);
        return budgetLockUser;
    }

    /**
     * Sets the budgetLockUser attribute.
     * 
     * @param budgetLockUser The budgetLockUser to set.
     * @deprecated
     */
    public void setBudgetLockUser(Person budgetLockUser) {
        this.budgetLockUser = budgetLockUser;
    }

    public Person getBudgetTransactionLockUser() {
        budgetTransactionLockUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(budgetTransactionLockUserIdentifier, budgetTransactionLockUser);
        return budgetTransactionLockUser;
    }

    /**
     * Sets the budgetTransactionLockUser attribute value.
     * 
     * @param budgetTransactionLockUser The budgetTransactionLockUser to set.
     * @deprecated
     */
    public void setBudgetTransactionLockUser(Person budgetTransactionLockUser) {
        this.budgetTransactionLockUser = budgetTransactionLockUser;
    }

    /**
     * Gets the organizationLevelOrganization attribute.
     * 
     * @return Returns the organizationLevelOrganization.
     */
    public Organization getOrganizationLevelOrganization() {
        return organizationLevelOrganization;
    }

    /**
     * Sets the organizationLevelOrganization attribute value.
     * 
     * @param organizationLevelOrganization The organizationLevelOrganization to set.
     * @deprecated
     */
    public void setOrganizationLevelOrganization(Organization organizationLevelOrganization) {
        this.organizationLevelOrganization = organizationLevelOrganization;
    }

    /**
     * Gets the subAccount attribute.
     * 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * 
     * @param subAccount The subAccount to set.
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the previousUniversityFiscalYear attribute.
     * 
     * @return Returns the previousUniversityFiscalYear.
     */
    public Integer getPreviousUniversityFiscalYear() {
        if (previousUniversityFiscalYear == null) {
            this.previousUniversityFiscalYear = this.getUniversityFiscalYear() - 1;
        }
        return previousUniversityFiscalYear;
    }

    /**
     * Sets the previousUniversityFiscalYear attribute value.
     * 
     * @param previousUniversityFiscalYear The previousUniversityFiscalYear to set.
     */
    public void setPreviousUniversityFiscalYear(Integer previousUniversityFiscalYear) {
        this.previousUniversityFiscalYear = previousUniversityFiscalYear;
    }

    /**
     * Gets the budgetConstructionAccountReports attribute.
     * 
     * @return Returns the budgetConstructionAccountReports.
     */
    public BudgetConstructionAccountReports getBudgetConstructionAccountReports() {
        return budgetConstructionAccountReports;
    }

    /**
     * Sets the budgetConstructionAccountReports attribute value.
     * 
     * @param budgetConstructionAccountReports The budgetConstructionAccountReports to set.
     * @deprecated
     */
    public void setBudgetConstructionAccountReports(BudgetConstructionAccountReports budgetConstructionAccountReports) {
        this.budgetConstructionAccountReports = budgetConstructionAccountReports;
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        // return new ArrayList();
        List managedLists = super.buildListOfDeletionAwareLists();

        managedLists.add(getPendingBudgetConstructionGeneralLedgerRevenueLines());
        managedLists.add(getPendingBudgetConstructionGeneralLedgerExpenditureLines());
        // managedLists.add(getSourceAccountingLines());
        // managedLists.add(getTargetAccountingLines());

        return managedLists;
    }

    public List<PendingBudgetConstructionGeneralLedger> getPendingBudgetConstructionGeneralLedgerRevenueLines() {
        return pendingBudgetConstructionGeneralLedgerRevenueLines;
    }

    public void setPendingBudgetConstructionGeneralLedgerRevenueLines(List pendingBudgetConstructionGeneralLedgerRevenueLines) {
        this.pendingBudgetConstructionGeneralLedgerRevenueLines = pendingBudgetConstructionGeneralLedgerRevenueLines;
    }

    public List<PendingBudgetConstructionGeneralLedger> getPendingBudgetConstructionGeneralLedgerExpenditureLines() {
        return pendingBudgetConstructionGeneralLedgerExpenditureLines;
    }

    public void setPendingBudgetConstructionGeneralLedgerExpenditureLines(List pendingBudgetConstructionGeneralLedgerExpenditureLines) {
        this.pendingBudgetConstructionGeneralLedgerExpenditureLines = pendingBudgetConstructionGeneralLedgerExpenditureLines;
    }

    /**
     * Gets the expenditureAccountLineAnnualBalanceAmountTotal attribute.
     * 
     * @return Returns the expenditureAccountLineAnnualBalanceAmountTotal.
     */
    public KualiInteger getExpenditureAccountLineAnnualBalanceAmountTotal() {
        return expenditureAccountLineAnnualBalanceAmountTotal;
    }

    /**
     * Sets the expenditureAccountLineAnnualBalanceAmountTotal attribute value.
     * 
     * @param expenditureAccountLineAnnualBalanceAmountTotal The expenditureAccountLineAnnualBalanceAmountTotal to set.
     */
    public void setExpenditureAccountLineAnnualBalanceAmountTotal(KualiInteger expenditureAccountLineAnnualBalanceAmountTotal) {
        this.expenditureAccountLineAnnualBalanceAmountTotal = expenditureAccountLineAnnualBalanceAmountTotal;
    }

    /**
     * Gets the expenditureFinancialBeginningBalanceLineAmountTotal attribute.
     * 
     * @return Returns the expenditureFinancialBeginningBalanceLineAmountTotal.
     */
    public KualiInteger getExpenditureFinancialBeginningBalanceLineAmountTotal() {
        return expenditureFinancialBeginningBalanceLineAmountTotal;
    }

    /**
     * Sets the expenditureFinancialBeginningBalanceLineAmountTotal attribute value.
     * 
     * @param expenditureFinancialBeginningBalanceLineAmountTotal The expenditureFinancialBeginningBalanceLineAmountTotal to set.
     */
    public void setExpenditureFinancialBeginningBalanceLineAmountTotal(KualiInteger expenditureFinancialBeginningBalanceLineAmountTotal) {
        this.expenditureFinancialBeginningBalanceLineAmountTotal = expenditureFinancialBeginningBalanceLineAmountTotal;
    }

    /**
     * Gets the revenueAccountLineAnnualBalanceAmountTotal attribute.
     * 
     * @return Returns the revenueAccountLineAnnualBalanceAmountTotal.
     */
    public KualiInteger getRevenueAccountLineAnnualBalanceAmountTotal() {
        return revenueAccountLineAnnualBalanceAmountTotal;
    }

    /**
     * Sets the revenueAccountLineAnnualBalanceAmountTotal attribute value.
     * 
     * @param revenueAccountLineAnnualBalanceAmountTotal The revenueAccountLineAnnualBalanceAmountTotal to set.
     */
    public void setRevenueAccountLineAnnualBalanceAmountTotal(KualiInteger revenueAccountLineAnnualBalanceAmountTotal) {
        this.revenueAccountLineAnnualBalanceAmountTotal = revenueAccountLineAnnualBalanceAmountTotal;
    }

    /**
     * Gets the revenueFinancialBeginningBalanceLineAmountTotal attribute.
     * 
     * @return Returns the revenueFinancialBeginningBalanceLineAmountTotal.
     */
    public KualiInteger getRevenueFinancialBeginningBalanceLineAmountTotal() {
        return revenueFinancialBeginningBalanceLineAmountTotal;
    }

    /**
     * Sets the revenueFinancialBeginningBalanceLineAmountTotal attribute value.
     * 
     * @param revenueFinancialBeginningBalanceLineAmountTotal The revenueFinancialBeginningBalanceLineAmountTotal to set.
     */
    public void setRevenueFinancialBeginningBalanceLineAmountTotal(KualiInteger revenueFinancialBeginningBalanceLineAmountTotal) {
        this.revenueFinancialBeginningBalanceLineAmountTotal = revenueFinancialBeginningBalanceLineAmountTotal;
    }

    /**
     * Gets the expenditurePercentChangeTotal attribute.
     * 
     * @return Returns the expenditurePercentChangeTotal.
     */
    public KualiDecimal getExpenditurePercentChangeTotal() {
        if (expenditureFinancialBeginningBalanceLineAmountTotal == null || expenditureFinancialBeginningBalanceLineAmountTotal.isZero()) {
            this.expenditurePercentChangeTotal = null;
        }
        else {
            BigDecimal diffRslt = (expenditureAccountLineAnnualBalanceAmountTotal.bigDecimalValue().setScale(4)).subtract(expenditureFinancialBeginningBalanceLineAmountTotal.bigDecimalValue().setScale(4));
            BigDecimal divRslt = diffRslt.divide((expenditureFinancialBeginningBalanceLineAmountTotal.bigDecimalValue().setScale(4)), KualiDecimal.ROUND_BEHAVIOR);
            this.expenditurePercentChangeTotal = new KualiDecimal(divRslt.multiply(BigDecimal.valueOf(100)).setScale(2));
        }
        return expenditurePercentChangeTotal;
    }

    /**
     * Sets the expenditurePercentChangeTotal attribute value.
     * 
     * @param expenditurePercentChangeTotal The expenditurePercentChangeTotal to set.
     */
    public void setExpenditurePercentChangeTotal(KualiDecimal expenditurePercentChangeTotal) {
        this.expenditurePercentChangeTotal = expenditurePercentChangeTotal;
    }

    /**
     * Gets the revenuePercentChangeTotal attribute.
     * 
     * @return Returns the revenuePercentChangeTotal.
     */
    public KualiDecimal getRevenuePercentChangeTotal() {
        if (revenueFinancialBeginningBalanceLineAmountTotal == null || revenueFinancialBeginningBalanceLineAmountTotal.isZero()) {
            this.revenuePercentChangeTotal = null;
        }
        else {
            BigDecimal diffRslt = (revenueAccountLineAnnualBalanceAmountTotal.bigDecimalValue().setScale(4)).subtract(revenueFinancialBeginningBalanceLineAmountTotal.bigDecimalValue().setScale(4));
            BigDecimal divRslt = diffRslt.divide((revenueFinancialBeginningBalanceLineAmountTotal.bigDecimalValue().setScale(4)), KualiDecimal.ROUND_BEHAVIOR);
            this.revenuePercentChangeTotal = new KualiDecimal(divRslt.multiply(BigDecimal.valueOf(100)).setScale(2));
        }
        return revenuePercentChangeTotal;
    }

    /**
     * Sets the revenuePercentChangeTotal attribute value.
     * 
     * @param revenuePercentChangeTotal The revenuePercentChangeTotal to set.
     */
    public void setRevenuePercentChangeTotal(KualiDecimal revenuePercentChangeTotal) {
        this.revenuePercentChangeTotal = revenuePercentChangeTotal;
    }

    /**
     * Gets the isBenefitsCalcNeeded attribute.
     * 
     * @return Returns the isBenefitsCalcNeeded.
     */
    public boolean isBenefitsCalcNeeded() {
        return isBenefitsCalcNeeded;
    }

    /**
     * Sets the isBenefitsCalcNeeded attribute value.
     * 
     * @param isBenefitsCalcNeeded The isBenefitsCalcNeeded to set.
     */
    public void setBenefitsCalcNeeded(boolean isBenefitsCalcNeeded) {
        this.isBenefitsCalcNeeded = isBenefitsCalcNeeded;
    }

    /**
     * Gets the isMonthlyBenefitsCalcNeeded attribute.
     * 
     * @return Returns the isMonthlyBenefitsCalcNeeded.
     */
    public boolean isMonthlyBenefitsCalcNeeded() {
        return isMonthlyBenefitsCalcNeeded;
    }

    /**
     * Sets the isMonthlyBenefitsCalcNeeded attribute value.
     * 
     * @param isMonthlyBenefitsCalcNeeded The isMonthlyBenefitsCalcNeeded to set.
     */
    public void setMonthlyBenefitsCalcNeeded(boolean isMonthlyBenefitsCalcNeeded) {
        this.isMonthlyBenefitsCalcNeeded = isMonthlyBenefitsCalcNeeded;
    }

    /**
     * Gets the isSalarySettingOnly attribute.
     * 
     * @return Returns the isSalarySettingOnly.
     */
    public boolean isSalarySettingOnly() {
        if (this.getAccountSalarySettingOnlyCause() == AccountSalarySettingOnlyCause.MISSING_PARAM || this.getAccountSalarySettingOnlyCause() == AccountSalarySettingOnlyCause.NONE) {
            isSalarySettingOnly = false;
        }
        else {
            isSalarySettingOnly = true;
        }
        return isSalarySettingOnly;
    }

    /**
     * Sets the isSalarySettingOnly attribute value.
     * 
     * @param isSalarySettingOnly The isSalarySettingOnly to set.
     */
    public void setSalarySettingOnly(boolean isSalarySettingOnly) {
        this.isSalarySettingOnly = isSalarySettingOnly;
    }

    /**
     * Gets the accountSalarySettingOnlyCause attribute.
     * 
     * @return Returns the accountSalarySettingOnlyCause.
     */
    public AccountSalarySettingOnlyCause getAccountSalarySettingOnlyCause() {
        if (accountSalarySettingOnlyCause == null) {
            accountSalarySettingOnlyCause = SpringContext.getBean(BudgetParameterService.class).isSalarySettingOnlyAccount(this);
        }

        return accountSalarySettingOnlyCause;
    }

    /**
     * Sets the accountSalarySettingOnlyCause attribute value.
     * 
     * @param accountSalarySettingOnlyCause The accountSalarySettingOnlyCause to set.
     */
    public void setAccountSalarySettingOnlyCause(AccountSalarySettingOnlyCause accountSalarySettingOnlyCause) {
        this.accountSalarySettingOnlyCause = accountSalarySettingOnlyCause;
    }

    /**
     * Gets the containsTwoPlug attribute.
     * 
     * @return Returns the containsTwoPlug.
     */
    public boolean isContainsTwoPlug() {
        return containsTwoPlug;
    }

    /**
     * Sets the containsTwoPlug attribute value.
     * 
     * @param containsTwoPlug The containsTwoPlug to set.
     */
    public void setContainsTwoPlug(boolean containsTwoPlug) {
        this.containsTwoPlug = containsTwoPlug;
    }

    /**
     * Gets the budgetableDocument attribute.
     * 
     * @return Returns the budgetableDocument.
     */
    public boolean isBudgetableDocument() {
        return budgetableDocument;
    }

    /**
     * Sets the budgetableDocument attribute value.
     * 
     * @param budgetableDocument The budgetableDocument to set.
     */
    public void setBudgetableDocument(boolean budgetableDocument) {
        this.budgetableDocument = budgetableDocument;
    }

    /**
     * Gets the cleanupModeActionForceCheck attribute.
     * 
     * @return Returns the cleanupModeActionForceCheck.
     */
    public boolean isCleanupModeActionForceCheck() {
        return cleanupModeActionForceCheck;
    }

    /**
     * Sets the cleanupModeActionForceCheck attribute value.
     * 
     * @param cleanupModeActionForceCheck The cleanupModeActionForceCheck to set.
     */
    public void setCleanupModeActionForceCheck(boolean cleanupModeActionForceCheck) {
        this.cleanupModeActionForceCheck = cleanupModeActionForceCheck;
    }

    /**
     * the budget construction document never appears in anyone's in-box budget construction controls access by a
     * "pull-up/push-down" mechanism instead but, a budget construction document is routed so that the routing hierarchy can be used
     * to trace who has modified the document we override the routine below from Document we record the processed document state. a
     * budget construction document will never be "cancelled" or "disapproved"
     * 
     * @see org.kuali.rice.kns.document.Document#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        if (getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.ENROUTE);
        }
        /* the status below is comparable to "approved" status for other documents */
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            getDocumentHeader().setFinancialDocumentStatusCode(BCConstants.BUDGET_CONSTRUCTION_DOCUMENT_INITIAL_STATUS);
        }
        LOG.info("Status is: " + getDocumentHeader().getFinancialDocumentStatusCode());
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        return m;
    }

}
