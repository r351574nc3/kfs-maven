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
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * 
 * This class is the Capital Asset System State
 */
public class CapitalAssetSystemState extends PersistableBusinessObjectBase implements Inactivateable{

	private String capitalAssetSystemStateCode;
	private String capitalAssetSystemStateDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public CapitalAssetSystemState() {

	}

	public String getCapitalAssetSystemStateCode() { 
		return capitalAssetSystemStateCode;
	}

	public void setCapitalAssetSystemStateCode(String capitalAssetSystemStateCode) {
		this.capitalAssetSystemStateCode = capitalAssetSystemStateCode;
	}

	public String getCapitalAssetSystemStateDescription() {
        return capitalAssetSystemStateDescription;
    }

    public void setCapitalAssetSystemStateDescription(String capitalAssetSystemStateDescription) {
        this.capitalAssetSystemStateDescription = capitalAssetSystemStateDescription;
    }

    public boolean isActive() { 
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("capitalAssetSystemStateCode", this.capitalAssetSystemStateCode);
	    return m;
    }
}
