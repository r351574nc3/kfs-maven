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
package org.kuali.kfs.fp.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * This class returns list of payment method key value pairs.
 */
public class PaymentMethodValuesFinder extends KeyValuesBase {

    static List<KeyLabelPair> activeLabels = new ArrayList<KeyLabelPair>();
    static {
        activeLabels.add(new KeyLabelPair("", ""));
        activeLabels.add(new KeyLabelPair("P", "P - Check/ACH"));
        activeLabels.add(new KeyLabelPair("F", "F - Foreign Draft"));
        activeLabels.add(new KeyLabelPair("W", "W - Wire Transfer"));
    }
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        return activeLabels;
    }

}
