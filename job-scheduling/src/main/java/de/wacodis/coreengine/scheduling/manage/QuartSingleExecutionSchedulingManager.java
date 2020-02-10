/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.manage;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import de.wacodis.coreengine.scheduling.job.JobContext;
import de.wacodis.coreengine.scheduling.job.SingleExecutionJobContextFactory;
import java.util.Date;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * schedule single wacodis job excution
 * @author Arne
 */
@Component
public class QuartSingleExecutionSchedulingManager implements SingleExecutionSchedulingManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartSingleExecutionSchedulingManager.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private SingleExecutionJobContextFactory jCFactory;

    /**
     * schedule single retry, scheduler will fire immediately
     * @param jobDefinition
     * @param context
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleExecutionImmediately(WacodisJobDefinition jobDefinition, WacodisJobExecutionContext context) {
        Date firstFiringTime = null;
        JobContext quartzJobContext = jCFactory.createSingleExecutionJobContextStartNow(jobDefinition, context);

        try {
            firstFiringTime = schedulerFactoryBean.getScheduler().scheduleJob(quartzJobContext.getJobDetails(), quartzJobContext.getTrigger());	//runs QuartzJob's execute()
            LOGGER.info("Scheduling retry attempt for job {} was successful. Retry fire time: {}", quartzJobContext.getJobDetails().getKey(), firstFiringTime);

        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while trying to schedule retry attempt for  job " + quartzJobContext.getJobDetails().getKey(), ex);
        }

        return firstFiringTime;
    }

    /**
     * schedule single retry, scheduler will fire delayed, firing time is calculate considering jobDefinition.retrySettings.retryDelaysMillies
     * @param jobDefinition
     * @param context
     * @return firing time, null if scheduling failed
     */
    @Override
    public Date scheduleSingleExecutionDelayed(WacodisJobDefinition jobDefinition, WacodisJobExecutionContext context) {
        Date firstFiringTime = null;
        JobContext quartzJobContext = jCFactory.createdSingleExecutionContextStartDelayed(jobDefinition, context); //respect delay for schedule

        try {
            firstFiringTime = schedulerFactoryBean.getScheduler().scheduleJob(quartzJobContext.getJobDetails(), quartzJobContext.getTrigger());	//runs QuartzJob's execute()
            LOGGER.info("Scheduling retry attempt for job {} was successful. Retry fire time: {}", quartzJobContext.getJobDetails().getKey(), firstFiringTime);
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while trying to schedule retry attempt for  job " + quartzJobContext.getJobDetails().getKey(), ex);
        }

        return firstFiringTime;
    }

}
