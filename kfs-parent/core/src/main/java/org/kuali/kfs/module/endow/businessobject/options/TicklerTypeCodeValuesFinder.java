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
package org.kuali.kfs.module.endow.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.TicklerTypeCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * This class defines a values finder for Tickler Type Code.
 */
public class TicklerTypeCodeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {

        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<TicklerTypeCode> codes = boService.findAll(TicklerTypeCode.class);
        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
        
        labels.add(new KeyLabelPair("", ""));
        for (Iterator<TicklerTypeCode> iter = codes.iterator(); iter.hasNext();) {
            TicklerTypeCode ticklerTypeCode = (TicklerTypeCode) iter.next();
            labels.add(new KeyLabelPair(ticklerTypeCode.getCode(), ticklerTypeCode.getName()));
        }

        return labels;
    }
    
}
