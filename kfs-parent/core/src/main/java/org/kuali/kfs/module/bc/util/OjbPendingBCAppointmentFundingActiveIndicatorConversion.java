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
package org.kuali.kfs.module.bc.util;

import org.kuali.rice.kns.util.OjbCharBooleanConversion;

/**
 * Handles conversion of active indicator so as to piggy back onto appointmentFundingDeleteIndicator by reversing the values.
 * Converts database delete values of Y and N to java active N and Y and back.
 */
public class OjbPendingBCAppointmentFundingActiveIndicatorConversion extends OjbCharBooleanConversion {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OjbPendingBCAppointmentFundingActiveIndicatorConversion.class);

    public final static String INDICATOR_NO  = "N";
    public final static String INDICATOR_YES = "Y";
    
    /**
     * This handles checking the boolean value coming in and converts it to 
     * the appropriate Y or N value.
     * @see FieldConversion#javaToSql(Object)
     */
    @Override
    public Object javaToSql(Object source) {
        Object sqlValue = super.javaToSql(source);
        
        if(INDICATOR_NO.equals(sqlValue)) {
            return INDICATOR_YES;
        }
        else if(INDICATOR_YES.equals(sqlValue)) {
            return INDICATOR_NO;
        }

        return sqlValue;
    }

    /**
     * This handles checking the sql coming back from the database and converting 
     * it to the appropriate boolean true or false value.
     * @see FieldConversion#sqlToJava(Object)
     */
    @Override
    public Object sqlToJava(Object source) {
        Object javaValue = super.sqlToJava(source);      
        
        if(javaValue == null) {
            return null;
        }
        else if(Boolean.TRUE.equals(javaValue)) {
            return Boolean.FALSE;
        }
        else if(Boolean.FALSE.equals(javaValue)) { 
            return Boolean.TRUE;
        }
         
        return javaValue;
    }
}
