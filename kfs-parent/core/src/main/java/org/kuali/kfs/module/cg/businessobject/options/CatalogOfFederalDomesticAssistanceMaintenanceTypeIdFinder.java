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
package org.kuali.kfs.module.cg.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.cg.businessobject.CFDA;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * Allows some information about persisted {@link Cfda} instances to be looked up.
 */
public class CatalogOfFederalDomesticAssistanceMaintenanceTypeIdFinder extends KeyValuesBase {

    /**
     * Retrieves the list of possible CFDA Maintenance Type IDs and generates a collection with all the possible values.
     * 
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection codes = boService.findAll(CFDA.class);

        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
        labels.add(new KeyLabelPair("", ""));

        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            CFDA cfdaReference = (CFDA) iter.next();

            if (!isDuplicateValue(labels, cfdaReference.getCfdaMaintenanceTypeId())) {
                labels.add(new KeyLabelPair(cfdaReference.getCfdaMaintenanceTypeId(), cfdaReference.getCfdaMaintenanceTypeId()));
            }
        }

        return labels;
    }

    /**
     * This method determines if a value already exists in the collection.
     * 
     * @param collection The collection to be examined.
     * @param value The value to be added to the collection if it does not already exist within it.
     * @return True if the value passed in already exists in the collection, false otherwise.
     */
    private boolean isDuplicateValue(List<KeyLabelPair> collection, String value) {
        boolean duplicate = false;

        for (KeyLabelPair klp : collection) {
            String klpLabel = klp.getLabel();
            if (klpLabel != null) {
                duplicate |= klpLabel.trim().equalsIgnoreCase(value.trim());
            }
        }

        return duplicate;
    }

}
