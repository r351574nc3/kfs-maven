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
package org.kuali.kfs.module.bc.exception;

import org.kuali.kfs.module.bc.BCKeyConstants;

/**
 * Thrown when a position retrieval is requested from an external system and is not found
 * 
 * @see org.kuali.kfs.module.bc..service.HumanResourcesPayrollService
 */
public class PositionNotFoundException extends RuntimeException {
    private Integer universityFiscalYear;
    private String positionNumber;

    /**
     * Constructs a PositionNotFoundException.java.
     */
    public PositionNotFoundException(Integer universityFiscalYear, String positionNumber) {
        super();

        this.universityFiscalYear = universityFiscalYear;
        this.positionNumber = positionNumber;
    }

    public String getMessageKey() {
        return BCKeyConstants.ERROR_EXTERNAL_POSITION_NOT_FOUND;
    }

    public String[] getMessageParameters() {
        return new String[] { universityFiscalYear.toString(), positionNumber };
    }

}
