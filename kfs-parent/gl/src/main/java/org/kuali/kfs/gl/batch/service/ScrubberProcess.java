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
package org.kuali.kfs.gl.batch.service;

import java.sql.Date;

import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.impl.ScrubberStatus;


public interface ScrubberProcess {

    /**
     * Scrub this single group read only. This will only output the scrubber report. It won't output any other groups.
     * 
     * @param group the origin entry group that should be scrubbed
     * @param the document number of any specific entries to scrub
     */
    public void scrubGroupReportOnly(String fileName, String documentNumber);

    /**
     * Scrubs all entries in all groups and documents.
     */
    public void scrubEntries();

    /**
     * Scrubs the origin entry and ID billing details if the given batch. Store all scrubber output into the collectorReportData
     * parameter. NOTE: DO NOT CALL ANY OF THE scrub* METHODS OF THIS CLASS AFTER CALLING THIS METHOD FOR EVERY UNIQUE INSTANCE OF
     * THIS CLASS, OR THE COLLECTOR REPORTS MAY BE CORRUPTED
     * 
     * @param batch the data gathered from a Collector file
     * @param collectorReportData the statistics generated by running the Collector
     */
    public void scrubCollectorBatch(ScrubberStatus scrubberStatus, CollectorBatch batch, CollectorReportData collectorReportData);

    /**
     * Scrub all entries that need it in origin entry. Put valid scrubbed entries in a scrubber valid group, put errors in a
     * scrubber error group, and transactions with an expired account in the scrubber expired account group.
     * @param group the specific origin entry group to scrub
     * @param documentNumber the number of the document with entries to scrub
     */
    public void scrubEntries(boolean reportOnlyMode, String documentNumber);

    /**
     * The demerger process reads all of the documents in the error group, then moves all of the original entries for that document
     * from the valid group to the error group. It does not move generated entries to the error group. Those are deleted. It also
     * modifies the doc number and origin code of cost share transfers.
     * 
     * @param errorGroup this scrubber run's error group
     * @param validGroup this scrubber run's valid group
     */
    public void performDemerger();

    /**
     * Sets the proper cost share object code in an entry and its offset
     * 
     * @param costShareEntry GL Entry for cost share
     * @param originEntry Scrubbed GL Entry that this is based on
     */
    public void setCostShareObjectCode(OriginEntryFull costShareEntry, OriginEntryInformation originEntry);

    /**
     * This method modifies the run date if it is before the cutoff time specified by the RunTimeService See
     * KULRNE-70 This method is public to facilitate unit testing
     * 
     * @param currentDate the date the scrubber should report as having run on
     * @return the run date
     */
    public Date calculateRunDate(java.util.Date currentDate);

}
