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

package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;

/**
 * 
 */
public class Room extends PersistableBusinessObjectBase implements Inactivateable {

    private String campusCode;
    private String buildingCode;
    private String buildingRoomNumber;
    private String buildingRoomType;
    private String buildingRoomDepartment;
    private String buildingRoomDescription;
    private boolean active;

    private Campus campus;
    private Building building;

    /**
     * Default constructor.
     */
    public Room() {

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
     * Gets the buildingRoomType attribute.
     * 
     * @return Returns the buildingRoomType
     */
    public String getBuildingRoomType() {
        return buildingRoomType;
    }

    /**
     * Sets the buildingRoomType attribute.
     * 
     * @param buildingRoomType The buildingRoomType to set.
     */
    public void setBuildingRoomType(String buildingRoomType) {
        this.buildingRoomType = buildingRoomType;
    }


    /**
     * Gets the buildingRoomDepartment attribute.
     * 
     * @return Returns the buildingRoomDepartment
     */
    public String getBuildingRoomDepartment() {
        return buildingRoomDepartment;
    }

    /**
     * Sets the buildingRoomDepartment attribute.
     * 
     * @param buildingRoomDepartment The buildingRoomDepartment to set.
     */
    public void setBuildingRoomDepartment(String buildingRoomDepartment) {
        this.buildingRoomDepartment = buildingRoomDepartment;
    }


    /**
     * Gets the buildingRoomDescription attribute.
     * 
     * @return Returns the buildingRoomDescription
     */
    public String getBuildingRoomDescription() {
        return buildingRoomDescription;
    }

    /**
     * Sets the buildingRoomDescription attribute.
     * 
     * @param buildingRoomDescription The buildingRoomDescription to set.
     */
    public void setBuildingRoomDescription(String buildingRoomDescription) {
        this.buildingRoomDescription = buildingRoomDescription;
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
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("campusCode", this.campusCode);
        m.put("buildingCode", this.buildingCode);
        m.put("buildingRoomNumber", this.buildingRoomNumber);
        return m;
    }

    /**
     * Gets the building attribute.
     * 
     * @return Returns the building
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Sets the building attribute.
     * 
     * @param building The building to set.
     * @deprecated
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     * 
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     * 
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
