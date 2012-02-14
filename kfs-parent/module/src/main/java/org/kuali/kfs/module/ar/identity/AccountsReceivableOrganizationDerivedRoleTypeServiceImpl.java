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
package org.kuali.kfs.module.ar.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.ChartOrgHolderImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kim.service.support.impl.PassThruRoleTypeServiceBase;
import org.kuali.rice.kns.service.BusinessObjectService;

public class AccountsReceivableOrganizationDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
    protected static final String PROCESSOR_ROLE_NAME = "Processor";

    private BusinessObjectService businessObjectService;
    private FinancialSystemUserService financialSystemUserService;

    protected ChartOrgHolder getProcessingChartOrg( AttributeSet qualification ) {
        ChartOrgHolderImpl chartOrg = null;
        if ( qualification != null && !qualification.isEmpty() ) {
            chartOrg = new ChartOrgHolderImpl();
            // if the processing org is specified from the qualifications, use that
            chartOrg.setChartOfAccountsCode( qualification.get(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE) );
            chartOrg.setOrganizationCode( qualification.get(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_ORGANIZATION_CODE) );
            // otherwise default to the normal chart/org values and derive the processing chart/org
            if (StringUtils.isBlank(chartOrg.getChartOfAccountsCode()) || StringUtils.isBlank(chartOrg.getOrganizationCode())) {
                chartOrg.setChartOfAccountsCode( qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
                chartOrg.setOrganizationCode( qualification.get(KfsKimAttributes.ORGANIZATION_CODE) );
                if (StringUtils.isBlank(chartOrg.getChartOfAccountsCode()) || StringUtils.isBlank(chartOrg.getOrganizationCode())) {
                    return null;
                }
                Map<String, Object> arOrgOptPk = new HashMap<String, Object>( 2 );
                arOrgOptPk.put(ArPropertyConstants.OrganizationOptionsFields.CHART_OF_ACCOUNTS_CODE, chartOrg.getChartOfAccountsCode());
                arOrgOptPk.put(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_CODE, chartOrg.getOrganizationCode());
                OrganizationOptions oo = (OrganizationOptions) getBusinessObjectService().findByPrimaryKey(OrganizationOptions.class, arOrgOptPk);
                if (oo != null) {
                    chartOrg.setChartOfAccountsCode( oo.getProcessingChartOfAccountCode() );
                    chartOrg.setOrganizationCode( oo.getProcessingOrganizationCode() );
                } else {
                    chartOrg.setChartOfAccountsCode( PassThruRoleTypeServiceBase.UNMATCHABLE_QUALIFICATION );
                    chartOrg.setOrganizationCode( PassThruRoleTypeServiceBase.UNMATCHABLE_QUALIFICATION );
                }
            }
        }
        return chartOrg;
    }

    public boolean hasProcessorRole(ChartOrgHolder userOrg, AttributeSet qualification) {
        ChartOrgHolder processingOrg = getProcessingChartOrg(qualification);
        // if no org passed, check if their primary org is a processing org
        if ( processingOrg == null ) {
            // check the org options for this org
            Map<String, Object> arProcessOrgCriteria = new HashMap<String, Object>( 2 );
            arProcessOrgCriteria.put(ArPropertyConstants.SystemInformationFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, userOrg.getChartOfAccountsCode());
            arProcessOrgCriteria.put(ArPropertyConstants.SystemInformationFields.PROCESSING_ORGANIZATION_CODE, userOrg.getOrganizationCode());
            // return true if any matching org options records
            return getBusinessObjectService().countMatching(SystemInformation.class, arProcessOrgCriteria) > 0;
        } else { // org was passed, user's org must match
            return processingOrg.equals(userOrg);
        }        
    }

    public boolean hasBillerRole(ChartOrgHolder userOrg, AttributeSet qualification) {
        ChartOrgHolderImpl billingOrg = new ChartOrgHolderImpl();
        if ( qualification != null && !qualification.isEmpty()) {
            billingOrg.setChartOfAccountsCode( qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
            billingOrg.setOrganizationCode( qualification.get(KfsKimAttributes.ORGANIZATION_CODE) );
        }
        if (StringUtils.isBlank(billingOrg.getChartOfAccountsCode()) || StringUtils.isBlank(billingOrg.getOrganizationCode())) {
            Map<String, Object> arOrgOptPk = new HashMap<String, Object>( 2 );
            arOrgOptPk.put(ArPropertyConstants.OrganizationOptionsFields.CHART_OF_ACCOUNTS_CODE, userOrg.getChartOfAccountsCode());
            arOrgOptPk.put(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_CODE, userOrg.getOrganizationCode());
            return getBusinessObjectService().countMatching(OrganizationOptions.class, arOrgOptPk) > 0;
        } else {
            return billingOrg.equals(userOrg);
        }
    }
    
    
    @Override
    public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        if (getFinancialSystemUserService().isActiveFinancialSystemUser(principalId)) {
            ChartOrgHolder userOrg = getFinancialSystemUserService().getPrimaryOrganization(principalId, ArConstants.AR_NAMESPACE_CODE);
            if (PROCESSOR_ROLE_NAME.equals(roleName)) {
                return hasProcessorRole(userOrg, qualification);
            } else {  // billing role
                return hasBillerRole(userOrg, qualification) || hasProcessorRole(userOrg, qualification);
            }
        }
        return false;
    }
    
    @Override
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        List<RoleMembershipInfo> results = new ArrayList<RoleMembershipInfo>();
        Set<String> principalIds = new HashSet<String>();
        if (PROCESSOR_ROLE_NAME.equals(roleName)) {
            ChartOrgHolder processingOrg = getProcessingChartOrg(qualification);
            if ( processingOrg == null ) {
                // get all users for all processing orgs
                // build a set
                List<OrganizationOptions> ooList = (List<OrganizationOptions>)getBusinessObjectService().findAll(OrganizationOptions.class);
                for ( OrganizationOptions oo : ooList ) {
                    principalIds.clear();
                    principalIds.addAll( 
                            getFinancialSystemUserService().getPrincipalIdsForFinancialSystemOrganizationUsers(
                                    namespaceCode, 
                                    new ChartOrgHolderImpl( oo.getProcessingChartOfAccountCode(), oo.getProcessingOrganizationCode() )));
                    if ( !principalIds.isEmpty() ) {
                        AttributeSet roleQualifier = new AttributeSet(2);
                        roleQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, oo.getProcessingChartOfAccountCode());
                        roleQualifier.put(KfsKimAttributes.ORGANIZATION_CODE, oo.getProcessingOrganizationCode());
                        for ( String principalId : principalIds ) {
                            results.add( new RoleMembershipInfo( null, null, principalId, Role.PRINCIPAL_MEMBER_TYPE, roleQualifier ) );
                        }
                    }
                }
            } else {
                // get all users for the given org
                principalIds.addAll( getFinancialSystemUserService().getPrincipalIdsForFinancialSystemOrganizationUsers(ArConstants.AR_NAMESPACE_CODE, processingOrg) );
                if ( !principalIds.isEmpty() ) {
                    AttributeSet roleQualifier = new AttributeSet(2);
                    roleQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, processingOrg.getChartOfAccountsCode());
                    roleQualifier.put(KfsKimAttributes.ORGANIZATION_CODE, processingOrg.getOrganizationCode());
                    for ( String principalId : principalIds ) {
                        results.add( new RoleMembershipInfo( null, null, principalId, Role.PRINCIPAL_MEMBER_TYPE, roleQualifier ) );
                    }
                }
            }
        } else { // billing role
            ChartOrgHolderImpl billingOrg = new ChartOrgHolderImpl();
            if ( qualification != null && !qualification.isEmpty() ) {
                billingOrg.setChartOfAccountsCode( qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
                billingOrg.setOrganizationCode( qualification.get(KfsKimAttributes.ORGANIZATION_CODE) );
            }
            if (StringUtils.isBlank(billingOrg.getChartOfAccountsCode()) || StringUtils.isBlank(billingOrg.getOrganizationCode())) {
                // get all users for all billing orgs
                List<OrganizationOptions> ooList = (List<OrganizationOptions>)getBusinessObjectService().findAll(OrganizationOptions.class);
                for ( OrganizationOptions oo : ooList ) {
                    principalIds.clear();
                    principalIds.addAll( 
                            getFinancialSystemUserService().getPrincipalIdsForFinancialSystemOrganizationUsers(
                                    namespaceCode, 
                                    new ChartOrgHolderImpl( oo.getChartOfAccountsCode(), oo.getOrganizationCode() )));
                    if ( !principalIds.isEmpty() ) {
                        AttributeSet roleQualifier = new AttributeSet(2);
                        roleQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, oo.getChartOfAccountsCode());
                        roleQualifier.put(KfsKimAttributes.ORGANIZATION_CODE, oo.getOrganizationCode());
                        for ( String principalId : principalIds ) {
                            results.add( new RoleMembershipInfo( null, null, principalId, Role.PRINCIPAL_MEMBER_TYPE, roleQualifier ) );
                        }
                    }
                }
            } else {
                // get all users for given org
                principalIds.addAll( getFinancialSystemUserService().getPrincipalIdsForFinancialSystemOrganizationUsers(ArConstants.AR_NAMESPACE_CODE, billingOrg) );
                if ( !principalIds.isEmpty() ) {
                    AttributeSet roleQualifier = new AttributeSet(2);
                    roleQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, billingOrg.getChartOfAccountsCode());
                    roleQualifier.put(KfsKimAttributes.ORGANIZATION_CODE, billingOrg.getOrganizationCode());
                    for ( String principalId : principalIds ) {
                        results.add( new RoleMembershipInfo( null, null, principalId, Role.PRINCIPAL_MEMBER_TYPE, roleQualifier ) );
                    }
                }
            }
        }
        return results;
    }
    

    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    

    protected final FinancialSystemUserService getFinancialSystemUserService() {
        if (financialSystemUserService == null) {
            financialSystemUserService = SpringContext.getBean(FinancialSystemUserService.class);
        }
        return financialSystemUserService;
    }

}
