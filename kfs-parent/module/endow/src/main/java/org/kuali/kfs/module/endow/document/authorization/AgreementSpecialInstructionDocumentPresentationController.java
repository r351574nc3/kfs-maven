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
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.AgreementSpecialInstruction;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class AgreementSpecialInstructionDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {
    
    /**
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {

        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);
        AgreementSpecialInstruction agreementSpecialInstruction = (AgreementSpecialInstruction) document.getNewMaintainableObject().getBusinessObject();

        // Set preset values ready only
        if (EndowConstants.AgreementSpecialInstructionCode.AGRMNT_SPCL_INSTRC_CD_NONE.equals(agreementSpecialInstruction.getCode())) {
            readOnlyPropertyNames.add(EndowPropertyConstants.KUALICODEBASE_ACTIVE_INDICATOR);
        }

        return readOnlyPropertyNames;
    }
}
