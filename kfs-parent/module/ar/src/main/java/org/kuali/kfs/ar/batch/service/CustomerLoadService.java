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
package org.kuali.kfs.module.ar.batch.service;

import java.util.List;

import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;
import org.kuali.rice.kns.document.MaintenanceDocument;

public interface CustomerLoadService {

    /**
     * Validates and parses all files ready to go in the batch staging area.
     * @return
     */
    public boolean loadFiles();

    /**
     * Validates and parses the file identified by the given files name. If successful, parsed entries are stored.
     * 
     * @param fileNaem Name of file to be uploaded and processed.
     * @return True if the file load and store was successful, false otherwise.
     */
    public boolean loadFile(String fileName);
    
    /**
     * 
     * Performs specific validation on the parsed file contents. If errors were found, method will return false and
     * GlobalVariables.errorMap will contain the error message. If no errors were encountered the method will return true.
     * 
     * @param customerUploads A List of CustomerDigesterVO objects, that are the processed 
     *        results of the uploaded files.
     * @return True if no errors were encountered, False otherwise.
     */
    public boolean validate(List<CustomerDigesterVO> customerUploads);
    
    /**
     * 
     * Performs specific validation on the parsed file contents. If errors were found, method will return false and
     * GlobalVariables.errorMap will contain the error message. If no errors were encountered the method will return true.
     * 
     * @param customerUploads A List of CustomerDigesterVO objects, that are the processed 
     *        results of the uploaded files.
     * @param customerMaintDocs A list of the customerMaintDocs that are returned by the validateAndPrepare method.  A valid list 
     *        should be passed in, and MaintDocs will be added to it.
     * @return True if no errors were encountered, False otherwise.
     */
    public boolean validateAndPrepare(List<CustomerDigesterVO> customerUploads, List<MaintenanceDocument> customerMaintDocs, boolean useGlobalErrorMap);

}

