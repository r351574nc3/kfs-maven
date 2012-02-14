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
package org.kuali.kfs.fp.document.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.fp.document.YearEndDocument;
import org.kuali.kfs.fp.document.service.YearEndPendingEntryService;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.exception.ReferentialIntegrityException;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * The default implementation of the YearEndPendingEntryService
 */
public class YearEndPendingEntryServiceImpl implements YearEndPendingEntryService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(YearEndPendingEntryServiceImpl.class);
    
    private static final String FINAL_ACCOUNTING_PERIOD = "13";
    
    protected UniversityDateService universityDateService;
    protected SufficientFundsService sufficientFundsService;
    protected OffsetDefinitionService offsetDefinitionService;

    /**
     * @see org.kuali.kfs.fp.document.service.YearEndPendingEntryService#customizeExplicitGeneralLedgerPendingEntry(org.kuali.rice.kns.document.TransactionalDocument, org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    public void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        if (!YearEndDocument.class.isAssignableFrom(transactionalDocument.getClass())) {
            throw new IllegalArgumentException("invalid (not a year end document) for class:" + transactionalDocument.getClass());
        }
        YearEndDocument yearEndDocument = (YearEndDocument) transactionalDocument;
        explicitEntry.setUniversityFiscalPeriodCode(getAccountingPeriodForYearEndEntry());
        explicitEntry.setUniversityFiscalYear(getPreviousFiscalYear());
    }

    /**
     * @see org.kuali.kfs.fp.document.service.YearEndPendingEntryService#customizeOffsetGeneralLedgerPendingEntry(org.kuali.rice.kns.document.TransactionalDocument, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    public boolean customizeOffsetGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        if (!(transactionalDocument instanceof YearEndDocument)) {
            throw new IllegalArgumentException("invalid (not a year end document) for class:" + transactionalDocument.getClass());
        }
        OffsetDefinition offsetDefinition = getOffsetDefinitionService().getByPrimaryId(getPreviousFiscalYear(), explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialDocumentTypeCode(), explicitEntry.getFinancialBalanceTypeCode());
        if (!ObjectUtils.isNull(offsetDefinition)) {
            String offsetObjectCode = getOffsetFinancialObjectCode(offsetDefinition);
            offsetEntry.setFinancialObjectCode(offsetObjectCode);
            if (offsetObjectCode.equals(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode())) {
                // no BO, so punt
                offsetEntry.setAcctSufficientFundsFinObjCd(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode());
            }
            else {
                offsetDefinition.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
                ObjectCode financialObject = offsetDefinition.getFinancialObject();
                // The ObjectCode reference may be invalid because a flexible offset account changed its chart code.
                if (ObjectUtils.isNull(financialObject)) {
                    throw new ReferentialIntegrityException("offset object code " + offsetEntry.getUniversityFiscalYear() + "-" + offsetEntry.getChartOfAccountsCode() + "-" + offsetEntry.getFinancialObjectCode());
                }
                offsetEntry.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
                offsetEntry.setAcctSufficientFundsFinObjCd(getSufficientFundsService().getSufficientFundsObjectCode(financialObject, offsetEntry.getAccount().getAccountSufficientFundsCode()));
            }

            offsetEntry.setFinancialObjectTypeCode(getOffsetFinancialObjectTypeCode(offsetDefinition));
            
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.fp.document.service.YearEndPendingEntryService#getFinalAccountingPeriod()
     */
    public String getFinalAccountingPeriod() {
        return FINAL_ACCOUNTING_PERIOD;
    }
    
    /**
     * @return the accounting period the year end entry should be populated with
     */
    protected String getAccountingPeriodForYearEndEntry() {
        return getFinalAccountingPeriod();
    }

    /**
     * @see org.kuali.kfs.fp.document.service.YearEndPendingEntryService#getPreviousFiscalYear()
     */
    public Integer getPreviousFiscalYear() {
        int i = getUniversityDateService().getCurrentFiscalYear().intValue() - 1;
        return new Integer(i);
    }
    
    /**
     * Helper method that determines the offset entry's financial object code.
     * 
     * @param offsetDefinition
     * @return String
     */
    protected String getOffsetFinancialObjectCode(OffsetDefinition offsetDefinition) {
        LOG.debug("getOffsetFinancialObjectCode(OffsetDefinition) - start");

        if (null != offsetDefinition) {
            String returnString = (!StringUtils.isBlank(offsetDefinition.getFinancialObjectCode())) ? offsetDefinition.getFinancialObjectCode() : AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode();
            LOG.debug("getOffsetFinancialObjectCode(OffsetDefinition) - end");
            return returnString;
        }
        else {
            LOG.debug("getOffsetFinancialObjectCode(OffsetDefinition) - end");
            return AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode();
        }

    }
    
    /**
     * Helper method that determines the offset entry's financial object type code.
     * 
     * @param offsetDefinition
     * @return String
     */
    protected String getOffsetFinancialObjectTypeCode(OffsetDefinition offsetDefinition) {
        LOG.debug("getOffsetFinancialObjectTypeCode(OffsetDefinition) - start");

        if (null != offsetDefinition && null != offsetDefinition.getFinancialObject()) {
            String returnString = (!StringUtils.isBlank(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode())) ? offsetDefinition.getFinancialObject().getFinancialObjectTypeCode() : AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectType();
            LOG.debug("getOffsetFinancialObjectTypeCode(OffsetDefinition) - end");
            return returnString;
        }
        else {
            LOG.debug("getOffsetFinancialObjectTypeCode(OffsetDefinition) - end");
            return AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectType();
        }

    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public SufficientFundsService getSufficientFundsService() {
        return sufficientFundsService;
    }

    public void setSufficientFundsService(SufficientFundsService sufficientFundsService) {
        this.sufficientFundsService = sufficientFundsService;
    }

    public OffsetDefinitionService getOffsetDefinitionService() {
        return offsetDefinitionService;
    }

    public void setOffsetDefinitionService(OffsetDefinitionService offsetDefinitionService) {
        this.offsetDefinitionService = offsetDefinitionService;
    }

}
