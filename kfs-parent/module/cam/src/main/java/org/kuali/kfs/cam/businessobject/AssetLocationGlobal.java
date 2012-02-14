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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.document.validation.impl.AssetLocationGlobalRule;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.GlobalBusinessObject;
import org.kuali.rice.kns.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetLocationGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

	private String documentNumber;
    private DocumentHeader documentHeader;
    private List<AssetLocationGlobalDetail> assetLocationGlobalDetails;
    
	/**
	 * Default constructor.
	 */
	public AssetLocationGlobal() {
        assetLocationGlobalDetails = new TypedArrayList(AssetLocationGlobalDetail.class);
	}

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	/**
     * Gets the documentHeader attribute. 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader The documentHeader to set.
     * @deprecated
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }
    
    /**
     * Gets the assetLocationGlobalDetails attribute. 
     * @return Returns the assetLocationGlobalDetails.
     */
    public List<AssetLocationGlobalDetail> getAssetLocationGlobalDetails() {
        return assetLocationGlobalDetails;
    }

    /**
     * Sets the assetLocationGlobalDetails attribute value.
     * @param assetLocationGlobalDetails The assetLocationGlobalDetails to set.
     */
    public void setAssetLocationGlobalDetails(List<AssetLocationGlobalDetail> assetLocationGlobalDetails) {
        this.assetLocationGlobalDetails = assetLocationGlobalDetails;
    }

    /**
     * @see org.kuali.rice.kns.document.GlobalBusinessObject#getGlobalChangesToDelete()
     */
    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        return null;
    }

    /**
     * This returns a list of Assets to update
     * 
     * @see org.kuali.rice.kns.bo.GlobalBusinessObject#generateGlobalChangesToPersist()
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        // the list of persist-ready BOs
        List<PersistableBusinessObject> persistables = new ArrayList();

        // walk over each change detail record
        for (AssetLocationGlobalDetail detail : assetLocationGlobalDetails) {

            // load the object by keys
            Asset asset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, detail.getPrimaryKeys());

            // if we got a valid account, do the processing
            if (asset != null) {

                if (StringUtils.isNotBlank(detail.getCampusCode())) {
                    asset.setCampusCode(detail.getCampusCode());
                }
                
                if (StringUtils.isNotBlank(detail.getBuildingCode())) {
                    asset.setBuildingCode(detail.getBuildingCode());
                }
                
                if (StringUtils.isNotBlank(detail.getBuildingRoomNumber())) {
                    asset.setBuildingRoomNumber(detail.getBuildingRoomNumber());
                }
                
                if (StringUtils.isNotBlank(detail.getBuildingSubRoomNumber())) {
                    asset.setBuildingSubRoomNumber(detail.getBuildingSubRoomNumber());
                }

                // set tag number to null if no data in field
                if (StringUtils.isNotBlank(detail.getCampusTagNumber()) && !StringUtils.equalsIgnoreCase(detail.getCampusTagNumber(), asset.getCampusTagNumber())) {
                    asset.setOldTagNumber(asset.getCampusTagNumber());
                    asset.setCampusTagNumber(detail.getCampusTagNumber());
                }
                asset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));

                persistables.add(asset);
            }
        }

        return persistables;
    }
    
    public boolean isPersistable() {
        return true;
    }
    
    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        return getAssetLocationGlobalDetails();
    }
    
    /**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
	    return m;
    }
}
