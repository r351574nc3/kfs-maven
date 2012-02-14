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
package org.kuali.kfs.sys.batch;

import java.util.Properties;

import javax.sql.DataSource;

import org.kuali.rice.ksb.messaging.quartz.MessageServiceExecutorJobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

/**
 * This class wraps the spring version to allow deploy time determination of whether to actually create a scheduler and whether to
 * use the jdbc or ram job store.
 */
public class SchedulerFactoryBean extends org.springframework.scheduling.quartz.SchedulerFactoryBean {
    private static final Scheduler SCHEDULER_DUMMY = new SchedulerDummy();
    private boolean useQuartzScheduling;
    private boolean useJdbcJobstore;
    private DataSource dataSourceReference;
    private Properties quartzPropertiesReference;
    private DataSource nonTransactionalDataSourceReference;

    @Override
    public void destroy() throws SchedulerException {
        if (useQuartzScheduling) {
            super.destroy();
        }
    }

    public void afterPropertiesSet() throws Exception {
        if (useQuartzScheduling) {
            if (useJdbcJobstore) {
                quartzPropertiesReference.put("org.quartz.jobStore.useProperties", "false");
                quartzPropertiesReference.put("org.quartz.jobStore.isClustered", "true");
                setDataSource(dataSourceReference);
                setNonTransactionalDataSource(nonTransactionalDataSourceReference);
            }
            setQuartzProperties(quartzPropertiesReference);
            super.afterPropertiesSet();
        }
    }

    public Object getObject() {
        if (useQuartzScheduling) {
            return super.getObject();
        }
        return SCHEDULER_DUMMY;
    }

    /**
     * @see org.springframework.scheduling.quartz.SchedulerFactoryBean#createScheduler(org.quartz.SchedulerFactory, java.lang.String)
     */
    @Override
    protected Scheduler createScheduler(SchedulerFactory schedulerFactory, String schedulerName) throws SchedulerException {
        Scheduler scheduler = super.createScheduler(schedulerFactory, schedulerName);
        scheduler.addJobListener(new MessageServiceExecutorJobListener());
        return scheduler;
    }
    
    /**
     * Sets the dataSourceReference attribute value.
     * 
     * @param dataSourceReference The dataSourceReference to set.
     */
    public void setDataSourceReference(DataSource dataSourceReference) {
        this.dataSourceReference = dataSourceReference;
    }

    /**
     * Sets the useJdbcJobstore attribute value.
     * 
     * @param useJdbcJobstore The useJdbcJobstore to set.
     */
    public void setUseJdbcJobstore(boolean useJdbcJobstore) {
        this.useJdbcJobstore = useJdbcJobstore;
    }

    /**
     * Sets the useQuartzScheduling attribute value.
     * 
     * @param useQuartzScheduling The useQuartzScheduling to set.
     */
    public void setUseQuartzScheduling(boolean useQuartzScheduling) {
        this.useQuartzScheduling = useQuartzScheduling;
    }

    /**
     * Sets the quartzPropertiesReference attribute value.
     * 
     * @param quartzPropertiesReference The quartzPropertiesReference to set.
     */
    public void setQuartzPropertiesReference(Properties quartzPropertiesReference) {
        this.quartzPropertiesReference = quartzPropertiesReference;
    }

    public DataSource getNonTransactionalDataSourceReference() {
        return nonTransactionalDataSourceReference;
    }

    public void setNonTransactionalDataSourceReference(DataSource nonTransactionalDataSourceReference) {
        this.nonTransactionalDataSourceReference = nonTransactionalDataSourceReference;
    }
}
