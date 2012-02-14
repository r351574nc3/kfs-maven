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
package org.kuali.kfs.module.ld.businessobject.lookup;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.LedgerBalanceForEffortCertification;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.kns.util.KNSConstants;

public class LedgerBalanceForEffortCertificationLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    
    private LookupableHelperService effortLedgerBalanceLookupableHelperService;

    /**
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KNSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KNSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KNSConstants.REFERENCES_TO_REFRESH));
        
        List<LedgerBalanceForEffortCertification> searchResults = effortLedgerBalanceLookupableHelperService.getSearchResults(fieldValues);
        
        return searchResults;
    }

    /**
     * Sets the effortLedgerBalanceLookupableHelperService attribute value.
     * @param effortLedgerBalanceLookupableHelperService The effortLedgerBalanceLookupableHelperService to set.
     */
    public void setEffortLedgerBalanceLookupableHelperService(LookupableHelperService effortLedgerBalanceLookupableHelperService) {
        if(effortLedgerBalanceLookupableHelperService != null) {
            this.effortLedgerBalanceLookupableHelperService = effortLedgerBalanceLookupableHelperService;
        }
        else {
            this.effortLedgerBalanceLookupableHelperService = new KualiLookupableHelperServiceImpl();
        }
    }
}
