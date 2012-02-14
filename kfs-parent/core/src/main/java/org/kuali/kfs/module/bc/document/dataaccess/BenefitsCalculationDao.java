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
package org.kuali.kfs.module.bc.document.dataaccess;

public interface BenefitsCalculationDao {


    /**
     * calculate the annual benefits for the budget construction general ledger key passed in, and stores them in the database
     * 
     * @param documentNumber
     * @param fiscalYear
     * @param chartOfAccounts
     * @param accountNumber
     * @param subAccountNumber
     */
    public void calculateAnnualBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String finObjTypeExpenditureexpCd);

    /**
     * calculate the monthly budget benefits for the budget construction general ledger key passed in, and stores them in the
     * database
     * 
     * @param documentNumber
     * @param fiscalYear
     * @param chartOfAccounts
     * @param accountNumber
     * @param subAccountNumber
     */
    public void calculateMonthlyBudgetConstructionGeneralLedgerBenefits(String documentNumber, Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber, String finObjTypeExpenditureexpCd);


}
