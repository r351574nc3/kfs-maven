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
package org.kuali.kfs.module.cg.identity;

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimCommonUtils;

public class ResearchRiskReviewRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    DocumentTypeService documentTypeService;
    
    @Override
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        if (KimCommonUtils.storedValueNotSpecifiedOrInputValueMatches(roleQualifier, qualification, CgKimAttributes.RESEARCH_RISK_TYPE_CODE)) {
            Set<String> potentialParentDocumentTypeNames = new HashSet<String>(1);
            if (roleQualifier.containsKey(KfsKimAttributes.DOCUMENT_TYPE_NAME)) {
                potentialParentDocumentTypeNames.add(roleQualifier.get(KfsKimAttributes.DOCUMENT_TYPE_NAME));
            }
            return potentialParentDocumentTypeNames.isEmpty() || qualification.get(KfsKimAttributes.DOCUMENT_TYPE_NAME).equalsIgnoreCase(roleQualifier.get(KfsKimAttributes.DOCUMENT_TYPE_NAME)) || (KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().findByName(qualification.get(KfsKimAttributes.DOCUMENT_TYPE_NAME)), potentialParentDocumentTypeNames) != null);
        }
        return false;
    }
    
    protected DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = KEWServiceLocator.getDocumentTypeService();
        }
        return this.documentTypeService;
    }
}
