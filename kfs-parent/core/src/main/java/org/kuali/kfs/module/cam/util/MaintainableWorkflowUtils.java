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
package org.kuali.kfs.module.cam.util;

import org.apache.commons.lang.math.NumberUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

/**
 * In situation where the Maintainable does not have access to the document,  this class is a utility which 
 * will retrieve the workflow document 
 * 
 */
public final class MaintainableWorkflowUtils {

    private MaintainableWorkflowUtils() {
    }

    /**
     * This method checks if the Workflow document is in Saved or Enroute status
     * 
     * @param documentNumber
     * @return
     */
    public static boolean isDocumentSavedOrEnroute(String documentNumber) {
        boolean isSaveOrEnroute = false;
        KualiWorkflowDocument workflowDocument = getKualiWorkflowDocument(documentNumber);
        
        if (workflowDocument != null) {
            isSaveOrEnroute = workflowDocument.stateIsSaved() || workflowDocument.stateIsEnroute();
        }
        return isSaveOrEnroute;
    }
    
    /**
     * Retrieve the KualiWorkflowDocument base on documentNumber
     * 
     * @param documentNumber
     * @return
     */
    private static KualiWorkflowDocument getKualiWorkflowDocument(String documentNumber) {

        KualiWorkflowDocument workflowDocument = null;
        WorkflowDocumentService workflowDocumentService = SpringContext.getBean(WorkflowDocumentService.class);
        // we need to use the system user here, since this code could be called within the
        // context of workflow, where there is no user session
        Person person = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(KNSConstants.SYSTEM_USER);
        try {
            workflowDocument = workflowDocumentService.createWorkflowDocument(NumberUtils.createLong(documentNumber), person);
        }
        catch (WorkflowException ex) {
            throw new RuntimeException("Error to retrieve workflow document: " + documentNumber, ex);
        }
        return workflowDocument ;
    }



}
