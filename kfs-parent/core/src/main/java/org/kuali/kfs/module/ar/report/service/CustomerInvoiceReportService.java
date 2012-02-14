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
package org.kuali.kfs.module.ar.report.service;

import java.io.File;
import java.util.Date;

import org.kuali.kfs.module.ar.report.util.CustomerInvoiceReportDataHolder;

/**
 * To generate the working progress reports for the effort certification
 */
public interface CustomerInvoiceReportService {

    /**
     * generate report for effort certification extract process witht the given report data and information
     * 
     * @param reportDataHolder the given report data holder
     * @param runDate the datetime of the repor generation
     */
    public File generateReport(CustomerInvoiceReportDataHolder reportDataHolder, Date runDate);
}
