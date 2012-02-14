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

import java.io.InputStream;
import java.util.List;

import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;

public class EndowmentTransactionLineParserBase implements EndowmentTransactionLineParser {
    // TODO these methods don't do anything for now, added just to allow coding the EndowmentTransactionLinesDocumentBase

    public String getExpectedTransactionLineFormatAsString(Class<? extends EndowmentTransactionLine> transactionLineClass) {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getSourceTransactionLineFormat() {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getTargetTransactionLineFormat() {
        // TODO Auto-generated method stub
        return null;
    }

    public List importEndowmentSourceTransactionLines(String fileName, InputStream stream, EndowmentTransactionLinesDocument document) {
        // TODO Auto-generated method stub
        return null;
    }

    public List importEndowmentTargetTransactionLines(String fileName, InputStream stream, EndowmentTransactionLinesDocument document) {
        // TODO Auto-generated method stub
        return null;
    }

}
