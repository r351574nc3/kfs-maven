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
package org.kuali.kfs.module.endow.businessobject;

/**
 * Represents the "target" grouping of accounting lines in a given Endowment Transfer of Funds document. Its counterpart is the
 * SourceEndowmentAccountingLine class. Both objects' data is stored in the DB in a single table named "ENDOW_ACCT_LINES_T."
 */
public class TargetEndowmentAccountingLine extends EndowmentAccountingLineBase {

    /**
     * Initializes the financialDocumentLineTypeCode attribute to the value for this class.
     */
    public TargetEndowmentAccountingLine() {
        super();
        super.financialDocumentLineTypeCode = "T";
    }

}
