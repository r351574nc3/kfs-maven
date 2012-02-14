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
package org.kuali.kfs.module.external.kc.webService;
/*
 * 
 */

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.service.KfsService;

/**
 * This class was generated by Apache CXF 2.2.10
 * Wed Mar 02 08:02:23 HST 2011
 * Generated source version: 2.2.10
 * 
 */


@WebServiceClient(name = KcConstants.Cfda.SOAP_SERVICE_NAME, 
                  wsdlLocation = "http://test.kc.kuali.org:80/kc-trunk/remoting/cfdaNumberSoapService?wsdl",
                  targetNamespace = KcConstants.KC_NAMESPACE_URI) 
public class CfdaNumberSoapService extends KfsService {

     public final static QName CfdaNumberServicePort = new QName(KcConstants.KC_NAMESPACE_URI, KcConstants.Cfda.SERVICE_PORT);
     static {
         try {
            getWsdl(KcConstants.Cfda.SERVICE); 
          } catch (MalformedURLException e) {
              LOG.warn("Can not initialize the wsdl");
          }
     }

    public CfdaNumberSoapService() throws MalformedURLException {
        super(getWsdl(KcConstants.Cfda.SERVICE), KcConstants.Cfda.SERVICE);
    }
    

    /**
     * 
     * @return
     *     returns CfdaNumberService
     */
    @WebEndpoint(name =  KcConstants.Cfda.SERVICE_PORT)
    public CfdaNumberService getCfdaNumberServicePort() {
        return super.getPort(CfdaNumberServicePort, CfdaNumberService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CfdaNumberService
     */
    @WebEndpoint(name =  KcConstants.Cfda.SERVICE_PORT)
    public CfdaNumberService getCfdaNumberServicePort(WebServiceFeature... features) {
        return super.getPort(CfdaNumberServicePort, CfdaNumberService.class, features);
    }

    public URL getWsdl() throws MalformedURLException {
        return super.getWsdl(KcConstants.Cfda.SERVICE);
    }

}
