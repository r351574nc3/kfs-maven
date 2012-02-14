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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.identity.KfsKimDocumentAttributeData;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.identity.OrgReviewRoleLookupableHelperServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.role.dto.DelegateMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.KimTypeInfoService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class represents the business rules for the maintenance of {@link AccountGlobal} business objects
 */
public class OrgReviewRoleRule extends MaintenanceDocumentRuleBase {

    protected UiDocumentService uiDocumentService;
    protected transient static GroupService groupService;
    protected transient static KimTypeInfoService typeInfoService;
    protected static Logger LOG = Logger.getLogger(OrgReviewRoleRule.class);

    /**
     * Need to override to avoid the primary key check which (wrongly) assumes that the object's PKs can be found in the persistence service.
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processGlobalSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processGlobalSaveDocumentBusinessRules(MaintenanceDocument document) {
        return dataDictionaryValidate(document);
    }
    
    @Override
    public boolean processRouteDocument(Document document) {
        boolean valid = super.processRouteDocument(document);
        OrgReviewRole orr = (OrgReviewRole)((MaintenanceDocument)document).getNewMaintainableObject().getBusinessObject();
        OrgReviewRoleLookupableHelperServiceImpl lookupableHelperService = new OrgReviewRoleLookupableHelperServiceImpl();
        lookupableHelperService.validateDocumentType(orr.getFinancialSystemDocumentTypeCode());
        if(orr!=null){
            if(!orr.hasAnyMember()){
                valid = false;
                putFieldError("principalMemberPrincipalName", KFSKeyConstants.NO_MEMBER_SELECTED);
            } else{
                validateRoleMembersToSave(orr);
                if(orr.isDelegate()){
                    // Save delegation(s)
                    valid = validateDelegation(orr, ((MaintenanceDocument)document).isEdit());
                } else{
                    // Save role member(s)
                    valid = validateRoleMember(orr, ((MaintenanceDocument)document).isEdit());
                }
            }
        }
        return valid;
    }
    
    protected void validateRoleMembersToSave(OrgReviewRole orr){
        if(orr==null) return;
        boolean valid = true;
        String memberId;
        if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
            KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalName(orr.getPrincipalMemberPrincipalName());
            if(principal == null || StringUtils.isEmpty(principal.getPrincipalId())){
                GlobalVariables.getMessageMap().putError(MAINTAINABLE_ERROR_PATH+"."+OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME, 
                        KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_MUST_BEVALID, KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL);
                valid = false;
            }
        }
        if(StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
            memberId = getRoleService().getRoleIdByName(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());
            if(memberId == null){
                GlobalVariables.getMessageMap().putError(MAINTAINABLE_ERROR_PATH+"."+OrgReviewRole.ROLE_NAME_FIELD_NAME, 
                        KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_MUST_BEVALID, KimConstants.KimUIConstants.MEMBER_TYPE_ROLE);
                valid = false;
            }
        }
        if(StringUtils.isNotEmpty(orr.getGroupMemberGroupName())){
            GroupInfo groupInfo = getGroupService().getGroupInfoByName(orr.getGroupMemberGroupNamespaceCode(), orr.getGroupMemberGroupName());
            if(groupInfo == null || StringUtils.isEmpty(groupInfo.getGroupId())){
                GlobalVariables.getMessageMap().putError(MAINTAINABLE_ERROR_PATH+"."+OrgReviewRole.GROUP_NAME_FIELD_NAME, 
                        KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_MUST_BEVALID, KimConstants.KimUIConstants.MEMBER_TYPE_GROUP);
                valid = false;
            }
        }
        if(!valid)
            throw new ValidationException("Invalid Role Member Data");
    }
    
    protected boolean validateDelegation(OrgReviewRole orr, boolean isEdit){
        boolean valid = true;
        String roleId;
        if(!isEdit && orr.getRoleNamesToConsider()!=null){
            for(String roleName: orr.getRoleNamesToConsider()){
                roleId = KIMServiceLocator.getRoleService().getRoleIdByName(
                        KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                Map<String, String> criteria = new HashMap<String, String>();
                criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
                List<DelegateMemberCompleteInfo> roleDelegationMembers = getRoleService().findDelegateMembersCompleteInfo(criteria);
                
                DelegateMemberCompleteInfo member;
                String memberId;
                //validate if the newly entered delegation members are already assigned to the role
                if(roleDelegationMembers!=null){
                    for(DelegateMemberCompleteInfo delegationMember: roleDelegationMembers){
                        member = orr.getDelegationMemberOfType(delegationMember.getMemberTypeCode());
                        if(member!=null && StringUtils.isNotEmpty(member.getMemberName())){
                            memberId = getUiDocumentService().getMemberIdByName(member.getMemberTypeCode(), member.getMemberNamespaceCode(), member.getMemberName());
                            boolean attributesUnique = areAttributesUnique(orr, delegationMember.getQualifier());
                            if(!attributesUnique && member!=null && StringUtils.isNotEmpty(memberId) && memberId.equals(delegationMember.getMemberId())
                                    && (StringUtils.isNotEmpty(orr.getRoleMemberId()) && StringUtils.isNotEmpty(delegationMember.getRoleMemberId()) 
                                            && orr.getRoleMemberId().equals(delegationMember.getRoleMemberId()))){
                               putFieldError(orr.getDelegationMemberFieldName(member), KFSKeyConstants.ALREADY_ASSIGNED_MEMBER);
                               valid = false;
                            }
                        }
                    }
                }
            }
        }
        if(StringUtils.isNotEmpty(orr.getRoleMemberId())){
            valid = validateAmounts(orr);
            //TODO: put from and to amounts validation here
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put("roleMemberId", orr.getRoleMemberId());
            List<RoleMemberCompleteInfo> roleMembers = getRoleService().findRoleMembersCompleteInfo(criteria);
            RoleMemberCompleteInfo roleMember = null;
            if(roleMembers!=null && roleMembers.size()>0)
                roleMember = roleMembers.get(0);
            KimRoleInfo roleInfo = getRoleService().getRole(roleMember.getRoleId());
            KimTypeInfo typeInfo = getTypeInfoService().getKimType(roleInfo.getKimTypeId());
            List<KfsKimDocumentAttributeData> attributes = orr.getAttributeSetAsQualifierList(typeInfo, roleMember.getQualifier());
            if(roleMember!=null && attributes!=null){
                for(KfsKimDocumentAttributeData attribute: attributes){
                    if(KfsKimAttributes.FROM_AMOUNT.equals(attribute.getKimAttribute().getAttributeName())){
                        KualiDecimal roleMemberFromAmount = new KualiDecimal(attribute.getAttrVal());
                        if(orr.getFromAmount()!=null){
                            KualiDecimal inputFromAmount = orr.getFromAmount();
                            if((roleMemberFromAmount!=null && inputFromAmount==null) || (inputFromAmount!=null && inputFromAmount.isLessThan(roleMemberFromAmount))){
                                putFieldError(KfsKimAttributes.FROM_AMOUNT, KFSKeyConstants.FROM_AMOUNT_OUT_OF_RANGE);
                                valid = false;
                            }
                        }
                    }
                    if(KfsKimAttributes.TO_AMOUNT.equals(attribute.getKimAttribute().getAttributeName())){
                        KualiDecimal roleMemberToAmount = new KualiDecimal(attribute.getAttrVal());
                        if(orr.getToAmount()!=null){
                            KualiDecimal inputToAmount = orr.getToAmount();
                            if((roleMemberToAmount!=null && inputToAmount==null) || (inputToAmount!=null && inputToAmount.isGreaterThan(roleMemberToAmount))){
                                putFieldError(KfsKimAttributes.TO_AMOUNT, KFSKeyConstants.TO_AMOUNT_OUT_OF_RANGE);
                                valid = false;
                            }
                        }
                    }
                }
            }
        }
        //putFieldError("bankOfficeCode", KFSKeyConstants.ERROR_DOCUMENT_ACHBANKMAINT_INVALID_OFFICE_CODE);
        return valid;
    }

    protected boolean validateAmounts(OrgReviewRole orr){
        boolean valid = true;
        if(orr.getFromAmount()!=null && orr.getToAmount()!=null && orr.getFromAmount().isGreaterThan(orr.getToAmount())){
            putFieldError(KfsKimAttributes.FROM_AMOUNT, KFSKeyConstants.FROM_AMOUNT_GREATER_THAN_TO_AMOUNT);
            valid = false;
        }
        return valid;
    }
    
    protected boolean validateRoleMember(OrgReviewRole orr, boolean isEdit){
        boolean valid = true;
        String roleId;
        if(orr==null) return false;
        boolean attributesUnique = true;
        valid = validateAmounts(orr);
        if(!isEdit && orr.getRoleNamesToConsider()!=null){
            for(String roleName: orr.getRoleNamesToConsider()){
                roleId = KIMServiceLocator.getRoleService().getRoleIdByName(
                        KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                //validate if the newly entered role members are already assigned to the role
                Map<String, Object> criteria = new HashMap<String, Object>();
                criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
                List<String> roleIds = new ArrayList<String>();
                roleIds.add(roleId);
                List<RoleMembershipInfo> roleMembershipInfoList = getRoleService().getFirstLevelRoleMembers(roleIds);
                RoleMemberCompleteInfo member;
                String memberId;
                if(roleMembershipInfoList!=null){
                    for(RoleMembershipInfo roleMembershipInfo: roleMembershipInfoList){
                        member = orr.getRoleMemberOfType(roleMembershipInfo.getMemberTypeCode());
                        if(member!=null && StringUtils.isNotEmpty(member.getMemberName())){
                            memberId = getUiDocumentService().getMemberIdByName(member.getMemberTypeCode(), member.getMemberNamespaceCode(), member.getMemberName());
                            attributesUnique = areAttributesUnique(orr, roleMembershipInfo.getQualifier());
                            if(!attributesUnique && member!=null && StringUtils.isNotEmpty(memberId) && memberId.equals(roleMembershipInfo.getMemberId()) && 
                                    member.getMemberTypeCode().equals(roleMembershipInfo.getMemberTypeCode())){
                               putFieldError(orr.getMemberFieldName(member), KFSKeyConstants.ALREADY_ASSIGNED_MEMBER);
                               valid = false;
                            }
                        }
                    }
                }
            }
        }
        return valid;
    }

    protected boolean areAttributesUnique(OrgReviewRole orr, AttributeSet attributeSet){
        String docTypeName = orr.getFinancialSystemDocumentTypeCode();
        String chartOfAccountCode = orr.getChartOfAccountsCode();
        String organizationCode = orr.getOrganizationCode();
        boolean uniqueAttributes = 
            !StringUtils.equals(docTypeName, getAttributeValue(attributeSet, KfsKimAttributes.DOCUMENT_TYPE_NAME)) ||
            !StringUtils.equals(chartOfAccountCode, getAttributeValue(attributeSet, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE)) ||
            !StringUtils.equals(organizationCode, getAttributeValue(attributeSet, KfsKimAttributes.ORGANIZATION_CODE));
        return uniqueAttributes;
    }
    
    protected String getAttributeValue(AttributeSet aSet, String attributeName){
        if(StringUtils.isEmpty(attributeName)) return null;
        for(String attributeNameKey: aSet.keySet()){
            if(attributeName.equals(attributeNameKey))
                return aSet.get(attributeNameKey);
        }
        return null;
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        return super.processCustomRouteDocumentBusinessRules(document);
    }

    /**
     * Gets the uiDocumentService attribute. 
     * @return Returns the uiDocumentService.
     */
    public UiDocumentService getUiDocumentService() {
        if(this.uiDocumentService==null){
            this.uiDocumentService = KIMServiceLocator.getUiDocumentService();
        }
        return this.uiDocumentService;
    }

    /**
     * Sets the uiDocumentService attribute value.
     * @param uiDocumentService The uiDocumentService to set.
     */
    public void setUiDocumentService(UiDocumentService uiDocumentService) {
        this.uiDocumentService = uiDocumentService;
    }

    protected KimTypeInfoService getTypeInfoService(){
        if(typeInfoService==null){
            typeInfoService = KIMServiceLocator.getTypeInfoService();
        }
        return typeInfoService;
    }

    protected GroupService getGroupService(){
        if(groupService==null){
            groupService = KIMServiceLocator.getGroupService();
        }
        return groupService;
    }
}
