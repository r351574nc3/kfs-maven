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
package org.kuali.kfs.module.ar.identity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;

public class InvoiceRecurrenceDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

    @Override
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        List<RoleMembershipInfo> members = new ArrayList<RoleMembershipInfo>(1);
        if ((qualification != null && !qualification.isEmpty()) && StringUtils.isNotBlank(qualification.get(ArPropertyConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_INITIATOR_USER_ID))) {
            members.add(new RoleMembershipInfo(null, null, ArPropertyConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_INITIATOR_USER_ID, Role.PRINCIPAL_MEMBER_TYPE, null));
        }
        return members;
    }
}
