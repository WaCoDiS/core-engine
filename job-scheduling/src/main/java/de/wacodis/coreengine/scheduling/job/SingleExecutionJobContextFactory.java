/*
 * Copyright 2018-2021 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * factory class for job context information for single (retry) wacodis job
 * execution
 *
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
     *
     * @param jobDefinition
     * @param jobData provide additional data to be stored in the job data map
     * @param customIdentifier
     * @return
     */
    public JobContext createSingleExecutionJobContextStartNow(WacodisJobDefinition jobDefinition, Map<String, String> jobData, String customIdentifier) {
        JobContext jobContext = new JobContext();
        jobContext.setJobDetails(createJobDetail(jobDefinition, jobData, customIdentifier));
        jobContext.setTrigger(createFireOnceTriggerNow(customIdentifier));
        return jobContext;
    }

    /**
     * create job context with trigger that fires once with a delay specified in
     * jobDefiontion.retrySettings, if retrySettings are not available default
     * delay of 10 minutes is applied
     *
     * @param jobDefinition
     * @param jobData provide additional data to be stored in the job data map
     * @param customIdentifier
     * @return
     */
    public JobContext createSingleExecutionContextStartDelayed(WacodisJobDefinition jobDefinition, Map<String, String> jobData, String customIdentifier) {
        WacodisJobDefinitionRetrySettings retrySettings = (jobDefinition.getRetrySettings() != null) ? jobDefinition.getRetrySettings() : getDefaultRetrySettings();
        Date fireAt = getDelayedFireTime(retrySettings);

        return createSingleExecutionContextStartAt(jobDefinition, jobData, fireAt, customIdentifier);
    }

    /**
     * create job context with trigger that fires at the specified data
     *
     * @param jobDefinition
     * @param jobData
     * @param fireAt date when the trigger fires
     * @param customIdentifier
     * @return
     */
    public JobContext createSingleExecutionContextStartAt(WacodisJobDefinition jobDefinition, Map<String, String> jobData, Date fireAt, String customIdentifier) {
        JobContext jobContext = new JobContext();
        jobContext.setJobDetails(createJobDetail(jobDefinition, jobData, customIdentifier));
        jobContext.setTrigger(createFireOnceTriggerAt(fireAt, customIdentifier));
        return jobContext;
    }

    /**
     * create job context with trigger that fires once immediately,
     * jobDefinition.id is used as triggerKey and jobName
     *
     * @param jobDefinition
     * @param jobData provide additional data to be stored in the job data map
     * @return
     */
    public JobContext createSingleExecutionJobContextStartNow(WacodisJobDefinition jobDefinition, Map<String, String> jobData) {
        return createSingleExecutionJobContextStartNow(jobDefinition, jobData, jobDefinition.getId().toString());
    }

    /**
     * create job context with trigger that fires once with a delay specified in
     * jobDefiontion.retrySettings, if retrySettings are not available default
     * delay of 10 minutes is applied, jobDefinition.id is used as triggerKey and jobName
     *
     * @param jobDefinition
     * @param jobData provide additional data to be stored in the job data map
     * @return
     */
    public JobContext createSingleExecutionContextStartDelayed(WacodisJobDefinition jobDefinition, Map<String, String> jobData) {
        return createSingleExecutionContextStartDelayed(jobDefinition, jobData, jobDefinition.getId().toString());
    }

    /**
     * create job context with trigger that fires at the specified data,
     * jobDefinition.id is used as triggerKey and jobName
     *
     * @param jobDefinition
     * @param jobData
     * @param fireAt date when the trigger fires
     * @return
     */
    public JobContext createSingleExecutionContextStartAt(WacodisJobDefinition jobDefinition, Map<String, String> jobData, Date fireAt) {
        return createSingleExecutionContextStartAt(jobDefinition, jobData, fireAt, jobDefinition.getId().toString());
    }

    private Trigger createFireOnceTriggerNow(String triggerKey) {
        SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity(jobContextFactory.createTriggerKey(triggerKey))
                .startNow()
                .build();

        return trigger;
    }

    private Trigger createFireOnceTriggerAt(Date at, String triggerKey) {
        Trigger trigger = newTrigger()
                .withIdentity(jobContextFactory.createTriggerKey(triggerKey))
                .startAt(at)
                .build();
        
        return trigger;
    }

    private JobDetail createJobDetail(WacodisJobDefinition jobDefinition, Map<String, String> jobData, String jobName) {
        return jobDetailFactory.createJob(WacodisJob.class, false, false,
                jobName, GROUP_NAME, createJobDataMap(jobDefinition, jobData));
    }

    private JobDataMap createJobDataMap(WacodisJobDefinition jobDefinition, Map<String, String> jobData) {
        JobDataMap jdm = jobContextFactory.createJobDataMap(jobDefinition);
        jdm.putAll(jobData);

        return jdm;
    }

    private Date getDelayedFireTime(WacodisJobDefinitionRetrySettings retrySettings) {
        long now = System.currentTimeMillis();
        long delay = retrySettings.getRetryDelayMillies();

        return new Date(now + delay);
    }

    private WacodisJobDefinitionRetrySettings getDefaultRetrySettings() {
        WacodisJobDefinitionRetrySettings retrySettings = new WacodisJobDefinitionRetrySettings();
        retrySettings.setMaxRetries(DEFAULTMAXRETRIES);
        retrySettings.setRetryDelayMillies(DEFAULTRETRYDELAY);

        return retrySettings;
    }
}
