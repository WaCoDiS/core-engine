/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.listener;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.executor.events.WacodisJobExecutionFailedEvent;
import de.wacodis.coreengine.scheduling.manage.QuartzSchedulingManager;
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
public class WacodisJobExecutionFailedHandler implements ApplicationListener<WacodisJobExecutionFailedEvent>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WacodisJobExecutionFailedHandler.class);
    
    @Autowired
    private QuartzSchedulingManager schedulingManager;

    @Override
    public void onApplicationEvent(WacodisJobExecutionFailedEvent e) {
        LOGGER.info("handle failed execution of wacodis job {}", e.getWacodisJob().getId());
        
        WacodisJobDefinition jobDef = e.getWacodisJob();     
        this.schedulingManager.triggerJobNow(jobDef);
    }
    
}
