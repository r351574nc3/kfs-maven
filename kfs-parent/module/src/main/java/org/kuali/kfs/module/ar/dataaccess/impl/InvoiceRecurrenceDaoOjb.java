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

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.module.ar.dataaccess.InvoiceRecurrenceDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class InvoiceRecurrenceDaoOjb extends PlatformAwareDaoBaseOjb implements InvoiceRecurrenceDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceRecurrenceDaoOjb.class);

    /**
     * @see org.kuali.module.ar.dao.OrganizationOptionsDao#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public InvoiceRecurrence getByPrimaryId(String invoiceNumber) {
        LOG.debug("getByPrimaryId() started"); 

        Criteria criteria = new Criteria();
        criteria.addEqualTo("invoiceNumber", invoiceNumber);

        QueryByCriteria query = new QueryByCriteria(InvoiceRecurrence.class, criteria);
        query.addOrderByAscending("invoiceNumber");
        
        return (InvoiceRecurrence) getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

    public Iterator<InvoiceRecurrence> getAllActiveInvoiceRecurrences() {
        LOG.debug("getAllActiveInvoiceRecurrences() started"); 

        Criteria criteria = new Criteria();
        criteria.addEqualTo("active", true);

        QueryByCriteria query = new QueryByCriteria(InvoiceRecurrence.class, criteria);
        query.addOrderByAscending("invoiceNumber");
        
        return (Iterator<InvoiceRecurrence>)getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }
    
    public Iterator<InvoiceRecurrence> getAllInvoiceRecurrences() {
        LOG.debug("getAllInvoiceRecurrencees() started");
        Criteria criteria = new Criteria();
        QueryByCriteria query = new QueryByCriteria(InvoiceRecurrence.class, criteria);
        query.addOrderByAscending("invoiceNumber");
        
        return (Iterator<InvoiceRecurrence>)getPersistenceBrokerTemplate().getIteratorByQuery(query);
        
    }

}
