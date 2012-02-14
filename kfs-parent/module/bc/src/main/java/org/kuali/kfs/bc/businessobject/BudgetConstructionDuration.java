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
public class BudgetConstructionDuration extends PersistableBusinessObjectBase implements Inactivateable {

    private String appointmentDurationCode;
    private String appointmentDurationDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public BudgetConstructionDuration() {

    }

    /**
     * Gets the appointmentDurationCode attribute.
     * 
     * @return Returns the appointmentDurationCode
     */
    public String getAppointmentDurationCode() {
        return appointmentDurationCode;
    }

    /**
     * Sets the appointmentDurationCode attribute.
     * 
     * @param appointmentDurationCode The appointmentDurationCode to set.
     */
    public void setAppointmentDurationCode(String appointmentDurationCode) {
        this.appointmentDurationCode = appointmentDurationCode;
    }


    /**
     * Gets the appointmentDurationDescription attribute.
     * 
     * @return Returns the appointmentDurationDescription
     */
    public String getAppointmentDurationDescription() {
        return appointmentDurationDescription;
    }

    /**
     * Sets the appointmentDurationDescription attribute.
     * 
     * @param appointmentDurationDescription The appointmentDurationDescription to set.
     */
    public void setAppointmentDurationDescription(String appointmentDurationDescription) {
        this.appointmentDurationDescription = appointmentDurationDescription;
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
        m.put("appointmentDurationCode", this.appointmentDurationCode);
        return m;
    }
}
