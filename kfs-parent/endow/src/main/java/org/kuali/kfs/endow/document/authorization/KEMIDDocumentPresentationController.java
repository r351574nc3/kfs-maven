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
package org.kuali.kfs.module.endow.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KNSConstants;

public class KEMIDDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {


    /**
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {

        Set<String> fields = super.getConditionallyReadOnlyPropertyNames(document);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        String kemidValueSystemParam = parameterService.getParameterValue(KEMID.class, EndowParameterKeyConstants.KEMID_VALUE);

        if (EndowConstants.KemidValueOptions.AUTOMATIC.equalsIgnoreCase(kemidValueSystemParam)) {
            fields.add(EndowPropertyConstants.KEMID);
        }

        if (KNSConstants.MAINTENANCE_NEW_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction()) || KNSConstants.MAINTENANCE_COPY_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction())) {
            fields.add(EndowPropertyConstants.KEMID_DORMANT_IND);
            fields.add(EndowPropertyConstants.KEMID_CLOSED_IND);

        }
        if (KNSConstants.MAINTENANCE_NEW_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction())) {

            fields.add(EndowPropertyConstants.KEMID_CLOSED_TO_KEMID);
            fields.add(EndowPropertyConstants.KEMID_CLOSE_CODE);
            fields.add(EndowPropertyConstants.KEMID_DISPOSITION_OF_FUNDS);
            fields.add(EndowPropertyConstants.KEMID_DATE_CLOSED);

        }


        return fields;
    }
}
