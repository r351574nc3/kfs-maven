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
package org.kuali.kfs.sec.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Represents the assignment of one or more security definitions and one or more models to a principal
 */
public class SecurityPrincipal extends PersistableBusinessObjectBase {
    private String principalId;

    private Person securityPerson;

    private List<SecurityPrincipalDefinition> principalDefinitions;
    private List<SecurityModelMember> principalModels;

    public SecurityPrincipal() {
        super();

        principalDefinitions = new TypedArrayList(SecurityPrincipalDefinition.class);
        principalModels = new TypedArrayList(SecurityModelMember.class);
    }


    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }


    /**
     * Sets the principalId attribute value.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * Gets the securityPerson attribute.
     * 
     * @return Returns the securityPerson.
     */
    public Person getSecurityPerson() {
        securityPerson = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(principalId, securityPerson);
        return securityPerson;
    }


    /**
     * Sets the securityPerson attribute value.
     * 
     * @param securityPerson The securityPerson to set.
     */
    public void setSecurityPerson(Person securityPerson) {
        this.securityPerson = securityPerson;
    }


    /**
     * Gets the principalDefinitions attribute.
     * 
     * @return Returns the principalDefinitions.
     */
    public List<SecurityPrincipalDefinition> getPrincipalDefinitions() {
        return principalDefinitions;
    }


    /**
     * Sets the principalDefinitions attribute value.
     * 
     * @param principalDefinitions The principalDefinitions to set.
     */
    public void setPrincipalDefinitions(List<SecurityPrincipalDefinition> principalDefinitions) {
        this.principalDefinitions = principalDefinitions;
    }


    /**
     * Gets the principalModels attribute.
     * 
     * @return Returns the principalModels.
     */
    public List<SecurityModelMember> getPrincipalModels() {
        return principalModels;
    }


    /**
     * Sets the principalModels attribute value.
     * 
     * @param principalModels The principalModels to set.
     */
    public void setPrincipalModels(List<SecurityModelMember> principalModels) {
        this.principalModels = principalModels;
    }

    /**
     * Returns String of definition names assigned to principal
     */
    public String getPrincipalDefinitionNames() {
        String definitionNames = "";

        for (SecurityPrincipalDefinition definition : principalDefinitions) {
            if (StringUtils.isNotBlank(definitionNames)) {
                definitionNames += ", ";
            }
            definitionNames += definition.getSecurityDefinition().getName();
        }

        return definitionNames;
    }

    /**
     * Returns String of model names assigned to principal
     */
    public String getPrincipalModelNames() {
        String modelNames = "";

        for (SecurityModelMember modelMember : principalModels) {
            if (StringUtils.isNotBlank(modelNames)) {
                modelNames += ", ";
            }
            modelNames += modelMember.getSecurityModel().getName();
        }

        return modelNames;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.PRINCIPAL_ID, this.principalId);

        return m;
    }

}
