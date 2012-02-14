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
package org.kuali.kfs.module.cam.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class is a calculator which will distribute the payment amounts by ratio. Inputs received are
 * <li>Asset Payment Details</li>
 * <li>Asset Details</li>
 * <li>total historical cost for asset</li>
 * 
 * It provides a table mapping of asset payment distributions key off of AssetPaymentDetail and 
 * AssetPaymentAssetDetail 
 * 
 * Logic is best explained as below
 * <li>Compute the asset ratio of amount to be distributed per asset</li>
 * <li>For each Asset Payment Details, create proportional asset payments base on the asset ratio</li>
 *      <li>* Keep track of unallocated amount within each asset payment loop* <li>
 *      <li>* For the last asset in each payment detail iteration, use the rest of unallocated amount</li>
 */
public class AssetPaymentDistributor {

    private List<AssetPaymentDetail> assetPaymentDetailLines;

    private List<AssetPaymentAssetDetail> assetPaymentAssetDetails;
    
    private KualiDecimal totalHistoricalCost;
    
    private Map<String, Map<AssetPaymentAssetDetail, KualiDecimal>> paymentDistributionMap;
    
    /**
     * Constructor and instantiate the detai lists as empty
     */
    public AssetPaymentDistributor(List<AssetPaymentDetail> assetPaymentDetailLines, List<AssetPaymentAssetDetail> assetPaymentAssetDetails, KualiDecimal totalHistoricalCost) {
        this.assetPaymentDetailLines = assetPaymentDetailLines;
        this.assetPaymentAssetDetails = assetPaymentAssetDetails;
        this.totalHistoricalCost = totalHistoricalCost;
        
        //init method
        calculateAssetPaymentDistributions();
    }

    /**
     * Pre-calculate the asset payments base on AssetPaymentDetail(AccountSouceLines) and AssetPaymentAssetDetails
     * 
     * This will iterate by the AssetPaymentDetail as the outer iterator such that payment totals will match up by the AccountingSouceLines
     * in (GL).  The unallocated payment amount will be depleted per each AssetPaymentDetails
     * 
     *  NOTE: reversing the iteration sequence will cause a discrepancy in the AssetPaymentDetail totals 
     * 
     * @param document
     * @param assetPaymentDetailLines
     * @param assetPaymentAssetDetails
     */
    private void calculateAssetPaymentDistributions(){

        Map<String, Map<AssetPaymentAssetDetail, KualiDecimal>> assetPaymentAssetDetailMap = new HashMap<String, Map<AssetPaymentAssetDetail, KualiDecimal>>();

        // calculate the asset payment percentage and store into a map
        Map<AssetPaymentAssetDetail, Double> assetPaymentsPercentage = new HashMap<AssetPaymentAssetDetail, Double>(assetPaymentAssetDetails.size());
        
        for (AssetPaymentAssetDetail assetPaymentAssetDetail : assetPaymentAssetDetails) {
            // Doing the re-distribution of the cost based on the previous total cost of each asset compared with the total previous cost of the assets.
            // store the result in a temporary map 
            assetPaymentsPercentage.put(assetPaymentAssetDetail, getAssetDetailPercentage(assetPaymentAssetDetails.size(), new Double(totalHistoricalCost.toString()), assetPaymentAssetDetail));
        }

        // Start the iteration base from the AssetPaymentDetail - accountingSouceLines
        for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetailLines) {

            int paymentCount = assetPaymentAssetDetails.size();
            KualiDecimal amount = KualiDecimal.ZERO;

            // Keep unallocated amount so it could be used for last payment amount for the asset (to avoid rounding issue)
            KualiDecimal unallocatedAmount = assetPaymentDetail.getAmount();

            Map<AssetPaymentAssetDetail, KualiDecimal> assetDetailMap = new HashMap<AssetPaymentAssetDetail, KualiDecimal>();
            for (AssetPaymentAssetDetail assetPaymentAssetDetail : assetPaymentAssetDetails) {
                // Doing the re-distribution of the cost based on the previous total cost of each asset compared with the total
                // previous cost of the assets.
                Double percentage = assetPaymentsPercentage.get(assetPaymentAssetDetail);

                if (paymentCount-- == 1) {
                    // Deplete the rest of the payment for last payment
                    amount = unallocatedAmount;
                }
                else {
                    // Normal payment will be calculated by asset percentage
                    Double paymentAmount = new Double(assetPaymentDetail.getAmount().toString());
                    amount = new KualiDecimal(paymentAmount.doubleValue() * percentage.doubleValue());
                    unallocatedAmount = unallocatedAmount.subtract(amount);
                }
                assetDetailMap.put(assetPaymentAssetDetail, amount);
            }
            assetPaymentAssetDetailMap.put(assetPaymentDetail.getAssetPaymentDetailKey(), assetDetailMap);
        }
        
        paymentDistributionMap = assetPaymentAssetDetailMap;
        
        System.out.println(paymentDistributionMap);
    }
    
    /**
     * Retrieve the asset payment distributions
     * 
     * @return
     */
    public Map<String, Map<AssetPaymentAssetDetail, KualiDecimal>> getAssetPaymentDistributions() {
        return paymentDistributionMap;
    }
    
    /**
     * Get each Asset's allocation totals base the payment distributions
     * 
     * @return map of asset detail and its totals
     */
    public Map<AssetPaymentAssetDetail, KualiDecimal> getTotalAssetAllocations() {
        Map<AssetPaymentAssetDetail, KualiDecimal> assetTotalAllocationMap = new HashMap<AssetPaymentAssetDetail, KualiDecimal>();
        KualiDecimal allocation, total;
        
        //iterate all the distributions
        for (Map<AssetPaymentAssetDetail, KualiDecimal> assetDistrbution : getAssetPaymentDistributions().values()){
            
            for (AssetPaymentAssetDetail assetDetail : assetDistrbution.keySet()){
                allocation = assetDistrbution.get(assetDetail);
                total = assetTotalAllocationMap.get(assetDetail);
                
                assetTotalAllocationMap.put(assetDetail, total == null? allocation : total.add(allocation));
            }
        }
        return assetTotalAllocationMap;
    }
    
    /**
     * Doing the re-distribution of the cost based on the previous total cost of each asset compared with the total previous cost of
     * the assets.
     * 
     * @param detailSize
     * @param totalHistoricalCost
     * @param assetPaymentAssetDetail
     * @return
     */
    private Double getAssetDetailPercentage(int detailSize, Double totalHistoricalCost, AssetPaymentAssetDetail assetPaymentAssetDetail) {
        Double previousTotalCostAmount = new Double("0");
        if (assetPaymentAssetDetail.getPreviousTotalCostAmount() != null) {
            previousTotalCostAmount = new Double(StringUtils.defaultIfEmpty(assetPaymentAssetDetail.getPreviousTotalCostAmount().toString(), "0"));
        }

        Double percentage = new Double(0);
        if (totalHistoricalCost.compareTo(new Double(0)) != 0)
            percentage = (previousTotalCostAmount / totalHistoricalCost);
        else
            percentage = (1 / (new Double(detailSize)));
        return percentage;
    }

}
