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
        WacodisJobDefinition jobDef = e.getWacodisJob().getJobDefinition();
        WacodisJobExecutionContext execContex = e.getWacodisJob().getExecutionContext();
         WacodisJobDefinitionRetrySettings retrySettings = jobDef.getRetrySettings();
        
        LOGGER.info("handle failed execution of wacodis job {}", jobDef.getId());
        
        if (retrySettings.getRetryDelayMillies()<= 0) {
            schedulingManager.scheduleRetryImmediately(jobDef, execContex);
        } else {
            Date retryAt = new Date(System.currentTimeMillis() + retrySettings.getRetryDelayMillies());
            schedulingManager.scheduleRetryAt(jobDef, execContex, retryAt);
        }
    }
    
}
