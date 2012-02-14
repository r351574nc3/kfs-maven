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
package org.kuali.kfs.module.ar.businessobject.inquiry;

import java.util.Properties;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;

public class CustomerInvoiceWriteoffLookupResultInquirableImpl extends KfsInquirableImpl {

    /**
     * Helper method to build an inquiry URLs for Customer Invoice Writeoff Lookup Results
     * 
     * @param businessObject the business object instance to build the urls for
     * @param attributeName the attribute name which links to an inquirable
     * @return String url to inquiry
     */
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName) {

        AnchorHtmlData inquiryHref = new AnchorHtmlData(KNSConstants.EMPTY_STRING, KNSConstants.EMPTY_STRING);
        if (ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER.equals(attributeName)) {
            String baseUrl = KFSConstants.INQUIRY_ACTION;
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Customer.class.getName());
            parameters.put(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, ObjectUtils.getPropertyValue((CustomerInvoiceWriteoffLookupResult) businessObject, attributeName));

            inquiryHref.setHref(UrlFactory.parameterizeUrl(baseUrl, parameters));
        } else if (ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER.equals(attributeName) ){
            
            String documentNumber = ObjectUtils.getPropertyValue((CustomerInvoiceDocument)businessObject, attributeName).toString();
            inquiryHref.setHref(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + documentNumber + "&command=displayDocSearchView");
        }
        return inquiryHref;
    }
}
