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
package org.kuali.kfs.module.ld.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborConstants.BenefitExpenseTransfer;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.util.ConsolidationUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.service.OptionsService;

/**
 * The class is the front-end for the balance inquiry of Ledger Balance For Benefit Expense Transfer processing.
 */
public class LedgerBalanceForBenefitExpenseTransferLookupableHelperServiceImpl extends LedgerBalanceForExpenseTransferLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerBalanceForBenefitExpenseTransferLookupableHelperServiceImpl.class);

    private OptionsService optionsService;

    /**
     * @see org.kuali.rice.kns.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        LOG.info("Start getSearchResults()");

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        String fiscalYearString = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        SystemOptions options = this.getOptions(fiscalYearString);

        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, options.getFinObjTypeExpenditureexpCd());
        fieldValues.put(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.FINANCIAL_OBJECT_FRINGE_OR_SALARY_CODE, BenefitExpenseTransfer.LABOR_LEDGER_BENEFIT_CODE);

        // get the ledger balances with actual balance type code
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, options.getActualFinancialBalanceTypeCd());
        Collection actualBalances = buildDetailedBalanceCollection(getBalanceService().findBalance(fieldValues, false), Constant.NO_PENDING_ENTRY);

        // get the ledger balances with effort balance type code
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_A21);
        Collection effortBalances = buildDetailedBalanceCollection(getBalanceService().findBalance(fieldValues, false), Constant.NO_PENDING_ENTRY);
        
        List<String> consolidationKeyList = getConsolidationKeyList();       
        Collection<LedgerBalance> consolidatedBalances = ConsolidationUtil.consolidateA2Balances(actualBalances, effortBalances, options.getActualFinancialBalanceTypeCd(), consolidationKeyList);
        this.resetFieldValues(consolidatedBalances);

        Integer recordCount = getBalanceService().getBalanceRecordCount(fieldValues, true);
        Long actualSize = OJBUtility.getResultActualSize(consolidatedBalances, recordCount, fieldValues, new LedgerBalance());

        return buildSearchResultList(consolidatedBalances, actualSize);
    }

    // reset the values for the specified fields
    private void resetFieldValues(Collection<LedgerBalance> consolidatedBalances) {
        for(LedgerBalance ledgerBalance : consolidatedBalances) {
            ledgerBalance.setEmplid(null);
            ledgerBalance.setPositionNumber(null);
        }
    }

    // get the consolidation key field names
    private List<String> getConsolidationKeyList() {
        List<String> consolidationKeyList = new ArrayList<String>();
        consolidationKeyList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        consolidationKeyList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        consolidationKeyList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        consolidationKeyList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        consolidationKeyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        consolidationKeyList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        consolidationKeyList.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        consolidationKeyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        return consolidationKeyList;
    }

    /**
     * get the Options object for the given fiscal year
     * 
     * @param fiscalYearString the given fiscal year
     * @return the Options object for the given fiscal year
     */
    private SystemOptions getOptions(String fiscalYearString) {
        SystemOptions options;
        if (fiscalYearString == null) {
            options = optionsService.getCurrentYearOptions();
        }
        else {
            Integer fiscalYear = Integer.valueOf(fiscalYearString.trim());
            options = optionsService.getOptions(fiscalYear);
        }
        return options;
    }

    /**
     * Sets the optionsService attribute value.
     * 
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
}
