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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.Properties;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionType;
import org.kuali.rice.kns.lookup.KualiLookupableImpl;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;

public class TaxRegionLookupableImpl extends KualiLookupableImpl {

    /**
     * Make create new action go to Tax Region Type lookup first. Adding the tax region from lookup indicator
     * (CREATE_TAX_REGION_FROM_LOOKUP_INDICATOR) to the conversion fields to control when you should do a regular tax region type
     * code lookup vs. one that actually creates a tax region.  This indicator is then used in
     * TaxRegionTypeLookupableImpl.
     * 
     * @see org.kuali.core.lookup.KualiLookupableImpl#getCreateNewUrl()
     */
    @Override
    public String getCreateNewUrl() {
        String url = "";

        if (getLookupableHelperService().allowsMaintenanceNewOrCopyAction()) {
            Properties parameters = new Properties();
            parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TaxRegionType.class.getName());
            parameters.put(KNSConstants.RETURN_LOCATION_PARAMETER, KNSServiceLocator.getKualiConfigurationService().getPropertyString(KNSConstants.APPLICATION_URL_KEY) + "/" + KNSConstants.MAPPING_PORTAL);
            parameters.put(KNSConstants.DOC_FORM_KEY, getTaxRegionDocumentTypeName()); 
            parameters.put(KNSConstants.CONVERSION_FIELDS_PARAMETER, "taxRegionTypeCode:taxRegionTypeCode");
            url = getCreateNewUrl(UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, parameters)); 
        }

        return url;
    }
    
    private String getTaxRegionDocumentTypeName() {
        return getDataDictionaryService().getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(TaxRegion.class).getDocumentTypeName();
    } 

}
