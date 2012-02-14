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

import org.kuali.kfs.coa.businessobject.SubAccount;


/**
 * This interface defines basic methods that SubAccount Dao's must provide
 */
public interface SubAccountDao {

    /**
     * Retrieves a {@link SubAccount} object by primary key.
     * 
     * @param chartOfAccountsCode - part of composite key
     * @param accountNumber - part of composite key
     * @param subAccountNumber - part of composite key
     * @return {@link SubAccount} by primary key
     */
    public SubAccount getByPrimaryId(String chartOfAccountsCode, String accountNumber, String subAccountNumber);

    /**
     * Retrieves {@link SubAccount} objects associated with the given chart-org-subAccount code combination
     * 
     * @param chartOfAccountsCode - 'Reports To' Chart of Accounts Code
     * @param organizationCode - 'Reports To' Organization Code
     * @param subAccountNumber - Sub Account Number
     * @return a list of {@link SubAccount} objects
     */
    public List getSubAccountsByReportsToOrganization(String chartOfAccountsCode, String organizationCode, String subAccountNumber);
}
