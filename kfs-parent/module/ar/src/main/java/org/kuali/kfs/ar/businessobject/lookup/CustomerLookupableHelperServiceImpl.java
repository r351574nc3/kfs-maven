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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.KNSConstants;

public class CustomerLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLookupableHelperServiceImpl.class);

    /***
     * This method was overridden to remove the COPY link from the actions and to add in the REPORT link.
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames){
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName()) && allowsMaintenanceEditAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        htmlDataList.add(getCustomerOpenItemReportUrl(businessObject));
        return htmlDataList;
    }

    /**
     * 
     * This method...
     * @param bo
     * @return
     */
    private AnchorHtmlData getCustomerOpenItemReportUrl(BusinessObject bo) {
        Customer customer = (Customer) bo;
        String href="../arCustomerOpenItemReportLookup.do" +
                "?businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail" +
                "&returnLocation=&lookupableImplementaionServiceName=arCustomerOpenItemReportLookupable" +
                "&methodToCall=search&customerNumber="+customer.getCustomerNumber()+ 
                "&reportName=" + KFSConstants.CustomerOpenItemReport.HISTORY_REPORT_NAME +
                "&customerName=" +customer.getCustomerName()+
                "&reportName=Customer History Report&docFormKey=88888888";
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArKeyConstants.CustomerConstants.ACTIONS_REPORT);
    }
}

