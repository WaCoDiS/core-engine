/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.manage;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.scheduling.job.JobContext;
import de.wacodis.coreengine.scheduling.job.SingleExecutionJobContextFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * schedule single wacodis job excution
 *
 * @author Arne
 */
@Component
public class QuartzSingleExecutionSchedulingManager implements SingleExecutionSchedulingManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzSingleExecutionSchedulingManager.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private SingleExecutionJobContextFactory jCFactory;

    /**
     * schedule single job execution, scheduler will fire immediately
     *
     * @param jobDefinition
     * @param jobData additional parameters used for job execution
     * @param customIdentifier  used as trigger key and job name
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleJobExecutionImmediately(WacodisJobDefinition jobDefinition, Map<String, String> jobData, String customIdentifier) {
        Date firstFiringTime = null;
        JobContext quartzJobContext = jCFactory.createSingleExecutionJobContextStartNow(jobDefinition, jobData, customIdentifier);

        try {
            firstFiringTime = schedulerFactoryBean.getScheduler().scheduleJob(quartzJobContext.getJobDetails(), quartzJobContext.getTrigger());	//runs QuartzJob's execute()
            LOGGER.info("Scheduling single execution for job {} was successful. Execution is triggerd at {} {}", quartzJobContext.getJobDetails().getKey(), firstFiringTime);

        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while trying to schedule retry attempt for  job " + quartzJobContext.getJobDetails().getKey(), ex);
        }

        return firstFiringTime;
    }

    /**
     * schedule single job execution, scheduler will fire delayed, firing time
     * is calculate considering jobDefinition.retrySettings.retryDelaysMillies
     *
     * @param jobDefinition
     * @param jobData additional parameters used for job execution
     * @param customIdentifier used as trigger key and job name
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleJobExecutionDelayed(WacodisJobDefinition jobDefinition, Map<String, String> jobData, String customIdentifier) {
        Date firstFiringTime = null;
        JobContext quartzJobContext = jCFactory.createSingleExecutionContextStartDelayed(jobDefinition, jobData, customIdentifier); //respect delay for schedule

        try {
            firstFiringTime = schedulerFactoryBean.getScheduler().scheduleJob(quartzJobContext.getJobDetails(), quartzJobContext.getTrigger());	//runs QuartzJob's execute()
            LOGGER.info("Scheduling single execution for job {} was successful. Execution is triggerd at {}", quartzJobContext.getJobDetails().getKey(), firstFiringTime);
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while trying to schedule single execution for  job " + quartzJobContext.getJobDetails().getKey(), ex);
        }

        return firstFiringTime;
    }

    /**
     * schedule single job execution, scheduler will fire on specified date
     *
     * @param jobDefinition
     * @param jobData additional parameters used for job execution
     * @param startAt
     * @param customIdentifier used as trigger key and job name
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleJobExecutionAt(WacodisJobDefinition jobDefinition, Map<String, String> jobData, Date startAt, String customIdentifier) {
        Date firstFiringTime = null;
        JobContext quartzJobContext = jCFactory.createSingleExecutionContextStartAt(jobDefinition, jobData, startAt, customIdentifier); //fire on specified date

        try {
            firstFiringTime = schedulerFactoryBean.getScheduler().scheduleJob(quartzJobContext.getJobDetails(), quartzJobContext.getTrigger());	//runs QuartzJob's execute()
            LOGGER.info("Scheduling single execution for job {} was successful. Execution is triggered at {}", quartzJobContext.getJobDetails().getKey(), firstFiringTime);
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while trying to schedule single execution for  job " + quartzJobContext.getJobDetails().getKey(), ex);
        }

        return firstFiringTime;
    }

    /**
     * schedule single job execution, scheduler will fire immediately,
     * jobDefinition.id is used as triggerKey and jobName
     *
     * @param jobDefinition
     * @param jobData additional parameters used for job execution
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleJobExecutionImmediately(WacodisJobDefinition jobDefinition, Map<String, String> jobData) {
        return scheduleSingleJobExecutionImmediately(jobDefinition, jobData, jobDefinition.getId().toString());
    }

    /**
     * schedule single job execution, scheduler will fire delayed, firing time
     * is calculate considering jobDefinition.retrySettings.retryDelaysMillies,
     * jobDefinition.id is used as triggerKey and jobName
     *
     * @param jobDefinition
     * @param jobData additional parameters used for job execution
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleJobExecutionDelayed(WacodisJobDefinition jobDefinition, Map<String, String> jobData) {
        return scheduleSingleJobExecutionDelayed(jobDefinition, jobData, jobDefinition.getId().toString());
    }

    /**
     * schedule single job execution, scheduler will fire on specified date,
     * jobDefinition.id is used as triggerKey and jobName
     *
     * @param jobDefinition
     * @param jobData additional parameters used for job execution
     * @param startAt
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleJobExecutionAt(WacodisJobDefinition jobDefinition, Map<String, String> jobData, Date startAt) {
        return scheduleSingleJobExecutionAt(jobDefinition, jobData, startAt, jobDefinition.getId().toString());
    }

    /**
     * schedule single job execution, scheduler will fire immediately,
     * jobDefinition.id is used as triggerKey and jobName
     *
     * @param jobDefinition
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleJobExecutionImmediately(WacodisJobDefinition jobDefinition) {
        return this.scheduleSingleJobExecutionImmediately(jobDefinition, new HashMap<>()); //empty list of additional params
    }

    /**
     * schedule single job execution, scheduler will fire delayed, firing time
     * is calculate considering jobDefinition.retrySettings.retryDelaysMillies,
     * jobDefinition.id is used as triggerKey
     *
     * @param jobDefinition
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleJobExecutionDelayed(WacodisJobDefinition jobDefinition) {
        return this.scheduleSingleJobExecutionDelayed(jobDefinition, new HashMap<>()); //empty list of additional params
    }

    /**
     * schedule single job execution, scheduler will fire on specified date,
     * jobDefinition.id is used as triggerKey and jobName
     *
     * @param jobDefinition
     * @param startAt
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleJobExecutionAt(WacodisJobDefinition jobDefinition, Date startAt) {
        return scheduleSingleJobExecutionAt(jobDefinition, new HashMap<>(), startAt); //empty list of additional params
    }

    /**
     * schedule single job execution, scheduler will fire immediately
     *
     * @param jobDefinition
     * @param customIdentifier used as trigger key and job name
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleJobExecutionImmediately(WacodisJobDefinition jobDefinition, String customIdentifier) {
        return this.scheduleSingleJobExecutionImmediately(jobDefinition, new HashMap<>(), customIdentifier); //empty list of additional params
    }

    /**
     * schedule single job execution, scheduler will fire delayed, firing time
     * is calculate considering jobDefinition.retrySettings.retryDelaysMillies
     *
     * @param jobDefinition
     * @param customIdentifier used as trigger key and job name
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleJobExecutionDelayed(WacodisJobDefinition jobDefinition, String customIdentifier) {
        return this.scheduleSingleJobExecutionDelayed(jobDefinition, new HashMap<>(), customIdentifier); //empty list of additional params
    }

    /**
     * schedule single job execution, scheduler will fire on specified date
     *
     * @param jobDefinition
     * @param startAt
     * @param customIdentifier used as trigger key and job name
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleJobExecutionAt(WacodisJobDefinition jobDefinition, Date startAt, String customIdentifier) {
        return scheduleSingleJobExecutionAt(jobDefinition, new HashMap<>(), startAt, customIdentifier); //empty list of additional params
    }
}
