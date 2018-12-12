/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.quartz;

import de.wacodis.core.models.WacodisJobDefinition;
import java.util.Date;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class SchedulingManager {

    @Autowired
    private QuartzScheduler quartzScheduler;

    @Autowired
    private JobContextFactory jCFactory;

    public Date scheduleNewJob(WacodisJobDefinition jobDefinition) throws SchedulerException {
        JobContext jobContext = jCFactory.createJobContext(jobDefinition);
        return scheduleNewJob(jobContext.getJobDetails(), jobContext.getTrigger());
    }

    public Date scheduleNewJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        return quartzScheduler.getScheduler().scheduleJob(jobDetail, trigger);	//runs QuartzJob's execute()
    }

}
