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
package org.kuali.kfs.sec.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sec.SecConstants;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;


/**
 * Returns list of operator codes for security definition
 */
public class SecurityOperatorCodeFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();

        activeLabels.add(new KeyLabelPair(SecConstants.SecurityDefinitionOperatorCodes.EQUAL, "Equal"));
        activeLabels.add(new KeyLabelPair(SecConstants.SecurityDefinitionOperatorCodes.NOT_EQUAL, "Not Equal"));
        activeLabels.add(new KeyLabelPair(SecConstants.SecurityDefinitionOperatorCodes.GREATER_THAN, "Greater Than"));
        activeLabels.add(new KeyLabelPair(SecConstants.SecurityDefinitionOperatorCodes.GREATER_THAN_EQUAL, "Greater Than or Equal"));
        activeLabels.add(new KeyLabelPair(SecConstants.SecurityDefinitionOperatorCodes.LESS_THAN, "Less Than"));
        activeLabels.add(new KeyLabelPair(SecConstants.SecurityDefinitionOperatorCodes.LESS_THAN_EQUAL, "Less Than or Equal"));

        return activeLabels;
    }
}
