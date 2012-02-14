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
package org.kuali.kfs.coa.identity;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;

public class OrganizationHierarchyReviewRoleTypeServiceImpl extends OrganizationHierarchyAwareRoleTypeServiceBase {

    private DocumentTypeService documentTypeService;

    /**
     * Attributes: Chart Code (required) Organization Code Document Type Name Requirement - Traverse the org hierarchy but not the
     * document type hierarchy
     * 
     * @see org.kuali.kfs.coa.identity.OrganizationOptionalHierarchyRoleTypeServiceImpl#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet,
     *      org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        if (isParentOrg(qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), qualification.get(KfsKimAttributes.ORGANIZATION_CODE), roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), roleQualifier.get(KfsKimAttributes.ORGANIZATION_CODE), true)) {
            Set<String> potentialParentDocumentTypeNames = new HashSet<String>(1);
            if (roleQualifier.containsKey(KfsKimAttributes.DOCUMENT_TYPE_NAME)) {
                potentialParentDocumentTypeNames.add(roleQualifier.get(KfsKimAttributes.DOCUMENT_TYPE_NAME));
            }
            return potentialParentDocumentTypeNames.isEmpty() 
                    || StringUtils.equalsIgnoreCase( qualification.get(KfsKimAttributes.DOCUMENT_TYPE_NAME), roleQualifier.get(KfsKimAttributes.DOCUMENT_TYPE_NAME)) 
                    || (KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().findByName(qualification.get(KfsKimAttributes.DOCUMENT_TYPE_NAME)), potentialParentDocumentTypeNames) != null);
        }
        return false;
    }

    protected DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = KEWServiceLocator.getDocumentTypeService();
        }
        return this.documentTypeService;
    }

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#getAttributeDefinitions(java.lang.String)
     */
    @Override
    public AttributeDefinitionMap getAttributeDefinitions(String kimTypeId) {
        AttributeDefinitionMap map = super.getAttributeDefinitions(kimTypeId);
        for (AttributeDefinition definition : map.values()) {
            if (KfsKimAttributes.ORGANIZATION_CODE.equals(definition.getName())) {
                definition.setRequired(Boolean.FALSE);
            }
        }
        return map;
    }
}
