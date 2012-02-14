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
package org.kuali.kfs.module.purap.identity;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;


public class SensitiveDataRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    
    {
        requiredAttributes.add(PurapKimAttributes.SENSITIVE_DATA_CODE);
        checkRequiredAttributes = false; // role document bombs for this doc if this is set to true
    }
    
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        if ( qualification == null ) {
            return false;
        }
        String[] codes = StringUtils.split(qualification.get(PurapKimAttributes.SENSITIVE_DATA_CODE),';');
        if ( codes == null ) {
            return false;
        }
        return Arrays.asList( codes ).contains(roleQualifier.get(PurapKimAttributes.SENSITIVE_DATA_CODE));
    }
}
