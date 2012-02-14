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
package org.kuali.kfs.module.ar.document.validation.event;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;

/**
 * Defines methods for all events related to cash control details
 */
public interface CashControlDetailEvent extends KualiDocumentEvent {

    /**
     * This method returns the customer invoice related to the event
     * @return CustomerInvoiceDetail
     */
    public CashControlDetail getCashControlDetail();
}
