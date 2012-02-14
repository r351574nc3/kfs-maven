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
package org.kuali.kfs.module.cam.document.service;

import java.util.Map;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.rice.kns.bo.BusinessObject;

public interface AssetLocationService {

    public static enum LocationField {
        CAMPUS_CODE, BUILDING_CODE, ROOM_NUMBER, SUB_ROOM_NUMBER, CONTACT_NAME, STREET_ADDRESS, CITY_NAME, STATE_CODE, ZIP_CODE, COUNTRY_CODE; 
    }

    /**
     * The method will set Off Campus Location from the assetLocations collection
     */
    public void setOffCampusLocation(Asset asset);

    /**
     * Update user input into reference of Asset Location
     */
    public void updateOffCampusLocation(Asset newAsset);

    boolean validateLocation(Map<LocationField, String> fieldMap, BusinessObject businessObject, boolean isCapital, AssetType assetType);

    /** 
     * check if offCampusLocation is holding any location information.
     * 
     * @param offCampusLocation
     */
    public boolean isOffCampusLocationExists(AssetLocation offCampusLocation);
    
    /**
     * check if offCampusLocation is empty
     * 
     * @param offCampusLocation
     */
    public boolean isOffCampusLocationEmpty(AssetLocation offCampusLocation);
}
