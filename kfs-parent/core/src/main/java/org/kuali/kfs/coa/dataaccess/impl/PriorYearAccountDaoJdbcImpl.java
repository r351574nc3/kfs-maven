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
package org.kuali.kfs.coa.dataaccess.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.PriorYearAccount;
import org.kuali.kfs.coa.dataaccess.PriorYearAccountDaoJdbc;
import org.kuali.rice.kns.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.kns.service.PersistenceStructureService;

/**
 * This class performs actions against the database through direct SQL command calls.
 */
public class PriorYearAccountDaoJdbcImpl extends PlatformAwareDaoBaseJdbc implements PriorYearAccountDaoJdbc {

    /** Constant used to retrieve row counts for tables. Obj_Id value exists in all tables in DB. */
    private static final String OBJ_ID = "OBJ_ID";

    private PersistenceStructureService persistenceStructureService;

    /**
     * This method purges all records in the Prior Year Account table in the DB.
     * 
     * @return Number of records that were purged.
     */
    public int purgePriorYearAccounts() {
        final String priorYrAcctTableName = getPersistenceStructureService().getTableName(PriorYearAccount.class);

        // 1. Count how many rows are currently in the prior year acct table
        int count = getSimpleJdbcTemplate().queryForInt("SELECT COUNT(1) FROM " + priorYrAcctTableName);

        // 2. Purge all the rows from the prior year acct table
        getSimpleJdbcTemplate().update("DELETE FROM " + priorYrAcctTableName);

        return count;
    }

    /**
     * This method copies all organization records from the current Account table to the Prior Year Account table.
     * 
     * @return Number of records that were copied.
     */
    public int copyCurrentAccountsToPriorYearTable() {
        final String priorYrAcctTableName = getPersistenceStructureService().getTableName(PriorYearAccount.class);
        final String acctTableName = getPersistenceStructureService().getTableName(Account.class);

        // 1. Copy all the rows from the current org table to the prior year acct table
        getSimpleJdbcTemplate().update(
                "INSERT INTO " + priorYrAcctTableName + "(FIN_COA_CD, ACCOUNT_NBR, OBJ_ID, VER_NBR, ACCOUNT_NM, ACCT_FSC_OFC_UID, ACCT_SPVSR_UNVL_ID, ACCT_MGR_UNVL_ID, ORG_CD, ACCT_TYP_CD, ACCT_PHYS_CMP_CD, SUB_FUND_GRP_CD, ACCT_FRNG_BNFT_CD, FIN_HGH_ED_FUNC_CD, ACCT_RSTRC_STAT_CD, ACCT_RSTRC_STAT_DT, ACCT_CITY_NM, ACCT_STATE_CD, ACCT_STREET_ADDR, ACCT_ZIP_CD, RPTS_TO_FIN_COA_CD, RPTS_TO_ACCT_NBR, ACCT_CREATE_DT, ACCT_EFFECT_DT, ACCT_EXPIRATION_DT, CONT_FIN_COA_CD, CONT_ACCOUNT_NBR, ENDOW_FIN_COA_CD, ENDOW_ACCOUNT_NBR, CONTR_CTRL_FCOA_CD, CONTR_CTRLACCT_NBR, INCOME_FIN_COA_CD, INCOME_ACCOUNT_NBR, ACCT_ICR_TYP_CD, AC_CSTM_ICREXCL_CD, FIN_SERIES_ID, ICR_FIN_COA_CD, ICR_ACCOUNT_NBR, ACCT_IN_FP_CD, BDGT_REC_LVL_CD, ACCT_SF_CD, ACCT_PND_SF_CD, FIN_EXT_ENC_SF_CD, FIN_INT_ENC_SF_CD, FIN_PRE_ENC_SF_CD, FIN_OBJ_PRSCTRL_CD, CG_CFDA_NBR, ACCT_OFF_CMP_IND, ACCT_CLOSED_IND) "
                        + " SELECT FIN_COA_CD, ACCOUNT_NBR, OBJ_ID, VER_NBR, ACCOUNT_NM, ACCT_FSC_OFC_UID, ACCT_SPVSR_UNVL_ID, ACCT_MGR_UNVL_ID, ORG_CD, ACCT_TYP_CD, ACCT_PHYS_CMP_CD, SUB_FUND_GRP_CD, ACCT_FRNG_BNFT_CD, FIN_HGH_ED_FUNC_CD, ACCT_RSTRC_STAT_CD, ACCT_RSTRC_STAT_DT, ACCT_CITY_NM, ACCT_STATE_CD, ACCT_STREET_ADDR, ACCT_ZIP_CD, RPTS_TO_FIN_COA_CD, RPTS_TO_ACCT_NBR, ACCT_CREATE_DT, ACCT_EFFECT_DT, ACCT_EXPIRATION_DT, CONT_FIN_COA_CD, CONT_ACCOUNT_NBR, ENDOW_FIN_COA_CD, ENDOW_ACCOUNT_NBR, CONTR_CTRL_FCOA_CD, CONTR_CTRLACCT_NBR, INCOME_FIN_COA_CD, INCOME_ACCOUNT_NBR, ACCT_ICR_TYP_CD, AC_CSTM_ICREXCL_CD, FIN_SERIES_ID, ICR_FIN_COA_CD, ICR_ACCOUNT_NBR, ACCT_IN_FP_CD, BDGT_REC_LVL_CD, ACCT_SF_CD, ACCT_PND_SF_CD, FIN_EXT_ENC_SF_CD, FIN_INT_ENC_SF_CD, FIN_PRE_ENC_SF_CD, FIN_OBJ_PRSCTRL_CD, CG_CFDA_NBR, ACCT_OFF_CMP_IND, ACCT_CLOSED_IND " + " FROM " + acctTableName);

        // 2. Count how many rows are currently in the prior year acct table
        return getSimpleJdbcTemplate().queryForInt("SELECT COUNT(1) FROM " + priorYrAcctTableName);
    }

    /**
     * Gets the persistenceStructureService attribute.
     * 
     * @return Returns the persistenceStructureService.
     */
    public PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     * 
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

}
