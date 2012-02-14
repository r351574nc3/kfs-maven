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
package org.kuali.kfs.module.ar.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.dataaccess.impl.ObjectCodeDaoOjb;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.dataaccess.CustomerDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class CustomerDaoOjb extends PlatformAwareDaoBaseOjb implements CustomerDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectCodeDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CustomerDao#getByPrimaryId(java.lang.String)
     */
    public Customer getByPrimaryId(String customerNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("customerNumber", customerNumber==null?customerNumber:customerNumber.toUpperCase());

        return (Customer) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Customer.class, criteria));
    }

    public Customer getByTaxNumber(String taxNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("customerTaxNbr", taxNumber);

        return (Customer) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Customer.class, criteria));
    }
    
    public Customer getByName(String customerName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("customerName", customerName);

        return (Customer) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Customer.class, criteria));
    }

}
