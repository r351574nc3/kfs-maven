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
package org.kuali.kfs.fp.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransactionDetail;
import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.DeleteAccountingLineEvent;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This class handles specific Actions requests for the ProcurementCard.
 */
public class ProcurementCardAction extends KualiAccountingDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardAction.class);

    /**
     * Override to accomodate multiple target lines.
     * 
     * @param transForm
     */
    @Override
    protected void processAccountingLineOverrides(KualiAccountingDocumentFormBase transForm) {
        ProcurementCardForm procurementCardForm = (ProcurementCardForm) transForm;

        processAccountingLineOverrides(procurementCardForm.getNewSourceLine());
        processAccountingLineOverrides(procurementCardForm.getNewTargetLines());
        if (procurementCardForm.hasDocumentId()) {
            AccountingDocument financialDocument = (AccountingDocument) procurementCardForm.getDocument();

            processAccountingLineOverrides(financialDocument.getSourceAccountingLines());
            processAccountingLineOverrides(financialDocument.getTargetAccountingLines());
        }
    }

    /**
     * Override to add the new accounting line to the correct transaction
     * 
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#insertTargetLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProcurementCardForm procurementCardForm = (ProcurementCardForm) form;
        ProcurementCardDocument procurementCardDocument = (ProcurementCardDocument) procurementCardForm.getDocument();

        // get index of new target line
        int newTargetIndex = super.getSelectedLine(request);
        ProcurementCardTargetAccountingLine line = (ProcurementCardTargetAccountingLine) procurementCardForm.getNewTargetLines().get(newTargetIndex);
        
        // populate chartOfAccountsCode from account number if accounts cant cross chart and Javascript is turned off
        //SpringContext.getBean(AccountService.class).populateAccountingLineChartIfNeeded(line);

        ProcurementCardTransactionDetail transactionDetail = (ProcurementCardTransactionDetail) procurementCardDocument.getTransactionEntries().get(newTargetIndex);
        line.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());

        // check any business rules
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINES_PROPERTY_NAME + "[" + Integer.toString(newTargetIndex) + "]", procurementCardForm.getDocument(), (AccountingLine) line));

        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).retrieveNonKeyFields(line);
            insertAccountingLine(false, procurementCardForm, line);

            // clear the used newTargetIndex
            procurementCardForm.getNewTargetLines().set(newTargetIndex, new ProcurementCardTargetAccountingLine());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#performBalanceInquiryForTargetLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward performBalanceInquiryForTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProcurementCardForm procurementCardForm = (ProcurementCardForm) form;
        ProcurementCardDocument procurementCardDocument = (ProcurementCardDocument) procurementCardForm.getDocument();

        int targetContainerIndex = this.getSelectedContainer(request);
        ProcurementCardTransactionDetail ProcurementCardTransactionDetail = (ProcurementCardTransactionDetail) procurementCardDocument.getTransactionEntries().get(targetContainerIndex);

        int targetIndex = super.getSelectedLine(request);
        TargetAccountingLine targetLine = (ProcurementCardTargetAccountingLine) ProcurementCardTransactionDetail.getTargetAccountingLines().get(targetIndex);

        return performBalanceInquiryForAccountingLine(mapping, form, request, targetLine);
    }

    /**
     * Override to resync base accounting lines. New lines on the PCDO document can be inserted anywhere in the list, not necessary
     * at the end.
     * 
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#insertAccountingLine(boolean,
     *      org.kuali.module.financial.web.struts.form.KualiFinancialDocumentFormBase, org.kuali.rice.kns.bo.AccountingLine)
     */
    @Override
    protected void insertAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, AccountingLine line) {
        AccountingDocument tdoc = financialDocumentForm.getFinancialDocument();

        // add it to the document
        tdoc.addTargetAccountingLine((TargetAccountingLine) line);
    }

    /**
     * Override to get the correct container of the transaction and then delete the correct accounting line
     * 
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#deleteTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response
     */
    @Override
    public ActionForward deleteTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int targetContainerIndex = this.getSelectedContainer(request);
        int targetIndex = this.getSelectedLine(request);
        
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;

        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME + "[" + targetIndex + "]";
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteAccountingLineEvent(errorPath, financialDocumentForm.getDocument(), ((AccountingDocument) financialDocumentForm.getDocument()).getTargetAccountingLine(targetIndex), false));

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteAccountingLineFromTransactionContainer(financialDocumentForm, targetContainerIndex, targetIndex);
        }
        else {
            String[] errorParams = new String[] { "target", Integer.toString(targetIndex + 1) };
            GlobalVariables.getMessageMap().putError(errorPath, KFSKeyConstants.ERROR_ACCOUNTINGLINE_DELETERULE_INVALIDACCOUNT, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Override to remove the accounting line from the correct transaction
     * 
     * @see org.kuali.module.financial.web.struts.action.KualiFinancialDocumentActionBase#deleteAccountingLine(boolean,
     *      org.kuali.module.financial.web.struts.form.KualiFinancialDocumentFormBase, int)
     */
    @Override
    protected void deleteAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, int deleteIndex) {
        ProcurementCardDocument procurementCardDocument = (ProcurementCardDocument) financialDocumentForm.getDocument();
        procurementCardDocument.removeTargetAccountingLine(deleteIndex);
    }

    /**
     * Ensures that ProcurementCardForm.newTargetLines is cleared. Otherwise works like super.reload.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#reload(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ProcurementCardForm procurementCardForm = (ProcurementCardForm) form;
        procurementCardForm.setNewTargetLines(new TypedArrayList(ProcurementCardTargetAccountingLine.class));

        return super.reload(mapping, procurementCardForm, request, response);
    }

    // get the index of selected transaction entry
    protected int getSelectedContainer(HttpServletRequest request) {
        int selectedContainer = -1;
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            String lineNumber = StringUtils.substringBetween(parameterName, ".transactionEntries[", "].");
            selectedContainer = Integer.parseInt(lineNumber);
        }

        return selectedContainer;
    }
    
    /**
     * Removes the target accounting line at the given index from the transaction container transaction entries.
     * 
     * @param financialDocumentForm, targetContainerIndex, targetIndex
     */
    protected void deleteAccountingLineFromTransactionContainer(KualiAccountingDocumentFormBase financialDocumentForm, int targetContainerIndex, int targetIndex) {
        ProcurementCardDocument procurementCardDocument = (ProcurementCardDocument) financialDocumentForm.getDocument(); 
        ProcurementCardTransactionDetail procurementCardTransactionDetail = (ProcurementCardTransactionDetail) procurementCardDocument.getTransactionEntries().get(targetContainerIndex);
        procurementCardTransactionDetail.getTargetAccountingLines().remove(targetIndex);
    }
}
