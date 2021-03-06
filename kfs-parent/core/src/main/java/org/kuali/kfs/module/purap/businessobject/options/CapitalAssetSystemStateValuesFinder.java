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
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.CapitalAssetSystemState;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * Values finder for CapitalAssetSystemStates
 */
public class CapitalAssetSystemStateValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all CapitalAssetSystemStates.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection types = boService.findAll(CapitalAssetSystemState.class);
        List labels = new ArrayList();
        labels.add(new KeyLabelPair("", ""));
        for (Object type : types) {
            CapitalAssetSystemState camsSystemState = (CapitalAssetSystemState)type;           
            labels.add(new KeyLabelPair(camsSystemState.getCapitalAssetSystemStateCode(), camsSystemState.getCapitalAssetSystemStateDescription()));
        }

        return labels;
    }

}
