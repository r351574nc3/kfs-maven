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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.Donor;
import org.kuali.kfs.module.endow.document.service.DonorService;
import org.kuali.rice.kns.service.BusinessObjectService;

public class DonorServiceImpl implements DonorService {

    private BusinessObjectService businessObjectService;


    /**
     * @see org.kuali.kfs.module.endow.document.service.DonorService#getByPrimaryKey(java.lang.String)
     */
    public Donor getByPrimaryKey(String donorId) {
        Donor donor = null;
        if (StringUtils.isNotBlank(donorId)) {
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put(EndowPropertyConstants.DONR_ID, donorId);

            donor = (Donor) businessObjectService.findByPrimaryKey(Donor.class, criteria);
        }
        return donor;
    }

    /**
     * Gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
