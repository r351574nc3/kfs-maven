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
package org.kuali.kfs.module.bc.document.validation.event;

import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.validation.SalarySettingRule;
import org.kuali.rice.kns.rule.BusinessRule;

/**
 * Event triggered when normalizing the pay rate and salary amount.
 */
public class NormalizePayrateAndAmountEvent extends SalarySettingBaseEvent {
    PendingBudgetConstructionAppointmentFunding appointmentFunding;

    /**
     * Constructs a PayrateAndAmountNormalizationEvent.java.
     * 
     * @param errorPathPrefix the specified error path prefix
     * @param appointmentFunding the given appointment funding
     */
    public NormalizePayrateAndAmountEvent(String description, String errorPathPrefix, BudgetConstructionDocument document, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        super(description, errorPathPrefix, document);
        this.appointmentFunding = appointmentFunding;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent#invokeExpansionRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    @Override
    public boolean invokeExpansionRuleMethod(BusinessRule rule) {
        return ((SalarySettingRule) rule).processNormalizePayrateAndAmount(appointmentFunding);
    }
}
