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
package org.kuali.kfs.module.bc.document.web.struts;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.action.KualiAction;

/**
 * Handles close action to implement Budget return to caller (expansion screen) flow.
 */
public class BudgetExpansionAction extends KualiAction {

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetExpansionForm budgetExpansionForm = (BudgetExpansionForm) form;

        // check for session timeout if not initial (re)load of BC selection
        if (!(mapping.getType().endsWith("BudgetConstructionSelectionAction") && (budgetExpansionForm.getMethodToCall().equals(BCConstants.LOAD_EXPANSION_SCREEN_METHOD) || budgetExpansionForm.getMethodToCall().equals(BCConstants.LOAD_EXPANSION_SCREEN_METHOD_SESSION_TIMEOUT)))) {

            Boolean isBCHeartBeating = (Boolean) GlobalVariables.getUserSession().retrieveObject(BCConstants.BC_HEARTBEAT_SESSIONFLAG);
            if (isBCHeartBeating == null) {
                budgetExpansionForm.setLostSession(Boolean.TRUE);
                
                if (form instanceof SalarySettingBaseForm){
                    SalarySettingBaseForm salarySettingBaseForm = (SalarySettingBaseForm) form;
                    if (!salarySettingBaseForm.isBudgetByAccountMode()){
                        GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_PREVIOUS_SESSION_TIMEOUT);
                        return mapping.findForward(BCConstants.MAPPING_ORGANIZATION_SALARY_SETTING_RETURNING);
                    }
                }
                if (budgetExpansionForm.isMainWindow()){
                    Properties parameters = new Properties();
                    parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.LOAD_EXPANSION_SCREEN_METHOD_SESSION_TIMEOUT);

                    String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.BC_SELECTION_ACTION, parameters);

                    return new ActionForward(lookupUrl, true);
                }
                else {
                    GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_PREVIOUS_SESSION_TIMEOUT);
                    return mapping.findForward(BCConstants.MAPPING_LOST_SESSION_RETURNING);
                }
            }
        }

        return super.execute(mapping, form, request, response);
    }

    /**
     * Handling for screen close. Default action is return to caller.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return returnToCaller(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.moveCallBackMessagesInPlace();
        this.removeCallBackMessagesObjectFromSession();

        return super.refresh(mapping, form, request, response);
    }

    /**
     * Return to form's back location (usually previous screen). Returns back the form key that was passed in for the previous form
     * and any previous anchor position. Default refresh method is executed.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetExpansionForm budgetExpansionForm = (BudgetExpansionForm) form;

        // if this form is session scoped remove it 
        this.cleanupAnySessionForm(mapping, request);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_SELECTION_REFRESH_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, budgetExpansionForm.getReturnFormKey());

        if (StringUtils.isNotEmpty(budgetExpansionForm.getReturnAnchor())) {
            parameters.put(KFSConstants.ANCHOR, budgetExpansionForm.getReturnAnchor());
        }
        parameters.put(KFSConstants.REFRESH_CALLER, this.getClass().getName());

        this.addCallBackMessagesAsObjectInSession(budgetExpansionForm);

        String backUrl = UrlFactory.parameterizeUrl(budgetExpansionForm.getBackLocation(), parameters);
        return new ActionForward(backUrl, true);
    }

    /**
     * add the callback messages and error messages as objects in session variable
     */
    public void addCallBackMessagesAsObjectInSession(BudgetExpansionForm budgetExpansionForm) {
        if (!budgetExpansionForm.getCallBackMessages().isEmpty()) {
            GlobalVariables.getUserSession().addObject(BCPropertyConstants.CALL_BACK_MESSAGES, budgetExpansionForm.getCallBackMessages());
        }

        if (budgetExpansionForm.getCallBackErrors().hasErrors()) {
            GlobalVariables.getUserSession().addObject(BCPropertyConstants.CALL_BACK_ERRORS, budgetExpansionForm.getCallBackErrors());
        }
    }

    /**
     * remove the objects that hold the callback messages and error messages from session variable
     */
    public void removeCallBackMessagesObjectFromSession() {
        GlobalVariables.getUserSession().removeObject(BCPropertyConstants.CALL_BACK_MESSAGES);
        GlobalVariables.getUserSession().removeObject(BCPropertyConstants.CALL_BACK_ERRORS);
    }

    /**
     * move the callback messages and error messages in place
     */
    public void moveCallBackMessagesInPlace() {
        MessageList messagesList = (MessageList) GlobalVariables.getUserSession().retrieveObject(BCPropertyConstants.CALL_BACK_MESSAGES);
        if (messagesList != null) {
            GlobalVariables.getMessageList().addAll(messagesList);
        }

        ErrorMap errorMap = (ErrorMap) GlobalVariables.getUserSession().retrieveObject(BCPropertyConstants.CALL_BACK_ERRORS);
        if (errorMap != null) {
            GlobalVariables.setErrorMap(errorMap);
        }
    }

    /**
     * remove any session form attribute
     * 
     * @param mapping
     * @param request
     */
    public void cleanupAnySessionForm(ActionMapping mapping, HttpServletRequest request) {
        if (BCConstants.MAPPING_SCOPE_SESSION.equals(mapping.getScope())) {
            HttpSession sess = request.getSession(Boolean.FALSE);
            String formName = mapping.getAttribute();
            sess.removeAttribute(formName);
        }
    }
}
