/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.quartz;

import de.wacodis.core.models.WacodisJobDefinition;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class SchedulingManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingManager.class);

    @Autowired
    private QuartzScheduler quartzScheduler;

    @Autowired
    private JobContextFactory jCFactory;

    public void scheduleNewJob(WacodisJobDefinition jobDefinition) {
        try {
            JobContext jobContext = jCFactory.createJobContext(jobDefinition);
            scheduleNewJob(jobContext.getJobDetails(), jobContext.getTrigger());
        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while parsing job definition", ex);
        }
    }

    public void scheduleNewJob(WacodisJobDefinition jobDefinition, String timeZoneId) {
        try {
            JobContext jobContext = jCFactory.createJobContext(jobDefinition, timeZoneId);
            scheduleNewJob(jobContext.getJobDetails(), jobContext.getTrigger());
        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while parsing job definition", ex);
        }
    }

    public void scheduleNewJob(JobDetail jobDetail, Trigger trigger) {
        try {
            Date date = quartzScheduler.getScheduler().scheduleJob(jobDetail, trigger);	//runs QuartzJob's execute()
            LOGGER.info("Scheduling for job {} was successful. First fire time: {}", jobDetail.getKey(), date);
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while trying to schedule job", ex);
        }
    }

}
