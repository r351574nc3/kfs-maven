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
package org.kuali.kfs.pdp.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.core.util.KeyLabelPair;

public class PaymentStatusValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("unchecked")
    public List<KeyLabelPair> getKeyValues() {
        List<PaymentStatus> boList = (List) SpringContext.getBean(KeyValuesService.class).findAll(PaymentStatus.class);
        List<KeyLabelPair> keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair("", ""));
        for (PaymentStatus element : boList) {
            keyValues.add(new KeyLabelPair(element.getCode(), element.getCode()));
        }
        keyValues.add(new KeyLabelPair(PdpConstants.PaymentStatusCodes.HELD_TAX_ALL, PdpConstants.PaymentStatusCodes.HELD_TAX_ALL));

        return keyValues;
    }

}
