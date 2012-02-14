<%--
 Copyright 2007 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Batch" />
<div class="body">
	<c:if test="${ConfigProperties.module.accounts.receivable.enabled == 'true'}">
		<strong>Accounts Receivable</strong><br/>
	    <ul class="chan">
			<li><portal:portalLink displayTitle="true" title="Customer Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=customerLoadInputFileType" /></li>				
		</ul>
	</c:if>
	<strong>Financial Processing</strong><br/>
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Procurement Card Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=procurementCardInputFileType" /></li>
	</ul>
	<strong>General Ledger</strong><br/>
    <ul class="chan">
	    <li><portal:portalLink displayTitle="true" title="Collector Flat File Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorFlatFileInputFileType" /></li>
		<li><portal:portalLink displayTitle="true" title="Collector XML Upload" url="batchUpload.do?methodToCall=start&batchUpload.batchInputTypeName=collectorXmlInputFileType" /></li>				
		<li><portal:portalLink displayTitle="true" title="Enterprise Feed Upload" url="batchUploadFileSet.do?methodToCall=start&batchUpload.batchInputTypeName=enterpriseFeederFileSetType" /></li>
		<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
			<li><portal:portalLink displayTitle="true" title="Labor Enterprise Feed Upload" url="laborBatchUploadFileSet.do?methodToCall=start&batchUpload.batchInputTypeName=laborEnterpriseFeederFileSetType" /></li>
		</c:if>
	</ul>
	<strong>System</strong><br/>
    <ul class="chan">
    	<li><portal:portalLink displayTitle="true" title="Batch File" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.batch.BatchFile&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Schedule" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.batch.BatchJobStatus&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&conversionFields=name:name,group:group" /></li>
		<li><portal:portalLink displayTitle="true" title="Special Batch File Upload" url="batchFileUpload" /></li>
	</ul>
	<c:if test="${ConfigProperties.module.access.security.enabled == 'true'}">
		<strong>Security</strong><br/>
	    <ul class="chan">
	    	<li><portal:portalLink displayTitle="true" title="Access Security Simulation" url="${ConfigProperties.kr.url}/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sec.businessobject.AccessSecuritySimulation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		</ul>	
	</c:if>
</div>
<channel:portalChannelBottom />
                
