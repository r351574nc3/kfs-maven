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

package org.kuali.kfs.vnd.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * An association between a <code>Campus</code> and a <code>VendorAddress</code> to indicate that the Address is the default one
 * for this Campus among the various Addresses available for this Vendor.
 * 
 * @see org.kuali.rice.kns.bo.Campus
 * @see org.kuali.kfs.vnd.businessobject.VendorAddress
 */
public class VendorDefaultAddress extends PersistableBusinessObjectBase implements VendorRoutingComparable, Inactivateable {

    private Integer vendorDefaultAddressGeneratedIdentifier;
    private Integer vendorAddressGeneratedIdentifier;
    private String vendorCampusCode;
    private boolean active;

    private VendorAddress vendorAddress;
    private CampusParameter vendorCampus;

    /**
     * Default constructor.
     */
    public VendorDefaultAddress() {

    }

    public String getVendorCampusCode() {

        return vendorCampusCode;
    }

    public void setVendorCampusCode(String vendorCampusCode) {
        this.vendorCampusCode = vendorCampusCode;
    }

    public Integer getVendorAddressGeneratedIdentifier() {

        return vendorAddressGeneratedIdentifier;
    }

    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public VendorAddress getVendorAddress() {

        return vendorAddress;
    }

    /**
     * Sets the vendorAddress attribute.
     * 
     * @param vendorAddress The vendorAddress to set.
     * @deprecated
     */
    public void setVendorAddress(VendorAddress vendorAddress) {
        this.vendorAddress = vendorAddress;
    }


    public CampusParameter getVendorCampus() {
        return vendorCampus;
    }

    /**
     * Sets the vendorCampus attribute.
     * 
     * @param vendorCampus The vendorCampus to set.
     * @deprecated
     */
    public void setVendorCampus(CampusParameter vendorCampus) {
        this.vendorCampus = vendorCampus;
    }

    public Integer getVendorDefaultAddressGeneratedIdentifier() {

        return vendorDefaultAddressGeneratedIdentifier;
    }

    public void setVendorDefaultAddressGeneratedIdentifier(Integer vendorDefaultAddressGeneratedIdentifier) {
        this.vendorDefaultAddressGeneratedIdentifier = vendorDefaultAddressGeneratedIdentifier;
    }

    /**
     * @see org.kuali.kfs.vnd.document.routing.VendorRoutingComparable#isEqualForRouting(java.lang.Object)
     */
    public boolean isEqualForRouting(Object toCompare) {
        if ((ObjectUtils.isNull(toCompare)) || !(toCompare instanceof VendorDefaultAddress)) {

            return false;
        }
        else {
            VendorDefaultAddress vda = (VendorDefaultAddress) toCompare;

            return new EqualsBuilder().append(this.getVendorDefaultAddressGeneratedIdentifier(), vda.getVendorDefaultAddressGeneratedIdentifier()).append(this.getVendorAddressGeneratedIdentifier(), vda.getVendorAddressGeneratedIdentifier()).append(this.getVendorCampusCode(), vda.getVendorCampusCode()).isEquals();
        }
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.vendorAddressGeneratedIdentifier != null) {
            m.put("vendorAddressGeneratedIdentifier", this.vendorAddressGeneratedIdentifier.toString());
        }
        m.put("vendorCampusCode", this.vendorCampusCode);

        return m;
    }


}
