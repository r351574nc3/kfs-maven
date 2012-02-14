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
package org.kuali.kfs.sys.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.kuali.rice.kns.web.listener.JstlConstantsInitListener;
import org.kuali.rice.ksb.messaging.MessageFetcher;
import org.kuali.rice.ksb.service.KSBServiceLocator;

public class WebApplicationInitListener extends JstlConstantsInitListener implements ServletContextListener {
    private static final String JSTL_CONSTANTS_CLASSNAMES_KEY = "jstl.constants.classnames";
    private static final String JSTL_CONSTANTS_MAIN_CLASS = "jstl.constants.main.class";
    private static final String JSTL_MAIN_CLASS_CONTEXT_NAME = "Constants";
    private static Logger LOG;

    @SuppressWarnings("unchecked")
    public void contextInitialized(ServletContextEvent sce) {
        Log4jConfigurer.configureLogging(true);
        LOG = Logger.getLogger(WebApplicationInitListener.class);
        SpringContext.initializeApplicationContext();
        MessageFetcher messageFetcher = new MessageFetcher((Integer)null); 
        KSBServiceLocator.getThreadPool().execute(messageFetcher); 
        for (String jstlConstantsClassname : PropertyLoadingFactoryBean.getBaseListProperty(JSTL_CONSTANTS_CLASSNAMES_KEY)) {
            try {
                Class jstlConstantsClass = Class.forName(jstlConstantsClassname);
                Object jstlConstantsObj = jstlConstantsClass.newInstance();
                sce.getServletContext().setAttribute(jstlConstantsClass.getSimpleName(), jstlConstantsObj);
                if (jstlConstantsClassname.equals(JSTL_CONSTANTS_MAIN_CLASS)) {
                    sce.getServletContext().setAttribute(JSTL_MAIN_CLASS_CONTEXT_NAME, jstlConstantsObj);
                }
            }
            catch (Exception e) {
                LOG.warn("Unable to load jstl constants class: " + jstlConstantsClassname, e);
            }
        }
        super.contextInitialized(sce);
        LOG.info("Finished web application context initialization");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("Started web application context destruction");
        try {
            SpringContext.close();
        } catch ( Exception ex ) {
            LOG.error( "Unable to close down Spring Context." );
        }
        super.contextDestroyed(sce);
    }
}
