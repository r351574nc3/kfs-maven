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
package org.kuali.kfs.coa.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.rice.kns.document.MaintenanceLock;

public interface SubObjectTrickleDownInactivationService {
    public List<MaintenanceLock> generateTrickleDownMaintenanceLocks(Account inactivatedAccount, String documentNumber);
    
    public List<MaintenanceLock> generateTrickleDownMaintenanceLocks(ObjectCode inactivatedObject, String documentNumber);
    
    public void trickleDownInactivateSubObjects(Account inactivatedAccount, String documentNumber);
    
    public void trickleDownInactivateSubObjects(ObjectCode inactivatedObject, String documentNumber);
}
