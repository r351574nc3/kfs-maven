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

import org.kuali.kfs.module.cam.businessobject.AssetComponent;

public interface AssetComponentService {
    /**
     * Finds out the maximum sequence number available for an asset component with respect to an asset
     * 
     * @param assetComponent Asset Component class
     * @return Maximum value of component sequence
     */
    Integer getMaxSequenceNumber(AssetComponent assetComponent);
}
