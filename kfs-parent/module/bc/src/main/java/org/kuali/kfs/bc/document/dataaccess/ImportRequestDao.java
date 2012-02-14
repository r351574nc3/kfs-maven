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
package org.kuali.kfs.module.bc.document.dataaccess;

import java.util.List;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionRequestMove;
import org.kuali.rice.kns.bo.BusinessObject;

/**
 * Facilates Budget Construction Import requests
 * 
 */
public interface ImportRequestDao {
    
    /**
     * 
     * @return header record or null if record does not exist.
     */
    public BudgetConstructionHeader getHeaderRecord(BudgetConstructionRequestMove record, Integer budgetYear);
    
    /**
     * find all BudgetConstructionRequestMove records with null error codes
     * 
     * @return List<BudgetConstructionRequestMove>
     */
    public List<BudgetConstructionRequestMove> findAllNonErrorCodeRecords(String principalId);
    
    /**
     * Save or update business object based on isUpdate
     * 
     * @param businessObject
     * @param isUpadate
     */
    public void save(BusinessObject businessObject, boolean isUpdate);
   
}

