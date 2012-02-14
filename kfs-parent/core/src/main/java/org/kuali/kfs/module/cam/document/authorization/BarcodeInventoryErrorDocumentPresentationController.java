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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryLoadService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Presentation Controller for Barcode Error Documents
 */
public class BarcodeInventoryErrorDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {
    protected AssetBarcodeInventoryLoadService assetBarcodeInventoryLoadService;
    
    public BarcodeInventoryErrorDocumentPresentationController() {
        assetBarcodeInventoryLoadService = SpringContext.getBean(AssetBarcodeInventoryLoadService.class); 
    }    
    @Override
    protected boolean canSave(Document document) {
        return false;
    }

    @Override
    protected boolean canRoute(Document document) {
        return false;
    }

    @Override
    protected boolean canBlanketApprove(Document document) {
        return false;
    }
    
    @Override
    protected boolean canAddAdhocRequests(Document document) {
        return SpringContext.getBean(AssetService.class).isDocumentEnrouting(document);        
    }

    @Override
    protected boolean canCancel(Document document) {
        return assetBarcodeInventoryLoadService.isCurrentUserInitiator(document);
    }
    
    @Override
    protected boolean canDisapprove(Document document) {
        return assetBarcodeInventoryLoadService.isCurrentUserInitiator(document);
    }
}
