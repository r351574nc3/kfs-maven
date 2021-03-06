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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Validates that an accounting document does not have any pending
 * labor ledger entries with the same emplID, periodCode, accountNumber, objectCode 
 */
public class SalaryExpenseTransferPendingLegerEntryValidation extends GenericValidation {
    private Document documentForValidation;
    
    /**
     * Validates that the accounting lines in the accounting document does not have 
     * any pending labor ledger entries with the same emplID, periodCode, accountNumber, objectCode 
     * <strong>Expects an accounting document as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        Document documentForValidation = getDocumentForValidation();
        AccountingDocument accountingDocument = (AccountingDocument) documentForValidation;
        
        result = !hasPendingLedgerEntry(accountingDocument);
        return result ;    
    }

    /**
     * Checks whether amounts by object codes are unchanged
     * 
     * @param accountingDocumentForValidation The accounting document from which the amounts by objects codes are checked
     * @return True if the given accounting documents amounts by object code are unchanged, false otherwise.
     */ 
    protected boolean hasPendingLedgerEntry(AccountingDocument accountingDocument) {
        boolean entriesExist = false ;
        
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        List<ExpenseTransferAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();

        Map<String, String> fieldValues = new HashMap<String, String>();
        for (ExpenseTransferAccountingLine sourceAccountingLine : sourceAccountingLines) {
            String payPeriodCode = sourceAccountingLine.getPayrollEndDateFiscalPeriodCode();
            String accountNumber = sourceAccountingLine.getAccountNumber();
            String objectCode = sourceAccountingLine.getFinancialObjectCode();
            String emplId = sourceAccountingLine.getEmplid();
            String documentNumber = accountingDocument.getDocumentNumber();

            fieldValues.put(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, payPeriodCode);
            fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
            fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
            fieldValues.put(KFSPropertyConstants.EMPLID, emplId);
            fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, KNSConstants.NOT_LOGICAL_OPERATOR + documentNumber);
            
            if (SpringContext.getBean(LaborLedgerPendingEntryService.class).hasPendingLaborLedgerEntry(fieldValues)) {
                GlobalVariables.getMessageMap().putError(LaborConstants.EMPLOYEE_LOOKUP_ERRORS, LaborKeyConstants.PENDING_SALARY_TRANSFER_ERROR, emplId, payPeriodCode, accountNumber, objectCode);
                return true;
            }                        
        }
        return entriesExist ;
    }

    /**
     * Gets the documentForValidation attribute. 
     * @return Returns the documentForValidation.
     */
    public Document getDocumentForValidation() {
        return documentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param documentForValidation The documentForValidation to set.
     */
    public void setDocumentForValidation(Document documentForValidation) {
        this.documentForValidation = documentForValidation;
    }    
}
