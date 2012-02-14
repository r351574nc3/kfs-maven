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
package org.kuali.kfs.sec.service.impl;

import java.util.List;

import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.document.validation.impl.AccessSecurityAccountingDocumentRuleBase;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.impl.TransactionalDocumentDictionaryServiceImpl;


/**
 * Overrides of transaction dictionary service to return the access security business rule class for certain doc types
 */
public class SecTransactionalDocumentDictionaryServiceImpl extends TransactionalDocumentDictionaryServiceImpl {
    protected ParameterService parameterService;

    /**
     * Checks system parameter defining document types implementing access security and if type of given document is in list returns the access security rule base for the rules
     * class
     * 
     * @see org.kuali.rice.kns.service.impl.TransactionalDocumentDictionaryServiceImpl#getBusinessRulesClass(org.kuali.rice.kns.document.TransactionalDocument)
     */
    @Override
    public Class getBusinessRulesClass(TransactionalDocument document) {
        String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentType();

        // list of document types configured for access security
        List<String> documentTypes = parameterService.getParameterValues(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ACCESS_SECURITY_DOCUMENT_TYPES);
        if (documentTypes.contains(documentType)) {
            return AccessSecurityAccountingDocumentRuleBase.class;
        }

        return super.getBusinessRulesClass(document);
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


}
