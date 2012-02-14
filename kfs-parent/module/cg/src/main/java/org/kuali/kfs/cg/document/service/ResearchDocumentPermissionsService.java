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
package org.kuali.kfs.module.cg.document.service;

import java.util.List;

import org.kuali.kfs.module.cg.businessobject.ResearchAdhocPermissionType;

/**
 * This interface defines methods that a BudgetPermissionsService must provide
 */
public interface ResearchDocumentPermissionsService {


    /**
     * Check whether given user is in the org routing hierarchy.
     * 
     * @param String orgXml
     * @param String uuid
     * @return boolean
     */
    public boolean isUserInOrgHierarchy(String orgXml, String documentType, String uuid);
}

