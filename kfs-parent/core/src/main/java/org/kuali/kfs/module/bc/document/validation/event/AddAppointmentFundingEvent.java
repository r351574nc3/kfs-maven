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

import java.util.List;

import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.validation.SalarySettingRule;
import org.kuali.rice.kns.rule.BusinessRule;

public class AddAppointmentFundingEvent extends SalarySettingBaseEvent {
    List<PendingBudgetConstructionAppointmentFunding> existingAppointmentFunding;
    PendingBudgetConstructionAppointmentFunding appointmentFunding;

    /**
     * Constructs a AddAppointmentFundingEvent.java.
     * 
     * @param errorPathPrefix the specified error path prefix
     * @param appointmentFundings the given appointment funding
     */
    public AddAppointmentFundingEvent(String description, String errorPathPrefix, BudgetConstructionDocument document, List<PendingBudgetConstructionAppointmentFunding> existingAppointmentFunding, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        super(description, errorPathPrefix, document);
        this.appointmentFunding = appointmentFunding;
        this.existingAppointmentFunding = existingAppointmentFunding;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent#invokeExpansionRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
     */
    @Override
    public boolean invokeExpansionRuleMethod(BusinessRule rule) {
        return ((SalarySettingRule) rule).processAddAppointmentFunding(existingAppointmentFunding, appointmentFunding);
    }
}
