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
package org.kuali.kfs.module.purap.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.AttachmentTypeCodes;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.core.util.KeyLabelPair;

public class PaymentRequestAttachmentTypeValuesFinder extends KeyValuesBase {

    public List getKeyValues() {
        List keyValues = new ArrayList();
        
        keyValues.add(new KeyLabelPair("", ""));
        keyValues.add(new KeyLabelPair(AttachmentTypeCodes.ATTACHMENT_TYPE_OTHER, AttachmentTypeCodes.ATTACHMENT_TYPE_OTHER));
        keyValues.add(new KeyLabelPair(AttachmentTypeCodes.ATTACHMENT_TYPE_INVOICE_IMAGE, AttachmentTypeCodes.ATTACHMENT_TYPE_INVOICE_IMAGE));

        return keyValues;
    }

}
