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
package org.kuali.kfs.coa.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.dataaccess.SubAccountDao;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kns.util.spring.Cached;

/**
 * This class is the service implementation for the SubAccount structure. This is the default implementation that gets delivered
 * with Kuali.
 */

@NonTransactional
public class SubAccountServiceImpl implements SubAccountService {
    private static final Logger LOG = Logger.getLogger(SubAccountServiceImpl.class);

    private SubAccountDao subAccountDao;

    /**
     * @see org.kuali.kfs.coa.service.SubAccountService#getByPrimaryId(java.lang.String, java.lang.String, java.lang.String)
     */
    public SubAccount getByPrimaryId(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        return subAccountDao.getByPrimaryId(chartOfAccountsCode, accountNumber, subAccountNumber);
    }

    /**
     * Method is used by KualiAccountAttribute to enable caching of accounts for routing.
     * 
     * @see org.kuali.kfs.coa.service.impl.SubAccountServiceImpl#getByPrimaryId(String, String, String)
     */
    @Cached
    public SubAccount getByPrimaryIdWithCaching(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        return subAccountDao.getByPrimaryId(chartOfAccountsCode, accountNumber, subAccountNumber);
    }

    /**
     * Retrieves SubAccount objects associated with the given chart-org-subAccount code combination
     * 
     * @param chartOfAccountsCode - 'Reports To' Chart of Accounts Code
     * @param organizationCode - 'Reports To' Organization Code
     * @param subAccountNumber - Sub Account Number
     * @return a list of SubAccount objects
     */
    public List getSubAccountsByReportsToOrganization(String chartOfAccountsCode, String organizationCode, String subAccountNumber) {
        return subAccountDao.getSubAccountsByReportsToOrganization(chartOfAccountsCode, organizationCode, subAccountNumber);
    }

    /**
     * @return SubAccountDao
     */
    public SubAccountDao getSubAccountDao() {
        return subAccountDao;
    }

    /**
     * @param subAccountDao
     */
    public void setSubAccountDao(SubAccountDao subAccountDao) {
        this.subAccountDao = subAccountDao;
    }
}
