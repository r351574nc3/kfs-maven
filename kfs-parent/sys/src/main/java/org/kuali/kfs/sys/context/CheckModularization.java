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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.ServerImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.InactivationBlockingDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.datadictionary.TransactionalDocumentEntry;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.springframework.core.io.DefaultResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uk.ltd.getahead.dwr.impl.DTDEntityResolver;
import uk.ltd.getahead.dwr.util.LogErrorHandler;

public class CheckModularization {

    private static final Map<String, String> OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX = new HashMap<String, String>();
    static {
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-AR", "ar");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-BC", "bc");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-CAB", "cab");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-CAM", "cam");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-CG", "cg");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-EC", "ec");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-LD", "ld");
        OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-PURAP", "purap");
    }
    
    private static final Map<String, String> OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES =
            new TreeBidiMap(OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX).inverseBidiMap();
    
    private static final Map<String, String> SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX = new HashMap<String, String>();
    static {
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-COA", "coa");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-FP", "fp");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-GL", "gl");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-PDP", "pdp");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-SYS", "sys");
        SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.put("KFS-VND", "vnd");
    }
    
    private static Map<String,List<String>> PACKAGE_PREFIXES_BY_MODULE = new HashMap<String, List<String>>();
    private static Map<String,List<String>> OJB_FILES_BY_MODULE = new HashMap<String, List<String>>();
    private static Map<String,List<String>> DWR_FILES_BY_MODULE = new HashMap<String, List<String>>();
    
    private static String MODULE_SPRING_PATH_PATTERN = "org/kuali/kfs/module/{0}/spring-{0}.xml";
    
    /*
     * open up classpath:configuration.properties - get locations of spring files?
     * alter the spring.source.files property and re-save
     * hold original version and restore?
     * 
     * use location of config.properties as the class root for scanning source files?
     * How do you test .class files for symbols?
     */
    static String coreSpringFiles; 
    static String coreSpringTestFiles; 
    static File configPropertiesFile;
    public static void main(String[] args) {
        CheckModularization mt = new CheckModularization();
        try {
            Properties configProps = new Properties();
            URL propLocation = CheckModularization.class.getClassLoader().getResource("configuration.properties" );
            System.out.println( "URL: " + propLocation );
            System.out.println( "Path: " + propLocation.getPath() );
            configPropertiesFile = new File( propLocation.getPath() );
            configProps.load( CheckModularization.class.getClassLoader().getResourceAsStream("configuration.properties") );
            coreSpringFiles = configProps.getProperty("core.spring.source.files");
            coreSpringTestFiles = configProps.getProperty("core.spring.test.files");

            LogUtils.getL7dLogger(ServerImpl.class).setLevel(Level.SEVERE);
            try {
                SpringContext.initializeTestApplicationContext();                
                KualiModuleService kualiModuleService = (KualiModuleService)SpringContext.getBean(KualiModuleService.class);
                
                for ( ModuleService module : kualiModuleService.getInstalledModuleServices() ) {
                    PACKAGE_PREFIXES_BY_MODULE.put(module.getModuleConfiguration().getNamespaceCode(), module.getModuleConfiguration().getPackagePrefixes() );
                    OJB_FILES_BY_MODULE.put(module.getModuleConfiguration().getNamespaceCode(), module.getModuleConfiguration().getDatabaseRepositoryFilePaths() );
                    DWR_FILES_BY_MODULE.put(module.getModuleConfiguration().getNamespaceCode(), module.getModuleConfiguration().getScriptConfigurationFilePaths() );
                }
                
            } catch ( Exception ex ) {
                ex.printStackTrace();
            } finally {
                stopSpringContext();
            }
            
            // bring up Spring once to get all the configuration information, store by namespace code
            // list of core namespaces, all others must be independent
            
            // test class references
            boolean testsPassed = true;
            System.out.println( "**************************************************");
            System.out.println( "Testing Spring Startup");
            System.out.println( "**************************************************");
            if ( !mt.testSpring() ) {
                System.out.println( "FAILED" );
                testsPassed = false;
            } else {
                System.out.println( "SUCCEEDED" );
            }
            System.out.println( "**************************************************");
            System.out.println( "Testing OJB References");
            System.out.println( "**************************************************");
            if ( !mt.testOjb() ) {
                System.out.println( "FAILED" );
                testsPassed = false;
            } else {
                System.out.println( "SUCCEEDED" );
            }
            System.out.println( "**************************************************");
            System.out.println( "Testing DWR References");
            System.out.println( "**************************************************");
            if ( !mt.testDwr() ) {
                System.out.println( "FAILED" );
                testsPassed = false;
            } else {
                System.out.println( "SUCCEEDED" );
            }
            System.out.println( "**************************************************");
            System.out.println( "Testing DD Class References");
            System.out.println( "**************************************************");
            if ( !mt.testDd() ) {
                System.out.println( "FAILED" );
                testsPassed = false;
            } else {
                System.out.println( "SUCCEEDED" );
            }
//            testsPassed &= mt.testDd();
            
            if ( !testsPassed ) {
                System.exit(1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
    
    
    protected String buildOptionalModuleSpringFileList( ModuleGroup moduleGroup ) {
        StringBuffer sb = new StringBuffer();
        sb.append( MessageFormat.format(MODULE_SPRING_PATH_PATTERN, OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.get( moduleGroup.namespaceCode ) ) );
        for ( String depMod : moduleGroup.optionalModuleDependencyNamespaceCodes ) {
            sb.append( ',' );            
            sb.append( MessageFormat.format(MODULE_SPRING_PATH_PATTERN, OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.get( depMod ) ) );
        }
        return sb.toString();
    }

    StringBuffer dwrErrorMessage = new StringBuffer("The following optional modules have interdependencies in DWR configuration:");
    boolean dwrTestSucceeded = true;
    StringBuffer ddErrorMessage = new StringBuffer("The following optional modules have interdependencies in DD class references:");
    boolean ddTestSucceeded = true;
    
    public boolean testSpring() throws Exception {
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer();
        // test the core modules alone
        System.out.println( "\n\n------>Testing for core modules:");
        System.out.println( "------>Using Base Configuration:   " + coreSpringFiles );
        testSucceeded &= testOptionalModuleSpringConfiguration(new ModuleGroup(KFSConstants.ParameterNamespaces.KFS), coreSpringFiles, errorMessage);
        if ( !testSucceeded ) {
            errorMessage.insert( 0, "The Core modules have dependencies on the optional modules:\n" );
        }
        
        errorMessage.append( "The following optional modules have interdependencies in Spring configuration:\n");
        List<ModuleGroup> optionalModuleGroups = retrieveOptionalModuleGroups();
        for (ModuleGroup optionalModuleGroup : optionalModuleGroups) {
//            if ( !optionalModuleGroup.namespaceCode.equals( "KFS-AR" ) ) continue;
            System.out.println( "\n\n------>Testing for optional module group: " + optionalModuleGroup );
            System.out.println( "------>Using Base Configuration:   " + coreSpringFiles );
            String moduleConfigFiles = buildOptionalModuleSpringFileList(optionalModuleGroup);
            System.out.println( "------>Module configuration files: " + moduleConfigFiles );
            testSucceeded &= testOptionalModuleSpringConfiguration(optionalModuleGroup, coreSpringFiles+","+moduleConfigFiles, errorMessage);
        }
        if (!testSucceeded) {
            System.out.print(errorMessage.append("\n\n").toString());
        }
        return testSucceeded;
    }

    protected boolean testOptionalModuleSpringConfiguration(ModuleGroup optionalModuleGroup, String springConfigFiles, StringBuffer errorMessage) {
        try {
            // update the configuration.properties file
            Properties configProps = new Properties();
            configProps.load( new FileInputStream( configPropertiesFile ) );
            configProps.setProperty( "spring.source.files", springConfigFiles );
            configProps.setProperty( "spring.test.files", coreSpringTestFiles );
            configProps.setProperty( "validate.ebo.references", "false" );
            configProps.store( new FileOutputStream( configPropertiesFile ), "Testing Module: " + optionalModuleGroup.namespaceCode );
            configProps.load( new FileInputStream( configPropertiesFile ) );
            SpringContext.initializeTestApplicationContext();
            dwrTestSucceeded &= testDwrModuleConfiguration(optionalModuleGroup, dwrErrorMessage);
            ddTestSucceeded &= testDdModuleConfiguration(optionalModuleGroup, ddErrorMessage);
            return true;
        } catch (Exception e) {
            errorMessage.append("\n\n").append(optionalModuleGroup.namespaceCode).append("\n\t").append(e.getMessage());
            dwrErrorMessage.append( "\n\n" + optionalModuleGroup.namespaceCode + " : Unable to test due to Spring test failure." );
            ddErrorMessage.append( "\n\n" + optionalModuleGroup.namespaceCode + " : Unable to test due to Spring test failure." );
            ddTestSucceeded &= false;
            dwrTestSucceeded &= false;
            e.printStackTrace();
            return false;
        }
        finally {
            stopSpringContext();
        }
    }

    public boolean testOjb() throws Exception {
        boolean testSucceeded = true;
        StringBuffer errorMessage = new StringBuffer("The following optional modules have interdependencies in OJB configuration:");
        List<ModuleGroup> allModuleGroups = retrieveModuleGroups();
        for (ModuleGroup moduleGroup : allModuleGroups) {
            testSucceeded = testSucceeded & testOptionalModuleOjbConfiguration(moduleGroup, errorMessage);
        }
        if (!testSucceeded) {
            System.out.print(errorMessage.append("\n\n").toString());
        }
        return testSucceeded;
    }

    protected boolean testOptionalModuleOjbConfiguration(ModuleGroup moduleGroup, StringBuffer errorMessage) throws FileNotFoundException {
        boolean testSucceeded = true;
        for (String referencedNamespaceCode : OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.keySet()) {
            if (!(moduleGroup.namespaceCode.equals(referencedNamespaceCode) || moduleGroup.optionalModuleDependencyNamespaceCodes.contains(referencedNamespaceCode))) {
                if ( OJB_FILES_BY_MODULE.get(moduleGroup.namespaceCode).isEmpty() ) continue;
                String firstDatabaseRepositoryFilePath = OJB_FILES_BY_MODULE.get(moduleGroup.namespaceCode).iterator().next();
                // the first database repository file path is typically the file that comes shipped with KFS.  If institutions override it, this unit test will not test them
                Scanner scanner = new Scanner(new File("work/src/" + firstDatabaseRepositoryFilePath));
                int count = 0;
                while (scanner.hasNext()) {
                    String token = scanner.next();
                    String firstPackagePrefix = PACKAGE_PREFIXES_BY_MODULE.get( referencedNamespaceCode ).iterator().next();
                    // A module may be responsible for many packages, but the first one should be the KFS built-in package that is *not* the module's integration package
                    if (token.contains(firstPackagePrefix)) {
                        count++;
                    }
                }
                if (count > 0) {
                    if (testSucceeded) {
                        testSucceeded = false;
                        errorMessage.append("\n").append(moduleGroup.namespaceCode).append(": ");
                    }
                    else {
                        errorMessage.append(", ");
                    }
                    errorMessage.append(count).append(" references to ").append(referencedNamespaceCode);
                }
            }
        }
        return testSucceeded;
    }
    
    protected boolean testDwr() throws Exception {
        if (!dwrTestSucceeded) {
            System.out.print(dwrErrorMessage.append("\n\n").toString());
        }
        return dwrTestSucceeded;
    }
    
    protected boolean testDwrModuleConfiguration(ModuleGroup moduleGroup, StringBuffer errorMessage) throws Exception {
        List<String> dwrFiles = DWR_FILES_BY_MODULE.get(moduleGroup.namespaceCode);
        boolean testSucceeded = true;
        if (dwrFiles != null && dwrFiles.size() > 0) {
            // the DWR file delivered with KFS (i.e. the base) should be the first element of the list
            String baseDwrFileName = dwrFiles.get(0);
            Document dwrDocument = generateDwrConfigDocument(baseDwrFileName);
            testSucceeded = testDwrModuleConfiguration(baseDwrFileName, dwrDocument, moduleGroup, errorMessage);
        }
        return testSucceeded;
    }
    
    protected boolean testDwrModuleConfiguration(String dwrFileName, Document dwrDocument, ModuleGroup moduleGroup, StringBuffer errorMessage) throws Exception {
       boolean beanClassNamesOK = testDwrBeanClassNames(dwrFileName, dwrDocument, moduleGroup, errorMessage);
       boolean springServicesOK = testDwrSpringServices(dwrFileName, dwrDocument, moduleGroup, errorMessage);
       return beanClassNamesOK && springServicesOK;
    }
    
    protected boolean testDwrBeanClassNames(String dwrFileName, Document dwrDocument, ModuleGroup moduleGroup, StringBuffer errorMessage) {
        boolean testSucceeded = true;
        List<String> dwrBeanClassNames = retrieveDwrBeanClassNames(dwrDocument);
        for (String referencedNamespaceCode : OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.keySet()) {
            if (!(referencedNamespaceCode.equals(moduleGroup.namespaceCode) || moduleGroup.optionalModuleDependencyNamespaceCodes.contains(referencedNamespaceCode))) {
                String firstPackagePrefix = PACKAGE_PREFIXES_BY_MODULE.get(referencedNamespaceCode).iterator().next();
                // A module may be responsible for many packages, but the first one should be the KFS built-in package that is *not* the module's integration package
                if (!firstPackagePrefix.endsWith(".")) {
                    firstPackagePrefix = firstPackagePrefix + ".";
                }
                int count = 0;
                for (String className : dwrBeanClassNames) {
                    if (className.contains(firstPackagePrefix)) {
                        count++;
                    }
                }
                if (count > 0) {
                    testSucceeded = false;
                    errorMessage.append("\n\n").append(dwrFileName).append(" (in module ").append(moduleGroup.namespaceCode).append(") has ").append(count).append(" references to business objects from ").append(referencedNamespaceCode);
                }
            }
        }
        return testSucceeded;
    }
    
    protected boolean testDwrSpringServices(String dwrFileName, Document dwrDocument, ModuleGroup moduleGroup, StringBuffer errorMessage) {
        boolean testSucceeded = true;
        
        try {
            List<String> serviceNames = retrieveDwrServiceNames(dwrDocument);
            for (String serviceName : serviceNames) {
                try {
                    SpringContext.getBean(serviceName);
                } catch ( Exception ex ) {
                    testSucceeded = false;
                    errorMessage.append("\n")
                            .append(dwrFileName)
                            .append(" (in module ")
                            .append(moduleGroup.namespaceCode)
                            .append(") has references to spring bean \"")
                            .append(serviceName).append("\" that is not defined in the available spring files");
                }
            }
        }
        catch (Exception e) {
            errorMessage.append("\n").append(moduleGroup.namespaceCode).append("\n\t").append(e.getMessage());
            e.printStackTrace();
            return testSucceeded = false;
        }
        
        return testSucceeded;
    }

    
    protected Document generateDwrConfigDocument(String fileName) throws Exception {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        InputStream in = resourceLoader.getResource(fileName).getInputStream();
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new DTDEntityResolver());
        db.setErrorHandler(new LogErrorHandler());

        Document doc = db.parse(in);
        return doc;
    }
    
    protected List<String> retrieveDwrServiceNames(Document dwrDocument) {
        List<String> serviceNames = new ArrayList<String>();
        // service names are in "create" elements
        Element root = dwrDocument.getDocumentElement();
        NodeList allows = root.getElementsByTagName("allow");
        for (int i = 0; i < allows.getLength(); i++) {
            Element allowElement = (Element) allows.item(i);
            NodeList creates = allowElement.getElementsByTagName("create");
            for (int j = 0; j < creates.getLength(); j++) {
                Element createElement = (Element) creates.item(j);
                if ("spring".equals(createElement.getAttribute("creator"))) {
                    NodeList params = createElement.getElementsByTagName("param");
                    for (int k = 0; k < params.getLength(); k++) {
                        Element paramElement = (Element) params.item(k);
                        if ("beanName".equals(paramElement.getAttribute("name"))) {
                            serviceNames.add(paramElement.getAttribute("value"));
                        }
                    }
                }
                
            }
        }
        return serviceNames;
    }
    
    protected List<String> retrieveDwrBeanClassNames(Document dwrDocument) {
        List<String> classNames = new ArrayList<String>();
        // class names are in "convert" elements
        Element root = dwrDocument.getDocumentElement();
        NodeList allows = root.getElementsByTagName("allow");
        for (int i = 0; i < allows.getLength(); i++) {
            Element allowElement = (Element) allows.item(i);
            NodeList converts = allowElement.getElementsByTagName("convert");
            for (int j = 0; j < converts.getLength(); j++) {
                Element convertElement = (Element) converts.item(j);
                if ("bean".equals(convertElement.getAttribute("converter"))) {
                    classNames.add(convertElement.getAttribute("match"));
                }
            }
        }
        return classNames;
    }
    
    protected boolean testDd() throws Exception {
        if (!ddTestSucceeded) {
            System.out.print(ddErrorMessage.append("\n\n").toString());
        }
        return ddTestSucceeded;
    }
    
    protected boolean testDdModuleConfiguration( ModuleGroup moduleGroup, StringBuffer errorMessage ) {
        boolean testPassed = true;
        
        List<String> disallowedPackagesForModule = new ArrayList<String>();
        for ( String otherNamespace : PACKAGE_PREFIXES_BY_MODULE.keySet() ) {
            // if an optional module
            if ( OPTIONAL_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.containsKey( otherNamespace ) ) {
                // and not the current module
                if ( !otherNamespace.equals( moduleGroup.namespaceCode ) ) {
                    // add to disallowed list
                    disallowedPackagesForModule.addAll( PACKAGE_PREFIXES_BY_MODULE.get(otherNamespace) );
                }
            }
        }
        System.out.println( "---Processing DD for Module: " + moduleGroup.namespaceCode );
        System.out.println( "---Disallowed packages: " + disallowedPackagesForModule );
        DataDictionary dd = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
        Collection<BusinessObjectEntry> bos = dd.getBusinessObjectEntries().values();
        for ( BusinessObjectEntry bo : bos ) {
            // only check bos for the current module (or all modules if checking the core)
            if ( ("KFS-SYS".equals( moduleGroup.namespaceCode) 
                    || doesPackagePrefixMatch( bo.getFullClassName(), PACKAGE_PREFIXES_BY_MODULE.get( moduleGroup.namespaceCode ) ))
                    && !bo.getFullClassName().startsWith("org.kuali.rice") ) {                               
                try {
                    if ( bo.getInactivationBlockingDefinitions() != null ) {
                        for ( InactivationBlockingDefinition ibd : bo.getInactivationBlockingDefinitions() ) {
                            validateDdBusinessObjectClassReference("Invalid Blocked BO Class", ibd.getBlockedBusinessObjectClass(), moduleGroup.namespaceCode, bo.getFullClassName(), null, disallowedPackagesForModule);
                            validateDdBusinessObjectClassReference("Invalid Blocking Reference BO Class", ibd.getBlockingReferenceBusinessObjectClass(), moduleGroup.namespaceCode, bo.getFullClassName(), null, disallowedPackagesForModule);
                            if ( ibd.getInactivationBlockingDetectionServiceBeanName() != null ) {
                                try {
                                    SpringContext.getBean( ibd.getInactivationBlockingDetectionServiceBeanName() );
                                } catch (Exception ex ) {
                                    addDdBusinessObjectError("Invalid inactivation blocking service", moduleGroup.namespaceCode, bo.getFullClassName(), null, ibd.getInactivationBlockingDetectionServiceBeanName());
                                }
                            }
                        }
                    }
                    
                    for ( AttributeDefinition ad : bo.getAttributes() ) {
                        try {
                            ControlDefinition cd = ad.getControl();
                            validateDdBusinessObjectClassReference("Invalid Formatter Class", ad.getFormatterClass(), moduleGroup.namespaceCode, bo.getFullClassName(), ad.getName(), disallowedPackagesForModule);
                            if ( cd != null ) {
                                validateDdBusinessObjectClassReference("Invalid Control Value Finder", cd.getValuesFinderClass(), moduleGroup.namespaceCode, bo.getFullClassName(), ad.getName(), disallowedPackagesForModule);
                                validateDdBusinessObjectClassReference("Invalid BO class for KeyLabelBusinessObjectValueFinder", cd.getBusinessObjectClass(), moduleGroup.namespaceCode, bo.getFullClassName(), ad.getName(), disallowedPackagesForModule);
                            }
                        } catch ( Exception ex ) {
                            addDdBusinessObjectError("Exception Testing BO", moduleGroup.namespaceCode, bo.getFullClassName(), ad.getName(), ex.getClass().getName() + " : " + ex.getMessage() );
                            System.err.println( "Exception testing BO: " + bo.getFullClassName() + "/" + ad.getName() );
                            ex.printStackTrace();
                            testPassed = false;
                        }
                    }
                } catch( Exception ex ) {
                    addDdBusinessObjectError("Exception Testing BO", moduleGroup.namespaceCode, bo.getFullClassName(), null, ex.getClass().getName() + " : " + ex.getMessage() );
                    System.err.println( "Exception testing BO: " + bo.getFullClassName() );
                    ex.printStackTrace();
                    testPassed = false;
                }
            }
        }
        
        for ( DocumentEntry de : dd.getDocumentEntries().values() ) {
            if ( (de instanceof MaintenanceDocumentEntry && ("KFS-SYS".equals( moduleGroup.namespaceCode) || doesPackagePrefixMatch( ((MaintenanceDocumentEntry)de).getBusinessObjectClass().getName(), PACKAGE_PREFIXES_BY_MODULE.get( moduleGroup.namespaceCode )) ))
                    || (de instanceof TransactionalDocumentEntry && ("KFS-SYS".equals( moduleGroup.namespaceCode) || doesPackagePrefixMatch( de.getDocumentClass().getName(), PACKAGE_PREFIXES_BY_MODULE.get( moduleGroup.namespaceCode ))) ) ) {
                try {
                    if ( de instanceof MaintenanceDocumentEntry ) {
                        MaintenanceDocumentEntry mde = (MaintenanceDocumentEntry)de;
                        validateDdDocumentClassReference("Invalid Maintainable Class", mde.getMaintainableClass(), moduleGroup.namespaceCode, de.getDocumentTypeName(), null, disallowedPackagesForModule);
                        for ( MaintainableSectionDefinition msd : mde.getMaintainableSections() ) {
                            for ( MaintainableItemDefinition mid : msd.getMaintainableItems() ) {
                                if ( mid instanceof MaintainableCollectionDefinition ) {
                                    testPassed &= checkMaintainableCollection(moduleGroup.namespaceCode, de.getDocumentTypeName(), (MaintainableCollectionDefinition)mid, disallowedPackagesForModule);
                                }
                                if ( mid instanceof MaintainableFieldDefinition ) {
                                    testPassed &= checkMaintainableField( moduleGroup.namespaceCode, de.getDocumentTypeName(), (MaintainableFieldDefinition)mid, disallowedPackagesForModule);
                                }
                            }
                        }
                    } else { // trans doc
                        
                    }
                    validateDdDocumentClassReference("Invalid Business Rules Class", de.getBusinessRulesClass(), moduleGroup.namespaceCode, de.getDocumentTypeName(), null, disallowedPackagesForModule);
                    validateDdDocumentClassReference("Invalid DerivedValuesSetterClass", de.getDerivedValuesSetterClass(), moduleGroup.namespaceCode, de.getDocumentTypeName(), null, disallowedPackagesForModule);
                    validateDdDocumentClassReference("Invalid DocumentAuthorizerClass", de.getDocumentAuthorizerClass(), moduleGroup.namespaceCode, de.getDocumentTypeName(), null, disallowedPackagesForModule);
                    validateDdDocumentClassReference("Invalid DocumentPresentationControllerClass", de.getDocumentPresentationControllerClass(), moduleGroup.namespaceCode, de.getDocumentTypeName(), null, disallowedPackagesForModule);
                    validateDdDocumentClassReference("Invalid DocumentSearchGeneratorClass", de.getDocumentSearchGeneratorClass(), moduleGroup.namespaceCode, de.getDocumentTypeName(), null, disallowedPackagesForModule);
                    validateDdDocumentClassReference("Invalid PromptBeforeValidationClass", de.getPromptBeforeValidationClass(), moduleGroup.namespaceCode, de.getDocumentTypeName(), null, disallowedPackagesForModule);
                } catch ( Exception ex ) {
                    addDdDocumentError("Exception validating Document", moduleGroup.namespaceCode, de.getDocumentTypeName(), null, ex.getClass().getName() + " : " + ex.getMessage() );
                    System.err.println( "Exception validating Document: " + de.getDocumentTypeName() );
                    testPassed = false;
                }
            }
        }
        
        return testPassed;
    }
    
    protected void addDdBusinessObjectError( String errorType, String namespaceCode, String businessObjectClassName, String attributeName, String problemClassName ) {
        ddErrorMessage.append( "\n" ).append( namespaceCode ).append( " - BO: " );
        ddErrorMessage.append( businessObjectClassName );
        if ( attributeName != null ) {
            ddErrorMessage.append( " / Attrib: " ).append( attributeName );
        }
        ddErrorMessage.append( " / " ).append( errorType ).append( ": " ).append( problemClassName );
    }

    protected boolean validateDdBusinessObjectClassReference( String errorType, String testClassName, String namespaceCode, String businessObjectClassName, String attributeName, List<String> disallowedPackages ) {
        if (StringUtils.isBlank(testClassName)) {
            return true;
        }
        try {
            Class<?> testClass = Class.forName(testClassName);
            return validateDdBusinessObjectClassReference(errorType, testClass, namespaceCode, businessObjectClassName, attributeName, disallowedPackages);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected boolean validateDdBusinessObjectClassReference( String errorType, Class<? extends Object> testClass, String namespaceCode, String businessObjectClassName, String attributeName, List<String> disallowedPackages ) {
        if ( testClass != null ) {
            if ( doesPackagePrefixMatch(testClass.getName(), disallowedPackages) ) {
                addDdBusinessObjectError(errorType, namespaceCode, businessObjectClassName, attributeName, testClass.getName());
                return false;
            }
        }
        return true;
    }

    protected void addDdDocumentError( String errorType, String namespaceCode, String documentTypeName, String fieldName, String problemClassName ) {
        ddErrorMessage.append( "\n" ).append( namespaceCode ).append( " - Doc: " );
        ddErrorMessage.append( documentTypeName );
        if ( fieldName != null ) {
            ddErrorMessage.append( " / Field: " ).append( fieldName );
        }
        ddErrorMessage.append( " / " ).append( errorType ).append( ": " ).append( problemClassName );
    }

    protected boolean validateDdDocumentClassReference( String errorType, Class<? extends Object> testClass, String namespaceCode, String documentTypeName, String fieldName, List<String> disallowedPackages ) {
        if ( testClass != null ) {
            if ( doesPackagePrefixMatch(testClass.getName(), disallowedPackages) ) {
                addDdDocumentError(errorType, namespaceCode, documentTypeName, fieldName, testClass.getName());
                return false;
            }
        }
        return true;
    }
    
    protected boolean checkMaintainableCollection( String namespaceCode, String documentTypeName, MaintainableCollectionDefinition collection, List<String> disallowedPackages ) {
        boolean testPassed = true;
        testPassed &= validateDdDocumentClassReference("Invalid Collection BO Class", collection.getBusinessObjectClass(), namespaceCode, documentTypeName, collection.getName(), disallowedPackages);
        testPassed &= validateDdDocumentClassReference("Invalid Collection Source Class", collection.getSourceClassName(), namespaceCode, documentTypeName, collection.getName(), disallowedPackages);
        for ( MaintainableFieldDefinition mfd : collection.getMaintainableFields() ) {
            testPassed &= checkMaintainableField( namespaceCode, documentTypeName, mfd, disallowedPackages);
        }
        for ( MaintainableCollectionDefinition mcd : collection.getMaintainableCollections() ) {
            testPassed &= checkMaintainableCollection( namespaceCode, documentTypeName, mcd, disallowedPackages);            
        }
        
        return testPassed;
    }
    protected boolean checkMaintainableField( String namespaceCode, String documentTypeName, MaintainableFieldDefinition field, List<String> disallowedPackages ) {
        boolean testPassed = true;
        try {
            testPassed &= validateDdDocumentClassReference("Invalid Default Value Finder Class", field.getDefaultValueFinderClass(), namespaceCode, documentTypeName, field.getName(), disallowedPackages);
            testPassed &= validateDdDocumentClassReference("Invalid Override Lookup Class", field.getOverrideLookupClass(), namespaceCode, documentTypeName, field.getName(), disallowedPackages);
        } catch ( Exception ex ) {
            addDdDocumentError("Exception validating Maint Doc Field", namespaceCode, documentTypeName, field.getName(), ex.getClass().getName() + " : " + ex.getMessage() );
            System.err.println( "Exception validating Maint Doc Field: " + documentTypeName + "/" + field.getName() );
            ex.printStackTrace();
            testPassed = false;
        }
        return testPassed;
    }
    
    protected boolean doesPackagePrefixMatch( String className, List<String> packagePrefixList ) {
        for ( String pkg : packagePrefixList ) {
            if ( className.startsWith(pkg) ) {
                return true;
            }
        }
        return false;
    }        
    
    public class ModuleGroup {
        public String namespaceCode;
        public HashSet<String> optionalModuleDependencyNamespaceCodes = new HashSet<String>();
        
        public ModuleGroup() {
            // TODO Auto-generated constructor stub
        }
        
        public ModuleGroup( String namespaceCode ) {
            this.namespaceCode = namespaceCode;
        }
        
        @Override
        public String toString() {
            return namespaceCode + (optionalModuleDependencyNamespaceCodes.isEmpty()?"":(" - depends on: " + optionalModuleDependencyNamespaceCodes));
        }
    }
    
    public List<ModuleGroup> retrieveModuleGroups() throws Exception {
        List<ModuleGroup> moduleGroups = new ArrayList<ModuleGroup>();
        
        for (String systemNamespaceCode : SYSTEM_NAMESPACE_CODES_TO_SPRING_FILE_SUFFIX.keySet()) {
            ModuleGroup systemModuleGroup = new ModuleGroup();
            systemModuleGroup.namespaceCode = systemNamespaceCode;
            moduleGroups.add(systemModuleGroup);
        }
        
        moduleGroups.addAll(retrieveOptionalModuleGroups());
        
        return moduleGroups;
    }
    
    public List<ModuleGroup> retrieveOptionalModuleGroups() throws Exception {
        Document designXmlDocument = getDesignXmlDocument();
        List<Element> optionalModuleDefinitions = retrieveOptionalModuleDefinitions(designXmlDocument);
        List<ModuleGroup> optionalModuleGroups = new ArrayList<ModuleGroup>();
        
        for (Element optionalModuleDefinition : optionalModuleDefinitions) {
            ModuleGroup optionalModuleGroup = buildOptionalModuleGroup(optionalModuleDefinition);
            if (optionalModuleGroup != null) {
                optionalModuleGroups.add(optionalModuleGroup);
            }
        }
        
        return optionalModuleGroups;
    }
    
    public Document getDesignXmlDocument() throws Exception {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        InputStream in = resourceLoader.getResource(DefaultResourceLoader.CLASSPATH_URL_PREFIX + "design.xml").getInputStream();
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(in);
        return doc;
    }
    
    public List<Element> retrieveOptionalModuleDefinitions(Document designXmlDocument) throws Exception {
        List<Element> optionalModuleDefinitions = new ArrayList<Element>();
        Element root = designXmlDocument.getDocumentElement();
        
        // in the design.xml file, an optional module/package is specified by a <package> tag that does not have the needdeclarations attribute equal false
        NodeList packages = root.getElementsByTagName("package");
        for (int i = 0; i < packages.getLength(); i++) {
            Element packageElement = (Element) packages.item(i);
            if (!"false".equals(packageElement.getAttribute("needdeclarations"))) {
                optionalModuleDefinitions.add(packageElement);
            }
        }
        return optionalModuleDefinitions;
    }
    
    public ModuleGroup buildOptionalModuleGroup(Element optionalPackageElement) {
        ModuleGroup moduleGroup = null;
        if (OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.containsKey(optionalPackageElement.getAttribute("name"))) {
            moduleGroup = new ModuleGroup();
            moduleGroup.namespaceCode = OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.get(optionalPackageElement.getAttribute("name"));
            if (StringUtils.isNotBlank(optionalPackageElement.getAttribute("depends"))) {
                if (OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.containsKey(optionalPackageElement.getAttribute("depends"))) {
                    moduleGroup.optionalModuleDependencyNamespaceCodes.add(OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.get(optionalPackageElement.getAttribute("depends")));
                }
            }
            NodeList dependsElements = optionalPackageElement.getElementsByTagName("depends");
            for (int i = 0; i < dependsElements.getLength(); i++) {
                Element dependsElement = (Element) dependsElements.item(i);
                if (OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.containsKey(StringUtils.trim(dependsElement.getTextContent()))) {
                    moduleGroup.optionalModuleDependencyNamespaceCodes.add(OPTIONAL_SPRING_FILE_SUFFIX_TO_NAMESPACE_CODES.get(StringUtils.trim(dependsElement.getTextContent())));
                }
            }
        }
        return moduleGroup;
    }
    
    protected static void stopSpringContext() {
        try {
            SpringContext.close();
        } catch (Exception e) {
            System.out.println("Caught exception shutting down spring");
            e.printStackTrace();
        }
    }
}
