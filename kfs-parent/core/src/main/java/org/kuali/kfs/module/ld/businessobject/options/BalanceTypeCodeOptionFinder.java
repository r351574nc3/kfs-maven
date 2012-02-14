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
package org.kuali.kfs.module.ld.businessobject.options;

import static org.kuali.kfs.module.ld.LaborConstants.BalanceInquiries.BALANCE_TYPE_AC_AND_A21;
import static org.kuali.kfs.sys.KFSConstants.BALANCE_TYPE_ACTUAL;
import static org.kuali.kfs.sys.KFSConstants.BALANCE_TYPE_INTERNAL_ENCUMBRANCE;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * Option Finder for Labor Balance Type Code.
 */
public class BalanceTypeCodeOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();
        labels.add(new KeyLabelPair(BALANCE_TYPE_ACTUAL, "Actual"));
        labels.add(new KeyLabelPair(BALANCE_TYPE_AC_AND_A21, "A21"));
        labels.add(new KeyLabelPair(BALANCE_TYPE_INTERNAL_ENCUMBRANCE, "Internal Encumbrance"));

        return labels;
    }

    /**
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return BALANCE_TYPE_ACTUAL;
    }
}
