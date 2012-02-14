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
package org.kuali.kfs.sys.web.struts;

import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_INVALID_ACCOUNT;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_REQUIRED;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_REQUIRED;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformationDetail;
import org.kuali.kfs.fp.businessobject.SalesTax;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.DeleteAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.kfs.sys.exception.AccountingLineParserException;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.Timer;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * This class handles UI actions for all shared methods of financial documents.
 */
public class KualiAccountingDocumentActionBase extends FinancialSystemTransactionalDocumentActionBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiAccountingDocumentActionBase.class);

    /**
     * Adds check for accountingLine updates, generates and dispatches any events caused by such updates
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Timer t0 = new Timer("KualiFinancialDocumentFormBase.execute");
        KualiAccountingDocumentFormBase transForm = (KualiAccountingDocumentFormBase) form;

        // handle changes to accountingLines
        if (transForm.hasDocumentId()) {
            AccountingDocument financialDocument = (AccountingDocument) transForm.getDocument();

            processAccountingLines(financialDocument, transForm, KFSConstants.SOURCE);
            processAccountingLines(financialDocument, transForm, KFSConstants.TARGET);
        }

        // This is after a potential handleUpdate(), to display automatically cleared overrides following a route or save.
        processAccountingLineOverrides(transForm);

        // proceed as usual
        ActionForward result = super.execute(mapping, form, request, response);
        t0.log();
        return result;
    }

    /**
     * All document-load operations get routed through here
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);


        KualiAccountingDocumentFormBase tform = (KualiAccountingDocumentFormBase) kualiDocumentFormBase;

        // clear out the new accounting line holders
        tform.setNewSourceLine(null);
        tform.setNewTargetLine(null);

        processAccountingLineOverrides(tform);
    }

    /**
     * Needed to override this to keep from losing Sales Tax information
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        refreshSalesTaxInfo(form);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Needed to override this to keep from losing Sales Tax information
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#toggleTab(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward toggleTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.toggleTab(mapping, form, request, response);
        refreshSalesTaxInfo(form);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    // Set of actions for which updateEvents should be generated
    protected static final Set UPDATE_EVENT_ACTIONS;
    static {
        String[] updateEventActions = { KFSConstants.SAVE_METHOD, KFSConstants.ROUTE_METHOD, KFSConstants.APPROVE_METHOD, KFSConstants.BLANKET_APPROVE_METHOD };
        UPDATE_EVENT_ACTIONS = new HashSet();
        for (int i = 0; i < updateEventActions.length; ++i) {
            UPDATE_EVENT_ACTIONS.add(updateEventActions[i]);
        }
    }

    /**
     * @param transForm
     */
    protected void processAccountingLineOverrides(KualiAccountingDocumentFormBase transForm) {
        processAccountingLineOverrides(transForm.getNewSourceLine());
        processAccountingLineOverrides(transForm.getNewTargetLine());
        if (transForm.hasDocumentId()) {
            AccountingDocument financialDocument = (AccountingDocument) transForm.getDocument();

            processAccountingLineOverrides(financialDocument.getSourceAccountingLines());
            processAccountingLineOverrides(financialDocument.getTargetAccountingLines());
        }
    }

    /**
     * @param line
     */
    protected void processAccountingLineOverrides(AccountingLine line) {
        processAccountingLineOverrides(Arrays.asList(new AccountingLine[] { line }));
    }

    /**
     * @param accountingLines
     */
    protected void processAccountingLineOverrides(List accountingLines) {
        if (!accountingLines.isEmpty()) {
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(accountingLines, AccountingLineOverride.REFRESH_FIELDS);

            for (Iterator i = accountingLines.iterator(); i.hasNext();) {
                AccountingLine line = (AccountingLine) i.next();
                AccountingLineOverride.processForOutput(line);
            }
        }
    }

    /**
     * @param transDoc
     * @param transForm
     * @param lineSet
     */
    protected void processAccountingLines(AccountingDocument transDoc, KualiAccountingDocumentFormBase transForm, String lineSet) {
        // figure out which set of lines we're looking at
        List formLines;
        String pathPrefix;
        boolean source;
        if (lineSet.equals(KFSConstants.SOURCE)) {
            formLines = transDoc.getSourceAccountingLines();
            pathPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME;
            source = true;
        }
        else {
            formLines = transDoc.getTargetAccountingLines();
            pathPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME;
            source = false;
        }

        // find and process corresponding form and baselines
        int index = 0;
        for (Iterator i = formLines.iterator(); i.hasNext(); index++) {
            AccountingLine formLine = (AccountingLine) i.next();

            // update sales tax required attribute for view
            // handleSalesTaxRequired(transDoc, formLine, source, false, index);
            checkSalesTax(transDoc, formLine, source, false, index);
        }
    }

    /**
     * Automatically clears any overrides that have become unneeded. This is for accounting lines that were changed right before
     * final actions like route. Normally the unneeded overrides are cleared in accountingLineOverrideField.tag instead, but that
     * requires another form submit. This method shouldn't be called on lines that haven't changed, to avoid automatically changing
     * read-only lines. This cannot be done in the Rule because Rules cannot change the AccountingLines; they only get a deepCopy.
     * 
     * @param formLine
     */
    protected void clearOverridesThatBecameUnneeded(AccountingLine formLine) {
        AccountingLineOverride currentlyNeeded = AccountingLineOverride.determineNeededOverrides(formLine);
        AccountingLineOverride currentOverride = AccountingLineOverride.valueOf(formLine.getOverrideCode());
        if (!currentOverride.isValidMask(currentlyNeeded)) {
            // todo: handle unsupported combinations of overrides (not a problem until we allow certain multiple overrides)
        }
        formLine.setOverrideCode(currentOverride.mask(currentlyNeeded).getCode());
    }

    /**
     * This method will remove a TargetAccountingLine from a FinancialDocument. This assumes that the user presses the delete button
     * for a specific accounting line on the document and that the document is represented by a FinancialDocumentFormBase.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;

        int deleteIndex = getLineToDelete(request);
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME + "[" + deleteIndex + "]";
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteAccountingLineEvent(errorPath, financialDocumentForm.getDocument(), ((AccountingDocument) financialDocumentForm.getDocument()).getTargetAccountingLine(deleteIndex), false));

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteAccountingLine(false, financialDocumentForm, deleteIndex);
        }
        else {
            String[] errorParams = new String[] { "target", Integer.toString(deleteIndex + 1) };
            GlobalVariables.getMessageMap().putError(errorPath, KFSKeyConstants.ERROR_ACCOUNTINGLINE_DELETERULE_INVALIDACCOUNT, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method will remove a SourceAccountingLine from a FinancialDocument. This assumes that the user presses the delete button
     * for a specific accounting line on the document and that the document is represented by a FinancialDocumentFormBase.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;

        int deleteIndex = getLineToDelete(request);
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + deleteIndex + "]";
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteAccountingLineEvent(errorPath, financialDocumentForm.getDocument(), ((AccountingDocument) financialDocumentForm.getDocument()).getSourceAccountingLine(deleteIndex), false));

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            deleteAccountingLine(true, financialDocumentForm, deleteIndex);
        }
        else {
            String[] errorParams = new String[] { "source", Integer.toString(deleteIndex + 1) };
            GlobalVariables.getMessageMap().putError(errorPath, KFSKeyConstants.ERROR_ACCOUNTINGLINE_DELETERULE_INVALIDACCOUNT, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Deletes the source or target accountingLine with the given index from the given form. Assumes that the rule- and form-
     * validation have already occurred.
     * 
     * @param isSource
     * @param financialDocumentForm
     * @param deleteIndex
     */
    protected void deleteAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, int deleteIndex) {
        if (isSource) {
            // remove from document
            financialDocumentForm.getFinancialDocument().getSourceAccountingLines().remove(deleteIndex);

        }
        else {
            // remove from document
            financialDocumentForm.getFinancialDocument().getTargetAccountingLines().remove(deleteIndex);
        }
        // update the doc total
        AccountingDocument tdoc = (AccountingDocument) financialDocumentForm.getDocument();
        if (tdoc instanceof AmountTotaling) {
            ((FinancialSystemDocumentHeader) financialDocumentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) tdoc).getTotalDollarAmount());
        }

    }


    /**
     * This action executes a call to upload CSV accounting line values as TargetAccountingLines for a given transactional document.
     * The "uploadAccountingLines()" method handles the multi-part request.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward uploadTargetLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // call method that sourceform and destination list
        uploadAccountingLines(false, form);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This action executes a call to upload CSV accounting line values as SourceAccountingLines for a given transactional document.
     * The "uploadAccountingLines()" method handles the multi-part request.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ActionForward uploadSourceLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {
        LOG.info("Uploading source accounting lines");
        // call method that sourceform and destination list
        uploadAccountingLines(true, form);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method determines whether we are uploading source or target lines, and then calls uploadAccountingLines directly on the
     * document object. This method handles retrieving the actual upload file as an input stream into the document.
     * 
     * @param isSource
     * @param form
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected void uploadAccountingLines(boolean isSource, ActionForm form) throws FileNotFoundException, IOException {
        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;

        List importedLines = null;

        AccountingDocument financialDocument = tmpForm.getFinancialDocument();
        AccountingLineParser accountingLineParser = financialDocument.getAccountingLineParser();

        // import the lines
        String errorPathPrefix = null;
        try {
            if (isSource) {
                errorPathPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS;
                FormFile sourceFile = tmpForm.getSourceFile();
                checkUploadFile(sourceFile);
                importedLines = accountingLineParser.importSourceAccountingLines(sourceFile.getFileName(), sourceFile.getInputStream(), financialDocument);
            }
            else {
                errorPathPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.TARGET_ACCOUNTING_LINE_ERRORS;
                FormFile targetFile = tmpForm.getTargetFile();
                checkUploadFile(targetFile);
                importedLines = accountingLineParser.importTargetAccountingLines(targetFile.getFileName(), targetFile.getInputStream(), financialDocument);
            }
        }
        catch (AccountingLineParserException e) {
            GlobalVariables.getMessageMap().putError(errorPathPrefix, e.getErrorKey(), e.getErrorParameters());
        }

        // add line to list for those lines which were successfully imported
        if (importedLines != null) {
            for (Iterator i = importedLines.iterator(); i.hasNext();) {
                AccountingLine importedLine = (AccountingLine) i.next();
                insertAccountingLine(isSource, tmpForm, importedLine);
            }
        }
    }

    protected void checkUploadFile(FormFile file) {
        if (file == null) {
            throw new AccountingLineParserException("invalid (null) upload file", KFSKeyConstants.ERROR_UPLOADFILE_NULL);
        }
    }

    /**
     * This method will add a TargetAccountingLine to a FinancialDocument. This assumes that the user presses the add button for a
     * specific accounting line on the document and that the document is represented by a FinancialDocumentFormBase. It first
     * validates the line for data integrity and then checks appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;
        TargetAccountingLine line = financialDocumentForm.getNewTargetLine();
        
        // populate chartOfAccountsCode from account number if accounts cant cross chart and Javascript is turned off
        //SpringContext.getBean(AccountService.class).populateAccountingLineChartIfNeeded(line);
        
        boolean rulePassed = true;
        // before we check the regular rules we need to check the sales tax rules
        // TODO: Refactor rules so we no longer have to call this before a copy of the
        // accountingLine
        rulePassed &= checkSalesTax((AccountingDocument) financialDocumentForm.getDocument(), line, false, true, 0);

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), line));

        // if the rule evaluation passed, let's add it
        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(line);
            insertAccountingLine(false, financialDocumentForm, line);

            // clear the used newTargetLine
            financialDocumentForm.setNewTargetLine(null);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This action executes an insert of a SourceAccountingLine into a document only after validating the accounting line and
     * checking any appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;       
        SourceAccountingLine line = financialDocumentForm.getNewSourceLine();
        
        // populate chartOfAccountsCode from account number if accounts cant cross chart and Javascript is turned off
        //SpringContext.getBean(AccountService.class).populateAccountingLineChartIfNeeded(line);
        
        boolean rulePassed = true;
        // before we check the regular rules we need to check the sales tax rules
        // TODO: Refactor rules so we no longer have to call this before a copy of the
        // accountingLine
        rulePassed &= checkSalesTax((AccountingDocument) financialDocumentForm.getDocument(), line, true, true, 0);
        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), line));

        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(line);
            insertAccountingLine(true, financialDocumentForm, line);

            // clear the used newTargetLine
            financialDocumentForm.setNewSourceLine(null);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Adds the given accountingLine to the appropriate form-related datastructures.
     * 
     * @param isSource
     * @param financialDocumentForm
     * @param line
     */
    protected void insertAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, AccountingLine line) {
        AccountingDocument tdoc = financialDocumentForm.getFinancialDocument();
        if (isSource) {
            // add it to the document
            tdoc.addSourceAccountingLine((SourceAccountingLine) line);

            // add PK fields to sales tax if needed
            if (line.isSalesTaxRequired()) {
                populateSalesTax(line);
            }

            // Update the doc total
            if (tdoc instanceof AmountTotaling)
                ((FinancialSystemDocumentHeader) financialDocumentForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(((AmountTotaling) tdoc).getTotalDollarAmount());
        }
        else {
            // add it to the document
            tdoc.addTargetAccountingLine((TargetAccountingLine) line);

            // add PK fields to sales tax if needed
            if (line.isSalesTaxRequired()) {
                populateSalesTax(line);
            }
        }
    }

    /**
     * TODO: remove this method once baseline accounting lines has been removed
     */
    protected List deepCopyAccountingLinesList(List originals) {
        if (originals == null) {
            return null;
        }
        List copiedLines = new ArrayList();
        for (int i = 0; i < originals.size(); i++) {
            copiedLines.add(ObjectUtils.deepCopy((AccountingLine) originals.get(i)));
        }
        return copiedLines;
    }

    /**
     * This action changes the value of the hide field in the user interface so that when the page is rendered, the UI knows to show
     * all of the labels for each of the accounting line values.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward showDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;
        tmpForm.setHideDetails(false);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is triggered when the user toggles the show/hide button to "hide" thus making the UI render without any of the
     * accounting line labels/descriptions showing up underneath the values in the UI.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward hideDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;
        tmpForm.setHideDetails(true);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Takes care of storing the action form in the User session and forwarding to the balance inquiry report menu action for a
     * source accounting line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performBalanceInquiryForSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SourceAccountingLine line = this.getSourceAccountingLine(form, request);
        return performBalanceInquiryForAccountingLine(mapping, form, request, line);
    }

    /**
     * Takes care of storing the action form in the User session and forwarding to the balance inquiry report menu action for a
     * target accounting line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performBalanceInquiryForTargetLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int lineIndex = getSelectedLine(request);

        TargetAccountingLine line = this.getTargetAccountingLine(form, request);

        return performBalanceInquiryForAccountingLine(mapping, form, request, line);
    }

    /**
     * This method is a helper method that will return a source accounting line. The reason we're making it protected in here is so
     * that we can override this method in some of the modules. PurchasingActionBase is one of the subclasses that will be
     * overriding this, because in PurchasingActionBase, we'll need to get the source accounting line using both an item index and
     * an account index.
     * 
     * @param form
     * @param request
     * @param isSource
     * @return
     */
    protected SourceAccountingLine getSourceAccountingLine(ActionForm form, HttpServletRequest request) {
        int lineIndex = getSelectedLine(request);
        SourceAccountingLine line = (SourceAccountingLine) ObjectUtils.deepCopy(((KualiAccountingDocumentFormBase) form).getFinancialDocument().getSourceAccountingLine(lineIndex));
        return line;
    }

    protected TargetAccountingLine getTargetAccountingLine(ActionForm form, HttpServletRequest request) {
        int lineIndex = getSelectedLine(request);
        TargetAccountingLine line = (TargetAccountingLine) ((KualiAccountingDocumentFormBase) form).getFinancialDocument().getTargetAccountingLine(lineIndex);

        return line;
    }

    /**
     * This method handles preparing all of the accounting line data so that it can be pushed up to the balance inquiries for
     * populating the search criteria of each.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param line
     * @return ActionForward
     */
    protected ActionForward performBalanceInquiryForAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, AccountingLine line) {
        // build out base path for return location
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        // build out the actual form key that will be used to retrieve the form on refresh
        String callerDocFormKey = GlobalVariables.getUserSession().addObject(form);

        // now add required parameters
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        // need this next param b/c the lookup's return back will overwrite
        // the original doc form key
        parameters.put(KFSConstants.BALANCE_INQUIRY_REPORT_MENU_CALLER_DOC_FORM_KEY, callerDocFormKey);
        parameters.put(KFSConstants.DOC_FORM_KEY, callerDocFormKey);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");

        if (line.getPostingYear() != null) {
            parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, line.getPostingYear().toString());
        }
        if (StringUtils.isNotBlank(line.getReferenceOriginCode())) {
            parameters.put("referenceOriginCode", line.getReferenceOriginCode());
        }
        if (StringUtils.isNotBlank(line.getReferenceNumber())) {
            parameters.put("referenceNumber", line.getReferenceNumber());
        }
        if (StringUtils.isNotBlank(line.getReferenceTypeCode())) {
            parameters.put("referenceTypeCode", line.getReferenceTypeCode());
        }
        if (StringUtils.isNotBlank(line.getDebitCreditCode())) {
            parameters.put("debitCreditCode", line.getDebitCreditCode());
        }
        if (StringUtils.isNotBlank(line.getChartOfAccountsCode())) {
            parameters.put("chartOfAccountsCode", line.getChartOfAccountsCode());
        }
        if (StringUtils.isNotBlank(line.getAccountNumber())) {
            parameters.put("accountNumber", line.getAccountNumber());
        }
        if (StringUtils.isNotBlank(line.getFinancialObjectCode())) {
            parameters.put("financialObjectCode", line.getFinancialObjectCode());
        }
        if (StringUtils.isNotBlank(line.getSubAccountNumber())) {
            parameters.put("subAccountNumber", line.getSubAccountNumber());
        }
        if (StringUtils.isNotBlank(line.getFinancialSubObjectCode())) {
            parameters.put("financialSubObjectCode", line.getFinancialSubObjectCode());
        }
        if (StringUtils.isNotBlank(line.getProjectCode())) {
            parameters.put("projectCode", line.getProjectCode());
        }
        if (StringUtils.isNotBlank(getObjectTypeCodeFromLine(line))) {
            if (!StringUtils.isBlank(line.getObjectTypeCode())) {
                parameters.put("objectTypeCode", line.getObjectTypeCode());
            }
            else {
                line.refreshReferenceObject("objectCode");
                parameters.put("objectTypeCode", line.getObjectCode().getFinancialObjectTypeCode());
            }
        }

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.BALANCE_INQUIRY_REPORT_MENU_ACTION, parameters);

        // register that we're going to come back w/ to this form w/ a refresh methodToCall
        ((KualiAccountingDocumentFormBase) form).registerEditableProperty(KNSConstants.DISPATCH_REQUEST_PARAMETER);

        return new ActionForward(lookupUrl, true);
    }

    /**
     * A hook so that most accounting lines - which don't have object types - can have their object type codes used in balance
     * inquiries
     * 
     * @param line the line to get the object type code from
     * @return the object type code the line would use
     */
    protected String getObjectTypeCodeFromLine(AccountingLine line) {
        line.refreshReferenceObject("objectCode");
        return line.getObjectCode().getFinancialObjectTypeCode();
    }

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;
        this.applyCapitalAssetInformation(tmpForm);

        ActionForward forward = super.save(mapping, form, request, response);

        // need to check on sales tax for all the accounting lines
        checkSalesTaxRequiredAllLines(tmpForm, tmpForm.getFinancialDocument().getSourceAccountingLines());
        checkSalesTaxRequiredAllLines(tmpForm, tmpForm.getFinancialDocument().getTargetAccountingLines());
        return forward;
    }

    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;
        this.applyCapitalAssetInformation(tmpForm);

        ActionForward forward = super.approve(mapping, form, request, response);

        // need to check on sales tax for all the accounting lines
        checkSalesTaxRequiredAllLines(tmpForm, tmpForm.getFinancialDocument().getSourceAccountingLines());
        checkSalesTaxRequiredAllLines(tmpForm, tmpForm.getFinancialDocument().getTargetAccountingLines());
        return forward;
    }

    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;
        this.applyCapitalAssetInformation(tmpForm);

        ActionForward forward = super.route(mapping, form, request, response);

        checkSalesTaxRequiredAllLines(tmpForm, tmpForm.getFinancialDocument().getSourceAccountingLines());
        checkSalesTaxRequiredAllLines(tmpForm, tmpForm.getFinancialDocument().getTargetAccountingLines());

        return forward;
    }

    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase tmpForm = (KualiAccountingDocumentFormBase) form;
        this.applyCapitalAssetInformation(tmpForm);

        ActionForward forward = super.blanketApprove(mapping, form, request, response);

        return forward;
    }

    /**
     * Encapsulate the rule check so we can call it from multiple places
     * 
     * @param document
     * @param line
     * @return true if sales is either not required or it contains sales tax
     */
    protected boolean checkSalesTax(AccountingDocument document, AccountingLine line, boolean source, boolean newLine, int index) {
        boolean passed = true;
        if (isSalesTaxRequired(document, line)) {
            // then set the salesTaxRequired on the accountingLine
            line.setSalesTaxRequired(true);
            populateSalesTax(line);
            // check to see if the sales tax info has been put in
            passed &= isValidSalesTaxEntered(line, source, newLine, index);
        }
        return passed;
    }

    /**
     * This method checks to see if this doctype needs sales tax If it does then it checks to see if the account and object code
     * require sales tax If it does then it returns true. Note - this is hackish as we shouldn't have to call rules directly from
     * the action class But we need to in this instance because we are copying the lines before calling rules and need a way to
     * modify them before they go on
     * 
     * @param accountingLine
     * @return true if sales tax check is needed, false otherwise
     */
    protected boolean isSalesTaxRequired(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean required = false;
        String docType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(financialDocument.getClass());
        // first we need to check just the doctype to see if it needs the sales tax check
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        // apply the rule, see if it fails
        ParameterEvaluator docTypeSalesTaxCheckEvaluator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, APPLICATION_PARAMETER.DOCTYPE_SALES_TAX_CHECK, docType);
        if (docTypeSalesTaxCheckEvaluator.evaluationSucceeds()) {
            required = true;
        }

        // second we need to check the account and object code combination to see if it needs sales tax
        if (required) {
            // get the object code and account
            String objCd = accountingLine.getFinancialObjectCode();
            String account = accountingLine.getAccountNumber();
            if (!StringUtils.isEmpty(objCd) && !StringUtils.isEmpty(account)) {
                String compare = account + ":" + objCd;
                ParameterEvaluator salesTaxApplicableAcctAndObjectEvaluator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, APPLICATION_PARAMETER.SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES, compare);
                if (!salesTaxApplicableAcctAndObjectEvaluator.evaluationSucceeds()) {
                    required = false;
                }
            }
            else {
                // the two fields are currently empty and we don't need to check yet
                required = false;
            }
        }
        return required;
    }

    /**
     * This method checks to see if the sales tax information was put into the accounting line
     * 
     * @param accountingLine
     * @return true if entered correctly, false otherwise
     */
    protected boolean isValidSalesTaxEntered(AccountingLine accountingLine, boolean source, boolean newLine, int index) {
        boolean valid = true;
        DictionaryValidationService dictionaryValidationService = SpringContext.getBean(DictionaryValidationService.class);
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        String objCd = accountingLine.getFinancialObjectCode();
        String account = accountingLine.getAccountNumber();
        SalesTax salesTax = accountingLine.getSalesTax();
        String pathPrefix = "";
        if (source && !newLine) {
            pathPrefix = "document." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";
        }
        else if (!source && !newLine) {
            pathPrefix = "document." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";
        }
        else if (source && newLine) {
            pathPrefix = KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
        }
        else if (!source && newLine) {
            pathPrefix = KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME;
        }
        GlobalVariables.getMessageMap().addToErrorPath(pathPrefix);
        if (ObjectUtils.isNull(salesTax)) {
            valid &= false;
            GlobalVariables.getMessageMap().putError("salesTax.chartOfAccountsCode", ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_REQUIRED, account, objCd);
        }
        else {

            if (StringUtils.isBlank(salesTax.getChartOfAccountsCode())) {
                valid &= false;
                GlobalVariables.getMessageMap().putError("salesTax.chartOfAccountsCode", ERROR_REQUIRED, "Chart of Accounts");
            }
            if (StringUtils.isBlank(salesTax.getAccountNumber())) {
                valid &= false;
                GlobalVariables.getMessageMap().putError("salesTax.accountNumber", ERROR_REQUIRED, "Account Number");
            }
            if (salesTax.getFinancialDocumentGrossSalesAmount() == null) {
                valid &= false;
                GlobalVariables.getMessageMap().putError("salesTax.financialDocumentGrossSalesAmount", ERROR_REQUIRED, "Gross Sales Amount");
            }
            if (salesTax.getFinancialDocumentTaxableSalesAmount() == null) {
                valid &= false;
                GlobalVariables.getMessageMap().putError("salesTax.financialDocumentTaxableSalesAmount", ERROR_REQUIRED, "Taxable Sales Amount");
            }
            if (salesTax.getFinancialDocumentSaleDate() == null) {
                valid &= false;
                GlobalVariables.getMessageMap().putError("salesTax.financialDocumentSaleDate", ERROR_REQUIRED, "Sale Date");
            }
            if (StringUtils.isNotBlank(salesTax.getChartOfAccountsCode()) && StringUtils.isNotBlank(salesTax.getAccountNumber())) {

                if (boService.getReferenceIfExists(salesTax, "account") == null) {
                    valid &= false;
                    GlobalVariables.getMessageMap().putError("salesTax.accountNumber", ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_INVALID_ACCOUNT, salesTax.getChartOfAccountsCode(), salesTax.getAccountNumber());

                }
            }
            if (!valid) {
                GlobalVariables.getMessageMap().putError("salesTax.chartOfAccountsCode", ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_REQUIRED, account, objCd);
            }
        }
        GlobalVariables.getMessageMap().removeFromErrorPath(pathPrefix);
        return valid;
    }

    /**
     * This method removes the sales tax information from a line that no longer requires it
     * 
     * @param accountingLine
     */
    protected void removeSalesTax(AccountingLine accountingLine) {
        SalesTax salesTax = accountingLine.getSalesTax();
        if (ObjectUtils.isNotNull(salesTax)) {
            accountingLine.setSalesTax(null);
        }
    }


    /**
     * This method checks to see if the given accounting needs sales tax and if it does it sets the salesTaxRequired variable on the
     * line If it doesn't and it has it then it removes the sales tax information from the line This method is called from the
     * execute() on all accounting lines that have been edited or lines that have already been added to the document, not on new
     * lines
     * 
     * @param transDoc
     * @param formLine
     * @param baseLine
     */
    protected void handleSalesTaxRequired(AccountingDocument transDoc, AccountingLine formLine, boolean source, boolean newLine, int index) {
        boolean salesTaxRequired = isSalesTaxRequired(transDoc, formLine);
        if (salesTaxRequired) {
            formLine.setSalesTaxRequired(true);
            populateSalesTax(formLine);
        }
        else if (!salesTaxRequired && hasSalesTaxBeenEntered(formLine, source, newLine, index)) {
            // remove it if it has been added but is no longer required
            removeSalesTax(formLine);
        }
    }

    protected boolean hasSalesTaxBeenEntered(AccountingLine accountingLine, boolean source, boolean newLine, int index) {
        boolean entered = true;
        String objCd = accountingLine.getFinancialObjectCode();
        String account = accountingLine.getAccountNumber();
        SalesTax salesTax = accountingLine.getSalesTax();
        if (ObjectUtils.isNull(salesTax)) {
            return false;
        }
        if (StringUtils.isBlank(salesTax.getChartOfAccountsCode())) {
            entered &= false;
        }
        if (StringUtils.isBlank(salesTax.getAccountNumber())) {
            entered &= false;
        }
        if (salesTax.getFinancialDocumentGrossSalesAmount() == null) {
            entered &= false;
        }
        if (salesTax.getFinancialDocumentTaxableSalesAmount() == null) {
            entered &= false;
        }
        if (salesTax.getFinancialDocumentSaleDate() == null) {
            entered &= false;
        }
        return entered;
    }

    /**
     * This method is called from the createDocument and processes through all the accouting lines and checks to see if they need
     * sales tax fields
     * 
     * @param kualiDocumentFormBase
     * @param baselineSourceLines
     */
    protected void handleSalesTaxRequiredAllLines(KualiDocumentFormBase kualiDocumentFormBase, List<AccountingLine> baselineAcctingLines) {
        AccountingDocument accoutingDocument = (AccountingDocument) kualiDocumentFormBase.getDocument();
        int index = 0;
        for (AccountingLine accountingLine : baselineAcctingLines) {
            boolean source = false;
            if (accountingLine.isSourceAccountingLine()) {
                source = true;
            }
            handleSalesTaxRequired(accoutingDocument, accountingLine, source, false, index);
            index++;
        }

    }

    protected boolean checkSalesTaxRequiredAllLines(KualiDocumentFormBase kualiDocumentFormBase, List<AccountingLine> baselineAcctingLines) {
        AccountingDocument accoutingDocument = (AccountingDocument) kualiDocumentFormBase.getDocument();
        boolean passed = true;
        int index = 0;
        for (AccountingLine accountingLine : baselineAcctingLines) {
            boolean source = false;
            if (accountingLine.isSourceAccountingLine()) {
                source = true;
            }
            passed &= checkSalesTax(accoutingDocument, accountingLine, source, false, index);
            index++;
        }
        return passed;
    }

    /**
     * This method refreshes the sales tax fields on a refresh or tab toggle so that all the information that was there before is
     * still there after a state change
     * 
     * @param form
     */
    protected void refreshSalesTaxInfo(ActionForm form) {
        KualiAccountingDocumentFormBase accountingForm = (KualiAccountingDocumentFormBase) form;
        AccountingDocument document = (AccountingDocument) accountingForm.getDocument();
        List sourceLines = document.getSourceAccountingLines();
        List targetLines = document.getTargetAccountingLines();
        handleSalesTaxRequiredAllLines(accountingForm, sourceLines);
        handleSalesTaxRequiredAllLines(accountingForm, targetLines);

        AccountingLine newTargetLine = accountingForm.getNewTargetLine();
        AccountingLine newSourceLine = accountingForm.getNewSourceLine();
        if (newTargetLine != null) {
            handleSalesTaxRequired(document, newTargetLine, false, true, 0);
        }
        if (newSourceLine != null) {
            handleSalesTaxRequired(document, newSourceLine, true, true, 0);
        }
    }

    /**
     * This method populates the sales tax for a given accounting line with the appropriate primary key fields from the accounting
     * line since OJB won't do it automatically for us
     * 
     * @param line
     */
    protected void populateSalesTax(AccountingLine line) {
        SalesTax salesTax = line.getSalesTax();

        if (ObjectUtils.isNotNull(salesTax)) {
            salesTax.setDocumentNumber(line.getDocumentNumber());
            salesTax.setFinancialDocumentLineTypeCode(line.getFinancialDocumentLineTypeCode());
            salesTax.setFinancialDocumentLineNumber(line.getSequenceNumber());
        }
    }

    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // parse out the business object name from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String boClassName = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KFSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);

        if (!StringUtils.equals(boClassName, GeneralLedgerPendingEntry.class.getName())) {
            return super.performLookup(mapping, form, request, response);
        }

        String path = super.performLookup(mapping, form, request, response).getPath();
        path = path.replaceFirst(KFSConstants.LOOKUP_ACTION, KFSConstants.GL_MODIFIED_INQUIRY_ACTION);

        return new ActionForward(path, true);
    }

    /**
     * clear up the capital asset information
     */
    public ActionForward clearCapitalAssetInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clearCapitalAssetInfo() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        CapitalAssetInformation capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);
        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        this.resetCapitalAssetInfo(capitalAssetInformation);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * add the capital asset information
     */
    public ActionForward addCapitalAssetInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("addCapitalAssetInfoDetail() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        CapitalAssetInformation capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);
        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        this.addCapitalAssetInfoDetailLines(capitalAssetInformation);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * delete a detail line from the capital asset information
     */
    public ActionForward deleteCapitalAssetInfoDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("deleteCapitalAssetDetailInfo() - start");

        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        CapitalAssetInformation capitalAssetInformation = this.getCurrentCapitalAssetInformationObject(kualiAccountingDocumentFormBase);
        if (capitalAssetInformation == null) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        int lineToDelete = this.getLineToDelete(request);
        List<CapitalAssetInformationDetail> detailLines = capitalAssetInformation.getCapitalAssetInformationDetails();

        detailLines.remove(lineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * get the capital asset information object currently associated with the document
     */
    protected CapitalAssetInformation getCurrentCapitalAssetInformationObject(KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase) {
        LOG.debug("getCurrentCapitalAssetInformationObject() - start");

        AccountingDocument financialDocument = kualiAccountingDocumentFormBase.getFinancialDocument();
        if (!(financialDocument instanceof CapitalAssetEditable) || !(kualiAccountingDocumentFormBase instanceof CapitalAssetEditable)) {
            return null;
        }

        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) financialDocument;
        CapitalAssetInformation capitalAssetInformation = capitalAssetEditable.getCapitalAssetInformation();
        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            return capitalAssetInformation;
        }

        CapitalAssetEditable capitalAssetEditableForm = (CapitalAssetEditable) kualiAccountingDocumentFormBase;
        CapitalAssetInformation newCapitalAssetInformation = capitalAssetEditableForm.getCapitalAssetInformation();

        return newCapitalAssetInformation;
    }

    /**
     * add detail lines into the given capital asset information
     * 
     * @param capitalAssetInformation the given capital asset information
     */
    protected void addCapitalAssetInfoDetailLines(CapitalAssetInformation capitalAssetInformation) {
        LOG.debug("addCapitalAssetInfoDetailLines() - start");

        if (ObjectUtils.isNull(capitalAssetInformation)) {
            return;
        }

        Integer quantity = capitalAssetInformation.getCapitalAssetQuantity();
        if (quantity == null || quantity <= 0) {
            String errorPath = KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.CAPITAL_ASSET_INFORMATION;
            GlobalVariables.getMessageMap().putError(errorPath, KFSKeyConstants.ERROR_INVALID_CAPITAL_ASSET_QUANTITY);
            return;
        }

        List<CapitalAssetInformationDetail> detailLines = capitalAssetInformation.getCapitalAssetInformationDetails();
        // If details collection has old lines, this loop will add new lines to make the total equal to the quantity.
        for (int index = 1; detailLines.size() < quantity; index++) {
            CapitalAssetInformationDetail detailLine = new CapitalAssetInformationDetail();
            detailLine.setItemLineNumber(getNextItemLineNumberAndIncremented(capitalAssetInformation));
            detailLines.add(detailLine);
        }
    }

    /**
     * Get next available item line number. If it's already stored in the session, pick it up and increment by 1. Otherwise get it
     * from the DB and save it to session.
     * 
     * @param capitalAssetInformation
     * @return
     */
    protected Integer getNextItemLineNumberAndIncremented(CapitalAssetInformation capitalAssetInformation) {
        Integer nextItemLineNumber = capitalAssetInformation.getNextItemLineNumber();
        if (nextItemLineNumber == null) {
            nextItemLineNumber = new Integer(getMaxItemLineNumber(capitalAssetInformation) + 1);
        }
        capitalAssetInformation.setNextItemLineNumber(new Integer(nextItemLineNumber.intValue() + 1));
        return nextItemLineNumber;
    }

    /**
     * Get the maximum item line number from DB.
     * 
     * @param capitalAssetInformation
     * @return
     */
    protected int getMaxItemLineNumber(CapitalAssetInformation capitalAssetInformation) {
        int maxItemLineNumber = 0;
        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            List<CapitalAssetInformationDetail> detailLines = capitalAssetInformation.getCapitalAssetInformationDetails();

            if (detailLines != null && !detailLines.isEmpty()) {
                maxItemLineNumber = detailLines.size();
            }

            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, capitalAssetInformation.getDocumentNumber());
            List<CapitalAssetInformationDetail> perisitentDetails = (List<CapitalAssetInformationDetail>) getBusinessObjectService().findMatching(CapitalAssetInformationDetail.class, fieldValues);
            for (CapitalAssetInformationDetail persistentDetail : perisitentDetails) {
                if (persistentDetail.getItemLineNumber().intValue() > maxItemLineNumber) {
                    maxItemLineNumber = persistentDetail.getItemLineNumber().intValue();
                }
            }
        }
        return maxItemLineNumber;
    }

    /**
     * reset the nonkey fields of the given capital asset information
     * 
     * @param capitalAssetInformation the given capital asset information
     */
    protected void resetCapitalAssetInfo(CapitalAssetInformation capitalAssetInformation) {
        if (capitalAssetInformation != null) {
            capitalAssetInformation.setCapitalAssetDescription(null);
            capitalAssetInformation.setCapitalAssetManufacturerModelNumber(null);
            capitalAssetInformation.setCapitalAssetManufacturerName(null);

            capitalAssetInformation.setCapitalAssetNumber(null);
            capitalAssetInformation.setCapitalAssetTypeCode(null);
            capitalAssetInformation.setCapitalAssetQuantity(null);

            capitalAssetInformation.setVendorDetailAssignedIdentifier(null);
            capitalAssetInformation.setVendorHeaderGeneratedIdentifier(null);
            // Set the BO to null cause it won't be updated automatically when vendorDetailAssetIdentifier and
            // VendorHeanderGeneratedIndentifier set to null.
            capitalAssetInformation.setVendorDetail(null);
            capitalAssetInformation.setVendorName(null);

            capitalAssetInformation.getCapitalAssetInformationDetails().clear();
        }
    }

    // assoicate the new capital asset information with the current document if any
    protected void applyCapitalAssetInformation(KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase) {
        LOG.debug("applyCapitalAssetInformation() - start");

        // do nothing if the given document is not required to have capital asset collection
        AccountingDocument document = kualiAccountingDocumentFormBase.getFinancialDocument();
        if (!(document instanceof CapitalAssetEditable)) {
            return;
        }

        // do nothing if there exists capital asset information associated with the current document
        CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) document;
        CapitalAssetInformation capitalAssetInformation = capitalAssetEditable.getCapitalAssetInformation();
        if (capitalAssetInformation != null || !(kualiAccountingDocumentFormBase instanceof CapitalAssetEditable)) {
            return;
        }

        CapitalAssetEditable capitalAssetEditableForm = (CapitalAssetEditable) kualiAccountingDocumentFormBase;
        CapitalAssetInformation newCapitalAssetInformation = capitalAssetEditableForm.getCapitalAssetInformation();
        // apply capitalAsset information if there is at least one movable object code associated with the source accounting
        // lines
        newCapitalAssetInformation.setDocumentNumber(document.getDocumentNumber());
        capitalAssetEditable.setCapitalAssetInformation(newCapitalAssetInformation);
    }

    /**
     * Overridden to guarantee that form of copied document is set to whatever the entry mode of the document is
     * @see org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase#copy(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.copy(mapping, form, request, response);
        
        // if the copied document has capital asset collection, remove the collection
        KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase = (KualiAccountingDocumentFormBase) form;
        AccountingDocument document = kualiAccountingDocumentFormBase.getFinancialDocument();
        if (document instanceof CapitalAssetEditable) {

            CapitalAssetEditable capitalAssetEditable = (CapitalAssetEditable) document;
            resetCapitalAssetInfo(capitalAssetEditable.getCapitalAssetInformation());
        }

        return forward;
    }

}
