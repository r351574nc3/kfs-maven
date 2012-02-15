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
package org.kuali.kfs.module.endow.document.service;

import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.ClassCode;

public interface ClassCodeService {

    /**
     * Gets a class code by primary key.
     * 
     * @param code
     * @return a Class Code
     */
    public ClassCode getByPrimaryKey(String code);

    /**
     * Gets all class codes with accrual method: Automated Cash Management, Time Deposits, Treasury Notes and Bonds, Dividends
     */
    public Collection<ClassCode> getClassCodesForAccrualProcessing();
}
