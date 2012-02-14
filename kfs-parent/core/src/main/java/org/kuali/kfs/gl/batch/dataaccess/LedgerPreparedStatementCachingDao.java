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
package org.kuali.kfs.gl.batch.dataaccess;

import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.SufficientFundBalances;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.batch.dataaccess.PreparedStatementCachingDao;

public interface LedgerPreparedStatementCachingDao extends PreparedStatementCachingDao {
    public int getMaxSequenceNumber(Transaction t);
    

    public Balance getBalance(Transaction t);

    public void insertBalance(Balance balance);

    public void updateBalance(Balance balance);

    public Encumbrance getEncumbrance(Entry entry);

    public void insertEncumbrance(Encumbrance encumbrance);

    public void updateEncumbrance(Encumbrance encumbrance);

    public ExpenditureTransaction getExpenditureTransaction(Transaction t);

    public void insertExpenditureTransaction(ExpenditureTransaction expenditureTransaction);

    public void updateExpenditureTransaction(ExpenditureTransaction expenditureTransaction);

    public SufficientFundBalances getSufficientFundBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode);

    public void insertSufficientFundBalances(SufficientFundBalances sufficientFundBalances);

    public void updateSufficientFundBalances(SufficientFundBalances sufficientFundBalances);

    public AccountBalance getAccountBalance(Transaction t);

    public void insertAccountBalance(AccountBalance accountBalance);

    public void updateAccountBalance(AccountBalance accountBalance);


    public void insertReversal(Reversal reversal);

    public void insertEntry(Entry entry);
}
