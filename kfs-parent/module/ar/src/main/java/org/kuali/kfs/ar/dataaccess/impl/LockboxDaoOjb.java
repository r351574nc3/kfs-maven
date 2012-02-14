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

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.ar.businessobject.Lockbox;
import org.kuali.kfs.module.ar.dataaccess.LockboxDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.util.TransactionalServiceUtils;

public class LockboxDaoOjb extends PlatformAwareDaoBaseOjb implements LockboxDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LockboxDaoOjb.class);

    /**
     * @see org.kuali.module.ar.dao.OrganizationOptionsDao#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public Lockbox getByPrimaryId(Long invoiceSequenceNumber) {
        LOG.debug("getByPrimaryId() started"); 

        Criteria criteria = new Criteria();
        criteria.addEqualTo("invoiceSequenceNumber", invoiceSequenceNumber);

        QueryByCriteria query = new QueryByCriteria(Lockbox.class, criteria);
        query.addOrderByAscending("processedInvoiceDate");
        query.addOrderByAscending("batchSequenceNumber");
        
        return (Lockbox) getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

    public Iterator<Lockbox> getByLockboxNumber(String lockboxNumber) {
        LOG.debug("getbyLockboxNumber() started");
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("lockboxNumber", lockboxNumber);
        
        QueryByCriteria query = new QueryByCriteria(Lockbox.class, criteria);
        query.addOrderByAscending("processedInvoiceDate");
        query.addOrderByAscending("batchSequenceNumber");
        
        return (Iterator<Lockbox>)getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }
    
    public Iterator<Lockbox> getAllLockboxes() {
        LOG.debug("getAllLockboxes() started");
        Criteria criteria = new Criteria();
        QueryByCriteria query = new QueryByCriteria(Lockbox.class, criteria);
        query.addOrderByAscending("processedInvoiceDate");
        query.addOrderByAscending("batchSequenceNumber");
        
        return (Iterator<Lockbox>)getPersistenceBrokerTemplate().getIteratorByQuery(query);
        
    }

    public Long getMaxLockboxSequenceNumber() {
        Criteria crit = new Criteria();
        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(Lockbox.class, crit);
        reportQuery.setAttributes(new String[] { "MAX(AR_INV_SEQ_NBR)" });

        Iterator<?> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        if (iter.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iter);
            BigDecimal max = (BigDecimal) data[0]; // Don't know why OJB returns a BigDecimal, but it does

            if (max == null) {
                return new Long(0);
            }
            else {
                return new Long(max.longValue());
            }
        }
        else {
            return new Long(0);
        }
    }
    
}
