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
package org.kuali.kfs.module.endow.dataaccess;

import java.math.BigDecimal;
import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;

public interface CurrentTaxLotBalanceDao {

    /**
     * Gets all records for the Security in END_CURR_TAX_LOT_BAL_T.
     * 
     * @param securityId the id of the security for which we retrieve the entries
     * @return all records for the Security in END_CURR_TAX_LOT_BAL_T
     */
    public Collection<CurrentTaxLotBalance> getAllCurrentTaxLotBalanceEntriesForSecurity(String securityId);

    /**
     * Calculates the total Holding market value based on FEE_BAL_TYP_CD = CU
     * @param feeMethod feeMethod object
     * @return totalHoldingUnits
     */
    public BigDecimal getCurrentTaxLotBalanceTotalHoldingUnits(FeeMethod feeMethod);
    
    /**
     * Calculates the total Holding market value based on FEE_BAL_TYP_CD = CMV
     * @param feeMethod feeMethod object
     * @return totalHoldingMarketValue
     */
    public BigDecimal getCurrentTaxLotBalanceTotalHoldingMarketValue(FeeMethod feeMethod);
    
}
