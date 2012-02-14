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


import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.PriorYearAccount;
import org.kuali.kfs.coa.dataaccess.PriorYearAccountDao;
import org.kuali.kfs.coa.dataaccess.PriorYearAccountDaoJdbc;
import org.kuali.kfs.coa.dataaccess.impl.PriorYearAccountDaoJdbcImpl;
import org.kuali.kfs.coa.service.PriorYearAccountService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the PriorYearAccountService interface.
 */
@Transactional
public class PriorYearAccountServiceImpl implements PriorYearAccountService {
    private static final Logger LOG = Logger.getLogger(PriorYearAccountServiceImpl.class);

    private PriorYearAccountDao priorYearAccountDao;
    private PriorYearAccountDaoJdbc priorYearAccountDaoJdbc;

    public PriorYearAccountServiceImpl() {
        super();
    }

    /**
     * 
     * @see org.kuali.kfs.coa.service.PriorYearAccountService#getByPrimaryKey(java.lang.String, java.lang.String)
     */
    public PriorYearAccount getByPrimaryKey(String chartCode, String accountNumber) {
        return priorYearAccountDao.getByPrimaryId(chartCode, accountNumber);
    }

    /**
     * @see org.kuali.kfs.coa.service.PriorYearAccountService#populatePriorYearAccountsFromCurrent()
     */
    public void populatePriorYearAccountsFromCurrent() {

        int purgedCount = priorYearAccountDaoJdbc.purgePriorYearAccounts();
        if (LOG.isInfoEnabled()) {
            LOG.info("number of prior year accounts purged : " + purgedCount);
        }

        int copiedCount = priorYearAccountDaoJdbc.copyCurrentAccountsToPriorYearTable();
        if (LOG.isInfoEnabled()) {
            LOG.info("number of current year accounts copied to prior year : " + copiedCount);
        }
    }


    /**
     * This method sets the local dao variable to the value provided.
     * 
     * @param priorYearAccountDao The priorYearAccountDao to set.
     */
    public void setPriorYearAccountDao(PriorYearAccountDao priorYearAccountDao) {
        this.priorYearAccountDao = priorYearAccountDao;
    }

    /**
     * This method sets the local dao jdbc variable to the value provided.
     * 
     * @param priorYearAccountDaoJdbc The priorYearAccountDaoJdbc to set.
     */
    public void setPriorYearAccountDaoJdbc(PriorYearAccountDaoJdbcImpl priorYearAccountDaoJdbc) {
        this.priorYearAccountDaoJdbc = priorYearAccountDaoJdbc;
    }
}
