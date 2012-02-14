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
package org.kuali.kfs.coa.dataaccess;

/**
 * Methods needed to copy prior year accounts from current year accounts; this population is best
 * done directly through JDBC
 */
public interface PriorYearAccountDaoJdbc {
    /**
     * This method purges all records in the Prior Year Account table in the DB.
     * 
     * @return Number of records that were purged.
     */
    public abstract int purgePriorYearAccounts();
    
    /**
     * This method copies all organization records from the current Account table to the Prior Year Account table.
     * 
     * @return Number of records that were copied.
     */
    public abstract int copyCurrentAccountsToPriorYearTable();
}
