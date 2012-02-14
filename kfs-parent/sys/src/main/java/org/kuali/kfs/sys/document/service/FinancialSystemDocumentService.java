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
package org.kuali.kfs.sys.document.service;

import java.util.Collection;

import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;

/**
 * This class is the financial system specific document service interface
 */
public interface FinancialSystemDocumentService {

    public <T extends Document> Collection<T> findByDocumentHeaderStatusCode(Class<T> clazz, String statusCode) throws WorkflowException;

}