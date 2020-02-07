/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.manage;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import de.wacodis.coreengine.scheduling.job.JobContext;
import de.wacodis.coreengine.scheduling.job.RetryJobContextFactory;
import java.util.Date;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 *
 * @author Arne
 */
@Component
public class QuartzRetrySchedulingManager implements RetrySchedulingManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzRetrySchedulingManager.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private RetryJobContextFactory jCFactory;

    @Override
    public Date scheduleRetryImmediately(WacodisJobDefinition jobDefinition, WacodisJobExecutionContext context) {
        Date firstFiringTime = null;
        JobContext quartzJobContext = jCFactory.createRetryJobContext(jobDefinition, context);

        try {
            firstFiringTime = schedulerFactoryBean.getScheduler().scheduleJob(quartzJobContext.getJobDetails(), quartzJobContext.getTrigger());	//runs QuartzJob's execute()
            LOGGER.info("Scheduling retry attempt for job {} was successful. Retry fire time: {}", quartzJobContext.getJobDetails().getKey(), firstFiringTime);

        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while trying to schedule retry attempt for  job " + quartzJobContext.getJobDetails().getKey(), ex);
        }

        return firstFiringTime;
    }

    @Override
    public Date scheduleRetryAt(WacodisJobDefinition jobDefinition, WacodisJobExecutionContext context, Date at) {
        Date firstFiringTime = null;
        JobContext quartzJobContext = jCFactory.createRetryJobContext(jobDefinition, context, at);

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
