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
package org.kuali.kfs.gl.batch;

import java.util.Date;

import org.kuali.kfs.gl.batch.service.EnterpriseFeederService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;

/**
 * This step executes the enterprise feeder
 */
public class EnterpriseFeedStep extends AbstractWrappedBatchStep {

    private EnterpriseFeederService enterpriseFeederService;

    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            public boolean execute() {
                enterpriseFeederService.feed("enterpriseFeedJob", true);
                return true;
            }
        };
    }

    /**
     * Sets the enterpriseFeederService attribute value.
     * 
     * @param enterpriseFeederService The enterpriseFeederService to set.
     * @see org.kuali.kfs.gl.batch.service.EnterpriseFeederService
     */
    public void setEnterpriseFeederService(EnterpriseFeederService enterpriseFeederService) {
        this.enterpriseFeederService = enterpriseFeederService;
    }

}
