/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.listener;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionRetrySettings;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import de.wacodis.coreengine.executor.events.WacodisJobExecutionFailedEvent;
import de.wacodis.coreengine.scheduling.manage.QuartzRetrySchedulingManager;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Arne
 */
@Component
public class WacodisJobExecutionFailedHandler implements ApplicationListener<WacodisJobExecutionFailedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WacodisJobExecutionFailedHandler.class);

    @Autowired
    private QuartzRetrySchedulingManager schedulingManager;

    @Override
    public void onApplicationEvent(WacodisJobExecutionFailedEvent e) {
        Date scheduledRetry;
        WacodisJobDefinition jobDef = e.getWacodisJob().getJobDefinition();
        WacodisJobExecutionContext execContex = e.getWacodisJob().getExecutionContext();
        WacodisJobDefinitionRetrySettings retrySettings = jobDef.getRetrySettings();

        LOGGER.debug("handle failed execution of wacodis job {}, retry attempt: {}, executionID: {}", jobDef.getId(), execContex.getRetryCount(), execContex.getExecutionID());

        if (retrySettings.getRetryDelayMillies() <= 0) {
            scheduledRetry = schedulingManager.scheduleRetryImmediately(jobDef, execContex);
        } else {
            Date retryAt = new Date(System.currentTimeMillis() + retrySettings.getRetryDelayMillies());
            scheduledRetry = schedulingManager.scheduleRetryAt(jobDef, execContex, retryAt);
        }

        if (scheduledRetry != null) {
            LOGGER.debug("scheduled retry for failed wacodis job {} at {}, retry attempt: {} of {}, executionID: {}", jobDef.getId(), scheduledRetry, execContex.getRetryCount(), retrySettings.getMaxRetries(), execContex.getExecutionID());
        } else {
            LOGGER.error("unable to schedule retry for failed wacodis job {}, retry attempt: {} of {}, executionID: {}", jobDef.getId(), execContex.getRetryCount(), retrySettings.getMaxRetries(), execContex.getExecutionID());
        }
    }

}
