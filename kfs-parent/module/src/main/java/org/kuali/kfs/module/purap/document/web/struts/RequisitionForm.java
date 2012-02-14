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

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.RequisitionDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetLocation;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItemCapitalAsset;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Struts Action Form for Requisition document.
 */
public class RequisitionForm extends PurchasingFormBase {

    protected String shopUrl;
    
    /**
     * Constructs a RequisitionForm instance and sets up the appropriately casted document.
     */
    public RequisitionForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "REQS";
    }
    
    public RequisitionDocument getRequisitionDocument() {
        return (RequisitionDocument) getDocument();
    }

    public void setRequisitionDocument(RequisitionDocument requisitionDocument) {
        setDocument(requisitionDocument);
    }
    
    public void populateHeaderFields(KualiWorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        if (ObjectUtils.isNotNull(this.getRequisitionDocument().getPurapDocumentIdentifier())) {
            getDocInfo().add(new HeaderField("DataDictionary.RequisitionDocument.attributes.purapDocumentIdentifier", ((RequisitionDocument) this.getDocument()).getPurapDocumentIdentifier().toString()));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.RequisitionDocument.attributes.purapDocumentIdentifier", "Not Available"));
        }
        if (ObjectUtils.isNotNull(this.getRequisitionDocument().getStatus())) {
            getDocInfo().add(new HeaderField("DataDictionary.RequisitionDocument.attributes.statusCode", ((RequisitionDocument) this.getDocument()).getStatus().getStatusDescription()));
        }
        else {
            getDocInfo().add(new HeaderField("DataDictionary.RequisitionDocument.attributes.statusCode", "Not Available"));
        }
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#shouldMethodToCallParameterBeUsed(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        if (KNSConstants.DISPATCH_REQUEST_PARAMETER.equals(methodToCallParameterName) && 
           ("displayB2BRequisition".equals(methodToCallParameterValue))) {
            return true;
        }
        return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
    }

    @Override
    public Class getCapitalAssetLocationClass() {
        return RequisitionCapitalAssetLocation.class;
    }

    @Override
    public Class getItemCapitalAssetClass() {
        return RequisitionItemCapitalAsset.class;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewPurchasingItemLine()
     */
    @Override
    public PurApItem setupNewPurchasingItemLine() {
        return new RequisitionItem();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewPurchasingAccountingLine()
     */
    @Override
    public RequisitionAccount setupNewPurchasingAccountingLine() {
        return new RequisitionAccount();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase#setupNewAccountDistributionAccountingLine()
     */
    @Override
    public RequisitionAccount setupNewAccountDistributionAccountingLine() {
        RequisitionAccount account = setupNewPurchasingAccountingLine();
        account.setAccountLinePercent(new BigDecimal(100));
        return account;
    }

    @Override
    public CapitalAssetLocation setupNewPurchasingCapitalAssetLocationLine() {
        return new RequisitionCapitalAssetLocation();
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    @Override
    public List<ExtraButton> getExtraButtons() {
        super.getExtraButtons();
        for (int i = 0; i < extraButtons.size(); i++) {
            ExtraButton extraButton = extraButtons.get(i);
            if ("methodToCall.calculate".equalsIgnoreCase(extraButton.getExtraButtonProperty()) ){ 
                if(canUserCalculate() == false){
                    extraButtons.remove(i);
                    return extraButtons;
                }
            }
        }
        return extraButtons;
    }

    @Override
    public boolean canUserCalculate(){        
        return documentActions != null && documentActions.containsKey(KNSConstants.KUALI_ACTION_CAN_EDIT) &&
        !getRequisitionDocument().isDocumentStoppedInRouteNode(NodeDetailEnum.ORG_REVIEW);       
    }    
}
