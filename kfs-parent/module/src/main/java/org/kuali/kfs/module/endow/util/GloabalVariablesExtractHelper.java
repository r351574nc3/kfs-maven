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
package org.kuali.kfs.module.endow.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;

/**
 * This class will help extract the error messages from GlobalVariables object 
 * and creates a list of string.
 */
public class GloabalVariablesExtractHelper {

    /**
     * Extracts errors for error report writing.
     * 
     * @return a list of error messages
     */
    public static List<String> extractGlobalVariableErrors() {
        List<String> result = new ArrayList<String>();

        MessageMap errorMap = GlobalVariables.getMessageMap();

        //Set<String> errorKeys = errorMap.keySet();  // deprecated
        Set<String> errorKeys = errorMap.getAllPropertiesWithErrors();
        List<ErrorMessage> errorMessages = null;
        Object[] messageParams;
        String errorKeyString;
        String errorString;

        for (String errorProperty : errorKeys) {
            //errorMessages = (List<ErrorMessage>) errorMap.get(errorProperty);  // deprecated
            errorMessages = (List<ErrorMessage>) errorMap.getErrorMessagesForProperty(errorProperty);

            for (ErrorMessage errorMessage : errorMessages) {
                errorKeyString = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(errorMessage.getErrorKey());
                messageParams = errorMessage.getMessageParameters();

                // MessageFormat.format only seems to replace one
                // per pass, so I just keep beating on it until all are gone.
                if (StringUtils.isBlank(errorKeyString)) {
                    errorString = errorMessage.getErrorKey();
                }
                else {
                    errorString = errorKeyString;
                }
                System.out.println(errorString);
                while (errorString.matches("^.*\\{\\d\\}.*$")) {
                    errorString = MessageFormat.format(errorString, messageParams);
                }
                result.add(errorString);
            }
        }

        // clear the stuff out of global vars, as we need to reformat it and put it back
        GlobalVariables.clear();
        return result;
    }
}
