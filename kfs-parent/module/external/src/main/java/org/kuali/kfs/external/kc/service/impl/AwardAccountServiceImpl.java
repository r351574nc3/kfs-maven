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
package org.kuali.kfs.module.external.kc.service.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAccountAwardInformation;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AwardAccount;
import org.kuali.kfs.module.external.kc.businessobject.AwardAccountDTO;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.webService.AwardAccountService;
import org.kuali.kfs.module.external.kc.webService.AwardAccountSoapService;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;

public class AwardAccountServiceImpl implements ExternalizableBusinessObjectService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AwardAccountServiceImpl.class);
    
    protected  AwardAccountService getWebService() {
        AwardAccountSoapService soapService = null;
        try {
            soapService = new AwardAccountSoapService();
        }
        catch (MalformedURLException ex) {
            LOG.error("Could not intialize AwardAccountSoapService: " + ex.getMessage());
            throw new RuntimeException("Could not intialize AwardAccountSoapService: " + ex.getMessage());
        }
        AwardAccountService port = soapService.getAwardAccountServicePort(); 
        return port;
    }

    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {        
        Collection ebos = findMatching(primaryKeys);
        
        if(ebos != null && ebos.iterator().hasNext()){
            return (ExternalizableBusinessObject) ebos.iterator().next();
        }else{
            return null;
        }
    }

    public Collection findMatching(Map fieldValues) {
        Map hashMapList = new HashMap();

        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            String key = (String) e.getKey();
            String val = (String) e.getValue();

            if ( KcConstants.AwardAccount.KC_ALLOWABLE_CRITERIA_PARAMETERS.contains(key)  && (val.length() > 0)) {
                hashMapList.put(key, val);
            }
        }
        
        String accountNumber = (String)hashMapList.get("accountNumber");
        String chartOfAccountsCode = (String)hashMapList.get("chartOfAccountsCode");
        
        List awardAccounts = new ArrayList();             
        List<AwardAccountDTO> awardAccountDTOs = null;
        
        //get award account DTO
        try{
            awardAccountDTOs = getWebService().getAwardAccounts(accountNumber, chartOfAccountsCode);
        } catch (WebServiceException ex) {
            LOG.error("Could not retrieve award accounts: "+ ex.getMessage());
        }
        
        if (awardAccountDTOs != null && !awardAccountDTOs.isEmpty()) {            
            ContractsAndGrantsAccountAwardInformation awardAccountInfo = null; 
            
            for(AwardAccountDTO awardAccount : (List<AwardAccountDTO>)awardAccountDTOs){
                //create if no error messages
                if(StringUtils.isEmpty(awardAccount.getErrorMessage())){                    
                    awardAccountInfo = new AwardAccount(awardAccount, accountNumber, chartOfAccountsCode, "");
                    awardAccounts.add(awardAccountInfo);
                }
            }
        }
                
        return awardAccounts;
    }
}
