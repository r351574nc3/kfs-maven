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
package org.kuali.kfs.sys.businessobject.defaultvalue;

import java.util.Collection;

import org.kuali.kfs.fp.businessobject.MessageOfTheDay;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.spring.CacheNoCopy;

public class MessageOfTheDayFinder implements ValueFinder {

    @CacheNoCopy
    public String getValue() {
        String motd = "unable to retrieve message of the day";
        Collection collection = SpringContext.getBean(BusinessObjectService.class).findAll(MessageOfTheDay.class);
        if (collection != null && !collection.isEmpty()) {
            motd = ((MessageOfTheDay) collection.iterator().next()).getFinancialSystemMessageOfTheDayText();
        }
        return motd;
    }

}
