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
package org.kuali.kfs.module.purap.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.QuoteTypeDescriptions;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * Value Finder for Purchase Order Quote Types.
 */
public class PurchaseOrderQuoteTypeValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all Purchase Order Quote Types.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteTypes.COMPETITIVE, QuoteTypeDescriptions.COMPETITIVE));
        keyValues.add(new KeyLabelPair(PurapConstants.QuoteTypes.PRICE_CONFIRMATION, QuoteTypeDescriptions.PRICE_CONFIRMATION));
        return keyValues;
    }
}
