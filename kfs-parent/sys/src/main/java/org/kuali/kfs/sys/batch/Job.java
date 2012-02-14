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
package org.kuali.kfs.sys.batch;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.MessageList;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.quartz.UnableToInterruptJobException;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.StopWatch;

public class Job implements StatefulJob, InterruptableJob {

    public static final String JOB_RUN_START_STEP = "JOB_RUN_START_STEP";
    public static final String JOB_RUN_END_STEP = "JOB_RUN_END_STEP";
    public static final String STEP_RUN_PARM_NM = "RUN_IND";    
    public static final String STEP_RUN_ON_DATE_PARM_NM = "RUN_DATE";
    public static final String STEP_USER_PARM_NM = "USER";
    
    private static final Logger LOG = Logger.getLogger(Job.class);
    private SchedulerService schedulerService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private List<Step> steps;
    private Step currentStep;
    private Appender ndcAppender;
    private boolean notRunnable;
    private transient Thread workerThread;

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        workerThread = Thread.currentThread();
        if (isNotRunnable()) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Skipping job because doNotRun is true: " + jobExecutionContext.getJobDetail().getName());
            }
            return;
        }
        int startStep = 0;
        try {
            startStep = Integer.parseInt(jobExecutionContext.getMergedJobDataMap().getString(JOB_RUN_START_STEP));
        }
        catch (NumberFormatException ex) {
            // not present, do nothing
        }
        int endStep = 0;
        try {
            endStep = Integer.parseInt(jobExecutionContext.getMergedJobDataMap().getString(JOB_RUN_END_STEP));
        }
        catch (NumberFormatException ex) {
            // not present, do nothing
        }
        Date jobRunDate = dateTimeService.getCurrentDate();
        int currentStepNumber = 0;
        try {
            LOG.info("Executing job: " + jobExecutionContext.getJobDetail() + " on machine " + getMachineName() + " scheduler instance id " + jobExecutionContext.getScheduler().getSchedulerInstanceId() + "\n" + jobDataMapToString(jobExecutionContext.getJobDetail().getJobDataMap()));
            for (Step step : getSteps()) {
                currentStepNumber++;
                // prevent starting of the next step if the thread has an interrupted status
                if (workerThread.isInterrupted()) {
                    LOG.warn("Aborting Job execution due to manual interruption");
                    schedulerService.updateStatus(jobExecutionContext.getJobDetail(), SchedulerService.CANCELLED_JOB_STATUS_CODE);
                    return;
                }
                if (startStep > 0 && currentStepNumber < startStep) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Skipping step " + currentStepNumber + " - startStep=" + startStep);
                    }
                    continue; // skip to next step
                }
                else if (endStep > 0 && currentStepNumber > endStep) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Ending step loop - currentStepNumber=" + currentStepNumber + " - endStep = " + endStep);
                    }
                    break;
                }
                step.setInterrupted(false);
                try {
                    if (!runStep(parameterService, jobExecutionContext.getJobDetail().getFullName(), currentStepNumber, step, jobRunDate)) {
                        break;
                    }
                }
                catch (InterruptedException ex) {
                    LOG.warn("Stopping after step interruption");
                    schedulerService.updateStatus(jobExecutionContext.getJobDetail(), SchedulerService.CANCELLED_JOB_STATUS_CODE);
                    return;
                }
                if (step.isInterrupted()) {
                    LOG.warn("attempt to interrupt step failed, step continued to completion");
                    LOG.warn("cancelling remainder of job due to step interruption");
                    schedulerService.updateStatus(jobExecutionContext.getJobDetail(), SchedulerService.CANCELLED_JOB_STATUS_CODE);
                    return;
                }
            }
        }
        catch (Exception e) {
            schedulerService.updateStatus(jobExecutionContext.getJobDetail(), SchedulerService.FAILED_JOB_STATUS_CODE);
            throw new JobExecutionException("Caught exception in " + jobExecutionContext.getJobDetail().getName(), e, false);
        }
        LOG.info("Finished executing job: " + jobExecutionContext.getJobDetail().getName());
        schedulerService.updateStatus(jobExecutionContext.getJobDetail(), SchedulerService.SUCCEEDED_JOB_STATUS_CODE);
    }

    public static boolean runStep(ParameterService parameterService, String jobName, int currentStepNumber, Step step, Date jobRunDate) throws InterruptedException, WorkflowException {
        
        boolean continueJob = true;
        if (GlobalVariables.getUserSession() == null) {
            LOG.info(new StringBuffer("Started processing step: ").append(currentStepNumber).append("=").append(step.getName()).append(" for user <unknown>"));
        }
        else {
            LOG.info(new StringBuffer("Started processing step: ").append(currentStepNumber).append("=").append(step.getName()).append(" for user ").append(GlobalVariables.getUserSession().getPrincipalName()));
        }        
        
        if (!skipStep(parameterService, step, jobRunDate)) {
            
            Step unProxiedStep = (Step) ProxyUtils.getTargetIfProxied(step);
            Class stepClass = unProxiedStep.getClass();
            
            GlobalVariables.setErrorMap(new ErrorMap());
            GlobalVariables.setMessageList(new MessageList());
            
            String stepUserName = KFSConstants.SYSTEM_USER;
            if (parameterService.parameterExists(stepClass, STEP_USER_PARM_NM)) {
                stepUserName = parameterService.getParameterValue(stepClass, STEP_USER_PARM_NM);
            }
            if (LOG.isInfoEnabled()) {
                LOG.info(new StringBuffer("Creating user session for step: ").append(step.getName()).append("=").append(stepUserName));
            }
            GlobalVariables.setUserSession(new UserSession(stepUserName));
            if (LOG.isInfoEnabled()) {
                LOG.info(new StringBuffer("Executing step: ").append(step.getName()).append("=").append(stepClass));
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start(jobName);
            try {
                continueJob = step.execute(jobName, jobRunDate);
            }
            catch (InterruptedException e) {
                LOG.error("Exception occured executing step", e);
                throw e;
            }
            catch (RuntimeException e) {
                LOG.error("Exception occured executing step", e);
                throw e;
            }
            stopWatch.stop();
            LOG.info(new StringBuffer("Step ").append(step.getName()).append(" of ").append(jobName).append(" took ").append(stopWatch.getTotalTimeSeconds() / 60.0).append(" minutes to complete").toString());
            if (!continueJob) {
                LOG.info("Stopping job after successful step execution");
            }
        }
        
        LOG.info(new StringBuffer("Finished processing step ").append(currentStepNumber).append(": ").append(step.getName()));
        return continueJob;
    }
    
    /**
     * This method determines whether the Job should not run the Step based on the RUN_IND and RUN_DATE Parameters.
     * When RUN_IND exists and equals 'Y' it takes priority and does not consult RUN_DATE. 
     * If RUN_DATE exists, but contains an empty value the step will not be skipped.
     */
    protected static boolean skipStep(ParameterService parameterService, Step step, Date jobRunDate) {
        Step unProxiedStep = (Step) ProxyUtils.getTargetIfProxied(step);
        Class stepClass = unProxiedStep.getClass();
        
        DateTimeService dTService = SpringContext.getBean(DateTimeService.class);
        String dateFormat = parameterService.getParameterValue(KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.ALL_DETAIL_TYPE, KNSConstants.SystemGroupParameterNames.DATE_TO_STRING_FORMAT_FOR_USER_INTERFACE);
        
        //RUN_IND takes priority: when RUN_IND exists and RUN_IND=Y always run the Step
        //RUN_DATE: when RUN_DATE exists, but the value is empty run the Step
        
        boolean runIndExists = parameterService.parameterExists(stepClass, STEP_RUN_PARM_NM);
        boolean runInd = (runIndExists ? parameterService.getIndicatorParameter(stepClass, STEP_RUN_PARM_NM) : true);
        
        boolean runDateExists = parameterService.parameterExists(stepClass, STEP_RUN_ON_DATE_PARM_NM);
        boolean runDateIsEmpty = (runDateExists ? StringUtils.isEmpty(parameterService.getParameterValue(stepClass, STEP_RUN_ON_DATE_PARM_NM)) : true);
        boolean runDateContainsTodaysDate = (runDateExists ? parameterService.getParameterValues(stepClass, STEP_RUN_ON_DATE_PARM_NM).contains(dTService.toString(jobRunDate, dateFormat)): true);

        if (!runInd && !runDateExists) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Skipping step due to system parameter: " + STEP_RUN_PARM_NM +" for "+ stepClass.getName());
            }            
            return true;
        }
        else if (!runInd && !runDateIsEmpty && !runDateContainsTodaysDate) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Skipping step due to system parameters: " + STEP_RUN_PARM_NM + " and " + STEP_RUN_ON_DATE_PARM_NM +" for "+ stepClass.getName());
            }
            return true;
        }
        else if (!runIndExists && !runDateIsEmpty && !runDateContainsTodaysDate) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Skipping step due to system parameter: " + STEP_RUN_ON_DATE_PARM_NM +" for "+ stepClass.getName());
            }
            return true;
        }
        else { //run step
            return false;
        }
    }

    /**
     * @throws UnableToInterruptJobException
     */
    public void interrupt() throws UnableToInterruptJobException {
        // ask the step to interrupt
        if (currentStep != null) {
            currentStep.interrupt();
        }
        // also attempt to interrupt the thread, to cause an InterruptedException if the step ever waits or sleeps
        workerThread.interrupt();
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public Appender getNdcAppender() {
        return ndcAppender;
    }

    public void setNdcAppender(Appender ndcAppender) {
        this.ndcAppender = ndcAppender;
    }

    public void setNotRunnable(boolean notRunnable) {
        this.notRunnable = notRunnable;
    }

    protected boolean isNotRunnable() {
        return notRunnable;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
    protected String jobDataMapToString(JobDataMap jobDataMap) {
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        Iterator keys = jobDataMap.keySet().iterator();
        boolean hasNext = keys.hasNext();
        while (hasNext) {
            String key = (String) keys.next();
            Object value = jobDataMap.get(key);
            buf.append(key).append("=");
            if (value == jobDataMap) {
                buf.append("(this map)");
            }
            else {
                buf.append(value);
            }
            hasNext = keys.hasNext();
            if (hasNext) {
                buf.append(", ");
            }
        }
        buf.append("}");
        return buf.toString();
    }
    
    protected String getMachineName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            return "Unknown";
        }
    }
}
