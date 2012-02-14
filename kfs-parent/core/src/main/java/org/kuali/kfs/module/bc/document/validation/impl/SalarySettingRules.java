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
package org.kuali.kfs.module.bc.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionRuleHelperService;
import org.kuali.kfs.module.bc.document.service.SalarySettingRuleHelperService;
import org.kuali.kfs.module.bc.document.validation.SalarySettingRule;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;

/**
 * the rule implementation for the actions of salary setting component
 */
public class SalarySettingRules implements SalarySettingRule {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingRules.class);

    private BudgetConstructionRuleHelperService budgetConstructionRuleHelperService = SpringContext.getBean(BudgetConstructionRuleHelperService.class);
    private SalarySettingRuleHelperService salarySettingRuleHelperService = SpringContext.getBean(SalarySettingRuleHelperService.class);
    private MessageMap errorMap = GlobalVariables.getMessageMap();

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processQuickSaveAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processQuickSaveAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("processQuickSaveAppointmentFunding() start");

        boolean hasValidFormat = budgetConstructionRuleHelperService.isFieldFormatValid(appointmentFunding, errorMap);
        if (!hasValidFormat) {
            return hasValidFormat;
        }

        boolean hasValidAmounts = this.hasValidAmountsQuickSalarySetting(appointmentFunding, errorMap);
        if (!hasValidAmounts) {
            return hasValidAmounts;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processSaveAppointmentFunding(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processSaveAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("processSaveAppointmentFunding() start");

        boolean hasValidFormat = budgetConstructionRuleHelperService.isFieldFormatValid(appointmentFunding, errorMap);
        if (!hasValidFormat) {
            return hasValidFormat;
        }

        boolean hasValidReferences = this.hasValidRefences(appointmentFunding, errorMap);
        if (!hasValidReferences) {
            return hasValidReferences;
        }

        boolean isObjectCodeMatching = salarySettingRuleHelperService.hasObjectCodeMatchingDefaultOfPosition(appointmentFunding, errorMap);
        if (!isObjectCodeMatching) {
            return isObjectCodeMatching;
        }

        boolean hasActiveJob = salarySettingRuleHelperService.hasActiveJob(appointmentFunding, errorMap);
        if (!hasActiveJob) {
            return hasActiveJob;
        }

        // using request amount as property to light up on error
        boolean isAssociatedWithBudgetableDocument = budgetConstructionRuleHelperService.isAssociatedWithValidDocument(appointmentFunding, errorMap, BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT);
        if (!isAssociatedWithBudgetableDocument) {
            return isAssociatedWithBudgetableDocument;
        }

        boolean hasValidAmounts = this.hasValidAmounts(appointmentFunding, errorMap);
        if (!hasValidAmounts) {
            return hasValidAmounts;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processAddAppointmentFunding(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processAddAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> existingAppointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("processAddAppointmentFunding() start");

        boolean hasNoExistingLine = salarySettingRuleHelperService.hasNoExistingLine(existingAppointmentFundings, appointmentFunding, errorMap);
        if (!hasNoExistingLine) {
            return hasNoExistingLine;
        }

        boolean hasValidFormat = budgetConstructionRuleHelperService.isFieldFormatValid(appointmentFunding, errorMap);
        if (!hasValidFormat) {
            return hasValidFormat;
        }

        boolean hasValidReferences = this.hasValidRefences(appointmentFunding, errorMap);
        if (!hasValidReferences) {
            return hasValidReferences;
        }

        boolean isObjectCodeMatching = salarySettingRuleHelperService.hasObjectCodeMatchingDefaultOfPosition(appointmentFunding, errorMap);
        if (!isObjectCodeMatching) {
            return isObjectCodeMatching;
        }

        boolean hasActiveJob = salarySettingRuleHelperService.hasActiveJob(appointmentFunding, errorMap);
        if (!hasActiveJob) {
            return hasActiveJob;
        }

        boolean isAssociatedWithBudgetableDocument = budgetConstructionRuleHelperService.isAssociatedWithValidDocument(appointmentFunding, errorMap, KFSPropertyConstants.ACCOUNT_NUMBER);
        if (!isAssociatedWithBudgetableDocument) {
            return isAssociatedWithBudgetableDocument;
        } else {
            appointmentFunding.setBudgetable(Boolean.TRUE);
        }

        boolean hasValidAmounts = this.hasValidAmounts(appointmentFunding, errorMap);
        if (!hasValidAmounts) {
            return hasValidAmounts;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processAdjustSalaraySettingLinePercent(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processAdjustSalaraySettingLinePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        boolean canBeAdjusted = salarySettingRuleHelperService.canBeAdjusted(appointmentFunding, errorMap);
        if (!canBeAdjusted) {
            return false;
        }

        boolean hasValidAdjustmentAmount = salarySettingRuleHelperService.hasValidAdjustmentAmount(appointmentFunding, errorMap);
        if (!hasValidAdjustmentAmount) {
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.SalarySettingRule#processNormalizePayrateAndAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean processNormalizePayrateAndAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        return salarySettingRuleHelperService.hasValidPayRateOrAnnualAmount(appointmentFunding, errorMap);
    }

    // test if all references of the given appointment funding are valid
    private boolean hasValidRefences(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        boolean hasValidReference = budgetConstructionRuleHelperService.hasValidChart(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidAccount(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidObjectCode(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidSubAccount(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidSubObjectCode(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasDetailPositionRequiredObjectCode(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidPosition(appointmentFunding, errorMap);
        hasValidReference &= budgetConstructionRuleHelperService.hasValidIncumbent(appointmentFunding, errorMap);

        return hasValidReference;
    }

    // test if all amounts are legal
    private boolean hasValidAmounts(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        boolean hasValidAmounts = salarySettingRuleHelperService.hasValidRequestedAmount(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedFteQuantity(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedFundingMonth(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedTimePercent(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasRequestedAmountZeroWhenFullYearLeave(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasRequestedFteQuantityZeroWhenFullYearLeave(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedCsfAmount(appointmentFunding, errorMap);
        hasValidAmounts &= salarySettingRuleHelperService.hasValidRequestedCsfTimePercent(appointmentFunding, errorMap);

        return hasValidAmounts;
    }

    // test if request amount and FTE interaction is legal
    private boolean hasValidAmountsQuickSalarySetting(PendingBudgetConstructionAppointmentFunding appointmentFunding, MessageMap errorMap) {
        boolean hasValidAmounts = salarySettingRuleHelperService.hasValidRequestedAmountQuickSalarySetting(appointmentFunding, errorMap);

        return hasValidAmounts;
    }
}
