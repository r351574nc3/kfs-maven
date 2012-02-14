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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItemMapping;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoicingDao;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class ElectronicInvoicingDaoOjb extends PlatformAwareDaoBaseOjb implements ElectronicInvoicingDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoicingDaoOjb.class);

    public ElectronicInvoicingDaoOjb() {
        super();
    }

    public ElectronicInvoiceLoadSummary getElectronicInvoiceLoadSummary(Integer loadId, String vendorDunsNumber) {
        LOG.debug("getElectronicInvoiceLoadSummary() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("id", loadId);
        criteria.addEqualTo("vendorDunsNumber", vendorDunsNumber);

        return (ElectronicInvoiceLoadSummary) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(ElectronicInvoiceLoadSummary.class, criteria));
    }

    public ElectronicInvoiceLoadSummary saveElectronicInvoiceLoadSummary(ElectronicInvoiceLoadSummary loadSummary) {
        LOG.debug("saveElectronicInvoiceLoadSummary() started");
        getPersistenceBrokerTemplate().store(loadSummary);
        getPersistenceBroker(true).retrieveAllReferences(loadSummary);
        return loadSummary;
    }

    public ElectronicInvoiceRejectDocument saveElectronicInvoiceReject(ElectronicInvoiceRejectDocument reject) {
        LOG.debug("saveElectronicInvoiceReject() started");
        getPersistenceBrokerTemplate().store(reject);
        getPersistenceBroker(true).retrieveAllReferences(reject);
        return reject;
    }

    public List getPendingElectronicInvoices() {
        LOG.debug("getPendingElectronicInvoices() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("status.code", PurapConstants.PaymentRequestStatuses.PENDING_E_INVOICE);
        criteria.addEqualTo("isElectronicInvoice", Boolean.TRUE);
        List invoices = (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentRequestDocument.class, criteria));
        for (Iterator iter = invoices.iterator(); iter.hasNext();) {
            PaymentRequestDocument p = (PaymentRequestDocument) iter.next();
        }

        return invoices;
    }

    public Map getDefaultItemMappingMap() {
        LOG.debug("getDefaultItemMappingMap() started");
        Criteria criteria = new Criteria();
        criteria.addIsNull("vendorHeaderGeneratedIdentifier");
        criteria.addIsNull("vendorDetailAssignedIdentifier");
        criteria.addEqualTo("active", true);
        return this.getItemMappingMap(criteria);
    }

    public Map getItemMappingMap(Integer vendorHeaderId, Integer vendorDetailId) {
        LOG.debug("getItemMappingMap() started for vendor id " + vendorHeaderId + "-" + vendorDetailId);
        Criteria criteria = new Criteria();
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorHeaderId);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorDetailId);
        criteria.addEqualTo("active", true);
        return this.getItemMappingMap(criteria);
    }

    protected Map getItemMappingMap(Criteria criteria) {
        Map hm = new HashMap();
        List itemMappings = (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ElectronicInvoiceItemMapping.class, criteria));

        for (Iterator iter = itemMappings.iterator(); iter.hasNext();) {
            ElectronicInvoiceItemMapping mapping = (ElectronicInvoiceItemMapping) iter.next();
            hm.put(mapping.getInvoiceItemTypeCode(), mapping);
        }
        return hm;
    }
}
