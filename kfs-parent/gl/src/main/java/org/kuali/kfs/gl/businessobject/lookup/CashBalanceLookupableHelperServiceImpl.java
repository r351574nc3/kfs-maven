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
package org.kuali.kfs.gl.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.batch.service.BalanceCalculator;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.CashBalance;
import org.kuali.kfs.gl.businessobject.inquiry.CashBalanceInquirableImpl;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

/**
 * An extension of KualiLookupableImpl to support cash lookups
 */
public class CashBalanceLookupableHelperServiceImpl extends AbstractGeneralLedgerLookupableHelperServiceImpl {
    private BalanceCalculator postBalance;
    private BalanceService balanceService;

    /**
     * Returns the URL for inquiries on fields returned in the lookup
     * @param bo the business object the field to inquiry on is in
     * @param propertyName the name of the property that an inquiry url is being asked of
     * @return the String of the url
     * @see org.kuali.rice.kns.lookup.Lookupable#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new CashBalanceInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * Generates a list of results for this inquiry
     * @param fieldValues the field values that the user entered for this inquiry
     * @return a List of results
     * @see org.kuali.rice.kns.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = getSelectedPendingEntryOption(fieldValues);

        // get the consolidation option
        boolean isConsolidated = isConsolidationSelected(fieldValues);

        // get the search result collection
        Iterator cashBalanceIterator = balanceService.findCashBalance(fieldValues, isConsolidated);
        Collection searchResultsCollection = this.buildCashBalanceCollection(cashBalanceIterator, isConsolidated);

        // update search results according to the selected pending entry option
        updateByPendingLedgerEntry(searchResultsCollection, fieldValues, pendingEntryOption, isConsolidated, false);

        // get the actual size of all qualified search results
        Integer recordCount = balanceService.getCashBalanceRecordCount(fieldValues, isConsolidated);
        Long actualSize = OJBUtility.getResultActualSize(searchResultsCollection, recordCount, fieldValues, new Balance());

        return this.buildSearchResultList(searchResultsCollection, actualSize);
    }

    /**
     * This method builds the cash balance collection based on the input iterator
     * 
     * @param iterator the iterator of search results of avaiable cash balance
     * @return the cash balance collection
     */
    private Collection buildCashBalanceCollection(Iterator iterator, boolean isConsolidated) {
        Collection balanceCollection = new ArrayList();

        while (iterator.hasNext()) {
            Object cashBalance = iterator.next();

            if (cashBalance.getClass().isArray()) {
                int i = 0;
                Object[] array = (Object[]) cashBalance;
                Balance balance = new CashBalance();

                balance.setUniversityFiscalYear(new Integer(array[i++].toString()));
                balance.setChartOfAccountsCode(array[i++].toString());
                balance.setAccountNumber(array[i++].toString());

                String subAccountNumber = isConsolidated ? Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER : array[i++].toString();
                balance.setSubAccountNumber(subAccountNumber);

                balance.setBalanceTypeCode(array[i++].toString());
                balance.setObjectCode(array[i++].toString());

                String subObjectCode = isConsolidated ? Constant.CONSOLIDATED_SUB_OBJECT_CODE : array[i++].toString();
                balance.setSubObjectCode(subObjectCode);

                String objectTypeCode = isConsolidated ? Constant.CONSOLIDATED_OBJECT_TYPE_CODE : array[i++].toString();
                balance.setObjectTypeCode(objectTypeCode);

                KualiDecimal annualAmount = new KualiDecimal(array[i++].toString());
                balance.setAccountLineAnnualBalanceAmount(annualAmount);

                KualiDecimal beginningAmount = new KualiDecimal(array[i++].toString());
                balance.setBeginningBalanceLineAmount(beginningAmount);

                KualiDecimal CGBeginningAmount = new KualiDecimal(array[i].toString());
                balance.setContractsGrantsBeginningBalanceAmount(CGBeginningAmount);

                KualiDecimal totalAvailableAmount = this.getTotalAvailableCashAmount(balance);
                balance.getDummyBusinessObject().setGenericAmount(totalAvailableAmount);

                balanceCollection.add(balance);
            }
        }
        return balanceCollection;
    }

    /**
     * Allows an updating of pending entry records before they are applied to the inquiry results
     * 
     * @param entryCollection a collection of balance entries
     * @param fieldValues the map containing the search fields and values
     * @param isApproved flag whether the approved entries or all entries will be processed
     * @param isConsolidated flag whether the results are consolidated or not
     * @param isCostShareExcluded flag whether the user selects to see the results with cost share subaccount
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableImpl#updateEntryCollection(java.util.Collection, java.util.Map,
     *      boolean, boolean, boolean)
     */
    @Override
    protected void updateEntryCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated, boolean isCostShareInclusive) {

        // convert the field names of balance object into corresponding ones of pending entry object
        Map pendingEntryFieldValues = BusinessObjectFieldConverter.convertToTransactionFieldValues(fieldValues);

        UniversityDate today = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
        String currentFiscalPeriodCode = today.getUniversityFiscalAccountingPeriod();
        Integer currentFiscalYear = today.getUniversityFiscalYear();

        // use the pending entry to update the input entry collection
        Iterator pendingEntryIterator = getGeneralLedgerPendingEntryService().findPendingLedgerEntriesForCashBalance(pendingEntryFieldValues, isApproved);
        while (pendingEntryIterator.hasNext()) {
            GeneralLedgerPendingEntry pendingEntry = (GeneralLedgerPendingEntry) pendingEntryIterator.next();

            // Fix the fiscal period/year if they are null
            // Don't want to use the GLPE service.fillInFiscalPeriodYear. It totally kills performance.
            // generalLedgerPendingEntryService.fillInFiscalPeriodYear(pendingEntry);

            if (pendingEntry.getUniversityFiscalYear() == null) {
                pendingEntry.setUniversityFiscalYear(currentFiscalYear);
            }

            if (pendingEntry.getUniversityFiscalPeriodCode() == null) {
                pendingEntry.setUniversityFiscalPeriodCode(currentFiscalPeriodCode);
            }

            // if consolidated, change the following fields into the default values for consolidation
            if (isConsolidated) {
                pendingEntry.setSubAccountNumber(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER);
                pendingEntry.setFinancialSubObjectCode(Constant.CONSOLIDATED_SUB_OBJECT_CODE);
                pendingEntry.setFinancialObjectTypeCode(Constant.CONSOLIDATED_OBJECT_TYPE_CODE);
            }
            Balance balance = postBalance.findBalance(entryCollection, pendingEntry);
            postBalance.updateBalance(pendingEntry, balance);

            KualiDecimal totalAvailableAmount = this.getTotalAvailableCashAmount(balance);
            balance.getDummyBusinessObject().setGenericAmount(totalAvailableAmount);
        }
    }

    // calculate the total available cash amont of the given balance record
    private KualiDecimal getTotalAvailableCashAmount(Balance balance) {
        KualiDecimal annualAmount = balance.getAccountLineAnnualBalanceAmount();
        KualiDecimal beginningAmount = balance.getBeginningBalanceLineAmount();
        KualiDecimal CGBeginningAmount = balance.getContractsGrantsBeginningBalanceAmount();

        KualiDecimal totalAvailableAmount = annualAmount.add(beginningAmount);
        totalAvailableAmount = totalAvailableAmount.add(CGBeginningAmount);

        return totalAvailableAmount;
    }

    /**
     * Sets the postBalance attribute value.
     * 
     * @param postBalance The postBalance to set.
     */
    public void setPostBalance(BalanceCalculator postBalance) {
        this.postBalance = postBalance;
    }

    /**
     * Sets the balanceService attribute value.
     * 
     * @param balanceService The balanceService to set.
     */
    public void setBalanceService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Override
    public List<Row> getRows() {
        // TODO Auto-generated method stub
        List<Row> rows = super.getRows();
        
        //look for field and replace BO class
        for (Iterator iter = rows.iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();                
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                    Field field = (Field) iterator.next();
                    
                    if(ObjectUtils.isNotNull(field) && StringUtils.equalsIgnoreCase(field.getPropertyName(), KFSPropertyConstants.ACCOUNT_NUMBER)){                        
                        field.setQuickFinderClassNameImpl(Account.class.getName());
                    }
            }
        }
        
        return rows;
    }

}
