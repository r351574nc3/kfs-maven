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
package org.kuali.kfs.module.endow.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.ParameterService;

public class EndowmentRecurringCashTransferDocumentTypeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("unchecked")
    public List<KeyLabelPair> getKeyValues() {
        // Create label and initialize with the first entry being blank.
        List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();
        keyValues.add(new KeyLabelPair("", ""));
        
        List<String> documentTypeNames = SpringContext.getBean(ParameterService.class).getParameterValues(EndowmentRecurringCashTransfer.class, EndowParameterKeyConstants.ENDOWMENT_RECURRING_CASH_TRANSFER_DOCUMENT_TYPES);
        DocumentTypeService documentTypeService = SpringContext.getBean(DocumentTypeService.class);

        String label= null;
        for (String documentTypeName : documentTypeNames) {
            if(documentTypeService.findByName(documentTypeName)== null){
                keyValues.add(new KeyLabelPair(documentTypeName, documentTypeName+" - Can't find it!"));
            }
            else{
                label = documentTypeService.findByName(documentTypeName).getLabel();
                keyValues.add(new KeyLabelPair(documentTypeName, documentTypeName+" - "+label));
            }            
        }

        return keyValues;
    }

}
