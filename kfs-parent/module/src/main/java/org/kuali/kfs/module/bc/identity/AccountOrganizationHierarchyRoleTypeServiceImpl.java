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
package org.kuali.kfs.module.bc.identity;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;

public class AccountOrganizationHierarchyRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    public static final String DESCEND_HIERARCHY_TRUE_VALUE = "Y";
    public static final String DESCEND_HIERARCHY_FALSE_VALUE = "N";

    {
        requiredAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        requiredAttributes.add(KfsKimAttributes.ORGANIZATION_CODE);
        checkRequiredAttributes = false;
    }

    private BudgetDocumentService budgetDocumentService;

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet,
     *      org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        // if no qualification given but the user is assigned an organization then return they have the role
        if ((qualification == null || qualification.isEmpty()) && roleQualifier != null && !roleQualifier.isEmpty()) {
            return true;
        }

        // if they don't have a qualification then return false for the role
        if (qualification == null || qualification.isEmpty() || roleQualifier == null || roleQualifier.isEmpty()) {
            return false;
        }

        String orgChartOfAccountsCode = qualification.get(BCPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        String organizationCode = qualification.get(KfsKimAttributes.ORGANIZATION_CODE);
        String roleChartOfAccountsCode = roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String roleOrganizationCode = roleQualifier.get(KfsKimAttributes.ORGANIZATION_CODE);

        String descendHierarchy = DESCEND_HIERARCHY_FALSE_VALUE;
        if (qualification.containsKey(KfsKimAttributes.DESCEND_HIERARCHY)) {
            descendHierarchy = qualification.get(KfsKimAttributes.DESCEND_HIERARCHY);
        }

        if (DESCEND_HIERARCHY_TRUE_VALUE.equals(descendHierarchy)) {
            Integer universityFiscalYear = Integer.valueOf(qualification.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR));
            String chartOfAccountsCode = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            String accountNumber = qualification.get(KfsKimAttributes.ACCOUNT_NUMBER);
            Integer organizationLevelCode = Integer.parseInt(qualification.get(BCPropertyConstants.ORGANIZATION_LEVEL_CODE));
            String accountReportExists = qualification.get(BCPropertyConstants.ACCOUNT_REPORTS_EXIST);
            
            // if account report mapping does not exist, want to give special view access
            if (Boolean.FALSE.toString().equals(accountReportExists)) {
                return true;
            }
            
            Integer roleOrganizationLevelCode = -1;
            List<BudgetConstructionAccountOrganizationHierarchy> accountOrganizationHierarchy = (List<BudgetConstructionAccountOrganizationHierarchy>) budgetDocumentService.retrieveOrBuildAccountOrganizationHierarchy(universityFiscalYear, chartOfAccountsCode, accountNumber);
            for (BudgetConstructionAccountOrganizationHierarchy accountOrganization : accountOrganizationHierarchy) {
                if (accountOrganization.getOrganizationChartOfAccountsCode().equals(roleChartOfAccountsCode) && accountOrganization.getOrganizationCode().equals(roleOrganizationCode)) {
                    roleOrganizationLevelCode = accountOrganization.getOrganizationLevelCode();
                }
            }
            
            if (roleOrganizationLevelCode == -1) {
                return false;
            }

            return roleOrganizationLevelCode.intValue() >= organizationLevelCode.intValue();
        }

        return roleChartOfAccountsCode.equals(orgChartOfAccountsCode) && roleOrganizationCode.equals(organizationCode);
    }

    /**
     * Sets the budgetDocumentService attribute value.
     * 
     * @param budgetDocumentService The budgetDocumentService to set.
     */
    public void setBudgetDocumentService(BudgetDocumentService budgetDocumentService) {
        this.budgetDocumentService = budgetDocumentService;
    }

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#getAttributeDefinitions(java.lang.String)
     */
    @Override
    public AttributeDefinitionMap getAttributeDefinitions(String kimTypId) {
        AttributeDefinitionMap map = super.getAttributeDefinitions(kimTypId);
        
        for (AttributeDefinition definition : map.values()) {
            if (KFSPropertyConstants.ORGANIZATION_CODE.equals(definition.getName())) {
                definition.setRequired(Boolean.FALSE);
            }
        }
        
        return map;
    }
}
