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
package org.kuali.kfs.module.endow.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.kfs.module.endow.document.service.impl.FrequencyCodeServiceImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class ACIModelDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase{

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);
        
        AutomatedCashInvestmentModel aciModel = (AutomatedCashInvestmentModel) document.getNewMaintainableObject().getBusinessObject();

        String aciFrequencyCode = aciModel.getAciFrequencyCode();
        if (StringUtils.isNotEmpty(aciFrequencyCode)) {
            FrequencyCodeServiceImpl frequencyCodeServiceImpl = (FrequencyCodeServiceImpl) SpringContext.getBean(FrequencyCodeServiceImpl.class);
            aciModel.setAciNextDueDate(frequencyCodeServiceImpl.calculateProcessDate(aciFrequencyCode));
        }

        return readOnlyPropertyNames;
    }



}
