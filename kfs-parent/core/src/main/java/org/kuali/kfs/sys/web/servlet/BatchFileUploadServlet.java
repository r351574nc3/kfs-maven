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
package org.kuali.kfs.sys.web.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.ModuleConfiguration;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

public class BatchFileUploadServlet extends HttpServlet {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchFileUploadServlet.class);

    protected void checkAuthorization( HttpServletRequest request ) {
        boolean authorized = false;
//        String principalName = GlobalVariables.getUserSession().getPrincipalName();
        String principalName = KIMServiceLocator.getIdentityManagementService().getAuthenticatedPrincipalName(request);
        LOG.info("Logged In User: " + principalName);
        if ( StringUtils.isNotBlank(principalName) ) {
            KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName( principalName );
            if ( principal != null ) {
//        if ( StringUtils.isNotBlank(principalName) ) {
//            KimPrincipalInfo principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName);
//            if ( principal != null ) {
                String principalId = principal.getPrincipalId();
                AttributeSet permissionDetails = new AttributeSet();
                permissionDetails.put( KimAttributes.DOCUMENT_TYPE_NAME, "GLCP");
                authorized = KIMServiceLocator.getIdentityManagementService().isAuthorizedByTemplateName( principalId, KNSConstants.KUALI_RICE_SYSTEM_NAMESPACE, KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT, permissionDetails, null );
                if ( !authorized ) {
                    permissionDetails.put( KimAttributes.DOCUMENT_TYPE_NAME, "LLCP");
                    authorized = KIMServiceLocator.getIdentityManagementService().isAuthorizedByTemplateName( principalId, KNSConstants.KUALI_RICE_SYSTEM_NAMESPACE, KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT, permissionDetails, null );
                }
            }
        }
        if ( !authorized ) {
            throw new RuntimeException( "You must be able to initiate the GLCP or LLCP documents to use this page.  (Backdoor users are not recognized.)" );
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        checkAuthorization(request);
        request.setAttribute("directories", getBatchDirectories());
        request.getRequestDispatcher("/WEB-INF/jsp/batchFileUpload.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        checkAuthorization(request);
        ServletFileUpload upload = new ServletFileUpload();

        String destPath = null;
        String fileName = null;
        String tempDir = System.getProperty("java.io.tmpdir");
        // Parse the request
        try {
            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                fileName = item.getName();
                LOG.info("Processing Item: " + item.getFieldName());
                if (item.isFormField()) {
                    if (item.getFieldName().equals("uploadDir")) {
                        Reader str = new InputStreamReader(item.openStream());
                        StringWriter sw = new StringWriter();
                        char buf[] = new char[100];
                        int len;
                        while ((len = str.read(buf)) > 0) {
                            sw.write(buf, 0, len);
                        }
                        destPath = sw.toString();
                    }
                } else {
                    InputStream stream = item.openStream();
                    fileName = item.getName();
                    LOG.info("Uploading to Directory: " + tempDir );
                    // Process the input stream
                    FileOutputStream fos = new FileOutputStream(new File(tempDir, fileName));
                    BufferedOutputStream bos = new BufferedOutputStream(fos, 1024 * 1024);
                    byte buf[] = new byte[10240];
                    int len;
                    while ((len = stream.read(buf)) > 0) {
                        bos.write(buf, 0, len);
                    }
                    bos.close();
                    stream.close();
                }
            }
            LOG.info("Copying to Directory: " + destPath);
            
            if ( !getBatchDirectories().contains(destPath) ) {
                new File(tempDir, fileName).delete();
                throw new RuntimeException( "Illegal Attempt to upload to an unauthorized path: '" + destPath + "'" );
            }
            
            BufferedInputStream bis = new BufferedInputStream( new FileInputStream( new File(tempDir, fileName) ) ); 
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destPath, fileName)), 1024 * 1024);
            byte buf[] = new byte[10240];
            int len;
            while ((len = bis.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            bos.close();
            bis.close();
        }
        catch (FileUploadException ex) {
            LOG.error("Problem Uploading file", ex);
        }
        if (fileName != null) {
            request.setAttribute("message", "Successfully uploaded " + fileName + " to " + destPath);
        }
        else {
            request.setAttribute("message", "Upload Failed");
        }
        doGet(request, response);
    }

    protected List<String> getBatchDirectories() {
        List<String> dirs = new ArrayList<String>();
        for (ModuleService moduleService : SpringContext.getBean(KualiModuleService.class).getInstalledModuleServices()) {
            ModuleConfiguration moduleConfiguration = moduleService.getModuleConfiguration();
            if (moduleConfiguration instanceof FinancialSystemModuleConfiguration) {
                List<String> batchFileDirectories = ((FinancialSystemModuleConfiguration) moduleConfiguration).getBatchFileDirectories();
                for (String batchFileDirectoryName : batchFileDirectories) {
                    String directory = new File(batchFileDirectoryName).getAbsolutePath();
                    if ( new File( directory, "originEntry" ).isDirectory() ) {
                        dirs.add( new File( directory, "originEntry" ).getAbsolutePath() );
                    }
                }
            }
        }
        return dirs;
    }

}
