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
/*
 * Created on Oct 14, 2005
 *
 */
package org.kuali.kfs.coa.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.dataaccess.ObjectTypeDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class ObjectTypeDaoOjb extends PlatformAwareDaoBaseOjb implements ObjectTypeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectTypeDaoOjb.class);

    /**
     * @see org.kuali.kfs.coa.dataaccess.ObjectTypeDao#getByPrimaryKey(java.lang.String)
     */
    public ObjectType getByPrimaryKey(String code) {
        LOG.debug("getByPrimaryKey() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("code", code);

        QueryByCriteria qbc = QueryFactory.newQuery(ObjectType.class, crit);

        return (ObjectType) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

}
