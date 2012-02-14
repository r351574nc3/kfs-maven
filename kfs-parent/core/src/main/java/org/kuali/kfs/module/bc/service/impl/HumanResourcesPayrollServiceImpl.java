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
package org.kuali.kfs.module.bc.service.impl;

import org.kuali.kfs.module.bc.BCConstants.SynchronizationCheckType;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.Incumbent;
import org.kuali.kfs.module.bc.businessobject.Position;
import org.kuali.kfs.module.bc.dataaccess.HumanResourcesPayrollDao;
import org.kuali.kfs.module.bc.exception.IncumbentNotFoundException;
import org.kuali.kfs.module.bc.exception.PositionNotFoundException;
import org.kuali.kfs.module.bc.service.HumanResourcesPayrollService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bootstrap implementation of HumanResourcesPayrollService. Only implements the methods so that Budget will function. Data is not
 * correct and should not be used in production.
 * 
 * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService
 */
public class HumanResourcesPayrollServiceImpl implements HumanResourcesPayrollService {
    HumanResourcesPayrollDao humanResourcesPayrollDao;
    private PersonService<Person> personService;
    
    /**
     * This is just a bootstrap implementation. Should be replaced by the real integration with the payroll/hr system.
     * 
     * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService#validatePositionUnionCode(java.lang.String)
     */
    @NonTransactional
    public boolean validatePositionUnionCode(String positionUnionCode) {
        return true;
    }

    /**
     * This is just a bootstrap implementation. Should be replaced by the real integration with the payroll/hr system.
     * 
     * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService#getPosition(java.lang.Integer, java.lang.String)
     */
    @Transactional
    public Position getPosition(Integer universityFiscalYear, String positionNumber) throws PositionNotFoundException {
        Position position = humanResourcesPayrollDao.getPosition(universityFiscalYear, positionNumber);

        if (position == null) {
            throw new PositionNotFoundException(universityFiscalYear, positionNumber);
        }

        return position;
    }

    /**
     * This is just a bootstrap implementation. Should be replaced by the real integration with the payroll/hr system.
     * 
     * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService#getIncumbent(java.lang.String)
     */
    @Transactional
    public Incumbent getIncumbent(String emplid) throws IncumbentNotFoundException {
        Person user = (Person) getPersonService().getPersonByEmployeeId(emplid);

        if (user == null) {
            throw new IncumbentNotFoundException(emplid);
        }

        Incumbent incumbent = new BudgetConstructionIntendedIncumbent();
        incumbent.setEmplid(emplid);
        incumbent.setName(user.getName());

        return incumbent;
    }

    /**
     * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService#isActiveJob(java.lang.String, java.lang.String,
     *      java.lang.Integer, org.kuali.kfs.module.bc.BCConstants.SynchronizationCheckType)
     */
    @Transactional
    public boolean isActiveJob(String emplid, String positionNumber, Integer fiscalYear, SynchronizationCheckType synchronizationCheckType) {
        return true;
    }

    /**
     * Sets the humanResourcesPayrollDao attribute value.
     * 
     * @param humanResourcesPayrollDao The humanResourcesPayrollDao to set.
     */
    @NonTransactional
    public void setHumanResourcesPayrollDao(HumanResourcesPayrollDao humanResourcesPayrollDao) {
        this.humanResourcesPayrollDao = humanResourcesPayrollDao;
    }

    /**
     * @return Returns the personService.
     */
    protected PersonService<Person> getPersonService() {
        if(personService==null)
            personService = SpringContext.getBean(PersonService.class);
        return personService;
    }

}
