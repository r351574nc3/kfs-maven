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
package org.kuali.kfs.sys.batch.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.batch.JobDescriptor;
import org.kuali.kfs.sys.batch.JobListener;
import org.kuali.kfs.sys.batch.ScheduleStep;
import org.kuali.kfs.sys.batch.SimpleTriggerDescriptor;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BatchModuleService;
import org.kuali.kfs.sys.service.impl.KfsModuleServiceImpl;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SchedulerServiceImpl implements SchedulerService {
    private static final Logger LOG = Logger.getLogger(SchedulerServiceImpl.class);
    protected static final String SCHEDULE_JOB_NAME = "scheduleJob";
    public static final String JOB_STATUS_PARAMETER = "status";
    protected static final String SOFT_DEPENDENCY_CODE = "softDependency";
    protected static final String HARD_DEPENDENCY_CODE = "hardDependency";

    private Scheduler scheduler;
    private JobListener jobListener;
    private KualiModuleService kualiModuleService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;
    private MailService mailService;
    /**
     * Holds a list of job name to job descriptor mappings for those jobs that are externalized (i.e. the module service is responsible for reporting their status)
     */
    protected Map<String, JobDescriptor> externalizedJobDescriptors;

    protected static final List<String> jobStatuses = new ArrayList<String>();

    static {
        jobStatuses.add(SCHEDULED_JOB_STATUS_CODE);
        jobStatuses.add(SUCCEEDED_JOB_STATUS_CODE);
        jobStatuses.add(CANCELLED_JOB_STATUS_CODE);
        jobStatuses.add(RUNNING_JOB_STATUS_CODE);
        jobStatuses.add(FAILED_JOB_STATUS_CODE);
    }

    public SchedulerServiceImpl() {
        externalizedJobDescriptors = new HashMap<String, JobDescriptor>();
    }
    
    /**
     * @see org.kuali.kfs.sys.batch.service.SchedulerService#initialize()
     */
    public void initialize() {
        LOG.info("Initializing the schedule");
        jobListener.setSchedulerService(this);
        try {
            scheduler.addGlobalJobListener(jobListener);
        } catch (SchedulerException e) {
            throw new RuntimeException("SchedulerServiceImpl encountered an exception when trying to register the global job listener", e);
        }
        for (ModuleService moduleService : kualiModuleService.getInstalledModuleServices()) {
            initializeJobsForModule(moduleService);
            initializeTriggersForModule(moduleService);
        }
    }
    /**
     * Initializes all of the jobs into Quartz for the given ModuleService
     * @param moduleService the ModuleService implementation to initalize jobs for
     */
    protected void initializeJobsForModule(ModuleService moduleService) {
        if ( LOG.isInfoEnabled() ) {
            LOG.info("Loading scheduled jobs for: " + moduleService.getModuleConfiguration().getNamespaceCode());
        }
        JobDescriptor jobDescriptor;
        if ( moduleService.getModuleConfiguration().getJobNames() != null ) { 
            for (String jobName : moduleService.getModuleConfiguration().getJobNames()) {
                try {
                    if (moduleService instanceof BatchModuleService && ((BatchModuleService) moduleService).isExternalJob(jobName)) {
                        jobDescriptor = new JobDescriptor();
                        jobDescriptor.setBeanName(jobName);
                        jobDescriptor.setGroup(SCHEDULED_GROUP);
                        jobDescriptor.setDurable(false);
                        externalizedJobDescriptors.put(jobName, jobDescriptor);
                    }
                    else {
                        jobDescriptor = BatchSpringContext.getJobDescriptor(jobName);
                    }
                    jobDescriptor.setNamespaceCode(moduleService.getModuleConfiguration().getNamespaceCode());
                    loadJob(jobDescriptor);
                } catch (NoSuchBeanDefinitionException ex) {
                    LOG.error("unable to find job bean definition for job: " + ex.getBeanName());
                } catch ( Exception ex ) {
                    LOG.error( "Unable to install " + jobName + " job into scheduler.", ex );
                }
            }
        }
    }

    /**
     * Loops through all the triggers associated with the given module service, adding each trigger to Quartz
     * @param moduleService the ModuleService instance to initialize triggers for
     */
    protected void initializeTriggersForModule(ModuleService moduleService) {
        if ( moduleService.getModuleConfiguration().getTriggerNames() != null ) {
            for (String triggerName : moduleService.getModuleConfiguration().getTriggerNames()) {
                try {
                    addTrigger(BatchSpringContext.getTriggerDescriptor(triggerName).getTrigger());
                } catch (NoSuchBeanDefinitionException ex) {
                    LOG.error("unable to find trigger definition: " + ex.getBeanName());
                } catch ( Exception ex ) {
                    LOG.error( "Unable to install " + triggerName + " trigger into scheduler.", ex );
                }
            }
        }
    }


    protected void loadJob(JobDescriptor jobDescriptor) {
        JobDetail jobDetail = jobDescriptor.getJobDetail();
        addJob(jobDetail);
        if (SCHEDULED_GROUP.equals(jobDetail.getGroup())) {
            jobDetail.setGroup(UNSCHEDULED_GROUP);
            addJob(jobDetail);
        }
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.SchedulerService#initializeJob(java.lang.String,org.kuali.kfs.sys.batch.Job)
     */
    public void initializeJob(String jobName, Job job) {
        job.setSchedulerService(this);
        job.setParameterService(parameterService);
        job.setSteps(BatchSpringContext.getJobDescriptor(jobName).getSteps());
        job.setDateTimeService(dateTimeService);
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.SchedulerService#hasIncompleteJob()
     */
    public boolean hasIncompleteJob() {
        try {
            StringBuffer log = new StringBuffer("The schedule has incomplete jobs.");
            boolean hasIncompleteJob = false;
            for (String scheduledJobName : scheduler.getJobNames(SCHEDULED_GROUP)) {
                JobDetail scheduledJobDetail = getScheduledJobDetail(scheduledJobName);
                boolean jobIsIncomplete = isIncomplete(scheduledJobDetail);
                if (jobIsIncomplete) {
                    log.append("\n\t").append(scheduledJobDetail.getFullName());
                    hasIncompleteJob = true;
                }
            }
            if (hasIncompleteJob) {
                LOG.info(log);
            }
            return hasIncompleteJob;
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while getting list of jobs to check for incompletes", e);
        }
    }

    protected boolean isIncomplete(JobDetail scheduledJobDetail) {
        if ( scheduledJobDetail == null ) {
            return false;
        }
        try {
            if (!SCHEDULE_JOB_NAME.equals(scheduledJobDetail.getName()) && (isPending(scheduledJobDetail) || isScheduled(scheduledJobDetail))) {
                Trigger[] triggersOfJob = scheduler.getTriggersOfJob(scheduledJobDetail.getName(), SCHEDULED_GROUP);
                if (triggersOfJob.length > 0) {
                    for (int triggerIndex = 0; triggerIndex < triggersOfJob.length; triggerIndex++) {
                        if (triggersOfJob[triggerIndex].getNextFireTime() != null && !isPastScheduleCutoffTime(dateTimeService.getCalendar(triggersOfJob[triggerIndex].getNextFireTime()), false)) {
                            return true;
                        }
                    }
                }
                else {
                    return true;
                }
            }
            return false;
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while checking job for completeness: " + scheduledJobDetail.getFullName(), e);
        }
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.SchedulerService#isPastScheduleCutoffTime()
     */
    public boolean isPastScheduleCutoffTime() {
        return isPastScheduleCutoffTime(dateTimeService.getCurrentCalendar(), true);
    }

    protected boolean isPastScheduleCutoffTime(Calendar dateTime, boolean log) {
        try {
            Date scheduleCutoffTimeTemp = scheduler.getTriggersOfJob(SCHEDULE_JOB_NAME, SCHEDULED_GROUP)[0].getPreviousFireTime();
            Calendar scheduleCutoffTime;
            if (scheduleCutoffTimeTemp == null) {
                scheduleCutoffTime = dateTimeService.getCurrentCalendar();
            }
            else {
                scheduleCutoffTime = dateTimeService.getCalendar(scheduleCutoffTimeTemp);
            }
            String[] scheduleStepCutoffTime = StringUtils.split(parameterService.getParameterValue(ScheduleStep.class, KFSConstants.SystemGroupParameterNames.BATCH_SCHEDULE_CUTOFF_TIME), ":");
            scheduleCutoffTime.set(Calendar.HOUR, Integer.parseInt(scheduleStepCutoffTime[0]));
            scheduleCutoffTime.set(Calendar.MINUTE, Integer.parseInt(scheduleStepCutoffTime[1]));
            scheduleCutoffTime.set(Calendar.SECOND, Integer.parseInt(scheduleStepCutoffTime[2]));
            if ("AM".equals(scheduleStepCutoffTime[3].trim())) {
                scheduleCutoffTime.set(Calendar.AM_PM, Calendar.AM);
            }
            else {
                scheduleCutoffTime.set(Calendar.AM_PM, Calendar.PM);
            }
            if (parameterService.getIndicatorParameter(ScheduleStep.class, KFSConstants.SystemGroupParameterNames.BATCH_SCHEDULE_CUTOFF_TIME_IS_NEXT_DAY)) {
                scheduleCutoffTime.add(Calendar.DAY_OF_YEAR, 1);
            }
            boolean isPastScheduleCutoffTime = dateTime.after(scheduleCutoffTime);
            if (log) {
                LOG.info(new StringBuffer("isPastScheduleCutoffTime=").append(isPastScheduleCutoffTime).append(" : ").append(dateTimeService.toDateTimeString(dateTime.getTime())).append(" / ").append(dateTimeService.toDateTimeString(scheduleCutoffTime.getTime())));
            }
            return isPastScheduleCutoffTime;
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Caught exception while checking whether we've exceeded the schedule cutoff time", e);
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while checking whether we've exceeded the schedule cutoff time", e);
        }
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.SchedulerService#processWaitingJobs()
     */
    public void processWaitingJobs() {
        try {
            for (String scheduledJobName : scheduler.getJobNames(SCHEDULED_GROUP)) {
                JobDetail jobDetail = getScheduledJobDetail(scheduledJobName);
                if (isPending(jobDetail)) {
                    if (shouldScheduleJob(jobDetail)) {
                        scheduleJob(SCHEDULED_GROUP, scheduledJobName, 0, 0, new Date(), null);
                    }
                    if (shouldCancelJob(jobDetail)) {
                        updateStatus(SCHEDULED_GROUP, scheduledJobName, CANCELLED_JOB_STATUS_CODE);
                    }
                }
            }
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while trying processing waiting jobs", e);
        }
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.SchedulerService#logScheduleResults()
     */
    public void logScheduleResults() {
        StringBuffer scheduleResults = new StringBuffer("The schedule completed.");
        try {
            for (String scheduledJobName : scheduler.getJobNames(SCHEDULED_GROUP)) {
                JobDetail jobDetail = getScheduledJobDetail(scheduledJobName);
                if ( jobDetail != null &&  !SCHEDULE_JOB_NAME.equals(jobDetail.getName())) {
                    scheduleResults.append("\n\t").append(jobDetail.getName()).append("=").append(getStatus(jobDetail));
                }
            }
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while trying to logs schedule results", e);
        }
        LOG.info(scheduleResults);
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.SchedulerService#shouldNotRun(org.quartz.JobDetail)
     */
    public boolean shouldNotRun(JobDetail jobDetail) {
        if (SCHEDULED_GROUP.equals(jobDetail.getGroup())) {
            if (isCancelled(jobDetail)) {
                if ( LOG.isInfoEnabled() ) {
                    LOG.info("Telling listener not to run job, because it has been cancelled: " + jobDetail.getName());
                }
                return true;
            }
            else {
                for (String dependencyJobName : getJobDependencies(jobDetail.getName()).keySet()) {
                    if (!isDependencySatisfiedPositively(jobDetail, getScheduledJobDetail(dependencyJobName))) {
                        if ( LOG.isInfoEnabled() ) {
                            LOG.info("Telling listener not to run job, because a dependency has not been satisfied positively: "+jobDetail.getName()+" (dependency job = "+dependencyJobName+")");
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.SchedulerService#updateStatus(org.quartz.JobDetail,java.lang.String jobStatus)
     */
    public void updateStatus(JobDetail jobDetail, String jobStatus) {
        if ( LOG.isInfoEnabled() ) {
            LOG.info("Updating status of job: "+jobDetail.getName()+"="+jobStatus);
        }
        jobDetail.getJobDataMap().put(JOB_STATUS_PARAMETER, jobStatus);
    }

    public void runJob(String jobName, String requestorEmailAddress) {
        runJob(jobName, 0, 0, new Date(), requestorEmailAddress);
    }

    public void runJob(String jobName, int startStep, int stopStep, Date startTime, String requestorEmailAddress) {
        runJob(UNSCHEDULED_GROUP, jobName, startStep, stopStep, startTime, requestorEmailAddress);
    }

    public void runJob(String groupName, String jobName, int startStep, int stopStep, Date jobStartTime, String requestorEmailAddress) {
        if ( LOG.isInfoEnabled() ) {
            LOG.info("Executing user initiated job: " + groupName + "." + jobName + " (startStep=" + startStep + " / stopStep=" + stopStep + " / startTime=" + jobStartTime + " / requestorEmailAddress=" + requestorEmailAddress + ")");
        }

        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobName, groupName);
            scheduleJob(groupName, jobName, startStep, stopStep, jobStartTime, requestorEmailAddress);
        }
        catch (SchedulerException ex) {
            throw new RuntimeException("Unable to run a job directly", ex);
        }
    }

    public void runStep(String groupName, String jobName, String stepName, Date startTime, String requestorEmailAddress) {
        if ( LOG.isInfoEnabled() ) {
            LOG.info("Executing user initiated step: " + stepName + " / requestorEmailAddress=" + requestorEmailAddress);
        }

        // abort if the step is already running
        if (isJobRunning(jobName)) {
            LOG.warn("Attempt to run job already executing, aborting");
            return;
        }
        int stepNum = 1;
        boolean stepFound = false;
        BatchJobStatus job = getJob(groupName, jobName);
        for (Step step : job.getSteps()) {
            if (step.getName().equals(stepName)) {
                stepFound = true;
                break;
            }
            stepNum++;
        }
        if (stepFound) {
            runJob(groupName, jobName, stepNum, stepNum, startTime, requestorEmailAddress);
        }
        else {
            LOG.warn("Unable to find step " + stepName + " in job " + groupName + "." + jobName);
        }
    }

    public boolean isJobRunning(String jobName) {
        List<JobExecutionContext> runningJobs = getRunningJobs();
        for (JobExecutionContext jobCtx : runningJobs) {
            if (jobCtx.getJobDetail().getName().equals(jobName)) {
                return true;
            }
        }
        return false;
    }

    protected void addJob(JobDetail jobDetail) {
        try {
            if ( LOG.isInfoEnabled() ) {
                LOG.info("Adding job: " + jobDetail.getFullName());
            }
            scheduler.addJob(jobDetail, true);
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while adding job: " + jobDetail.getFullName(), e);
        }
    }

    protected void addTrigger(Trigger trigger) {
        try {
            if (UNSCHEDULED_GROUP.equals(trigger.getGroup())) {
                LOG.error("Triggers should not be specified for jobs in the unscheduled group - not adding trigger: " + trigger.getName());
            }
            else {
                LOG.info("Adding trigger: " + trigger.getName());
                try {
                    scheduler.scheduleJob(trigger);
                }
                catch (ObjectAlreadyExistsException ex) {
                }
            }
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while adding trigger: " + trigger.getFullName(), e);
        }
    }

    protected void scheduleJob(String groupName, String jobName, int startStep, int endStep, Date startTime, String requestorEmailAddress) {
        try {
            updateStatus(groupName, jobName, SchedulerService.SCHEDULED_JOB_STATUS_CODE);
            SimpleTriggerDescriptor trigger = new SimpleTriggerDescriptor(jobName, groupName, jobName, dateTimeService);
            trigger.setStartTime(startTime);
            Trigger qTrigger = trigger.getTrigger();
            qTrigger.getJobDataMap().put(JobListener.REQUESTOR_EMAIL_ADDRESS_KEY, requestorEmailAddress);
            qTrigger.getJobDataMap().put(Job.JOB_RUN_START_STEP, String.valueOf(startStep));
            qTrigger.getJobDataMap().put(Job.JOB_RUN_END_STEP, String.valueOf(endStep));
            for (Trigger oldTrigger : scheduler.getTriggersOfJob(jobName, groupName)) {
                scheduler.unscheduleJob(oldTrigger.getName(), groupName);
            }
            scheduler.scheduleJob(qTrigger);
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Caught exception while scheduling job: " + jobName, e);
        }
    }

    protected boolean shouldScheduleJob(JobDetail jobDetail) {
        try {
            if (scheduler.getTriggersOfJob(jobDetail.getName(), SCHEDULED_GROUP).length > 0) {
                return false;
            }
            for (String dependencyJobName : getJobDependencies(jobDetail.getName()).keySet()) {
                JobDetail dependencyJobDetail = getScheduledJobDetail(dependencyJobName);
                if ( dependencyJobDetail == null ) {
                    LOG.error( "Unable to get JobDetail for dependency of " + jobDetail.getName() + " : " + dependencyJobName );
                    return false;
                }
                if (!isDependencySatisfiedPositively(jobDetail, dependencyJobDetail)) {
                    return false;
                }
            }
        }
        catch (SchedulerException se) {
            throw new RuntimeException("Caught scheduler exception while determining whether to schedule job: " + jobDetail.getName(), se);
        }
        return true;
    }

    protected boolean shouldCancelJob(JobDetail jobDetail) {
        if ( jobDetail == null ) {
            return true;
        }
        for (String dependencyJobName : getJobDependencies(jobDetail.getName()).keySet()) {
            JobDetail dependencyJobDetail = getScheduledJobDetail(dependencyJobName);
            if (isDependencySatisfiedNegatively(jobDetail, dependencyJobDetail)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isDependencySatisfiedPositively(JobDetail dependentJobDetail, JobDetail dependencyJobDetail) {
        if ( dependentJobDetail == null || dependencyJobDetail == null ) {
            return false;
        }
        return isSucceeded(dependencyJobDetail) || ((isFailed(dependencyJobDetail) || isCancelled(dependencyJobDetail)) && isSoftDependency(dependentJobDetail.getName(), dependencyJobDetail.getName()));
    }

    protected boolean isDependencySatisfiedNegatively(JobDetail dependentJobDetail, JobDetail dependencyJobDetail) {
        if ( dependentJobDetail == null || dependencyJobDetail == null ) {
            return true;
        }
        return (isFailed(dependencyJobDetail) || isCancelled(dependencyJobDetail)) && !isSoftDependency(dependentJobDetail.getName(), dependencyJobDetail.getName());
    }

    protected boolean isSoftDependency(String dependentJobName, String dependencyJobName) {
        return SOFT_DEPENDENCY_CODE.equals(getJobDependencies(dependentJobName).get(dependencyJobName));
    }

    protected Map<String, String> getJobDependencies(String jobName) {
        return BatchSpringContext.getJobDescriptor(jobName).getDependencies();
    }

    protected boolean isPending(JobDetail jobDetail) {
        return getStatus(jobDetail) == null;
    }

    protected boolean isScheduled(JobDetail jobDetail) {
        return SCHEDULED_JOB_STATUS_CODE.equals(getStatus(jobDetail));
    }

    protected boolean isSucceeded(JobDetail jobDetail) {
        return SUCCEEDED_JOB_STATUS_CODE.equals(getStatus(jobDetail));
    }

    protected boolean isFailed(JobDetail jobDetail) {
        return FAILED_JOB_STATUS_CODE.equals(getStatus(jobDetail));
    }

    protected boolean isCancelled(JobDetail jobDetail) {
        return CANCELLED_JOB_STATUS_CODE.equals(getStatus(jobDetail));
    }

    public String getStatus(JobDetail jobDetail) {
        if ( jobDetail == null ) {
            return FAILED_JOB_STATUS_CODE;
        }
        KfsModuleServiceImpl moduleService = (KfsModuleServiceImpl)
            SpringContext.getBean(KualiModuleService.class).getResponsibleModuleServiceForJob(jobDetail.getName());
        //If the module service has status information for a job, get the status from it
        //else get status from job detail data map 
        return (moduleService!=null && moduleService.isExternalJob(jobDetail.getName()))
                    ? moduleService.getExternalJobStatus(jobDetail.getName())
                    : jobDetail.getJobDataMap().getString(SchedulerServiceImpl.JOB_STATUS_PARAMETER);
    }

    protected JobDetail getScheduledJobDetail(String jobName) {
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobName, SCHEDULED_GROUP);
            if ( jobDetail == null ) {
                LOG.error( "Unable to obtain the job details for the scheduled version of: " + jobName );
            }
            return jobDetail;
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Caught scheduler exception while getting job detail: " + jobName, e);
        }
    }

    /**
     * Sets the scheduler attribute value.
     * 
     * @param scheduler The scheduler to set.
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the moduleService attribute value.
     * 
     * @param moduleService The moduleService to set.
     */
    public void setKualiModuleService(KualiModuleService moduleService) {
        this.kualiModuleService = moduleService;
    }

    /**
     * Sets the jobListener attribute value.
     * 
     * @param jobListener The jobListener to set.
     */
    public void setJobListener(JobListener jobListener) {
        this.jobListener = jobListener;
    }

    public List<BatchJobStatus> getJobs() {
        ArrayList<BatchJobStatus> jobs = new ArrayList<BatchJobStatus>();
        try {
            for (String jobGroup : scheduler.getJobGroupNames()) {
                for (String jobName : scheduler.getJobNames(jobGroup)) {
                    try {
                        JobDescriptor jobDescriptor = retrieveJobDescriptor(jobName);
                        JobDetail jobDetail = scheduler.getJobDetail(jobName, jobGroup);
                        jobs.add(new BatchJobStatus(jobDescriptor, jobDetail));
                    }
                    catch (NoSuchBeanDefinitionException ex) {
                        // do nothing, ignore jobs not defined in spring
                        LOG.warn("Attempt to find bean " + jobGroup + "." + jobName + " failed - not in Spring context");
                    }
                }
            }
        }
        catch (SchedulerException ex) {
            throw new RuntimeException("Exception while obtaining job list", ex);
        }
        return jobs;
    }

    public BatchJobStatus getJob(String groupName, String jobName) {
        for (BatchJobStatus job : getJobs()) {
            if (job.getName().equals(jobName) && job.getGroup().equals(groupName)) {
                return job;
            }
        }
        return null;
    }

    public List<BatchJobStatus> getJobs(String groupName) {
        ArrayList<BatchJobStatus> jobs = new ArrayList<BatchJobStatus>();
        try {
            for (String jobName : scheduler.getJobNames(groupName)) {
                try {
                    JobDescriptor jobDescriptor = retrieveJobDescriptor(jobName);
                    JobDetail jobDetail = scheduler.getJobDetail(jobName, groupName);
                    jobs.add(new BatchJobStatus(jobDescriptor, jobDetail));
                }
                catch (NoSuchBeanDefinitionException ex) {
                    // do nothing, ignore jobs not defined in spring
                    LOG.warn("Attempt to find bean " + groupName + "." + jobName + " failed - not in Spring context");
                }
            }
        }
        catch (SchedulerException ex) {
            throw new RuntimeException("Exception while obtaining job list", ex);
        }
        return jobs;
    }

    public List<JobExecutionContext> getRunningJobs() {
        try {
            List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
            return jobContexts;
        }
        catch (SchedulerException ex) {
            throw new RuntimeException("Unable to get list of running jobs.", ex);
        }
    }

    protected void updateStatus(String groupName, String jobName, String jobStatus) {
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobName, groupName);
            updateStatus(jobDetail, jobStatus);
            scheduler.addJob(jobDetail, true);
        }
        catch (SchedulerException e) {
            throw new RuntimeException(new StringBuffer("Caught scheduler exception while updating job status: ").append(jobName).append(", ").append(jobStatus).toString(), e);
        }
    }

    public void removeScheduled(String jobName) {
        try {
            scheduler.deleteJob(jobName, SCHEDULED_GROUP);
        }
        catch (SchedulerException ex) {
            throw new RuntimeException("Unable to remove scheduled job: " + jobName, ex);
        }
    }

    public void addScheduled(JobDetail job) {
        try {
            job.setGroup(SCHEDULED_GROUP);
            scheduler.addJob(job, true);
        }
        catch (SchedulerException ex) {
            throw new RuntimeException("Unable to add job to scheduled group: " + job.getName(), ex);
        }
    }

    public void addUnscheduled(JobDetail job) {
        try {
            job.setGroup(UNSCHEDULED_GROUP);
            scheduler.addJob(job, true);
        }
        catch (SchedulerException ex) {
            throw new RuntimeException("Unable to add job to unscheduled group: " + job.getName(), ex);
        }
    }

    public List<String> getSchedulerGroups() {
        try {
            return Arrays.asList(scheduler.getJobGroupNames());
        }
        catch (SchedulerException ex) {
            throw new RuntimeException("Exception while obtaining job list", ex);
        }
    }

    public List<String> getJobStatuses() {
        return jobStatuses;
    }

    public void interruptJob(String jobName) {
        List<JobExecutionContext> runningJobs = getRunningJobs();
        for (JobExecutionContext jobCtx : runningJobs) {
            if (jobName.equals(jobCtx.getJobDetail().getName())) {
                // if so...
                try {
                    ((Job) jobCtx.getJobInstance()).interrupt();
                }
                catch (UnableToInterruptJobException ex) {
                    LOG.warn("Unable to perform job interrupt", ex);
                }
                break;
            }
        }

    }

    public Date getNextStartTime(BatchJobStatus job) {
        try {
            Trigger[] triggers = scheduler.getTriggersOfJob(job.getName(), job.getGroup());
            Date nextDate = new Date(Long.MAX_VALUE);
            for (Trigger trigger : triggers) {
                if (trigger.getNextFireTime().getTime() < nextDate.getTime()) {
                    nextDate = trigger.getNextFireTime();
                }
            }
            if (nextDate.getTime() == Long.MAX_VALUE) {
                nextDate = null;
            }
            return nextDate;
        }
        catch (SchedulerException ex) {

        }
        return null;
    }

    public Date getNextStartTime(String groupName, String jobName) {
        BatchJobStatus job = getJob(groupName, jobName);

        return getNextStartTime(job);
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
    
    protected JobDescriptor retrieveJobDescriptor(String jobName) {
        if (externalizedJobDescriptors.containsKey(jobName)) {
            return externalizedJobDescriptors.get(jobName);
        }
        return BatchSpringContext.getJobDescriptor(jobName);
    }
    
    public void reinitializeScheduledJobs() {
        try {
            for (String scheduledJobName : scheduler.getJobNames(SCHEDULED_GROUP)) {
                if (scheduler.getTriggersOfJob(scheduledJobName, SCHEDULED_GROUP).length == 0) {
                    // jobs that have their own triggers will not be reinited
                    updateStatus(SCHEDULED_GROUP, scheduledJobName, null);
                }
            }
        }
        catch (Exception e) {
            LOG.error("Error occurred while trying to reinitialize jobs", e);
        }
    }
}
