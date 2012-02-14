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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerAddressType extends PersistableBusinessObjectBase implements Inactivateable {

	private String customerAddressTypeCode;
	private String customerAddressTypeDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public CustomerAddressType() {

	}

	/**
	 * Gets the customerAddressTypeCode attribute.
	 * 
	 * @return Returns the customerAddressTypeCode
	 * 
	 */
	public String getCustomerAddressTypeCode() { 
		return customerAddressTypeCode;
	}

	/**
	 * Sets the customerAddressTypeCode attribute.
	 * 
	 * @param customerAddressTypeCode The customerAddressTypeCode to set.
	 * 
	 */
	public void setCustomerAddressTypeCode(String customerAddressTypeCode) {
		this.customerAddressTypeCode = customerAddressTypeCode;
	}


	/**
	 * Gets the customerAddressTypeDescription attribute.
	 * 
	 * @return Returns the customerAddressTypeDescription
	 * 
	 */
	public String getCustomerAddressTypeDescription() { 
		return customerAddressTypeDescription;
	}

	/**
	 * Sets the customerAddressTypeDescription attribute.
	 * 
	 * @param customerAddressTypeDescription The customerAddressTypeDescription to set.
	 * 
	 */
	public void setCustomerAddressTypeDescription(String customerAddressTypeDescription) {
		this.customerAddressTypeDescription = customerAddressTypeDescription;
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

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
    @SuppressWarnings("unchecked")
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("customerAddressTypeCode", this.customerAddressTypeCode);
	    return m;
    }
}
