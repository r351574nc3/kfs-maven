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
package org.kuali.kfs.module.cg.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.cg.businessobject.ProposalPurpose;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * Gets a custom-formatted list of {@link ProposalPurpose} values.
 */
public class ProposalPurposeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        Collection<ProposalPurpose> codes = SpringContext.getBean(KeyValuesService.class).findAll(ProposalPurpose.class);

        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
        labels.add(new KeyLabelPair("", ""));

        for (ProposalPurpose proposalPurpose : codes) {
            if (proposalPurpose.isActive()) {
                labels.add(new KeyLabelPair(proposalPurpose.getProposalPurposeCode(), proposalPurpose.getProposalPurposeCode() + "-" + proposalPurpose.getProposalPurposeDescription()));
            }
        }

        return labels;
    }
}
