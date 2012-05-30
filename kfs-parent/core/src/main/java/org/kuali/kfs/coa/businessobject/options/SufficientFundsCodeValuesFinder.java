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
package org.kuali.kfs.coa.businessobject.options;

import org.kuali.kfs.coa.businessobject.SufficientFundsCode;


/**
 * This class creates a new finder for our forms view (creates a drop-down of {@link SufficientFundsCode}s)
 */
public class SufficientFundsCodeValuesFinder extends KualiSystemCodeValuesFinder {

    /**
     * This method is used to tell the superclass what the class being looked up is.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KualiSystemCodeValuesFinder#getValuesClass()
     */
    protected Class getValuesClass() {
        return SufficientFundsCode.class;
    }
}