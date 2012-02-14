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
package org.kuali.kfs.module.bc.document.dataaccess.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BCConstants.OrgSelControlOption;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountReports;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPullup;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.TransactionalServiceUtils;

/**
 * This class is the OJB implementation of the BudgetConstructionDao interface.
 */
public class BudgetConstructionDaoOjb extends PlatformAwareDaoBaseOjb implements BudgetConstructionDao {

    private DataDictionaryService dataDictionaryService;
    private KualiModuleService kualiModuleService;

    /**
     * This gets a BudgetConstructionHeader using the candidate key chart, account, subaccount, fiscalyear
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return BudgetConstructionHeader
     */
    public BudgetConstructionHeader getByCandidateKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addEqualTo("subAccountNumber", subAccountNumber);
        criteria.addEqualTo("universityFiscalYear", fiscalYear);

        return (BudgetConstructionHeader) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(BudgetConstructionHeader.class, criteria));
    }

    /**
     * This saves a BudgetConstructionHeader object to the database
     * 
     * @param bcHeader
     */
    public void saveBudgetConstructionHeader(BudgetConstructionHeader bcHeader) {
        getPersistenceBrokerTemplate().store(bcHeader);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#saveBudgetConstructionDocument(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    public void saveBudgetConstructionDocument(BudgetConstructionDocument bcDocument) {
        getPersistenceBrokerTemplate().store(bcDocument);
    }

    /**
     * This gets a BudgetConstructionFundingLock using the primary key chart, account, subaccount, fiscalyear, pUId
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @param principalId
     * @return BudgetConstructionFundingLock
     */
    public BudgetConstructionFundingLock getByPrimaryId(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId) {
        // LOG.debug("getByPrimaryId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("appointmentFundingLockUserId", principalId);
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addEqualTo("subAccountNumber", subAccountNumber);
        criteria.addEqualTo("universityFiscalYear", fiscalYear);

        return (BudgetConstructionFundingLock) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(BudgetConstructionFundingLock.class, criteria));
    }

    /**
     * This saves a BudgetConstructionFundingLock to the database
     * 
     * @param budgetConstructionFundingLock
     */
    public void saveBudgetConstructionFundingLock(BudgetConstructionFundingLock budgetConstructionFundingLock) {
        getPersistenceBrokerTemplate().store(budgetConstructionFundingLock);
    }

    /**
     * This deletes a BudgetConstructionFundingLock from the database
     * 
     * @param budgetConstructionFundingLock
     */
    public void deleteBudgetConstructionFundingLock(BudgetConstructionFundingLock budgetConstructionFundingLock) {
        getPersistenceBrokerTemplate().delete(budgetConstructionFundingLock);
    }

    /**
     * This gets the set of BudgetConstructionFundingLocks asssociated with a BC EDoc (account). Each BudgetConstructionFundingLock
     * has the positionNumber dummy attribute set to the associated Position that is locked. A positionNumber value of "NotFnd"
     * indicates the BudgetConstructionFundingLock is an orphan.
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return Collection<BudgetConstructionFundingLock>
     */
    public Collection<BudgetConstructionFundingLock> getFlocksForAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear) {
        Collection<BudgetConstructionFundingLock> fundingLocks;

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addEqualTo("subAccountNumber", subAccountNumber);
        criteria.addEqualTo("universityFiscalYear", fiscalYear);

        fundingLocks = (Collection<BudgetConstructionFundingLock>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(BudgetConstructionFundingLock.class, criteria));
        BudgetConstructionFundingLock fundingLock;
        Iterator<BudgetConstructionFundingLock> iter = fundingLocks.iterator();
        while (iter.hasNext()) {
            fundingLock = iter.next();
            fundingLock.setPositionNumber(getPositionAssociatedWithFundingLock(fundingLock));
        }

        return fundingLocks;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#getPositionAssociatedWithFundingLock(org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock)
     */
    public String getPositionAssociatedWithFundingLock(BudgetConstructionFundingLock budgetConstructionFundingLock) {

        String positionNumber = BCConstants.POSITION_NUMBER_NOT_FOUND; // default if there is no associated position that is locked
        // (orphaned)

        Criteria criteria = new Criteria();
        criteria.addEqualTo("pendingBudgetConstructionAppointmentFunding.chartOfAccountsCode", budgetConstructionFundingLock.getChartOfAccountsCode());
        criteria.addEqualTo("pendingBudgetConstructionAppointmentFunding.accountNumber", budgetConstructionFundingLock.getAccountNumber());
        criteria.addEqualTo("pendingBudgetConstructionAppointmentFunding.subAccountNumber", budgetConstructionFundingLock.getSubAccountNumber());
        criteria.addEqualTo("pendingBudgetConstructionAppointmentFunding.universityFiscalYear", budgetConstructionFundingLock.getUniversityFiscalYear());
        criteria.addEqualTo("positionLockUserIdentifier", budgetConstructionFundingLock.getAppointmentFundingLockUserId());
        String[] columns = new String[] { "positionNumber" };
        ReportQueryByCriteria q = QueryFactory.newReportQuery(BudgetConstructionPosition.class, columns, criteria, true);
        PersistenceBroker pb = getPersistenceBroker(true);

        Iterator<Object[]> iter = pb.getReportQueryIteratorByQuery(q);

        if (iter.hasNext()) {
            Object[] objs = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iter);
            if (objs[0] != null) {
                positionNumber = (String) objs[0];
            }
        }
        return positionNumber;
    }

    /**
     * Gets a BudgetConstructionPosition from the database by the primary key positionNumber, fiscalYear
     * 
     * @param positionNumber
     * @param fiscalYear
     * @return BudgetConstructionPosition
     */
    public BudgetConstructionPosition getByPrimaryId(String positionNumber, Integer fiscalYear) {
        // LOG.debug("getByPrimaryId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("positionNumber", positionNumber);
        criteria.addEqualTo("universityFiscalYear", fiscalYear);

        return (BudgetConstructionPosition) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(BudgetConstructionPosition.class, criteria));
    }

    /**
     * Saves a BudgetConstructionPosition to the database
     * 
     * @param bcPosition
     */
    public void saveBudgetConstructionPosition(BudgetConstructionPosition bcPosition) {
        getPersistenceBrokerTemplate().store(bcPosition);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#deleteBudgetConstructionPullupByUserId(java.lang.String)
     */
    public void deleteBudgetConstructionPullupByUserId(String principalName) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo("principalId", principalName);
        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(BudgetConstructionPullup.class, criteria));

    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#getBudgetConstructionPullupFlagSetByUserId(java.lang.String)
     */
    public List<BudgetConstructionPullup> getBudgetConstructionPullupFlagSetByUserId(String principalName) {
        List<BudgetConstructionPullup> orgs = new ArrayList<BudgetConstructionPullup>();

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, principalName);
        criteria.addGreaterThan("pullFlag", OrgSelControlOption.NO.getKey());
        orgs = (List<BudgetConstructionPullup>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(BudgetConstructionPullup.class, criteria));
        if (orgs.isEmpty() || orgs.size() == 0) {
            return (List<BudgetConstructionPullup>)Collections.EMPTY_LIST;
        }
        return orgs;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#getBcPullupChildOrgs(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public List<BudgetConstructionPullup> getBudgetConstructionPullupChildOrgs(String principalId, String chartOfAccountsCode, String organizationCode) {
        List<BudgetConstructionPullup> orgs = new ArrayList<BudgetConstructionPullup>();

        Criteria cycleCheckCriteria = new Criteria();
        cycleCheckCriteria.addEqualToField("chartOfAccountsCode", "reportsToChartOfAccountsCode");
        cycleCheckCriteria.addEqualToField("organizationCode", "reportsToOrganizationCode");
        cycleCheckCriteria.setEmbraced(true);
        cycleCheckCriteria.setNegative(true);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("reportsToChartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("reportsToOrganizationCode", organizationCode);
        criteria.addEqualTo("principalId", principalId);
        criteria.addAndCriteria(cycleCheckCriteria);

        QueryByCriteria query = QueryFactory.newQuery(BudgetConstructionPullup.class, criteria);
        query.addOrderByAscending("organization.organizationName");

        orgs = (List<BudgetConstructionPullup>) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        if (orgs.isEmpty() || orgs.size() == 0) {
            return (List<BudgetConstructionPullup>) Collections.EMPTY_LIST;
        }
        return orgs;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#getPendingBudgetConstructionAppointmentFundingRequestSum(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger)
     */
    public KualiInteger getPendingBudgetConstructionAppointmentFundingRequestSum(PendingBudgetConstructionGeneralLedger salaryDetailLine) {
        KualiInteger salarySum = KualiInteger.ZERO;

        Criteria criteria = new Criteria();
        criteria.addEqualTo("universityFiscalYear", salaryDetailLine.getUniversityFiscalYear());
        criteria.addEqualTo("chartOfAccountsCode", salaryDetailLine.getChartOfAccountsCode());
        criteria.addEqualTo("accountNumber", salaryDetailLine.getAccountNumber());
        criteria.addEqualTo("subAccountNumber", salaryDetailLine.getSubAccountNumber());
        criteria.addEqualTo("financialObjectCode", salaryDetailLine.getFinancialObjectCode());
        criteria.addEqualTo("financialSubObjectCode", salaryDetailLine.getFinancialSubObjectCode());
        String[] columns = new String[] { "financialObjectCode", "financialSubObjectCode", "sum(appointmentRequestedAmount)" };
        ReportQueryByCriteria q = QueryFactory.newReportQuery(PendingBudgetConstructionAppointmentFunding.class, columns, criteria, true);
        q.addGroupBy(new String[] { "financialObjectCode", "financialSubObjectCode" });
        PersistenceBroker pb = getPersistenceBroker(true);

        Iterator<Object[]> iter = pb.getReportQueryIteratorByQuery(q);

        if (iter.hasNext()) {
            Object[] objs = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iter);
            if (objs[2] != null) {
                salarySum = new KualiInteger((BigDecimal) objs[2]);
            }
        }
        return salarySum;
    }


    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#getDocumentPBGLFringeLines(java.lang.String,
     *      java.util.List)
     */
    public List getDocumentPBGLFringeLines(String documentNumber, List fringeObjects) {
        List documentPBGLfringeLines = new ArrayList();

        // we probably should just add a clearcache call at the end of all JDBC public methods that update the DB
        getPersistenceBrokerTemplate().clearCache();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentNumber", documentNumber);
        criteria.addIn("financialObjectCode", fringeObjects);
        QueryByCriteria query = QueryFactory.newQuery(PendingBudgetConstructionGeneralLedger.class, criteria);
        query.addOrderByAscending("financialObjectCode");
        documentPBGLfringeLines = (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return documentPBGLfringeLines;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#isDelegate(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public boolean isDelegate(String chartOfAccountsCode, String accountNumber, String principalId) {

        boolean retval = false;

        // active BC account delegates are marked with the BC document type or the special "ALL" document type
        List docTypes = new ArrayList();
        docTypes.add(BCConstants.DOCUMENT_TYPE_CODE_ALL);
        docTypes.add(KFSConstants.FinancialDocumentTypeCodes.BUDGET_CONSTRUCTION);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addEqualTo("accountDelegateSystemId", principalId);
        criteria.addEqualTo("active", "Y");
        criteria.addIn("financialDocumentTypeCode", docTypes);
        QueryByCriteria query = QueryFactory.newQuery(AccountDelegate.class, criteria);
        if (getPersistenceBrokerTemplate().getCount(query) > 0) {
            retval = true;
        }

        return retval;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#getAccountOrgHierForAccount(java.lang.String,
     *      java.lang.String, java.lang.Integer)
     */
    public List<BudgetConstructionAccountOrganizationHierarchy> getAccountOrgHierForAccount(String chartOfAccountsCode, String accountNumber, Integer universityFiscalYear) {
        List<BudgetConstructionAccountOrganizationHierarchy> accountOrgHier = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("universityFiscalYear", universityFiscalYear);
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);

        QueryByCriteria query = QueryFactory.newQuery(BudgetConstructionAccountOrganizationHierarchy.class, criteria);
        query.addOrderByAscending("organizationLevelCode");

        accountOrgHier = (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);


        return accountOrgHier;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#getAccountReports(java.lang.String, java.lang.String)
     */
    public BudgetConstructionAccountReports getAccountReports(String chartOfAccountsCode, String accountNumber) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);

        return (BudgetConstructionAccountReports) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(BudgetConstructionAccountReports.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#getOrganizationReports(java.lang.String,
     *      java.lang.String)
     */
    public BudgetConstructionOrganizationReports getOrganizationReports(String chartOfAccountsCode, String organizationCode) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("organizationCode", organizationCode);

        return (BudgetConstructionOrganizationReports) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(BudgetConstructionOrganizationReports.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#insertAccountIntoAccountOrganizationHierarchy(java.lang.String,
     *      java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    public boolean insertAccountIntoAccountOrganizationHierarchy(String rootChart, String rootOrganization, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, Integer currentLevelCode, String organizationChartOfAccountsCode, String organizationCode) {

        boolean overFlow = false;

        // insert the level
        BudgetConstructionAccountOrganizationHierarchy accountOrganizationHierarchy = new BudgetConstructionAccountOrganizationHierarchy();
        accountOrganizationHierarchy.setUniversityFiscalYear(universityFiscalYear);
        accountOrganizationHierarchy.setChartOfAccountsCode(chartOfAccountsCode);
        accountOrganizationHierarchy.setAccountNumber(accountNumber);
        accountOrganizationHierarchy.setOrganizationLevelCode(currentLevelCode);
        accountOrganizationHierarchy.setOrganizationChartOfAccountsCode(organizationChartOfAccountsCode);
        accountOrganizationHierarchy.setOrganizationCode(organizationCode);
        getPersistenceBrokerTemplate().store(accountOrganizationHierarchy);

        // if not currently the root, get the next reports to org, if not overflow call this to insert the next level
        if (!(rootChart.equalsIgnoreCase(organizationChartOfAccountsCode) && rootOrganization.equalsIgnoreCase(organizationCode))) {
            if (currentLevelCode < BCConstants.MAXIMUM_ORGANIZATION_TREE_DEPTH) {
                BudgetConstructionOrganizationReports organizationReports = this.getOrganizationReports(organizationChartOfAccountsCode, organizationCode);
                if (organizationReports != null) {
                    currentLevelCode++;
                    overFlow = this.insertAccountIntoAccountOrganizationHierarchy(rootChart, rootOrganization, universityFiscalYear, chartOfAccountsCode, accountNumber, currentLevelCode, organizationReports.getReportsToChartOfAccountsCode(), organizationReports.getReportsToOrganizationCode());
                }
                else {
                    // not the root but doesn't exist - done
                }
            }
            else {
                // overflow
                overFlow = true;
            }
        }
        return overFlow;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#deleteExistingAccountOrganizationHierarchy(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    public void deleteExistingAccountOrganizationHierarchy(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo("universityFiscalYear", universityFiscalYear);
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(BudgetConstructionAccountOrganizationHierarchy.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#getDetailSalarySettingLaborObjects(java.lang.Integer,
     *      java.lang.String)
     */
    public List<String> getDetailSalarySettingLaborObjects(Integer universityFiscalYear, String chartOfAccountsCode) {
        List<String> detailSalarySettingObjects = new ArrayList<String>();

        Map<String, Object> laborObjectCodeMap = new HashMap<String, Object>();
        laborObjectCodeMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        laborObjectCodeMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        laborObjectCodeMap.put(KFSPropertyConstants.DETAIL_POSITION_REQUIRED_INDICATOR, true);
        List<LaborLedgerObject> laborLedgerObjects = kualiModuleService.getResponsibleModuleService(LaborLedgerObject.class).getExternalizableBusinessObjectsList(LaborLedgerObject.class, laborObjectCodeMap);

        for (LaborLedgerObject laborObject : laborLedgerObjects) {
            detailSalarySettingObjects.add(laborObject.getFinancialObjectCode());
        }

        return detailSalarySettingObjects;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#getPBGLSalarySettingRows(java.lang.String,
     *      java.util.List)
     */
    public List getPBGLSalarySettingRows(String documentNumber, List salarySettingObjects) {
        List pbglSalarySettingRows = new ArrayList();

        // need to make sure we are getting the data that was updated by the jdbc benefits calc calls
        getPersistenceBrokerTemplate().clearCache();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentNumber", documentNumber);
        criteria.addIn("financialObjectCode", salarySettingObjects);
        QueryByCriteria query = QueryFactory.newQuery(PendingBudgetConstructionGeneralLedger.class, criteria);
        pbglSalarySettingRows = (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return pbglSalarySettingRows;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao#getAllFundingForPosition(java.lang.Integer,
     *      java.lang.String)
     */
    public List<PendingBudgetConstructionAppointmentFunding> getAllFundingForPosition(Integer universityFiscalYear, String positionNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        criteria.addEqualTo(BCPropertyConstants.POSITION_NUMBER, positionNumber);

        QueryByCriteria query = QueryFactory.newQuery(PendingBudgetConstructionAppointmentFunding.class, criteria);

        return (List<PendingBudgetConstructionAppointmentFunding>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * Sets the kualiModuleService attribute value.
     * 
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * Gets the dataDictionaryService attribute. 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}

