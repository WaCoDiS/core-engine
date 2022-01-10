/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
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
package de.wacodis.coreengine.scheduling.listener;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionRetrySettings;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import de.wacodis.coreengine.executor.events.WacodisJobExecutionFailedEvent;
import de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants;
import static de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants.EXECUTION_ID_KEY;
import static de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants.RETRY_COUNT_KEY;
import static de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants.RETRY_FIRST_EXECUTION_TIME_KEY;
import de.wacodis.coreengine.scheduling.manage.QuartzSingleExecutionSchedulingManager;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * handle failed wacodis job execution
 *
 * @author Arne
 */
@Component
public class WacodisJobExecutionFailedHandler implements ApplicationListener<WacodisJobExecutionFailedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WacodisJobExecutionFailedHandler.class);

    @Autowired
    private QuartzSingleExecutionSchedulingManager schedulingManager;

    /**
     * schedule retry for previously failed wacodis job execution
     *
     * @param e
     */
    @Override
    public void onApplicationEvent(WacodisJobExecutionFailedEvent e) {
        Date scheduledRetry;
        WacodisJobDefinition jobDef = e.getWacodisJob().getJobDefinition();
        WacodisJobExecutionContext execContex = e.getWacodisJob().getExecutionContext();
        WacodisJobDefinitionRetrySettings retrySettings = jobDef.getRetrySettings();
        String triggerKey = jobDef.getId().toString()+ "_execId_" + execContex.getExecutionID().toString()+ "_retry_" + execContex.getRetryCount();

        LOGGER.debug("handle failed execution of wacodis job {}, retry attempt: {}, executionID: {}", jobDef.getId(), execContex.getRetryCount(), execContex.getExecutionID());

        if (retrySettings.getRetryDelayMillies() <= 0) {
            scheduledRetry = schedulingManager.scheduleSingleJobExecutionImmediately(jobDef, getJobDataMapFromExecutionContext(execContex), triggerKey);
        } else {
            scheduledRetry = schedulingManager.scheduleSingleJobExecutionDelayed(jobDef, getJobDataMapFromExecutionContext(execContex), triggerKey);
        }

        if (scheduledRetry != null) {
            LOGGER.debug("scheduled retry for failed wacodis job {} at {}, retry attempt: {} of {}, executionID: {}", jobDef.getId(), scheduledRetry, execContex.getRetryCount(), retrySettings.getMaxRetries(), execContex.getExecutionID());
        } else {
            LOGGER.error("unable to schedule retry for failed wacodis job {}, retry attempt: {} of {}, executionID: {}", jobDef.getId(), execContex.getRetryCount(), retrySettings.getMaxRetries(), execContex.getExecutionID());
        }
    }

    private Map<String, String> getJobDataMapFromExecutionContext(WacodisJobExecutionContext context) {
        Map<String, String> jobData = new HashMap<>();
        jobData.put(EXECUTION_ID_KEY, context.getExecutionID().toString());
        jobData.put(RETRY_COUNT_KEY, String.valueOf(context.getRetryCount()));
        jobData.put(RETRY_FIRST_EXECUTION_TIME_KEY, context.getExecutionTime().toString());

        return jobData;
    }

}
