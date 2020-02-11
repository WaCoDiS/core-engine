/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.job;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionRetrySettings;
import static de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants.GROUP_NAME;
import java.util.Date;
import java.util.Map;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import static org.quartz.TriggerBuilder.newTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * factory class for job context information for single (retry) wacodis job execution
 * @author Arne
 */
@Component
public class SingleExecutionJobContextFactory {
    
    private final static int DEFAULTMAXRETRIES = 1;
    private final static long DEFAULTRETRYDELAY = 600000l; //10 min

    @Autowired
    private JobDetailFactory jobDetailFactory;

    @Autowired
    private JobContextFactory jobContextFactory;

    /**
     * create job context with trigger that fires once immediately
     * @param jobDefinition
     * @param jobData provide additional data to be stored in the job data map
     * @return 
     */
    public JobContext createSingleExecutionJobContextStartNow(WacodisJobDefinition jobDefinition, Map<String, Object> jobData) {
        JobContext jobContext = new JobContext();
        jobContext.setJobDetails(createJobDetail(jobDefinition, jobData));
        jobContext.setTrigger(createFireOnceTriggerNow(jobDefinition));
        return jobContext;
    }

    /**
     * create job context with trigger that fires once with a delay specified in jobDefiontion.retrySettings,
     * if retrySettings are not available default delay of 10 minutes is applied
     * @param jobDefinition
     * @param jobData provide additional data to be stored in the job data map
     * @return 
     */
    public JobContext createSingleExecutionContextStartDelayed(WacodisJobDefinition jobDefinition, Map<String, Object> jobData) {
        WacodisJobDefinitionRetrySettings retrySettings = (jobDefinition.getRetrySettings() != null) ? jobDefinition.getRetrySettings() : getDefaultRetrySettings();
        
        JobContext jobContext = new JobContext();
        jobContext.setJobDetails(createJobDetail(jobDefinition, jobData));
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

    private JobDetail createJobDetail(WacodisJobDefinition jobDefinition, Map<String, Object> jobData) {
        return jobDetailFactory.createJob(WacodisJob.class, false, false,
                jobDefinition.getId().toString(), GROUP_NAME, createJobDataMap(jobDefinition, jobData));
    }

    private JobDataMap createJobDataMap(WacodisJobDefinition jobDefinition, Map<String, Object> jobData) {
        JobDataMap jdm = jobContextFactory.createJobDataMap(jobDefinition);
        jdm.putAll(jobData);

        return jdm;
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
