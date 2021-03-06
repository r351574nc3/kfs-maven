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
package org.kuali.kfs.sys.batch;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface FileManager {
    public String getJobName();
    public String getStepName();
    
    // public List<BatchFile> getBatchFilesMatchingCriteria(Map<String, String> criteria);
    
    // will check KIM permissions
    public OutputStream getBatchFileContents(File file);
    
    public String getDirectoryPath();
    
    public void deleteOldFiles();
}
