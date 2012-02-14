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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;

public class ContractsAndGrantsResponsibilityRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    public static final String AWARD_ROUTE_NODE_NAME = "Award";

    @Override
    public List<RoleMembershipInfo> doRoleQualifiersMatchQualification(AttributeSet qualification, List<RoleMembershipInfo> roleMemberList) {
        // special handling for where the code is being called for a particular route node
        // and the code is blank
        if ( qualification != null ) {
            if ( StringUtils.equals( qualification.get(KimAttributes.ROUTE_NODE_NAME), AWARD_ROUTE_NODE_NAME) ) {
                if ( StringUtils.isBlank(qualification.get(KfsKimAttributes.CONTRACTS_AND_GRANTS_ACCOUNT_RESPONSIBILITY_ID)) ) {
                    return new ArrayList<RoleMembershipInfo>(0);
                }
            }
        }
        // otherwise, default to the normal behavior
        return super.doRoleQualifiersMatchQualification(qualification, roleMemberList);
    }
}
