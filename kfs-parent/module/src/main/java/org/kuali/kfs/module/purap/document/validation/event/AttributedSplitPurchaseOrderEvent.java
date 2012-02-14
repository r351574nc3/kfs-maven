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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.kns.document.Document;

public class AttributedSplitPurchaseOrderEvent extends AttributedDocumentEventBase {

    public AttributedSplitPurchaseOrderEvent(Document document) {
        this(KFSConstants.EMPTY_STRING, document);  
    }
    
    public AttributedSplitPurchaseOrderEvent(String errorPathPrefix, Document document) {
        super("Validating Split of document " + getDocumentId(document), errorPathPrefix, document);
    }
}
