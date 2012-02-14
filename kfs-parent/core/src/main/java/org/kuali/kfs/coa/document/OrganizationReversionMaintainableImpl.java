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
package org.kuali.kfs.coa.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.coa.businessobject.OrganizationReversionDetail;
import org.kuali.kfs.coa.service.OrganizationReversionDetailTrickleDownInactivationService;
import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

/**
 * This class provides some specific functionality for the {@link OrganizationReversion} maintenance document inner class for doing
 * comparisons on {@link OrganizationReversionCategory} populateBusinessObject setBusinessObject - pre-populate the static list of
 * details with each category isRelationshipRefreshable - makes sure that {@code organizationReversionGlobalDetails} isn't wiped out
 * accidentally
 */
public class OrganizationReversionMaintainableImpl extends FinancialSystemMaintainable {
    private transient OrganizationReversionService organizationReversionService;

    /**
     * This comparator is used internally for sorting the list of categories
     */
    private class categoryComparator implements Comparator<OrganizationReversionDetail> {

        public int compare(OrganizationReversionDetail detail0, OrganizationReversionDetail detail1) {

            OrganizationReversionCategory category0 = detail0.getOrganizationReversionCategory();
            OrganizationReversionCategory category1 = detail1.getOrganizationReversionCategory();

            String code0 = category0.getOrganizationReversionCategoryCode();
            String code1 = category1.getOrganizationReversionCategoryCode();

            return code0.compareTo(code1);
        }

    }

    /**
     * pre-populate the static list of details with each category
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#setBusinessObject(org.kuali.rice.kns.bo.BusinessObject)
     */
    public void setBusinessObject(PersistableBusinessObject businessObject) {

        OrganizationReversionService organizationReversionService = SpringContext.getBean(OrganizationReversionService.class);
        OrganizationReversion organizationReversion = (OrganizationReversion) businessObject;
        List<OrganizationReversionDetail> details = organizationReversion.getOrganizationReversionDetail();

        if (details == null) {
            details = new TypedArrayList(OrganizationReversionDetail.class);
            organizationReversion.setOrganizationReversionDetail(details);
        }

        if (details.size() == 0) {

            Collection<OrganizationReversionCategory> categories = organizationReversionService.getCategoryList();

            for (OrganizationReversionCategory category : categories) {
                if (category.isActive()) {
                    OrganizationReversionDetail detail = new OrganizationReversionDetail();
                    detail.setOrganizationReversionCategoryCode(category.getOrganizationReversionCategoryCode());
                    detail.setOrganizationReversionCategory(category);
                    details.add(detail);
                }
            }

            Collections.sort(details, new categoryComparator());

        }

        super.setBusinessObject(businessObject);
    }

    /**
     * A method that prevents lookups from refreshing the Organization Reversion Detail list (because, if it is refreshed before a
     * save...it ends up destroying the list).
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#isRelationshipRefreshable(java.lang.Class, java.lang.String)
     */
    @Override
    protected boolean isRelationshipRefreshable(Class boClass, String relationshipName) {
        if (relationshipName.equals("organizationReversionDetail")) {
            return false;
        }
        else {
            return super.isRelationshipRefreshable(boClass, relationshipName);
        }
    }
    
    /**
     * Determines if this maint doc is inactivating an organization reversion
     * @return true if the document is inactivating an active organization reversion, false otherwise
     */
    protected boolean isInactivatingOrganizationReversion() {
        // the account has to be closed on the new side when editing in order for it to be possible that we are closing the account
        if (KNSConstants.MAINTENANCE_EDIT_ACTION.equals(getMaintenanceAction()) && !((OrganizationReversion) getBusinessObject()).isActive()) {
            OrganizationReversion existingOrganizationReversionFromDB = retrieveExistingOrganizationReversion();
            if (ObjectUtils.isNotNull(existingOrganizationReversionFromDB)) {
                // now see if the original account was not closed, in which case, we are closing the account
                if (existingOrganizationReversionFromDB.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Determines if this maint doc is activating an organization reversion
     * @return true if the document is activating an inactive organization reversion, false otherwise
     */
    protected boolean isActivatingOrganizationReversion() {
        // the account has to be closed on the new side when editing in order for it to be possible that we are closing the account
        if (KNSConstants.MAINTENANCE_EDIT_ACTION.equals(getMaintenanceAction()) && ((OrganizationReversion) getBusinessObject()).isActive()) {
            OrganizationReversion existingOrganizationReversionFromDB = retrieveExistingOrganizationReversion();
            if (ObjectUtils.isNotNull(existingOrganizationReversionFromDB)) {
                // now see if the original account was not closed, in which case, we are closing the account
                if (!existingOrganizationReversionFromDB.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Grabs the old version of this org reversion from the database
     * @return the old version of this organization reversion
     */
    protected OrganizationReversion retrieveExistingOrganizationReversion() {
        final OrganizationReversion orgRev = (OrganizationReversion)getBusinessObject();
        final OrganizationReversion oldOrgRev = SpringContext.getBean(OrganizationReversionService.class).getByPrimaryId(orgRev.getUniversityFiscalYear(), orgRev.getChartOfAccountsCode(), orgRev.getOrganizationCode());
        return oldOrgRev;
    }

    /**
     * Overridden to trickle down inactivation or activation to details
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        final boolean isActivatingOrgReversion = isActivatingOrganizationReversion();
        final boolean isInactivatingOrgReversion = isInactivatingOrganizationReversion();
        
        super.saveBusinessObject();
        
        if (isActivatingOrgReversion) {
            SpringContext.getBean(OrganizationReversionDetailTrickleDownInactivationService.class).trickleDownActiveOrganizationReversionDetails((OrganizationReversion)getBusinessObject(), documentNumber);
        } else if (isInactivatingOrgReversion) {
            SpringContext.getBean(OrganizationReversionDetailTrickleDownInactivationService.class).trickleDownInactiveOrganizationReversionDetails((OrganizationReversion)getBusinessObject(), documentNumber);
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getSections(org.kuali.rice.kns.document.MaintenanceDocument, org.kuali.rice.kns.maintenance.Maintainable)
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, oldMaintainable);
        if (organizationReversionService == null) {
            organizationReversionService = SpringContext.getBean(OrganizationReversionService.class);
        }
        for (Section section : sections) {
            for (Row row : section.getRows()) {
                List<Field> updatedFields = new ArrayList<Field>();
                for (Field field : row.getFields()) {
                    if (shouldIncludeField(field)) {
                        updatedFields.add(field);
                    }
                }
                row.setFields(updatedFields);
            }
        }
        return sections;
    }
    
    /**
     * Determines if the given field should be included in the updated row, once we take out inactive categories
     * @param field the field to check
     * @return true if the field should be included (ie, it doesn't describe an organization reversion with an inactive category); false otherwise
     */
    protected boolean shouldIncludeField(Field field) {
        boolean includeField = true;
        if (field.getContainerRows() != null) {
            for (Row containerRow : field.getContainerRows()) {
                for (Field containedField : containerRow.getFields()) {
                    if (containedField.getPropertyName().matches("organizationReversionDetail\\[\\d+\\]\\.organizationReversionCategory\\.organizationReversionCategoryName")) {
                        final String categoryValue = containedField.getPropertyValue();
                        includeField = organizationReversionService.isCategoryActiveByName(categoryValue);
                    }
                }
            }
        }
        return includeField;
    }
    
}
