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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CreditCardDetail;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.fp.document.CreditCardReceiptDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;

/**
 * This class is the struts form for Credit Card Receipt document.
 */
public class CreditCardReceiptForm extends KualiAccountingDocumentFormBase implements CapitalAssetEditable {
    protected CreditCardDetail newCreditCardReceipt;

    protected CapitalAssetInformation capitalAssetInformation;

    /**
     * Constructs a CreditCardReceiptForm.java.
     */
    public CreditCardReceiptForm() {
        super();
        setNewCreditCardReceipt(new CreditCardDetail());

        this.setCapitalAssetInformation(new CapitalAssetInformation());
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "CCR";
    }
    
    /**
     * @return CreditCardReceiptDocument
     */
    public CreditCardReceiptDocument getCreditCardReceiptDocument() {
        return (CreditCardReceiptDocument) getDocument();
    }

    /**
     * @return CreditCardDetail
     */
    public CreditCardDetail getNewCreditCardReceipt() {
        return newCreditCardReceipt;
    }

    /**
     * @param newCreditCardReceipt
     */
    public void setNewCreditCardReceipt(CreditCardDetail newCreditCardReceipt) {
        this.newCreditCardReceipt = newCreditCardReceipt;
    }

    /**
     * Overrides the parent to call super.populate and then tells each line to check the associated data dictionary and modify the
     * values entered to follow all the attributes set for the values of the accounting line.
     * 
     * @see org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        //
        // now run through all of the accounting lines and make sure they've been uppercased and populated appropriately
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(getNewCreditCardReceipt());

        List<CreditCardDetail> creditCardReceipts = getCreditCardReceiptDocument().getCreditCardReceipts();
        for (CreditCardDetail detail : creditCardReceipts) {
            SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(detail);
        }

    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#getCapitalAssetInformation()
     */
    public CapitalAssetInformation getCapitalAssetInformation() {
        return capitalAssetInformation;
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#setCapitalAssetInformation(org.kuali.kfs.fp.businessobject.CapitalAssetInformation)
     */
    public void setCapitalAssetInformation(CapitalAssetInformation capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }
}
