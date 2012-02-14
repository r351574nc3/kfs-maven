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

package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Represents a relationship between a {@link Proposal} and a {@link ProjectDirector}.
 */
public class ProposalProjectDirector extends PersistableBusinessObjectBase implements Primaryable, CGProjectDirector, Inactivateable {

    private String principalId;
    private Long proposalNumber;
    private boolean proposalPrimaryProjectDirectorIndicator;
    private String proposalProjectDirectorProjectTitle;
    private boolean active = true;

    private Person projectDirector;

    
    private final String userLookupRoleNamespaceCode = KFSConstants.ParameterNamespaces.KFS;
    private final String userLookupRoleName = KFSConstants.SysKimConstants.CONTRACTS_AND_GRANTS_PROJECT_DIRECTOR;
    
    /**
     * Default constructor.
     */
    public ProposalProjectDirector() {
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#getPrincipalId()
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#setPrincipalId(java.lang.String)
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#getProposalNumber()
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#setProposalNumber(java.lang.Long)
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /**
     * Gets the proposalPrimaryProjectDirectorIndicator attribute.
     * 
     * @return Returns the proposalPrimaryProjectDirectorIndicator
     */
    public boolean isProposalPrimaryProjectDirectorIndicator() {
        return proposalPrimaryProjectDirectorIndicator;
    }

    /**
     * @see Primaryable#isPrimary()
     */
    public boolean isPrimary() {
        return isProposalPrimaryProjectDirectorIndicator();
    }

    /**
     * Sets the proposalPrimaryProjectDirectorIndicator attribute.
     * 
     * @param proposalPrimaryProjectDirectorIndicator The proposalPrimaryProjectDirectorIndicator to set.
     */
    public void setProposalPrimaryProjectDirectorIndicator(boolean proposalPrimaryProjectDirectorIndicator) {
        this.proposalPrimaryProjectDirectorIndicator = proposalPrimaryProjectDirectorIndicator;
    }


    /**
     * Gets the proposalProjectDirectorProjectTitle attribute.
     * 
     * @return Returns the proposalProjectDirectorProjectTitle
     */
    public String getProposalProjectDirectorProjectTitle() {
        return proposalProjectDirectorProjectTitle;
    }

    /**
     * Sets the proposalProjectDirectorProjectTitle attribute.
     * 
     * @param proposalProjectDirectorProjectTitle The proposalProjectDirectorProjectTitle to set.
     */
    public void setProposalProjectDirectorProjectTitle(String proposalProjectDirectorProjectTitle) {
        this.proposalProjectDirectorProjectTitle = proposalProjectDirectorProjectTitle;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#getProjectDirector()
     */
    public Person getProjectDirector() {
        if (principalId != null) {
            projectDirector = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(principalId, projectDirector);
        }
        return projectDirector;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#setProjectDirector(org.kuali.kfs.module.cg.businessobject.ProjectDirector)
     */
    public void setProjectDirector(Person projectDirector) {
        this.projectDirector = projectDirector;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        return m;
    }

    /**
     * This can be displayed by Proposal.xml lookup results.
     * 
     * @see Object#toString()
     */
    @Override
    public String toString() {
        // todo: get "nonexistent", "primary", and "secondary" from ApplicationResources.properties via KFSKeyConstants?
        String name = ObjectUtils.isNull(getProjectDirector()) ? "nonexistent" : getProjectDirector().getName();
        String title = getProposalProjectDirectorProjectTitle() == null ? "" : " " + getProposalProjectDirectorProjectTitle();
        return name + " " + (isProposalPrimaryProjectDirectorIndicator() ? "primary" : "secondary") + title;
    }

    /**
     * Gets the userLookupRoleNamespaceCode attribute. 
     * @return Returns the userLookupRoleNamespaceCode.
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    /**
     * Gets the userLookupRoleName attribute. 
     * @return Returns the userLookupRoleName.
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }
}

