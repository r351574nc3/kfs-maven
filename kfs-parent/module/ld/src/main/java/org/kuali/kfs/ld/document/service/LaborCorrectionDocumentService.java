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
package org.kuali.kfs.module.ld.document.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;
import org.kuali.kfs.gl.document.CorrectionDocumentUtils;
import org.kuali.kfs.gl.document.GeneralLedgerCorrectionProcessDocument;
import org.kuali.kfs.gl.document.web.CorrectionDocumentEntryMetadata;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.document.LaborCorrectionDocument;
import org.kuali.rice.kns.web.ui.Column;


/**
 * Defines methods that must be implemented by classes providing a LaborCorrectionDocumentServiceImpl.
 */
public interface LaborCorrectionDocumentService {
    public final static String CORRECTION_TYPE_MANUAL = "M";
    public final static String CORRECTION_TYPE_CRITERIA = "C";
    public final static String CORRECTION_TYPE_REMOVE_GROUP_FROM_PROCESSING = "R";

    public final static String SYSTEM_DATABASE = "D";
    public final static String SYSTEM_UPLOAD = "U";

    /**
     * When passed into {@link #retrievePersistedInputOriginEntries(CorrectionDocument, int)} and
     * {@link #retrievePersistedOutputOriginEntries(CorrectionDocument, int)} as the int parameter, this will signify that there is
     * no abort threshold (i.e. the methods should return all of the persisted rows, regardless of number of rows.
     */
    public final static int UNLIMITED_ABORT_THRESHOLD = CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_UNLIMITED;

    /**
     * Find and return correctionChangeGroup with document number and Group Number
     * 
     * @param docId, i
     * @return
     */
    public CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber(String docId, int i);

    /**
     * Find and return correctionChange with document number and Group Number
     * 
     * @param docId, i
     * @return list of correctionChange
     */
    public List findByDocumentHeaderIdAndCorrectionGroupNumber(String docId, int i);

    /**
     * Find and return list of correctionCriteria with document number and Group Number
     * 
     * @param docId, i
     * @return
     */
    public List findByDocumentNumberAndCorrectionGroupNumber(String docId, int i);

    /**
     * Find and return laborCorrectionDocument with document number
     * 
     * @param docId, i
     * @return
     */
    public LaborCorrectionDocument findByCorrectionDocumentHeaderId(String docId);

    /**
     * Returns metadata to help render columns in the LLCP. Do not modify this list or the contents in this list.
     * 
     * @param docId
     * @return
     */
    public List<Column> getTableRenderColumnMetadata(String docId);

    /**
     * This method persists an Iterator of input origin entries for a document that is in the initiated or saved state
     * 
     * @param document an initiated or saved document
     * @param entries
     */
    public void persistInputOriginEntriesForInitiatedOrSavedDocument(LaborCorrectionDocument document, Iterator<LaborOriginEntry> entries);

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#removePersistedInputOriginEntriesForInitiatedOrSavedDocument(org.kuali.kfs.module.ld.document.LaborCorrectionDocument)
     */
    public void removePersistedInputOriginEntries(LaborCorrectionDocument document);

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#removePersistedInputOriginEntriesForInitiatedOrSavedDocument(org.kuali.kfs.module.ld.document.LaborCorrectionDocument)
     */
    public void removePersistedInputOriginEntries(String docId);

    /**
     * Retrieves input origin entries that have been persisted for this document
     * 
     * @param document the document
     * @param abortThreshold if the file exceeds this number of rows, then null is returned. {@link UNLIMITED_ABORT_THRESHOLD}
     *        signifies that there is no limit
     * @return the list, or null if there are too many origin entries
     * @throws RuntimeException several reasons, primarily relating to underlying persistence layer problems
     */
    public List<LaborOriginEntry> retrievePersistedInputOriginEntries(LaborCorrectionDocument document, int abortThreshold);

    /**
     * Returns true if the system is storing input origin entries for this class. Note that this does not mean that there's at least
     * one input origin entry record persisted for this document, but merely returns true if and only if the underlying persistence
     * mechanism has a record of this document's origin entries. See the docs for the implementations of this method for more
     * implementation specific details.
     * 
     * @param document
     * @return
     */
    public boolean areInputOriginEntriesPersisted(LaborCorrectionDocument document);

    /**
     * Writes out the persisted input origin entries in an {@link OutputStream} in a flat file format
     * 
     * @param document
     * @param out an open and ready output stream
     * @throws IOException
     * @throws RuntimeException several reasons, including if the entries are not persisted
     */
    public void writePersistedInputOriginEntriesToStream(LaborCorrectionDocument document, OutputStream out) throws IOException;

    /**
     * This method persists an Iterator of input origin entries for a document that is in the initiated or saved state
     * 
     * @param document an initiated or saved document
     * @param entries
     */
    public void persistOutputLaborOriginEntriesForInitiatedOrSavedDocument(LaborCorrectionDocument document, Iterator<LaborOriginEntry> entries);

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#removePersistedOutputOriginEntriesForInitiatedOrSavedDocument(org.kuali.module.labor.document.CorrectionDocument)
     */
    public void removePersistedOutputOriginEntries(LaborCorrectionDocument document);

    /**
     * @see org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService#removePersistedOutputOriginEntriesForInitiatedOrSavedDocument(org.kuali.module.labor.document.CorrectionDocument)
     */
    public void removePersistedOutputOriginEntries(String docId);

    /**
     * Retrieves output origin entries that have been persisted for this document
     * 
     * @param document the document
     * @param abortThreshold if the file exceeds this number of rows, then null is returned. {@link UNLIMITED_ABORT_THRESHOLD}
     *        signifies that there is no limit
     * @return the list, or null if there are too many origin entries
     * @throws RuntimeException several reasons, primarily relating to underlying persistence layer problems
     */
    public List<LaborOriginEntry> retrievePersistedOutputOriginEntries(LaborCorrectionDocument document, int abortThreshold);

    /**
     * Retrieves input origin entries that have been persisted for this document in an iterator. Implementations of this method may
     * choose to implement this method in a way that consumes very little memory.
     * 
     * @param document the document
     * @return the iterator
     * @throws RuntimeException several reasons, primarily relating to underlying persistence layer problems
     */
    public Iterator<LaborOriginEntry> retrievePersistedInputOriginEntriesAsIterator(LaborCorrectionDocument document);

    /**
     * Retrieves output origin entries that have been persisted for this document in an iterator. Implementations of this method may
     * choose to implement this method in a way that consumes very little memory.
     * 
     * @param document the document
     * @return the iterator
     * @throws RuntimeException several reasons, primarily relating to underlying persistence layer problems
     */
    public Iterator<LaborOriginEntry> retrievePersistedOutputOriginEntriesAsIterator(LaborCorrectionDocument document);

    /**
     * Returns true if the system is storing output origin entries for this class. Note that this does not mean that there's at
     * least one output origin entry record persisted for this document, but merely returns true if and only if the underlying
     * persistence mechanism has a record of this document's origin entries. See the docs for the implementations of this method for
     * more implementation specific details.
     * 
     * @param document
     * @return
     */
    public boolean areOutputOriginEntriesPersisted(LaborCorrectionDocument document);

    /**
     * Writes out the persisted output origin entries in an {@link OutputStream} in a flat file format\
     * 
     * @param document
     * @param out axn open and ready output stream
     * @throws IOException
     * @throws RuntimeException several reasons, including if the entries are not persisted
     */
    public void writePersistedOutputOriginEntriesToStream(LaborCorrectionDocument document, OutputStream out) throws IOException;

    /**
     * Saves the input and output origin entry groups for a document prior to saving the document
     * 
     * @param document
     * @param LaborCorrectionDocumentEntryMetadata
     */
    public void persistOriginEntryGroupsForDocumentSave(LaborCorrectionDocument document, CorrectionDocumentEntryMetadata correctionDocumentEntryMetadata);
    
    public String generateOutputOriginEntryFileName(String docId);
    
    public String createOutputFileForProcessing(String docId, java.util.Date today);
    
    public String getBatchFileDirectoryName();
    
    public String getLlcpDirectoryName();
    
    /**
     * Generate a text report for the given correction document
     * 
     * @param document LLCP document to report on
     */
    public void generateCorrectionReport(LaborCorrectionDocument document);
    
    public void aggregateCorrectionDocumentReports(GeneralLedgerCorrectionProcessDocument document);
}
