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
package org.kuali.kfs.module.purap.document.service;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.rice.kns.util.GlobalVariables;

public interface PurchasingService {

    public void setupCapitalAssetItems(PurchasingDocument purDoc);
    
    public void deleteCapitalAssetItems(PurchasingDocument purDoc, Integer itemIdentifier);
    
    public void setupCapitalAssetSystem(PurchasingDocument purDoc);

    
    public String getDefaultAssetTypeCodeNotThisFiscalYear();
    /**
     * 
     * Gets the default value for use tax
     * @param purDoc
     * @return boolean indicating value of use tax indicator
     */
    public boolean getDefaultUseTaxIndicatorValue(PurchasingDocument purDoc);
    
    public boolean checkCapitalAssetLocation(CapitalAssetLocation location);
    
    public boolean checkValidRoomNumber(CapitalAssetLocation location);

}
