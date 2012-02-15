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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.KualiCodeBase;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class CAECode extends EndowmentCode {
    
    private String caeType;

    /**
     * Gets the caeType attribute. 
     * @return Returns the caeType.
     */
    public String getCaeType() {
        return caeType;
    }

    /**
     * Sets the caeType attribute value.
     * @param caeType to set.
     */
    public void setCaeType(String caeType) {
        this.caeType = caeType;
    }
    
}
