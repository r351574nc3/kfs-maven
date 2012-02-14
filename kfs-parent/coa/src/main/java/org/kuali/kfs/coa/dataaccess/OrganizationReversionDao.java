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
package org.kuali.kfs.coa.dataaccess;

import java.util.List;

import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;


/**
 * This interface provides data access methods for {@link OrganizationReversion} and {@link OrganizationReversionCategory}
 */
public interface OrganizationReversionDao {

    /**
     * Retrieves an OrganizationReversion by primary key.
     * 
     * @param universityFiscalYear - part of composite key
     * @param financialChartOfAccountsCode - part of composite key
     * @param organizationCode - part of composite key
     * @return {@link OrganizationReversion} based on primary key
     */
    public OrganizationReversion getByPrimaryId(Integer universityFiscalYear, String financialChartOfAccountsCode, String organizationCode);

    /**
     * Get all the categories {@link OrganizationReversionCategory}
     * 
     * @return list of categories
     */
    public List<OrganizationReversionCategory> getCategories();
}
