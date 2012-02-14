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
package org.kuali.kfs.module.purap.service.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.service.impl.AccountingLineRuleHelperServiceImpl;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class VendorCreditMemoAccountingLineRuleHelperServiceImpl extends PurapAccountingLineRuleHelperServiceImpl {

    /**
     * @see org.kuali.kfs.sys.document.service.impl.AccountingLineRuleHelperServiceImpl#isValidAccount(org.kuali.kfs.coa.businessobject.Account, org.kuali.rice.kns.datadictionary.DataDictionary, java.lang.String)
     */
    @Override
    public boolean isValidAccount(Account account, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getAccountLabel();

        // make sure it exists
        if (ObjectUtils.isNull(account)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        return true;
    }

}
