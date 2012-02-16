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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.util.Collection;

import org.kuali.kfs.module.endow.batch.service.KEMIDCurrentAvailableBalanceService;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentAvailableBalance;
import org.kuali.rice.kns.service.BusinessObjectService;

/**
 * This class implements the AvailableCashService.
 */
public class KEMIDCurrentAvailableBalanceServiceImpl implements KEMIDCurrentAvailableBalanceService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KEMIDCurrentAvailableBalanceServiceImpl.class);
    
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.batch.service.AvailableCashUpdateService#clearAvailableCash()
     * Method to clear all the records in the kEMIDCurrentAvailableBalance table
     */
    public void clearAllAvailableCash() {
        LOG.info("Step1: Clearing all available cash records");
        
        Collection<KEMIDCurrentAvailableBalance> KEMIDCurrentAvailableBalances = businessObjectService.findAll(KEMIDCurrentAvailableBalance.class);

        for (KEMIDCurrentAvailableBalance kEMIDCurrentAvailableBalance : KEMIDCurrentAvailableBalances) {
            businessObjectService.delete(kEMIDCurrentAvailableBalance);
        }
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.AvailableCashUpdateService#InsertAvailableCash(KemidCurrentCash)
     * Method to clear all the records in the kEMIDCurrentAvailableBalance table
     */
    public void InsertAvailableCash(KEMIDCurrentAvailableBalance kEMIDCurrentAvailableBalance) {
        if (kEMIDCurrentAvailableBalance == null) {
            throw new IllegalArgumentException("invalid (null) kEMIDCurrentAvailableBalance");
        }
        
        businessObjectService.save(kEMIDCurrentAvailableBalance);
    } 
    
    /**
     * Sets the BusinessObjectService
     * 
     * @param businessObjectService The BusinessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
