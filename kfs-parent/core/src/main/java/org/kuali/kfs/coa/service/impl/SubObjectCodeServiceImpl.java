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

import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.dataaccess.SubObjectCodeDao;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.UniversityDateService;

/**
 * This class is the service implementation for the SubObjectCode structure. This is the default implementation that gets delivered
 * with Kuali.
 */

@NonTransactional
public class SubObjectCodeServiceImpl implements SubObjectCodeService {
    private SubObjectCodeDao subObjectCodeDao;
    private UniversityDateService universityDateService;

    /**
     * @see org.kuali.kfs.coa.service.SubObjectCodeService#getByPrimaryId(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public SubObjectCode getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode) {
        return subObjectCodeDao.getByPrimaryId(universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectCode, financialSubObjectCode);
    }

    /**
     * @see org.kuali.kfs.coa.service.SubObjectCodeService#getByPrimaryIdForCurrentYear(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public SubObjectCode getByPrimaryIdForCurrentYear(String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode) {
        return this.getByPrimaryId(universityDateService.getCurrentFiscalYear(), chartOfAccountsCode, accountNumber, financialObjectCode, financialSubObjectCode);
    }

    /**
     * 
     * This method injects SubObjectCodeDao
     * @param subObjectCodeDao
     */
    public void setSubObjectCodeDao(SubObjectCodeDao subObjectCodeDao) {
        this.subObjectCodeDao = subObjectCodeDao;
    }

    /**
     * 
     * This method injects UniversityDateService
     * @param universityDateService
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
}
