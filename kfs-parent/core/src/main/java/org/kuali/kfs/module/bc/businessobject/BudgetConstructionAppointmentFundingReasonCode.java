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

package org.kuali.kfs.module.bc.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionAppointmentFundingReasonCode extends PersistableBusinessObjectBase implements Inactivateable {

    private String appointmentFundingReasonCode;
    private String appointmentFundingReasonDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public BudgetConstructionAppointmentFundingReasonCode() {

    }

    /**
     * Gets the appointmentFundingReasonCode attribute.
     * 
     * @return Returns the appointmentFundingReasonCode
     */
    public String getAppointmentFundingReasonCode() {
        return appointmentFundingReasonCode;
    }

    /**
     * Sets the appointmentFundingReasonCode attribute.
     * 
     * @param appointmentFundingReasonCode The appointmentFundingReasonCode to set.
     */
    public void setAppointmentFundingReasonCode(String appointmentFundingReasonCode) {
        this.appointmentFundingReasonCode = appointmentFundingReasonCode;
    }


    /**
     * Gets the appointmentFundingReasonDescription attribute.
     * 
     * @return Returns the appointmentFundingReasonDescription
     */
    public String getAppointmentFundingReasonDescription() {
        return appointmentFundingReasonDescription;
    }

    /**
     * Sets the appointmentFundingReasonDescription attribute.
     * 
     * @param appointmentFundingReasonDescription The appointmentFundingReasonDescription to set.
     */
    public void setAppointmentFundingReasonDescription(String appointmentFundingReasonDescription) {
        this.appointmentFundingReasonDescription = appointmentFundingReasonDescription;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("appointmentFundingReasonCode", this.appointmentFundingReasonCode);
        return m;
    }


}
