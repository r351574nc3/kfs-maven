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
package org.kuali.kfs.module.external.kc.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.service.ServiceRegistry;

public abstract class KfsService extends Service {
    protected static final Logger LOG = Logger.getLogger(KfsService.class);
    
    protected KfsService(URL wsdlDocumentLocation, QName serviceName) {
        super(wsdlDocumentLocation, serviceName);
    }
 
    public static String getWebServiceServerName() {
       return  KNSServiceLocator.getKualiConfigurationService().getPropertyString(KFSConstants.KC_APPLICATION_URL_KEY);
    }
    
    protected static URL getWsdl(QName qname) throws MalformedURLException {
        URL url = null;
        String webServiceServer =  getWebServiceServerName();

        //FIXME KC needs to get the services exposed and working on the KSB for this to work
        if (webServiceServer == null) {
            // look for service on the KSB registry
            ServiceRegistry serviceRegistry = SpringContext.getBean(ServiceRegistry.class);
            List<ServiceInfo> wsdlServices = serviceRegistry.fetchActiveByName(qname);
            if (wsdlServices.size() > 0 ) {
                ServiceInfo serviceInfo = wsdlServices.get(0);
                String wsdlName = serviceInfo.getActualEndpointUrl() + "?wsdl";
                url = new URL(wsdlName);
            }
        } else {
            url  = new URL(webServiceServer + "/remoting/" +  qname.getLocalPart() + "?wsdl");
        }  
        return url;
    }
    
    public abstract URL getWsdl() throws MalformedURLException;
}
