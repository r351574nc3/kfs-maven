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
package org.kuali.kfs.module.endow.dataaccess.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.EndowConstants.IncomePrincipalIndicator;
import org.kuali.kfs.module.endow.businessobject.FeeClassCode;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.FeeSecurity;
import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.kfs.module.endow.document.service.MonthEndDateService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KualiInteger;

public class HoldingHistoryDaoOjb extends PlatformAwareDaoBaseOjb implements HoldingHistoryDao {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HoldingHistoryDaoOjb.class);
    
    protected MonthEndDateService monthEndDateService;
    protected SecurityDao securityDao;
    
    /**
     * Prepares the criteria and selects the records from END_HLDG_HIST_T table
     */
    protected Collection<HoldingHistory> getHoldingHistoryForBlance(FeeMethod feeMethod) {
        Collection<HoldingHistory> holdingHistory = new ArrayList(); 
        
        Collection incomePrincipalValues = new ArrayList();
        incomePrincipalValues.add(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME);
        incomePrincipalValues.add(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL);
        
        Criteria criteria = new Criteria();
        
        if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME_AND_PRINCIPAL)) {
            criteria.addIn(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, incomePrincipalValues);
        }
        else {
            if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME)) {
                criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_INCOME);
            }
            
            if (feeMethod.getFeeBaseCode().equalsIgnoreCase(EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL)) {
                criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, EndowConstants.FeeMethod.FEE_BASE_CODE_VALUE_FOR_PRINCIPAL);
            }
        }

        Collection securityClassCodes =  new ArrayList();
        Collection securityIds = new ArrayList();
        
        if (feeMethod.getFeeByClassCode() && feeMethod.getFeeBySecurityCode()) {
            securityClassCodes = getSecurityClassCodes(feeMethod.getCode());
            securityIds = getSecurityIds(feeMethod.getCode());
            
            securityIds.addAll(securityClassCodes);
            if (securityIds.size() > 0) {
               criteria.addIn(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, securityIds);
            }
        }
        else {
            if (feeMethod.getFeeByClassCode()) {
                securityClassCodes = getSecurityClassCodes(feeMethod.getCode());
                if (securityClassCodes.size() > 0) {
                   criteria.addIn(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, securityClassCodes);
                }
            }
            
            if (feeMethod.getFeeBySecurityCode()) {
                securityIds = getSecurityIds(feeMethod.getCode());                
                if (securityIds.size() > 0) {
                    criteria.addIn(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, securityIds);
                }
            }
        }
        
        QueryByCriteria query = QueryFactory.newQuery(HoldingHistory.class, criteria);
            
        holdingHistory = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        
        return holdingHistory;
    }
       
    /**
     * Gets the security codes for a given securityClassCode in END_FEE_CLS_CD_T table
     * @feeMethodCode FEE_MTH
     * @return securityCodes
     */
    protected Collection getSecurityClassCodes(String feeMethodCode) {
        Collection securityClassCodes = new ArrayList();
        Collection<FeeClassCode> feeClassCodes = new ArrayList();        

        if (StringUtils.isNotBlank(feeMethodCode)) {        
            Map<String, String>  crit = new HashMap<String, String>();
            
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(FeeClassCode.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
                feeMethodCode = feeMethodCode.toUpperCase();
            }
            
            Criteria criteria = new Criteria();
            criteria.addEqualTo(EndowPropertyConstants.FEE_METHOD_CODE, feeMethodCode);
            criteria.addEqualTo(EndowPropertyConstants.FEE_CLASS_CODE_INCLUDE, EndowConstants.YES);
            
            QueryByCriteria query = QueryFactory.newQuery(FeeClassCode.class, criteria);
            
            feeClassCodes = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            for (FeeClassCode feeClassCode : feeClassCodes) {
                Collection <Security> securities = securityDao.getSecuritiesBySecurityClassCode(feeClassCode.getFeeClassCode());
                for (Security security : securities) {
                    securityClassCodes.add(security.getId());
                }
            }
        }
        
        return securityClassCodes;
    }
    
    /**
     * Gets the security ids for a given securityClassCode in END_FEE_SEC_T table
     * @feeMethodCode FEE_MTH
     * @return securityIds
     */
    protected Collection getSecurityIds(String feeMethodCode) {
        Collection securityIds = new ArrayList();
        Collection<FeeSecurity> feeSecuritys = new ArrayList();        

        if (StringUtils.isNotBlank(feeMethodCode)) {        
            Map<String, String>  crit = new HashMap<String, String>();
            
            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(FeeSecurity.class, EndowPropertyConstants.FEE_METHOD_CODE)) {
                feeMethodCode = feeMethodCode.toUpperCase();
            }
            
            Criteria criteria = new Criteria();
            criteria.addEqualTo(EndowPropertyConstants.FEE_METHOD_CODE, feeMethodCode);
            criteria.addEqualTo(EndowPropertyConstants.FEE_SECURITY_INCLUDE, EndowConstants.YES);
            
            QueryByCriteria query = QueryFactory.newQuery(FeeSecurity.class, criteria);
            
            feeSecuritys = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            for (FeeSecurity feeSecurity : feeSecuritys) {
                securityIds.add(feeSecurity.getSecurityCode());
            }
        }
        
        return securityIds;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao#getHoldingHistoryTotalHoldingUnits(FeeMethod)
     */
    public BigDecimal getHoldingHistoryTotalHoldingUnits(FeeMethod feeMethod) {
        BigDecimal totalHoldingUnits = BigDecimal.ZERO;
        
        Date lastProcessDate = feeMethod.getFeeLastProcessDate();
        Date mostRecentDate = monthEndDateService.getMostRecentDate();
        
        String feeBalanceTypeCode = feeMethod.getFeeBalanceTypeCode();
        
        Collection <HoldingHistory> holdingHistoryRecords = getHoldingHistoryForBlance(feeMethod);
        for (HoldingHistory holdingHistory : holdingHistoryRecords) {
            Date monthEndDate = monthEndDateService.getByPrimaryKey(holdingHistory.getMonthEndDateId());

            if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_UNITS) && (monthEndDate.compareTo(lastProcessDate) > 0)) {
                totalHoldingUnits = totalHoldingUnits.add(holdingHistory.getUnits());    
            }
            if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_UNITS) && (mostRecentDate.compareTo(lastProcessDate) > 0)) {
                totalHoldingUnits = totalHoldingUnits.add(holdingHistory.getUnits());    
            }
        }
        
        if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_UNITS)) {
            totalHoldingUnits = KEMCalculationRoundingHelper.divide(totalHoldingUnits, BigDecimal.valueOf(holdingHistoryRecords.size()), EndowConstants.Scale.SECURITY_UNIT_VALUE);
        }
        
        return totalHoldingUnits;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao#getHoldingHistoryTotalHoldingMarketValue(FeeMethod)
     */
    public BigDecimal getHoldingHistoryTotalHoldingMarketValue(FeeMethod feeMethod) {
        BigDecimal totalHoldingMarkteValue = BigDecimal.ZERO;
        
        Date lastProcessDate = feeMethod.getFeeLastProcessDate();
        Date mostRecentDate = monthEndDateService.getMostRecentDate();
        
        String feeBalanceTypeCode = feeMethod.getFeeBalanceTypeCode();
        
        Collection <HoldingHistory> holdingHistoryRecords = getHoldingHistoryForBlance(feeMethod);
        for (HoldingHistory holdingHistory : holdingHistoryRecords) {
            Date monthEndDate = monthEndDateService.getByPrimaryKey(holdingHistory.getMonthEndDateId());
            if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_MARKET_VALUE) && (monthEndDate.compareTo(lastProcessDate) > 0)) {
                totalHoldingMarkteValue = totalHoldingMarkteValue.add(holdingHistory.getMarketValue());    
            }
            if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_MARKET_VALUE) && (monthEndDate.compareTo(mostRecentDate) > 0)) {
                totalHoldingMarkteValue = totalHoldingMarkteValue.add(holdingHistory.getMarketValue());    
            }
        }
        
        if (feeBalanceTypeCode.equals(EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_MARKET_VALUE)) {
            totalHoldingMarkteValue = KEMCalculationRoundingHelper.divide(totalHoldingMarkteValue, BigDecimal.valueOf(holdingHistoryRecords.size()), EndowConstants.Scale.SECURITY_UNIT_VALUE);
        }
        
        return totalHoldingMarkteValue;
    }

    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao#getHoldingHistory(java.lang.String, java.lang.String)
     */
    public List<HoldingHistory> getHoldingHistory(String kemid, KualiInteger medId) {
       
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_MONTH_END_DATE_ID, medId);
        QueryByCriteria qbc = QueryFactory.newQuery(HoldingHistory.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID);
        
        return (List<HoldingHistory>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * 
     * @see org.kuali.kfs.module.endow.dataaccess.HoldingHistoryDao#getHoldingHistoryByKemid(java.lang.String)
     */
    public List<HoldingHistory> getHoldingHistoryByKemid(String kemid) {
       
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        QueryByCriteria qbc = QueryFactory.newQuery(HoldingHistory.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID);
        
        return (List<HoldingHistory>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
    
    public List<HoldingHistory> getHoldingHistoryByKemidIdAndMonthEndIdAndIpInd(String kemid, KualiInteger monthEndId, String ipInd) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_KEMID, kemid);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_MONTH_END_DATE_ID, monthEndId);
        criteria.addGreaterThan(EndowPropertyConstants.HOLDING_TAX_LOT_UNITS, BigDecimal.ZERO);
        if (ipInd.equalsIgnoreCase(IncomePrincipalIndicator.INCOME) || ipInd.equalsIgnoreCase(IncomePrincipalIndicator.PRINCIPAL)) {
            criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, ipInd);
        }
        
        QueryByCriteria qbc = QueryFactory.newQuery(HoldingHistory.class, criteria);
        qbc.addOrderByAscending(EndowPropertyConstants.KEMID);
        
        return (List<HoldingHistory>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        
    }
    
    /**
     * Gets the sum of the specified attribute values
     * 
     * @param kemid
     * @param medId
     * @param securityId
     * @param ipInd
     * @param attributeName
     * @return
     */
    public BigDecimal getSumOfHoldginHistoryAttribute(String attributeName, String kemid, KualiInteger medId, String securityId, String ipInd) {
        
        BigDecimal total = BigDecimal.ZERO;
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_TAX_LOT_KEMID, kemid);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_MONTH_END_DATE_ID, medId);
        criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_SECURITY_ID, securityId);
        criteria.addGreaterThan(EndowPropertyConstants.HOLDING_TAX_LOT_UNITS, BigDecimal.ZERO);
        if (ipInd.equalsIgnoreCase(IncomePrincipalIndicator.INCOME) || ipInd.equalsIgnoreCase(IncomePrincipalIndicator.PRINCIPAL)) {
            criteria.addEqualTo(EndowPropertyConstants.HOLDING_HISTORY_INCOME_PRINCIPAL_INDICATOR, ipInd);
        }
                
        ReportQueryByCriteria rqbc = QueryFactory.newReportQuery(HoldingHistory.class, criteria);        
        rqbc.setAttributes(new String[] {"sum(" + attributeName + ")"});
        rqbc.addGroupBy(attributeName);      
        Iterator<?> result = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);        

        while (result.hasNext()) {
            Object[] data = (Object[]) result.next();
            total = total.add((BigDecimal)data[0]);
        }
        
        return total;
    }
    
    /**
     * Gets the monthEndDateService attribute. 
     * @return Returns the monthEndDateService.
     */
    protected MonthEndDateService getMonthEndDateService() {
        return monthEndDateService;
    }

    /**
     * Sets the monthEndDateService attribute value.
     * @param monthEndDateService The monthEndDateService to set.
     */
    public void setMonthEndDateService(MonthEndDateService monthEndDateService) {
        this.monthEndDateService = monthEndDateService;
    }
    
    /**
     * Gets the securityDao attribute. 
     * @return Returns the securityDao.
     */
    protected SecurityDao getSecurityDao() {
        return securityDao;
    }

    /**
     * Sets the securityDao attribute value.
     * @param securityDao The securityDao to set.
     */
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }
}
