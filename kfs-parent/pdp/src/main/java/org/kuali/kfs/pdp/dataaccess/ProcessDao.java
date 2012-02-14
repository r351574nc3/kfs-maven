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
 * Created on Aug 19, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess;

import java.util.List;

import org.kuali.kfs.pdp.businessobject.PaymentProcess;
import org.kuali.rice.kim.bo.Person;


/**
 * @author jsissom
 */
public interface ProcessDao {
    
    public PaymentProcess createProcessToRun(Integer procId);
    
    public List<PaymentProcess> getAllExtractsToRun();
    
    public void setExtractProcessAsComplete(PaymentProcess paymentProcess);
    
}

