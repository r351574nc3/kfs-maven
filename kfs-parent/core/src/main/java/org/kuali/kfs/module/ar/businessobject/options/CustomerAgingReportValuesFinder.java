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
package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * Option Finder for Report Option in Customer Aging Report
 */
public class CustomerAgingReportValuesFinder extends KeyValuesBase implements ValueFinder {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair(ArConstants.CustomerAgingReportFields.PROCESSING_ORG, ArConstants.CustomerAgingReportFields.PROCESSING_ORG));
        keyValues.add(new KeyLabelPair(ArConstants.CustomerAgingReportFields.BILLING_ORG, ArConstants.CustomerAgingReportFields.BILLING_ORG));
        keyValues.add(new KeyLabelPair(ArConstants.CustomerAgingReportFields.ACCT, ArConstants.CustomerAgingReportFields.ACCT));
        return keyValues;
    }

    /**
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return ArConstants.CustomerAgingReportFields.PROCESSING_ORG;
    }
}
