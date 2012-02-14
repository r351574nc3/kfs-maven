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
package org.kuali.kfs.module.ec.dataaccess;

import java.util.List;

import org.kuali.kfs.integration.ec.EffortCertificationReport;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;

/**
 * Provides interface for data operations on the EffortCertificationReportDefinition table
 */
public interface EffortCertificationReportDefinitionDao {

    /**
     * Finds all effort certification report definitions that have the same report type code and are active (excluding the current record)
     * 
     * @param effortCertificationReportDefinition
     * @return
     */
    public List<EffortCertificationReportDefinition> getAllOtherActiveByType(EffortCertificationReportDefinition effortCertificationReportDefinition);

    /**
     * Retrieves all EffortCertificationReportDefinition records
     * 
     * @return list of EffortCertificationReportDefinition records
     */
    public List<EffortCertificationReportDefinition> getAll();
    
    /**
     * Retrieves all EffortCertificationReportDefinition records that have a begin or end period fiscal year equal to the given
     * fiscal year, and report on pay given by the position object group code.
     * 
     * @param fiscalYear - fiscal year for being or end period
     * @param positionObjectCode - position object group code for report definition
     * @return effort report definitions with a period in the given year and report for given position code
     */
    public List<EffortCertificationReport> getAllByYearAndPositionCode(Integer fiscalYear, String positionObjectCode);
}
