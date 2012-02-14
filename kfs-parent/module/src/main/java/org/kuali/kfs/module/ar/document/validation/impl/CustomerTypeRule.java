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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;

public class CustomerTypeRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerTypeRule.class);

    protected CustomerType newCustomerType;

    @Override
    public void setupConvenienceObjects() {
        newCustomerType = (CustomerType) super.getNewBo();
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        success = validateCustomerTypeDescription(newCustomerType);

        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

    /**
     * This method checks if there is another customer type in the database with the same description
     * 
     * @param customerType
     * @return true if there is no other customer type in the database with the same description, false otherwise
     */
    public boolean validateCustomerTypeDescription(CustomerType customerType) {
        boolean success = true;
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        List<CustomerType> dataToValidateList = new ArrayList<CustomerType>(businessObjectService.findAll(CustomerType.class));
        List<String> errorMessages = new ArrayList<String>();

        Map<String, Account> retrievedAccounts = new HashMap<String, Account>();

        for (CustomerType record : dataToValidateList) {
            if (customerType.getCustomerTypeDescription() != null && customerType.getCustomerTypeDescription().equalsIgnoreCase(record.getCustomerTypeDescription())) {
                if (customerType.getCustomerTypeCode() != null && !customerType.getCustomerTypeCode().equalsIgnoreCase(record.getCustomerTypeCode())) {
                    putFieldError(ArPropertyConstants.CustomerTypeFields.CUSTOMER_TYPE_DESC, ArKeyConstants.CustomerTypeConstants.ERROR_CUSTOMER_TYPE_DUPLICATE_VALUE);
                    success = false;
                    break;
                }
            }
        }
        return success;
    }
}
