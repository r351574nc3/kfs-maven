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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants.CreditMemoEditMode;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Struts Action Form for Credit Memo document.
 */
public class VendorCreditMemoForm extends AccountsPayableFormBase {

    /**
     * Constructs a PurchaseOrderForm instance and sets up the appropriately casted document.
     */
    public VendorCreditMemoForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "CM";
    }
    
    @Override
    public void populateHeaderFields(KualiWorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        if (ObjectUtils.isNotNull(((VendorCreditMemoDocument) getDocument()).getPurapDocumentIdentifier())) {
            getDocInfo().add(new HeaderField("DataDictionary.VendorCreditMemoDocument.attributes.purapDocumentIdentifier", ((VendorCreditMemoDocument) getDocument()).getPurapDocumentIdentifier().toString()));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.VendorCreditMemoDocument.attributes.purapDocumentIdentifier", "Not Available"));
        }
        if (ObjectUtils.isNotNull(((VendorCreditMemoDocument) getDocument()).getStatus())) {
            getDocInfo().add(new HeaderField("DataDictionary.VendorCreditMemoDocument.attributes.statusCode", ((VendorCreditMemoDocument) getDocument()).getStatus().getStatusDescription()));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.VendorCreditMemoDocument.attributes.statusCode", "Not Available"));
        }
    }

    /**
     * Build additional credit memo specific buttons and set extraButtons list.
     * 
     * @return - list of extra buttons to be displayed to the user
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        extraButtons.clear();
        VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument) getDocument();
        String externalImageURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
        String appExternalImageURL = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);

        if (getEditingMode().containsKey(CreditMemoEditMode.DISPLAY_INIT_TAB)) {
            addExtraButton("methodToCall.continueCreditMemo", externalImageURL + "buttonsmall_continue.gif", "Continue");
            addExtraButton("methodToCall.clearInitFields", externalImageURL + "buttonsmall_clear.gif", "Clear");
        }
        else {
            if (getEditingMode().containsKey(CreditMemoEditMode.HOLD)) {
                addExtraButton("methodToCall.addHoldOnCreditMemo", appExternalImageURL + "buttonsmall_hold.gif", "Hold");
            }

            if (getEditingMode().containsKey(CreditMemoEditMode.REMOVE_HOLD)) {
                addExtraButton("methodToCall.removeHoldFromCreditMemo", appExternalImageURL + "buttonsmall_removehold.gif", "Remove");
            }

            if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(cmDocument) == false && documentActions.containsKey(KNSConstants.KUALI_ACTION_CAN_EDIT)) {
                addExtraButton("methodToCall.calculate", appExternalImageURL + "buttonsmall_calculate.gif", "Calculate");
            }

            if (getEditingMode().containsKey(CreditMemoEditMode.ACCOUNTS_PAYABLE_PROCESSOR_CANCEL)) {
                if (cmDocument.isSourceDocumentPaymentRequest() || cmDocument.isSourceDocumentPurchaseOrder()) {
                    //if the source is PREQ or PO, check the PO status
                    if (PurapConstants.PurchaseOrderStatuses.CLOSED.equals(cmDocument.getPurchaseOrderDocument().getStatusCode())) {
                        //if the PO is CLOSED, show the 'open order' button; but only if there are no pending actions on the PO
                        if (!cmDocument.getPurchaseOrderDocument().isPendingActionIndicator()) {
                            addExtraButton("methodToCall.reopenPo", appExternalImageURL + "buttonsmall_openorder.gif", "Reopen PO");
                        }
                    }
                    else {
                        addExtraButton("methodToCall.cancel", externalImageURL + "buttonsmall_cancel.gif", "Cancel");
                    }
                }
                else {
                    //if the source is Vendor, no need to check the PO status
                    addExtraButton("methodToCall.cancel", externalImageURL + "buttonsmall_cancel.gif", "Cancel");
                }
            }
        }

        return extraButtons;
    }

    
}

