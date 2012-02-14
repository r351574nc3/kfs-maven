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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;

public class OrganizationOptionsPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    protected static final String ACCOUNTS_RECEIVABLE_MANAGER_ROLE_NAME = "Accounts Receivable Manager";
    
    @Override
    public Set<String> getConditionallyReadOnlySectionIds(MaintenanceDocument document) {
        Set<String> readOnlySectionIds = super.getConditionallyReadOnlySectionIds(document);
        setRemitToAddressSectionEditable(readOnlySectionIds);
        return readOnlySectionIds;
    }

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);
        setRemitToNameEditable(readOnlyPropertyNames);
        setOrgPostalZipCodeEditable(readOnlyPropertyNames);
        setBillingOrgFieldsEditable(readOnlyPropertyNames, document);
        setProcessingOrgFieldsEditable(readOnlyPropertyNames, document);
        return readOnlyPropertyNames;
    }

    /**
     * 
     * Billing Chart/Org are always read-only on an edit.  Always.
     * 
     * They are editable on an Add, but only if KIM lets you in on an Add, 
     * but thats handled elsewhere.
     * 
     * @param readOnlyPropertyNames
     * @param document
     */
    protected void setBillingOrgFieldsEditable(Set<String> readOnlyPropertyNames, MaintenanceDocument document) {
        if (document.isEdit()) {
            readOnlyPropertyNames.add(ArPropertyConstants.OrganizationOptionsFields.CHART_OF_ACCOUNTS_CODE);
            readOnlyPropertyNames.add(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_CODE);
        }
    }
    
    /**
     * Sets the processing Chart/Org code editable
     * 
     * @param readOnlyPropertyNames
     * @param document
     */
    protected void setProcessingOrgFieldsEditable(Set<String> readOnlyPropertyNames, MaintenanceDocument document) {
        
        if (document.isEdit()) {
            
            RoleManagementService rms = KIMServiceLocator.getRoleManagementService();
            
            Person user = GlobalVariables.getUserSession().getPerson();
            String principalId = user.getPrincipalId();
            
            List<String> roleIds = new ArrayList<String>();
            roleIds.add(rms.getRoleIdByName(KFSConstants.ParameterNamespaces.KFS, ACCOUNTS_RECEIVABLE_MANAGER_ROLE_NAME));
            
            // editable only for the AR Manager role
            if (!rms.principalHasRole(principalId, roleIds, null)) {
                readOnlyPropertyNames.add(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE);
                readOnlyPropertyNames.add(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_ORGANIZATION_CODE);
            } 
        }
    }
    
    /**
     * 
     * Sets the Remit-To Name (ie, OrgCheckPayableToName) to read only if thats how the system parameters are 
     * configured, otherwise leave it read/write.
     * 
     * @param readOnlyPropertyNames
     */
    protected void setRemitToNameEditable(Set<String> readOnlyPropertyNames) {
        ParameterService service = SpringContext.getBean(ParameterService.class);
        String nameEditable = service.getParameterValue(OrganizationOptions.class, ArConstants.REMIT_TO_NAME_EDITABLE_IND);
        if ("N".equalsIgnoreCase(nameEditable)) {
            readOnlyPropertyNames.add(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_CHECK_PAYABLE_TO_NAME);
        }
    }
    
    /**
     * 
     * Sets the OrgPostalZipCode to readonly if thats what the system parameters say, otherwise leave it 
     * read/write.
     * 
     * @param readOnlyPropertyNames
     */
    protected void setOrgPostalZipCodeEditable(Set<String> readOnlyPropertyNames) {
        ParameterService service = SpringContext.getBean(ParameterService.class);
        if (!service.getIndicatorParameter(KfsParameterConstants.ACCOUNTS_RECEIVABLE_DOCUMENT.class, ArConstants.ENABLE_SALES_TAX_IND) ){
            readOnlyPropertyNames.add(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_POSTAL_ZIP_CODE);
        }
    }
    
    /**
     * 
     * Sets the whole Remit-To Address section to read-only if thats what the system parameter says, otherwise leave
     * it read/wrtie.
     * 
     * @param readOnlySectionIds
     */
    protected void setRemitToAddressSectionEditable(Set<String> readOnlySectionIds) {
        ParameterService service = SpringContext.getBean(ParameterService.class);
        String addressEditable = service.getParameterValue(OrganizationOptions.class, ArConstants.REMIT_TO_ADDRESS_EDITABLE_IND);
        if ("N".equalsIgnoreCase(addressEditable)) {
            readOnlySectionIds.add(ArConstants.OrganizationOptionsSections.EDIT_ORGANIZATION_REMIT_TO_ADDRESS);
        }
    }
    
}
