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
package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.bo.PostalCode;
import org.kuali.rice.kns.bo.State;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.PostalCodeService;
import org.kuali.rice.kns.service.StateService;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetLocation extends PersistableBusinessObjectBase {

    private Long capitalAssetNumber;
    private String assetLocationTypeCode;
    private String assetLocationContactName;
    private String assetLocationContactIdentifier;
    private String assetLocationInstitutionName;
    private String assetLocationPhoneNumber;
    private String assetLocationStreetAddress;
    private String assetLocationCityName;
    private String assetLocationStateCode;
    private String assetLocationCountryCode;
    private String assetLocationZipCode;

    private Asset asset;
    private AssetLocationType assetLocationType;

    private State assetLocationState;
    private Country assetLocationCountry;
    private PostalCode postalZipCode;

    /**
     * Default constructor.
     */
    public AssetLocation() {

    }

    public AssetLocation(Long assetNumber) {
        this.capitalAssetNumber = assetNumber;
    }

    public AssetLocation(AssetLocation copiedLocation) {
        this.capitalAssetNumber = copiedLocation.getCapitalAssetNumber();
        this.assetLocationTypeCode = copiedLocation.getAssetLocationTypeCode();
        this.assetLocationContactName = copiedLocation.getAssetLocationContactName();
        this.assetLocationContactIdentifier = copiedLocation.getAssetLocationContactIdentifier();
        this.assetLocationInstitutionName = copiedLocation.getAssetLocationInstitutionName();
        this.assetLocationPhoneNumber = copiedLocation.getAssetLocationPhoneNumber();
        this.assetLocationStreetAddress = copiedLocation.getAssetLocationStreetAddress();
        this.assetLocationCityName = copiedLocation.getAssetLocationCityName();
        this.assetLocationStateCode = copiedLocation.getAssetLocationStateCode();
        this.assetLocationCountryCode = copiedLocation.getAssetLocationCountryCode();
        this.assetLocationZipCode = copiedLocation.getAssetLocationZipCode();
    }

    /**
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute.
     * 
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }


    /**
     * Gets the assetLocationTypeCode attribute.
     * 
     * @return Returns the assetLocationTypeCode
     */
    public String getAssetLocationTypeCode() {
        return assetLocationTypeCode;
    }

    /**
     * Sets the assetLocationTypeCode attribute.
     * 
     * @param assetLocationTypeCode The assetLocationTypeCode to set.
     */
    public void setAssetLocationTypeCode(String assetLocationTypeCode) {
        this.assetLocationTypeCode = assetLocationTypeCode;
    }


    /**
     * Gets the assetLocationContactName attribute.
     * 
     * @return Returns the assetLocationContactName
     */
    public String getAssetLocationContactName() {
        return assetLocationContactName;
    }

    /**
     * Sets the assetLocationContactName attribute.
     * 
     * @param assetLocationContactName The assetLocationContactName to set.
     */
    public void setAssetLocationContactName(String assetLocationContactName) {
        this.assetLocationContactName = assetLocationContactName;
    }


    /**
     * Gets the assetLocationContactIdentifier attribute.
     * 
     * @return Returns the assetLocationContactIdentifier
     */
    public String getAssetLocationContactIdentifier() {
        return assetLocationContactIdentifier;
    }

    /**
     * Sets the assetLocationContactIdentifier attribute.
     * 
     * @param assetLocationContactIdentifier The assetLocationContactIdentifier to set.
     */
    public void setAssetLocationContactIdentifier(String assetLocationContactIdentifier) {
        this.assetLocationContactIdentifier = assetLocationContactIdentifier;
    }


    /**
     * Gets the assetLocationInstitutionName attribute.
     * 
     * @return Returns the assetLocationInstitutionName
     */
    public String getAssetLocationInstitutionName() {
        return assetLocationInstitutionName;
    }

    /**
     * Sets the assetLocationInstitutionName attribute.
     * 
     * @param assetLocationInstitutionName The assetLocationInstitutionName to set.
     */
    public void setAssetLocationInstitutionName(String assetLocationInstitutionName) {
        this.assetLocationInstitutionName = assetLocationInstitutionName;
    }


    /**
     * Gets the assetLocationPhoneNumber attribute.
     * 
     * @return Returns the assetLocationPhoneNumber
     */
    public String getAssetLocationPhoneNumber() {
        return assetLocationPhoneNumber;
    }

    /**
     * Sets the assetLocationPhoneNumber attribute.
     * 
     * @param assetLocationPhoneNumber The assetLocationPhoneNumber to set.
     */
    public void setAssetLocationPhoneNumber(String assetLocationPhoneNumber) {
        this.assetLocationPhoneNumber = assetLocationPhoneNumber;
    }


    /**
     * Gets the assetLocationStreetAddress attribute.
     * 
     * @return Returns the assetLocationStreetAddress
     */
    public String getAssetLocationStreetAddress() {
        return assetLocationStreetAddress;
    }

    /**
     * Sets the assetLocationStreetAddress attribute.
     * 
     * @param assetLocationStreetAddress The assetLocationStreetAddress to set.
     */
    public void setAssetLocationStreetAddress(String assetLocationStreetAddress) {
        this.assetLocationStreetAddress = assetLocationStreetAddress;
    }


    /**
     * Gets the assetLocationCityName attribute.
     * 
     * @return Returns the assetLocationCityName
     */
    public String getAssetLocationCityName() {
        return assetLocationCityName;
    }

    /**
     * Sets the assetLocationCityName attribute.
     * 
     * @param assetLocationCityName The assetLocationCityName to set.
     */
    public void setAssetLocationCityName(String assetLocationCityName) {
        this.assetLocationCityName = assetLocationCityName;
    }


    /**
     * Gets the assetLocationStateCode attribute.
     * 
     * @return Returns the assetLocationStateCode
     */
    public String getAssetLocationStateCode() {
        return assetLocationStateCode;
    }

    /**
     * Sets the assetLocationStateCode attribute.
     * 
     * @param assetLocationStateCode The assetLocationStateCode to set.
     */
    public void setAssetLocationStateCode(String assetLocationStateCode) {
        this.assetLocationStateCode = assetLocationStateCode;
    }


    /**
     * Gets the assetLocationCountryCode attribute.
     * 
     * @return Returns the assetLocationCountryCode
     */
    public String getAssetLocationCountryCode() {
        return assetLocationCountryCode;
    }

    /**
     * Sets the assetLocationCountryCode attribute.
     * 
     * @param assetLocationCountryCode The assetLocationCountryCode to set.
     */
    public void setAssetLocationCountryCode(String assetLocationCountryCode) {
        this.assetLocationCountryCode = assetLocationCountryCode;
    }


    /**
     * Gets the assetLocationZipCode attribute.
     * 
     * @return Returns the assetLocationZipCode
     */
    public String getAssetLocationZipCode() {
        return assetLocationZipCode;
    }

    /**
     * Sets the assetLocationZipCode attribute.
     * 
     * @param assetLocationZipCode The assetLocationZipCode to set.
     */
    public void setAssetLocationZipCode(String assetLocationZipCode) {
        this.assetLocationZipCode = assetLocationZipCode;
    }

    /**
     * Gets the postalZipCode attribute.
     * 
     * @return Returns the postalZipCode
     */
    public PostalCode getPostalZipCode() {
        postalZipCode = SpringContext.getBean(PostalCodeService.class).getByPrimaryIdIfNecessary(assetLocationCountryCode, assetLocationZipCode, postalZipCode);
        return postalZipCode;
    }

    /**
     * Sets the postalZipCode attribute.
     * 
     * @param postalZipCode The postalZipCode to set.
     */
    public void setPostalZipCode(PostalCode postalZipCode) {
        this.postalZipCode = postalZipCode;
    }

    /**
     * Gets the asset attribute.
     * 
     * @return Returns the asset
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute.
     * 
     * @param asset The asset to set.
     * @deprecated
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    /**
     * Gets the assetLocationType attribute.
     * 
     * @return Returns the assetLocationType.
     */
    public AssetLocationType getAssetLocationType() {
        return assetLocationType;
    }

    /**
     * Sets the assetLocationType attribute value.
     * 
     * @param assetLocationType The assetLocationType to set.
     * @deprecated
     */
    public void setAssetLocationType(AssetLocationType assetLocationType) {
        this.assetLocationType = assetLocationType;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        m.put("assetLocationTypeCode", this.assetLocationTypeCode);
        return m;
    }

    public Country getAssetLocationCountry() {
        assetLocationCountry = SpringContext.getBean(CountryService.class).getByPrimaryIdIfNecessary(assetLocationCountryCode, assetLocationCountry);
        return assetLocationCountry;
    }

    public void setAssetLocationCountry(Country assetLocationCountry) {
        this.assetLocationCountry = assetLocationCountry;
    }

    public State getAssetLocationState() {
        assetLocationState = SpringContext.getBean(StateService.class).getByPrimaryIdIfNecessary(assetLocationCountryCode, assetLocationStateCode, assetLocationState);
        return assetLocationState;
    }

    public void setAssetLocationState(State assetLocationState) {
        this.assetLocationState = assetLocationState;
    }
}
