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

import org.kuali.kfs.integration.cam.CapitalAssetManagementAsset;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.KualiModuleService;


/**
 * Requisition Item Capital Asset Business Object.
 */
public class RequisitionItemCapitalAsset extends PurchasingItemCapitalAssetBase {

    private CapitalAssetManagementAsset asset;

    /**
     * Default constructor.
     */
    public RequisitionItemCapitalAsset() {
        super();
    }    
    
    public RequisitionItemCapitalAsset(Long capitalAssetNumber) {
        super(capitalAssetNumber);
    }

    public CapitalAssetManagementAsset getAsset() {
        return asset = (CapitalAssetManagementAsset) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CapitalAssetManagementAsset.class).retrieveExternalizableBusinessObjectIfNecessary(this, asset, "asset");
    }

}
