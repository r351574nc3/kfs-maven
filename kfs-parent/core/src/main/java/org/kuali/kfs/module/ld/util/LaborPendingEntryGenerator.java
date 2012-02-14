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
package org.kuali.kfs.module.ld.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit;
import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument;
import org.kuali.kfs.module.ld.document.service.LaborPendingEntryConverterService;
import org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService;
import org.kuali.kfs.module.ld.service.LaborPositionObjectBenefitService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class is used to help generating pending entries for the given labor documents
 */
public class LaborPendingEntryGenerator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborPendingEntryGenerator.class);

    /**
     * generate the expense pending entries based on the given document and accouting line
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given squence helper
     * @return a set of expense pending entries
     */
    public static List<LaborLedgerPendingEntry> generateExpensePendingEntries(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        List<LaborLedgerPendingEntry> expensePendingEntries = new ArrayList<LaborLedgerPendingEntry>();
        LaborLedgerPendingEntry expensePendingEntry = SpringContext.getBean(LaborPendingEntryConverterService.class).getExpensePendingEntry(document, accountingLine, sequenceHelper);
        expensePendingEntries.add(expensePendingEntry);

        // if the AL's pay FY and period do not match the University fiscal year and period need to create a reversal entry for
        // current period
        if (!isAccountingLinePayFYPeriodMatchesUniversityPayFYPeriod(document, accountingLine)) {
            LaborLedgerPendingEntry expenseA21PendingEntry = SpringContext.getBean(LaborPendingEntryConverterService.class).getExpenseA21PendingEntry(document, accountingLine, sequenceHelper);
            expensePendingEntries.add(expenseA21PendingEntry);

            LaborLedgerPendingEntry expenseA21ReversalPendingEntry = SpringContext.getBean(LaborPendingEntryConverterService.class).getExpenseA21ReversalPendingEntry(document, accountingLine, sequenceHelper);
            expensePendingEntries.add(expenseA21ReversalPendingEntry);
        }

        return expensePendingEntries;
    }

    /**
     * generate the benefit pending entries based on the given document and accounting line
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given squence helper
     * @return a set of benefit pending entries
     */
    public static List<LaborLedgerPendingEntry> generateBenefitPendingEntries(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        accountingLine.refreshReferenceObject(KFSPropertyConstants.LABOR_OBJECT);
        if (ObjectUtils.isNull(accountingLine.getLaborObject())) {
            return null;
        }

        String FringeOrSalaryCode = accountingLine.getLaborObject().getFinancialObjectFringeOrSalaryCode();
        if (!LaborConstants.SalaryExpenseTransfer.LABOR_LEDGER_SALARY_CODE.equals(FringeOrSalaryCode)) {
            return null;
        }

        Integer payrollFiscalyear = accountingLine.getPayrollEndDateFiscalYear();
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String objectCode = accountingLine.getFinancialObjectCode();
        Collection<PositionObjectBenefit> positionObjectBenefits = SpringContext.getBean(LaborPositionObjectBenefitService.class).getPositionObjectBenefits(payrollFiscalyear, chartOfAccountsCode, objectCode);

        List<LaborLedgerPendingEntry> benefitPendingEntries = new ArrayList<LaborLedgerPendingEntry>();
        for (PositionObjectBenefit positionObjectBenefit : positionObjectBenefits) {
            String fringeBenefitObjectCode = positionObjectBenefit.getBenefitsCalculation().getPositionFringeBenefitObjectCode();

            KualiDecimal benefitAmount = SpringContext.getBean(LaborBenefitsCalculationService.class).calculateFringeBenefit(positionObjectBenefit, accountingLine.getAmount());
            if (benefitAmount.isNonZero()) {
                List<LaborLedgerPendingEntry> pendingEntries = generateBenefitPendingEntries(document, accountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);
                benefitPendingEntries.addAll(pendingEntries);
            }
        }

        return benefitPendingEntries;
    }

    /**
     * generate the benefit pending entries with the given benefit amount and finge benefit object code based on the given document
     * and accouting line
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given squence helper
     * @param benefitAmount the given benefit amount
     * @param fringeBenefitObjectCode the given finge benefit object code
     * @return a set of benefit pending entries with the given benefit amount and finge benefit object code
     */
    public static List<LaborLedgerPendingEntry> generateBenefitPendingEntries(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {
        List<LaborLedgerPendingEntry> benefitPendingEntries = new ArrayList<LaborLedgerPendingEntry>();
        LaborLedgerPendingEntry benefitPendingEntry = SpringContext.getBean(LaborPendingEntryConverterService.class).getBenefitPendingEntry(document, accountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);
        benefitPendingEntries.add(benefitPendingEntry);

        // if the AL's pay FY and period do not match the University fiscal year and period
        if (!isAccountingLinePayFYPeriodMatchesUniversityPayFYPeriod(document, accountingLine)) {
            LaborLedgerPendingEntry benefitA21PendingEntry = SpringContext.getBean(LaborPendingEntryConverterService.class).getBenefitA21PendingEntry(document, accountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);
            benefitPendingEntries.add(benefitA21PendingEntry);

            LaborLedgerPendingEntry benefitA21ReversalPendingEntry = SpringContext.getBean(LaborPendingEntryConverterService.class).getBenefitA21ReversalPendingEntry(document, accountingLine, sequenceHelper, benefitAmount, fringeBenefitObjectCode);
            benefitPendingEntries.add(benefitA21ReversalPendingEntry);
        }

        return benefitPendingEntries;
    }

    /**
     * generate the benefit clearing pending entries with the given benefit amount and fringe benefit object code based on the given
     * document and accouting line
     * 
     * @param document the given accounting document
     * @param sequenceHelper the given squence helper
     * @param accountNumber the given clearing account number
     * @param chartOfAccountsCode the given clearing chart of accounts code
     * @return a set of benefit clearing pending entries
     */
    public static List<LaborLedgerPendingEntry> generateBenefitClearingPendingEntries(LaborLedgerPostingDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String accountNumber, String chartOfAccountsCode) {
        List<LaborLedgerPendingEntry> benefitClearingPendingEntries = new ArrayList<LaborLedgerPendingEntry>();

        Map<String, KualiDecimal> sourceLineBenefitAmountSum = new HashMap<String, KualiDecimal>();
        List<ExpenseTransferSourceAccountingLine> sourceAccountingLines = document.getSourceAccountingLines();
        for (ExpenseTransferSourceAccountingLine accountingLine : sourceAccountingLines) {
            updateBenefitAmountSum(sourceLineBenefitAmountSum, accountingLine);
        }

        Map<String, KualiDecimal> targetLineBenefitAmountSum = new HashMap<String, KualiDecimal>();
        List<ExpenseTransferTargetAccountingLine> targetAccountingLines = document.getTargetAccountingLines();
        for (ExpenseTransferTargetAccountingLine accountingLine : targetAccountingLines) {
            updateBenefitAmountSum(targetLineBenefitAmountSum, accountingLine);
        }

        Set<String> benefitTypeCodes = new HashSet<String>();
        for (String key : targetLineBenefitAmountSum.keySet()) {
            benefitTypeCodes.add(key);
        }

        for (String key : sourceLineBenefitAmountSum.keySet()) {
            benefitTypeCodes.add(key);
        }

        for (String benefitTypeCode : benefitTypeCodes) {
            KualiDecimal targetAmount = KualiDecimal.ZERO;
            if (targetLineBenefitAmountSum.containsKey(benefitTypeCode)) {
                targetAmount = targetLineBenefitAmountSum.get(benefitTypeCode);
            }

            KualiDecimal sourceAmount = KualiDecimal.ZERO;
            if (sourceLineBenefitAmountSum.containsKey(benefitTypeCode)) {
                sourceAmount = sourceLineBenefitAmountSum.get(benefitTypeCode);
            }

            KualiDecimal clearingAmount = sourceAmount.subtract(targetAmount);
            if (clearingAmount.isNonZero()) {
                benefitClearingPendingEntries.add(SpringContext.getBean(LaborPendingEntryConverterService.class).getBenefitClearingPendingEntry(document, sequenceHelper, accountNumber, chartOfAccountsCode, benefitTypeCode, clearingAmount));
            }
        }

        return benefitClearingPendingEntries;
    }

    /**
     * update the benefit amount summary map based on the given accounting line
     * 
     * @param benefitAmountSumByBenefitType the given benefit amount summary map
     * @param accountingLine the given accounting line
     */
    protected static void updateBenefitAmountSum(Map<String, KualiDecimal> benefitAmountSumByBenefitType, ExpenseTransferAccountingLine accountingLine) {
        accountingLine.refreshReferenceObject(KFSPropertyConstants.LABOR_OBJECT);
        if (ObjectUtils.isNull(accountingLine.getLaborObject())) {
            return;
        }

        String FringeOrSalaryCode = accountingLine.getLaborObject().getFinancialObjectFringeOrSalaryCode();
        if (!LaborConstants.SalaryExpenseTransfer.LABOR_LEDGER_SALARY_CODE.equals(FringeOrSalaryCode)) {
            return;
        }

        Integer payrollFiscalyear = accountingLine.getPayrollEndDateFiscalYear();
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String objectCode = accountingLine.getFinancialObjectCode();

        Collection<PositionObjectBenefit> positionObjectBenefits = SpringContext.getBean(LaborPositionObjectBenefitService.class).getPositionObjectBenefits(payrollFiscalyear, chartOfAccountsCode, objectCode);
        for (PositionObjectBenefit positionObjectBenefit : positionObjectBenefits) {
            String benefitTypeCode = positionObjectBenefit.getBenefitsCalculation().getPositionBenefitTypeCode();

            KualiDecimal benefitAmount = SpringContext.getBean(LaborBenefitsCalculationService.class).calculateFringeBenefit(positionObjectBenefit, accountingLine.getAmount());
            if (benefitAmountSumByBenefitType.containsKey(benefitTypeCode)) {
                benefitAmount = benefitAmount.add(benefitAmountSumByBenefitType.get(benefitTypeCode));
            }
            benefitAmountSumByBenefitType.put(benefitTypeCode, benefitAmount);
        }
    }

    /**
     * determine if the pay fiscal year and period from the accounting line match with its university fiscal year and period.
     * 
     * @param document the given document
     * @param accountingLine the given accounting line of the document
     * @return true if the pay fiscal year and period from the accounting line match with its university fiscal year and period;
     *         otherwise, false
     */
    protected static boolean isAccountingLinePayFYPeriodMatchesUniversityPayFYPeriod(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine) {
        Integer fiscalYear = document.getPostingYear();
        Integer payFiscalYear = accountingLine.getPayrollEndDateFiscalYear();
        if (!fiscalYear.equals(payFiscalYear)) {
            return false;
        }

        String periodCode = document.getPostingPeriodCode();
        String payPeriodCode = accountingLine.getPayrollEndDateFiscalPeriodCode();
        if (!StringUtils.equals(periodCode, payPeriodCode)) {
            return false;
        }

        return true;
    }
}
