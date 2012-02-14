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
package org.kuali.kfs.module.external.kc.webService;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.BudgetCategoryDTO;

/**
 * This class was generated by Apache CXF 2.2.10
 * Fri Jan 07 09:07:37 HST 2011
 * Generated source version: 2.2.10
 * 
 */
 
@WebService(targetNamespace = KcConstants.KC_NAMESPACE_URI, name = KcConstants.BudgetCategory.SERVICE_NAME)
public interface InstitutionalBudgetCategoryService {

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "lookupBudgetCategories", targetNamespace = KcConstants.KC_NAMESPACE_URI, className = "kc.LookupBudgetCategories")
    @WebMethod
    @ResponseWrapper(localName = "lookupBudgetCategoriesResponse", targetNamespace = KcConstants.KC_NAMESPACE_URI, className = "kc.LookupBudgetCategoriesResponse")
    public java.util.List<BudgetCategoryDTO> lookupBudgetCategories(
        @WebParam(name = "searchCriteria", targetNamespace = "")
        java.util.List<HashMapElement> searchCriteria
    );
}