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
package org.kuali.kfs.module.purap.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;

public class PurapSearchUtils {

    public static String getWorkFlowStatusString(FinancialSystemDocumentHeader documentHeader) {
        if (documentHeader.getWorkflowDocument().stateIsInitiated()) {
            return "INITIATED";
        }
        else if (documentHeader.getWorkflowDocument().stateIsEnroute()) {
            return "ENROUTE";
        }
        else if (documentHeader.getWorkflowDocument().stateIsDisapproved()) {
            return "DISAPPROVED";
        }
        else if (documentHeader.getWorkflowDocument().stateIsCanceled()) {
            return "CANCELLED";
        }
        else if (documentHeader.getWorkflowDocument().stateIsApproved()) {
            return "APPROVED";
        }
        else {
            return StringUtils.EMPTY;
        }
    }
}
