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
package org.kuali.kfs.module.cg.businessobject.defaultvalue;

import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;

/**
 * Returns the next Proposal number available.
 */
public class NextProposalNumberFinder implements ValueFinder {

    /**
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return getLongValue().toString();
    }

    /**
     * Gets the next sequence number as a long.
     * 
     * @return
     */
    public static Long getLongValue() {
        // no constant because this is the only place the sequence name is used
        SequenceAccessorService sas = KNSServiceLocator.getSequenceAccessorService();        
        return sas.getNextAvailableSequenceNumber("CGPRPSL_NBR_SEQ", Proposal.class);
    }
}
