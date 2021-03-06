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
package org.kuali.kfs.module.bc.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants.BudgetConstructionPositionConstants;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * This class returns list of ba fund restriction levels.
 */
public class BudgetConstructionPositionStatusValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair(BudgetConstructionPositionConstants.POSITION_STATUS_APPROVED, "Approved"));
        keyValues.add(new KeyLabelPair(BudgetConstructionPositionConstants.POSITION_STATUS_DELETED, "Deleted"));
        keyValues.add(new KeyLabelPair(BudgetConstructionPositionConstants.POSITION_STATUS_FROZEN, "Frozen"));
        keyValues.add(new KeyLabelPair(BudgetConstructionPositionConstants.POSITION_STATUS_TEMPORARILY_INACTIVE, "Temporarily Inactive"));

        return keyValues;
    }

}
