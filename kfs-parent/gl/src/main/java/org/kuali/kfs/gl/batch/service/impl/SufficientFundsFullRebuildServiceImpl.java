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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.Iterator;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.gl.batch.service.SufficientFundsFullRebuildService;
import org.kuali.kfs.gl.businessobject.SufficientFundRebuild;
import org.kuali.kfs.gl.dataaccess.SufficientFundRebuildDao;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of SufficientFundsFullRebuildService
 */
@Transactional
public class SufficientFundsFullRebuildServiceImpl implements SufficientFundsFullRebuildService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsFullRebuildServiceImpl.class);

    private SufficientFundRebuildDao sufficientFundRebuildDao;

    /**
     * Goes through all accounts in the database, and generates a sufficient fund rebuild record for each one!
     * @see org.kuali.kfs.gl.batch.service.SufficientFundsFullRebuildService#syncSufficientFunds()
     */
    public void syncSufficientFunds() {
        LOG.debug("syncSufficientFunds() started");

        sufficientFundRebuildDao.purgeSufficientFundRebuild();
        
        sufficientFundRebuildDao.populateSufficientFundRebuild();
        
    }

    public void setSufficientFundRebuildDao(SufficientFundRebuildDao sfd) {
        sufficientFundRebuildDao = sfd;
    }

}
