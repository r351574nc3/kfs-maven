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
package org.kuali.kfs.gl.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * An implementation of ValueFinder that returns all balance types, but which defaults to external encumbrance
 */
public class EncumbranceBalanceTypeOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns the default value of this ValueFinder, in this case, external encumbrance
     * 
     * @return a String with the key of the default value
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        OptionsService os = SpringContext.getBean(OptionsService.class);
        SystemOptions o = os.getCurrentYearOptions();

        return o.getExtrnlEncumFinBalanceTypCd();
    }

    /**
     * Returns a list of all balance types
     * 
     * @return a List of key/value pairs to populate a drop down box
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();

        OptionsService os = SpringContext.getBean(OptionsService.class);
        SystemOptions o = os.getCurrentYearOptions();

        labels.add(new KeyLabelPair(o.getExtrnlEncumFinBalanceTypCd(), o.getExtrnlEncumFinBalanceTypCd() + " - " + o.getExtrnlEncumFinBalanceTyp().getFinancialBalanceTypeName()));
        labels.add(new KeyLabelPair(o.getIntrnlEncumFinBalanceTypCd(), o.getIntrnlEncumFinBalanceTypCd() + " - " + o.getIntrnlEncumFinBalanceTyp().getFinancialBalanceTypeName()));
        labels.add(new KeyLabelPair(o.getPreencumbranceFinBalTypeCd(), o.getPreencumbranceFinBalTypeCd() + " - " + o.getPreencumbranceFinBalType().getFinancialBalanceTypeName()));

        return labels;
    }
}
