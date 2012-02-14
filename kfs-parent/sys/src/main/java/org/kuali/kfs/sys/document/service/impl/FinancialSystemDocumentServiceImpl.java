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
package org.kuali.kfs.sys.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentDao;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is a Financial System specific Document Service class to allow for the {@link #findByDocumentHeaderStatusCode(Class, String)} method.
 */
@Transactional
public class FinancialSystemDocumentServiceImpl implements FinancialSystemDocumentService {
    private FinancialSystemDocumentDao financialSystemDocumentDao;
    private DocumentService documentService;
    
    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#findByDocumentHeaderStatusCode(java.lang.Class, java.lang.String)
     */
    public <T extends Document> Collection<T> findByDocumentHeaderStatusCode(Class<T> clazz, String statusCode) throws WorkflowException {
        Collection<T> foundDocuments = getFinancialSystemDocumentDao().findByDocumentHeaderStatusCode(clazz, statusCode);
        Collection<T> returnDocuments = new ArrayList<T>();
        for (Iterator<T> iter = foundDocuments.iterator(); iter.hasNext();) {
            Document doc = (Document) iter.next();
            returnDocuments.add((T)getDocumentService().getByDocumentHeaderId(doc.getDocumentNumber()));
        }
        return returnDocuments;
    }

    public FinancialSystemDocumentDao getFinancialSystemDocumentDao() {
        return financialSystemDocumentDao;
    }

    public void setFinancialSystemDocumentDao(FinancialSystemDocumentDao financialSystemDocumentDao) {
        this.financialSystemDocumentDao = financialSystemDocumentDao;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    
}
