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
package org.kuali.kfs.module.cam.document.service;

import org.kuali.kfs.module.cam.document.AssetTransferDocument;

public interface AssetTransferService {
    /**
     * This method is called when the work flow document is reached its final approval
     * <ol>
     * <li>Gets the latest asset details from DB</li>
     * <li>Save asset owner data</li>
     * <li>Save location changes </li>
     * <li>Save organization changes</li>
     * <li>Create offset payments</li>
     * <li>Create new payments</li>
     * <li>Update original payments</li>
     * </ol>
     */
    void saveApprovedChanges(AssetTransferDocument document);

    /**
     * Creates GL Postables using the source plant account number and target plant account number
     */
    void createGLPostables(AssetTransferDocument document);


}
