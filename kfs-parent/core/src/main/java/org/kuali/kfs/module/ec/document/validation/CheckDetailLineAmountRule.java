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
package org.kuali.kfs.module.ec.document.validation;

import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.rice.kns.rule.BusinessRule;

public interface CheckDetailLineAmountRule <E extends EffortCertificationDocument, D extends EffortCertificationDetail> extends BusinessRule {

    /**
     * validate the amounts on the given effort certification detail line before it can be processed
     * 
     * @param effortCertificationDocument the given effort certification document
     * @param effortCertificationDetail the given effort certification detail line
     * @return true if all rules are valid; otherwise, false
     */
    public boolean processCheckDetailLineAmountRules(E effortCertificationDocument, D effortCertificationDetail);
}
