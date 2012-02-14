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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.sql.Date;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.CashSweepModel;
import org.kuali.kfs.module.endow.dataaccess.CashSweepModelDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class CashSweepModelDaoOjb extends PlatformAwareDaoBaseOjb implements CashSweepModelDao {

    /**
     * @see org.kuali.kfs.module.endow.dataaccess.CashSweepModelDao#getCashSweepModelWithNextPayDateEqualToCurrentDate()
     */
    public List<CashSweepModel> getCashSweepModelWithNextPayDateEqualToCurrentDate(Date currentDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.CASH_SWEEP_MODEL_NEXT_DUE_DATE, currentDate);
        criteria.addNotNull(EndowPropertyConstants.CASH_SWEEP_MODEL_FREQUENCY_CDOE);
        criteria.addEqualTo(EndowPropertyConstants.CASH_SWEEP_MODEL_ACTIVE_INDICATOR, "Y");
        return (List<CashSweepModel>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(CashSweepModel.class, criteria));
    }

}
