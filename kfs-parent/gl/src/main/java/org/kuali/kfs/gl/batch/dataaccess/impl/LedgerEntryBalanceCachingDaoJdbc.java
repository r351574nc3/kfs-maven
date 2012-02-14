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
package org.kuali.kfs.gl.batch.dataaccess.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.batch.dataaccess.LedgerEntryBalanceCachingDao;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.kns.service.PersistenceStructureService;

/**
 * This class...
 */
public class LedgerEntryBalanceCachingDaoJdbc extends PlatformAwareDaoBaseJdbc implements LedgerEntryBalanceCachingDao {

    PersistenceStructureService persistenceStructureService;
    UniversityDateService universityDateService;
    
    public List compareEntryHistory(Class entryClass, Class historyClass, int pastYears) {
        List<Map<String, Object>> data = null;

        String entryTable = persistenceStructureService.getTableName(entryClass);
        String historyTable = persistenceStructureService.getTableName(historyClass);
        
        //String[] pks = persistenceService.getPrimaryKeys(arg0)
        int fiscalYear = universityDateService.getCurrentFiscalYear()-pastYears;
        
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("select eh.* ");
        queryBuilder.append("from "+historyTable+" eh ");
        queryBuilder.append("left join ");
        queryBuilder.append("( ");
        queryBuilder.append("SELECT UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD, FIN_BALANCE_TYP_CD, UNIV_FISCAL_PRD_CD, TRN_DEBIT_CRDT_CD, ");
        queryBuilder.append("count(*) as entry_row_cnt, sum(TRN_LDGR_ENTR_AMT) as entry_amt ");
        queryBuilder.append("FROM "+entryTable+" ");
        queryBuilder.append("GROUP BY UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD, FIN_BALANCE_TYP_CD, UNIV_FISCAL_PRD_CD, TRN_DEBIT_CRDT_CD ");
        queryBuilder.append(") e ");
        queryBuilder.append("on eh.univ_fiscal_yr = e.univ_fiscal_yr and eh.fin_coa_cd = e.fin_coa_cd and eh.fin_object_cd = e.fin_object_cd and ");
        queryBuilder.append("eh.fin_balance_typ_cd = e.fin_balance_typ_cd and eh.univ_fiscal_prd_cd = e.univ_fiscal_prd_cd and eh.trn_debit_crdt_cd = e.trn_debit_crdt_cd ");
        queryBuilder.append("where e.univ_fiscal_yr >= "+fiscalYear+" and (eh.row_cnt <> e.entry_row_cnt or eh.trn_ldgr_entr_amt <> e.entry_amt or e.entry_row_cnt is null) ");

        data = getSimpleJdbcTemplate().queryForList(queryBuilder.toString());

        return data;

    }

    public List compareBalanceHistory(Class balanceClass, Class historyClass, int pastYears) {
        List<Map<String, Object>> data = null;

        String balanceTable = persistenceStructureService.getTableName(balanceClass);
        String historyTable = persistenceStructureService.getTableName(historyClass);
        
       
        int fiscalYear = universityDateService.getCurrentFiscalYear()-pastYears;
        
        StringBuilder queryBuilder = new StringBuilder();
        
        
        queryBuilder.append("select bh.* ");
        queryBuilder.append("from "+historyTable+" bh  ");
        queryBuilder.append("left join ( select ");
        queryBuilder.append("UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD,  FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, ");
        queryBuilder.append("ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT ");
        queryBuilder.append("from "+balanceTable+" ) e on ");
        queryBuilder.append("bh.UNIV_FISCAL_YR = e.UNIV_FISCAL_YR and bh.FIN_COA_CD = e.FIN_COA_CD and bh.FIN_OBJECT_CD = e.FIN_OBJECT_CD and bh.FIN_BALANCE_TYP_CD = e.FIN_BALANCE_TYP_CD and bh.SUB_ACCT_NBR = e.SUB_ACCT_NBR and bh.ACCOUNT_NBR = e.ACCOUNT_NBR and bh.FIN_SUB_OBJ_CD = e.FIN_SUB_OBJ_CD and bh.FIN_OBJ_TYP_CD = e.FIN_OBJ_TYP_CD ");
        queryBuilder.append(" where e.UNIV_FISCAL_YR >= "+fiscalYear+" ");
        queryBuilder.append("and (bh.ACLN_ANNL_BAL_AMT <> e.ACLN_ANNL_BAL_AMT or bh.FIN_BEG_BAL_LN_AMT <> e.FIN_BEG_BAL_LN_AMT or bh.CONTR_GR_BB_AC_AMT <> e.CONTR_GR_BB_AC_AMT or  ");
        queryBuilder.append("bh.MO1_ACCT_LN_AMT <> e.MO1_ACCT_LN_AMT or bh.MO2_ACCT_LN_AMT <> e.MO2_ACCT_LN_AMT or bh.MO3_ACCT_LN_AMT <> e.MO3_ACCT_LN_AMT or bh.MO4_ACCT_LN_AMT <> e.MO4_ACCT_LN_AMT or bh.MO5_ACCT_LN_AMT <> e.MO5_ACCT_LN_AMT or bh.MO6_ACCT_LN_AMT <> e.MO6_ACCT_LN_AMT or  ");
        queryBuilder.append("bh.MO7_ACCT_LN_AMT <> e.MO7_ACCT_LN_AMT or bh.MO8_ACCT_LN_AMT <> e.MO8_ACCT_LN_AMT or bh.MO9_ACCT_LN_AMT <> e.MO9_ACCT_LN_AMT or bh.MO10_ACCT_LN_AMT <> e.MO10_ACCT_LN_AMT or bh.MO11_ACCT_LN_AMT <> e.MO11_ACCT_LN_AMT or bh.MO12_ACCT_LN_AMT <> e.MO12_ACCT_LN_AMT or  ");
        queryBuilder.append("bh.MO13_ACCT_LN_AMT <> e.MO13_ACCT_LN_AMT) ");

        
        data = getSimpleJdbcTemplate().queryForList(queryBuilder.toString());

        return data;
        
    }
    
    public List accountBalanceCompareHistory(Class accountBalanceClass, Class historyClass, int pastYears) {
        List<Map<String, Object>> data = null;

        String accountBalanceTable = persistenceStructureService.getTableName(accountBalanceClass);
        String historyTable = persistenceStructureService.getTableName(historyClass);
        
        //String[] pks = persistenceService.getPrimaryKeys(arg0)
        int fiscalYear = universityDateService.getCurrentFiscalYear()-pastYears;
        
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("select abh.* ");
        queryBuilder.append("from "+historyTable+" abh ");
        queryBuilder.append("left join  ");
        queryBuilder.append("(select UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT ");
        queryBuilder.append("from "+accountBalanceTable+" ) ab on ");
        queryBuilder.append("abh.UNIV_FISCAL_YR = ab.UNIV_FISCAL_YR and abh.FIN_COA_CD = ab.FIN_COA_CD and abh.ACCOUNT_NBR = ab.ACCOUNT_NBR and abh.SUB_ACCT_NBR = ab.SUB_ACCT_NBR and abh.FIN_OBJECT_CD = ab.FIN_OBJECT_CD and abh.FIN_SUB_OBJ_CD = ab.FIN_SUB_OBJ_CD ");
        queryBuilder.append("where ab.UNIV_FISCAL_YR >= "+fiscalYear+" ");
        queryBuilder.append("and (abh.CURR_BDLN_BAL_AMT <> ab.CURR_BDLN_BAL_AMT or abh.ACLN_ACTLS_BAL_AMT <> ab.ACLN_ACTLS_BAL_AMT or abh.ACLN_ENCUM_BAL_AMT <> ab.ACLN_ENCUM_BAL_AMT) ");

        data = getSimpleJdbcTemplate().queryForList(queryBuilder.toString());

        return data;

    }
    
    public List encumbranceCompareHistory(Class encumbranceClass, Class historyClass, int pastYears) {
        List<Map<String, Object>> data = null;

        String encumbranceTable = persistenceStructureService.getTableName(encumbranceClass);
        String historyTable = persistenceStructureService.getTableName(historyClass);
        
        //String[] pks = persistenceService.getPrimaryKeys(arg0)
        int fiscalYear = universityDateService.getCurrentFiscalYear()-pastYears;
        
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("select eh.*  ");
        queryBuilder.append("from "+historyTable+" eh ");
        queryBuilder.append("left join  ( ");
        queryBuilder.append("select UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FDOC_TYP_CD, FS_ORIGIN_CD, FDOC_NBR, ACLN_ENCUM_AMT, ACLN_ENCUM_CLS_AMT from "+encumbranceTable+" ) e on ");
        queryBuilder.append("eh.UNIV_FISCAL_YR = e.UNIV_FISCAL_YR and eh.FIN_COA_CD = e.FIN_COA_CD and eh.ACCOUNT_NBR = e.ACCOUNT_NBR and eh.SUB_ACCT_NBR = e.SUB_ACCT_NBR and eh.FIN_OBJECT_CD = e.FIN_OBJECT_CD and eh.FIN_SUB_OBJ_CD = e.FIN_SUB_OBJ_CD and eh.FIN_BALANCE_TYP_CD = e.FIN_BALANCE_TYP_CD and eh.FDOC_TYP_CD = e.FDOC_TYP_CD and eh.FS_ORIGIN_CD = e.FS_ORIGIN_CD and eh.FDOC_NBR = e.FDOC_NBR ");
        queryBuilder.append("where e.UNIV_FISCAL_YR >= "+fiscalYear+" and (eh.ACLN_ENCUM_AMT <> e.ACLN_ENCUM_AMT or eh.ACLN_ENCUM_CLS_AMT <> e.ACLN_ENCUM_CLS_AMT) ");
        
        data = getSimpleJdbcTemplate().queryForList(queryBuilder.toString());

        return data;

    }

    /**
     * Sets the persistenceStructureService attribute value.
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Sets the universityDateService attribute value.
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * Gets the persistenceStructureService attribute. 
     * @return Returns the persistenceStructureService.
     */
    public PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    /**
     * Gets the universityDateService attribute. 
     * @return Returns the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    
}
