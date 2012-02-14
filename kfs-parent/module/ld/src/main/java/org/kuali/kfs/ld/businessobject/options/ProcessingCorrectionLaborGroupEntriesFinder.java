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
package org.kuali.kfs.module.ld.businessobject.options;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryGroupService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * Returns a list of done files in batch origin entry directory
 */
public class ProcessingCorrectionLaborGroupEntriesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        List<KeyLabelPair> activeLabels = new ArrayList<KeyLabelPair>();

        LaborOriginEntryGroupService originEntryGroupService = SpringContext.getBean(LaborOriginEntryGroupService.class);;
        File[] fileList = originEntryGroupService.getAllFileInBatchDirectory();
        if (fileList != null) {
            for (File file : fileList) {
                String fileName = file.getName();
                if (fileName.contains(GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION)) {
                    activeLabels.add(new KeyLabelPair(fileName, fileName));
                }
            }
        }

        return activeLabels;
    }
}
