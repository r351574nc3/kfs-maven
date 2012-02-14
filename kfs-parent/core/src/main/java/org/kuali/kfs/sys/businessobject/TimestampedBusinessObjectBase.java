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
package org.kuali.kfs.sys.businessobject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.GlobalVariables;

public abstract class TimestampedBusinessObjectBase extends PersistableBusinessObjectBase implements TimestampedBusinessObject {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TimestampedBusinessObjectBase.class);

    private Timestamp lastUpdate;
    private String lastUpdateUserId;

    /**
     * @see org.kuali.kfs.sys.businessobject.TimestampedBusinessObject#getLastUpdate()
     */
    public Timestamp getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.TimestampedBusinessObject#getLastUpdateUser()
     */
    public Person getLastUpdateUser() {
        Person user = null;
        if (StringUtils.isNotBlank(lastUpdateUserId)) {
            user = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPersonByPrincipalName(lastUpdateUserId);
        }

        return user;
    }

    /**
     * @see org.kuali.kfs.sys.businessobject.TimestampedBusinessObject#getLastUpdateUserId()
     */
    public String getLastUpdateUserId() {
        return this.lastUpdateUserId;
    }

    /**
     * @param lastUpdateUserId The lastUpdateUserId to set.
     */
    public void setLastUpdateUserId(String lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    /**
     * @param lastUpdate The lastUpdate to set.
     */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void beforeInsert(PersistenceBroker broker) throws PersistenceBrokerException {
        super.beforeInsert(broker);

        lastUpdate = new Timestamp((new Date()).getTime());
        lastUpdateUserId = GlobalVariables.getUserSession().getPerson().getPrincipalName();
    }

    public void beforeUpdate(PersistenceBroker broker) throws PersistenceBrokerException {
        super.beforeUpdate(broker);

        lastUpdate = new Timestamp((new Date()).getTime());
        lastUpdateUserId = GlobalVariables.getUserSession().getPerson().getPrincipalName();
    }
}
