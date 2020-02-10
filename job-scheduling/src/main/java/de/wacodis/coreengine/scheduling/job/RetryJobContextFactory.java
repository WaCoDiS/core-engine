/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.job;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionRetrySettings;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import static de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants.EXECUTION_ID_KEY;
import static de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants.GROUP_NAME;
import static de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants.RETRY_COUNT_KEY;
import java.util.Date;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import static org.quartz.TriggerBuilder.newTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Arne
 */
@Component
public class RetryJobContextFactory {
    
    private final static int DEFAULTMAXRETRIES = 3;
    private final static long DEFAULTRETRYDELAY = 600000l; //10 min

    @Autowired
    private JobDetailFactory jobDetailFactory;

    @Autowired
    private JobContextFactory jobContextFactory;

    public JobContext createRetryJobContextStartNow(WacodisJobDefinition jobDefinition, WacodisJobExecutionContext context) {
        JobContext jobContext = new JobContext();
        jobContext.setJobDetails(createJobDetail(jobDefinition, context));
        jobContext.setTrigger(createFireOnceTriggerNow(jobDefinition));
        return jobContext;
    }

    public JobContext createRetryJobContextStartDelayed(WacodisJobDefinition jobDefinition, WacodisJobExecutionContext context) {
        WacodisJobDefinitionRetrySettings retrySettings = (jobDefinition.getRetrySettings() != null) ? jobDefinition.getRetrySettings() : getDefaultRetrySettings();
        
        JobContext jobContext = new JobContext();
        jobContext.setJobDetails(createJobDetail(jobDefinition, context));
        jobContext.setTrigger(createFireOnceTriggerAt(jobDefinition, getDelayedFireTime(retrySettings)));
        return jobContext;
    }

    private Trigger createFireOnceTriggerNow(WacodisJobDefinition jobDefinition) {
        SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity(jobContextFactory.createTriggerKey(jobDefinition))
                .startNow()
                .build();

        return trigger;
    }

    private Trigger createFireOnceTriggerAt(WacodisJobDefinition jobDefinition, Date at) {
        Trigger trigger = newTrigger()
                .withIdentity(jobContextFactory.createTriggerKey(jobDefinition))
                .startAt(at)
                .build();

        return trigger;
    }

    private JobDetail createJobDetail(WacodisJobDefinition jobDefinition, WacodisJobExecutionContext context) {
        return jobDetailFactory.createJob(WacodisJob.class, false, false,
                jobDefinition.getId().toString(), GROUP_NAME, createJobDataMap(jobDefinition, context));
    }

    private JobDataMap createJobDataMap(WacodisJobDefinition jobDefiotnion, WacodisJobExecutionContext context) {
        JobDataMap jobData = jobContextFactory.createJobDataMap(jobDefiotnion);
        jobData.put(EXECUTION_ID_KEY, context.getExecutionID().toString());
        jobData.put(RETRY_COUNT_KEY, context.getRetryCount());

        return jobData;
    }

    private Date getDelayedFireTime(WacodisJobDefinitionRetrySettings retrySettings) {
        long now = System.currentTimeMillis();
        long delay = retrySettings.getRetryDelayMillies();

        return new Date(now + delay);
    }
    
    
    private WacodisJobDefinitionRetrySettings getDefaultRetrySettings(){
        WacodisJobDefinitionRetrySettings retrySettings = new WacodisJobDefinitionRetrySettings();
        retrySettings.setMaxRetries(DEFAULTMAXRETRIES);
        retrySettings.setRetryDelayMillies(DEFAULTRETRYDELAY);
        
        return retrySettings;
    }
}
