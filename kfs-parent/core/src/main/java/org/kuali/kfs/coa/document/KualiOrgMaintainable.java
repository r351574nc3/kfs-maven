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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

/**
 * This class overrides the getCoreSections method to provide specific field conversions for the postal code
 */
public class KualiOrgMaintainable extends FinancialSystemMaintainable {

    private static final long serialVersionUID = -3182120468758958991L;

    public static final String KUALI_ORG_SECTION = "Edit Organization Code";

    /**
     * Provides special field conversions for the Org.organizationZipCode
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#getCoreSections(org.kuali.rice.kns.maintenance.Maintainable)
     */
    @Override
    public List getCoreSections(MaintenanceDocument document, Maintainable oldMaintainable) {

        boolean fieldFound = false;
        boolean sectionFound = false;

        String orgPostalCodeFieldName = KFSPropertyConstants.ORGANIZATION_ZIP_CODE;

        // walk the sections
        List sections = super.getCoreSections(document, oldMaintainable);
        for (Iterator sectionIterator = sections.iterator(); sectionIterator.hasNext();) {
            Section section = (Section) sectionIterator.next();

            // if this is the section we're looking for
            if (section.getSectionTitle().equalsIgnoreCase(KUALI_ORG_SECTION)) {

                // mark that we found the section
                sectionFound = true;

                // walk the rows
                List rows = section.getRows();
                for (Iterator rowIterator = rows.iterator(); rowIterator.hasNext();) {
                    Row row = (Row) rowIterator.next();

                    // walk the fields
                    List fields = row.getFields();
                    for (Iterator fieldIterator = fields.iterator(); fieldIterator.hasNext();) {
                        Field field = (Field) fieldIterator.next();

                        // if this is the field we're looking for ...
                        if (field.getPropertyName().equalsIgnoreCase(orgPostalCodeFieldName)) {

                            // mark that we've found the field
                            fieldFound = true;

                            // build the fieldConversions for the UserID field lookup
                            Map fieldConversions = new HashMap();
                            fieldConversions.put(KFSPropertyConstants.POSTAL_CODE, KFSPropertyConstants.ORGANIZATION_ZIP_CODE);
                            fieldConversions.put(KFSPropertyConstants.POSTAL_STATE_CODE, KFSPropertyConstants.ORGANIZATION_STATE_CODE);
                            fieldConversions.put(KFSPropertyConstants.POSTAL_CITY_NAME, KFSPropertyConstants.ORGANIZATION_CITY_NAME);


                            // add the fieldConversions, lookupParameters and the lookup class
                            field.setFieldConversions(fieldConversions);
                            // field.setLookupParameters(lookupParameters);
                            // field.setQuickFinderClassNameImpl(Person.class.getName());
                        }
                    }
                }
            }

        }

        // if the section no longer exists, fail loudly
        if (!sectionFound) {
            throw new RuntimeException("There is no longer a section titled '" + KUALI_ORG_SECTION + "'. " + "As a result, the lookup setup will not work as expected and the maintenance document " + "will be broken.  The correct name needs to be set in the Constant in this class.");
        }
        // if the field was not found, fail loudly
        else if (!fieldFound) {
            throw new RuntimeException("There is no longer a field titled '" + KFSPropertyConstants.ORGANIZATION_ZIP_CODE + "'. " + "As a result, the lookup setup will not work as expected and the maintenance document " + "will be broken.  The correct name needs to be set in the KFSPropertyConstants class.");
        }

        return sections;
    }
}

