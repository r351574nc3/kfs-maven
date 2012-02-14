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
package org.kuali.kfs.sec.businessobject.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.document.SecurityDefinitionMaintainableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.ParameterService;


/**
 * Returns list of valid document type names for security definition
 */
public class SecurityDefinitionDocumentTypeFinder extends KeyValuesBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityDefinitionDocumentTypeFinder.class);

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();

        // add option to include all document types
        activeLabels.add(new KeyLabelPair(SecConstants.ALL_DOCUMENT_TYPE_NAME, SecConstants.ALL_DOCUMENT_TYPE_NAME));

        WorkflowInfo workflowInfo = new WorkflowInfo();

        List<String> documentTypes = SpringContext.getBean(ParameterService.class).getParameterValues(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ACCESS_SECURITY_DOCUMENT_TYPES);
     
        // copy list so it can be sorted (since it is unmodifiable)
        List<String> sortedDocumentTypes = new ArrayList<String>(documentTypes);
        Collections.sort(sortedDocumentTypes);
        
        for (String documentTypeName : sortedDocumentTypes) {
            DocumentTypeDTO documentType = null;
            try {
                documentType = workflowInfo.getDocType(documentTypeName);
            }
            catch (WorkflowException e) {
                LOG.error("Invalid document type configured for security: " + documentTypeName);
                throw new RuntimeException("Invalid document type configured for security: " + documentTypeName);
            }

            if (documentType != null) {
                activeLabels.add(new KeyLabelPair(documentTypeName, documentType.getDocTypeLabel()));
            }
        }

        return activeLabels;
    }
}
