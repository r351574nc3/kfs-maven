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
package org.kuali.kfs.sec.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.SecKeyConstants;
import org.kuali.kfs.sec.businessobject.SecurityDefinition;
import org.kuali.kfs.sec.businessobject.SecurityModel;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * Implements business rules checks on the SecurityDefinition maintenance document
 */
public class SecurityDefinitionRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityDefinitionRule.class);

    private SecurityDefinition oldSecurityDefinition;
    private SecurityDefinition newSecurityDefinition;

    public SecurityDefinitionRule() {
        super();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = super.processCustomApproveDocumentBusinessRules(document);

        if (!isValid) {
            return isValid;
        }

        boolean isMaintenanceEdit = document.isEdit();

        isValid &= validateSecurityDefinition(isMaintenanceEdit);

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        if (!isValid) {
            return isValid;
        }

        boolean isMaintenanceEdit = document.isEdit();

        isValid &= validateSecurityDefinition(isMaintenanceEdit);

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        oldSecurityDefinition = (SecurityDefinition) super.getOldBo();
        newSecurityDefinition = (SecurityDefinition) super.getNewBo();
    }

    /**
     * Validates business rules against a security definition record
     * 
     * @param isMaintenanceEdit boolean indicating whether the maintenance action is an edit (true), or a new/copy (false)
     * @return boolean true if all rules pass, false if at least one fails
     */
    protected boolean validateSecurityDefinition(boolean isMaintenanceEdit) {
        boolean isValid = true;

        if (!isMaintenanceEdit) {
            boolean validDefinitionName = verifyDefinitionNameIsUnique(newSecurityDefinition, KNSConstants.MAINTENANCE_NEW_MAINTAINABLE);
            if (!validDefinitionName) {
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * For new or copy action verifies the name given for the definition is not being used by another definition
     * 
     * @param securityDefinition SecurityDefinition with name to check
     * @param errorKeyPrefix String errorPrefix to use if any errors are found
     * @return boolean true if name exists, false if not
     */
    protected boolean verifyDefinitionNameIsUnique(SecurityDefinition securityDefinition, String errorKeyPrefix) {
        boolean isValid = true;

        Map<String, String> searchValues = new HashMap<String, String>();
        searchValues.put(KFSPropertyConstants.NAME, securityDefinition.getName());

        int matchCount = SpringContext.getBean(BusinessObjectService.class).countMatching(SecurityDefinition.class, searchValues);
        if (matchCount > 0) {
            GlobalVariables.getMessageMap().putError(errorKeyPrefix + KFSPropertyConstants.NAME, SecKeyConstants.ERROR_DEFINITION_NAME_NON_UNIQUE, securityDefinition.getName());
            isValid = false;
        }

        return isValid;
    }


}
