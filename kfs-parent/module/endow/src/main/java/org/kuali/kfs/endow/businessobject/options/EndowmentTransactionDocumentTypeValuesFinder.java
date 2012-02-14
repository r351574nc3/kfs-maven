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
import org.kuali.kfs.module.endow.batch.service.impl.CurrentTaxLotBalanceUpdateServiceImpl;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.ParameterService;

public class EndowmentTransactionDocumentTypeValuesFinder extends KeyValuesBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EndowmentTransactionDocumentTypeValuesFinder.class);
    
    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        
        // Create service objects.
        DocumentTypeService documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        ParameterService parameterService       = SpringContext.getBean(ParameterService.class);
        
        // Create label and initialize with the first entry being blank.
        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
        labels.add(new KeyLabelPair("", ""));
        
        // Read in parameter values.
        List<String> documentTypeNames =
            parameterService.getParameterValues(TransactionArchive.class, EndowParameterKeyConstants.TRANSACTION_ARCHIVE_DOCUMENT_TYPE_NAMES);
        String label= null;
        for (String documentTypeName : documentTypeNames) {
            if(documentTypeService.findByName(documentTypeName)== null){
                labels.add(new KeyLabelPair(documentTypeName, documentTypeName+" - Can't find it!"));
            }
            else{
                label = documentTypeService.findByName(documentTypeName).getLabel();
                labels.add(new KeyLabelPair(documentTypeName, documentTypeName+" - "+label));
            }
        }
        
        return labels;
    }
}
