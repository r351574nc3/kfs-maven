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
package org.kuali.kfs.fp.document.authorization;

import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentPresentationControllerBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class ProcurementCardDocumentPresentationController extends AccountingDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canCancel(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canCancel(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canCopy(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canCopy(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canDisapprove(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canDisapprove(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canEdit(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        List<String> activeNodes = getCurrentRouteLevels(workflowDocument);

        // FULL_ENTRY only if: a) person has an approval request, b) we are at the correct level, c) it's not a correction document,
        // d) it is not an ADHOC request (important so that ADHOC don't get full entry).
        if (workflowDocument.isApprovalRequested() && activeNodes.contains(KFSConstants.RouteLevelNames.ACCOUNT_REVIEW_FULL_EDIT) && (((FinancialSystemDocumentHeader) document.getDocumentHeader()).getFinancialDocumentInErrorNumber() == null) && !workflowDocument.isAdHocRequested()) {
            return true;
        }

        return super.canEdit(document);
    }
}
