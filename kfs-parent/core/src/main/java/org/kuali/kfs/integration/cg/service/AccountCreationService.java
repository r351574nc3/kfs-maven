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
package org.kuali.kfs.integration.cg.service;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.dto.AccountCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AccountParametersDTO;

@WebService(name = ContractsAndGrantsConstants.AccountCreationService.WEB_SERVICE_NAME, 
            targetNamespace = ContractsAndGrantsConstants.MODULE_TARGET_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, 
             parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface AccountCreationService {

    public AccountCreationStatusDTO createAccount(
            @WebParam(name="accountParametersDTO")AccountParametersDTO accountParametersDTO);
    
    public boolean isValidAccount(
            @WebParam(name="accountNumber")  String accountNumber);
    
    public boolean isValidChartAccount(
            @WebParam(name="chartOfAccountCode") String chartOfAccountsCode, @WebParam(name="accountNumber")  String accountNumber);
    
    public boolean isValidChartCode(
            @WebParam(name="chartOfAccountsCode")  String chartOfAccountsCode);
    
    public boolean accountsCanCrossCharts(); 

}
