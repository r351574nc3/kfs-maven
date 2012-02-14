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
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.RoleManagementService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService
 */
@Transactional
public class BudgetConstructionProcessorServiceImpl implements BudgetConstructionProcessorService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionProcessorServiceImpl.class);

    private RoleManagementService roleManagementService;
    private OrganizationService organizationService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService#getProcessorOrgs(org.kuali.rice.kim.bo.Person)
     */
    public List<Organization> getProcessorOrgs(Person person) {
        List<Organization> processorOrgs = new ArrayList<Organization>();

        List<AttributeSet> allQualifications = roleManagementService.getRoleQualifiersForPrincipalIncludingNested(person.getPrincipalId(), BCConstants.BUDGET_CONSTRUCTION_NAMESPACE, BCConstants.KimConstants.BC_PROCESSOR_ROLE_NAME, null);
        for (AttributeSet attributeSet : allQualifications) {
            String chartOfAccountsCode = attributeSet.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            String organizationCode = attributeSet.get(KfsKimAttributes.ORGANIZATION_CODE);

            if (StringUtils.isNotBlank(chartOfAccountsCode) && StringUtils.isNotBlank(organizationCode)) {
                Organization org = organizationService.getByPrimaryId(chartOfAccountsCode, organizationCode);
                if (org != null && !processorOrgs.contains(org)) {
                    processorOrgs.add(org);
                }
            }
        }

        return processorOrgs;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService#isOrgProcessor(java.lang.String,
     *      java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean isOrgProcessor(String chartOfAccountsCode, String organizationCode, Person person) {
        AttributeSet qualification = new AttributeSet();
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, organizationCode);

        return roleManagementService.principalHasRole(person.getPrincipalId(), getBudgetProcessorRoleIds(), qualification);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService#isOrgProcessor(org.kuali.kfs.coa.businessobject.Organization,
     *      org.kuali.rice.kim.bo.Person)
     */
    public boolean isOrgProcessor(Organization organization, Person person) {
        try {
            return isOrgProcessor(organization.getChartOfAccountsCode(), organization.getOrganizationCode(), person);
        }
        catch (Exception e) {
            String errorMessage = String.format("Fail to determine if %s is an approver for %s. ", person, organization);
            LOG.info(errorMessage + e);
        }

        return false;
    }
    
    /**
     * @return role id for the budget processor role
     */
    protected List<String> getBudgetProcessorRoleIds() {
        List<String> roleId = new ArrayList<String>();
        roleId.add(roleManagementService.getRoleIdByName(BCConstants.BUDGET_CONSTRUCTION_NAMESPACE, BCConstants.KimConstants.BC_PROCESSOR_ROLE_NAME));

        return roleId;
    }

    /**
     * Gets the roleManagementService attribute. 
     * @return Returns the roleManagementService.
     */
    public RoleManagementService getRoleManagementService() {
        return roleManagementService;
    }

    /**
     * Sets the roleManagementService attribute value.
     * @param roleManagementService The roleManagementService to set.
     */
    public void setRoleManagementService(RoleManagementService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    /**
     * Gets the organizationService attribute.
     * 
     * @return Returns the organizationService.
     */
    protected OrganizationService getOrganizationService() {
        return organizationService;
    }

    /**
     * Sets the organizationService attribute value.
     * 
     * @param organizationService The organizationService to set.
     */
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

}
