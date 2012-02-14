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

package org.kuali.kfs.module.purap.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * Purchase Order Contract Language Business Object.
 */
public class PurchaseOrderContractLanguage extends PersistableBusinessObjectBase implements Inactivateable{

    private Integer purchaseOrderContractLanguageIdentifier;
    private String campusCode;
    private String purchaseOrderContractLanguageDescription;
    private Date contractLanguageCreateDate;
    private boolean active;

    /**
     * Default constructor.
     */
    public PurchaseOrderContractLanguage() {
        this.setContractLanguageCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
    }

    public Integer getPurchaseOrderContractLanguageIdentifier() {
        return purchaseOrderContractLanguageIdentifier;
    }

    public void setPurchaseOrderContractLanguageIdentifier(Integer purchaseOrderContractLanguageIdentifier) {
        this.purchaseOrderContractLanguageIdentifier = purchaseOrderContractLanguageIdentifier;
    }

    public String getCampusCode() {
        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public String getPurchaseOrderContractLanguageDescription() {
        return purchaseOrderContractLanguageDescription;
    }

    public void setPurchaseOrderContractLanguageDescription(String purchaseOrderContractLanguageDescription) {
        this.purchaseOrderContractLanguageDescription = purchaseOrderContractLanguageDescription;
    }

    public Date getContractLanguageCreateDate() {
        return contractLanguageCreateDate;
    }

    public void setContractLanguageCreateDate(Date contractLanguageCreateDate) {
        this.contractLanguageCreateDate = contractLanguageCreateDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.purchaseOrderContractLanguageIdentifier != null) {
            m.put("purchaseOrderContractLanguageIdentifier", this.purchaseOrderContractLanguageIdentifier.toString());
        }
        return m;
    }

}
