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
package org.kuali.kfs.module.ar.document.validation;

import org.kuali.rice.kns.document.TransactionalDocument;

public interface GenerateReferenceDocumentRule <F extends TransactionalDocument > {

    /**
     * This method is called when a reference document is generated
     * @param transactionalDocument the cash control document
     * @return true if reference document can be generated, false otherwise
     */
    public boolean processGenerateReferenceDocumentBusinessRules(F transactionalDocument);
}
