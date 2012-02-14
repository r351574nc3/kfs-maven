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
package org.kuali.kfs.sys.dataaccess.impl;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.sys.businessobject.HomeOrigination;
import org.kuali.kfs.sys.dataaccess.HomeOriginationDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

/**
 * This class is the OJB implementation of the HomeOriginationDao interface.
 */
public class HomeOriginationDaoOjb extends PlatformAwareDaoBaseOjb implements HomeOriginationDao {

    private static Logger LOG = Logger.getLogger(HomeOriginationDaoOjb.class);


    /**
     * @see org.kuali.rice.kns.dao.HomeOriginationDao#getHomeOrigination()
     */
    public HomeOrigination getHomeOrigination() {
        HomeOrigination homeOrigination = null;

        homeOrigination = (HomeOrigination) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(HomeOrigination.class, new Criteria()));

        return homeOrigination;
    }
}
