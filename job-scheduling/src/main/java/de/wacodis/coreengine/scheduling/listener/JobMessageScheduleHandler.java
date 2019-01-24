/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.listener;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.scheduling.quartz.SchedulingManager;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class JobMessageScheduleHandler implements JobMessageHandler {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JobMessageScheduleHandler.class);

    @Autowired
    private SchedulingManager schedulingManager;

    @Override
    public void handleNewJob(WacodisJobDefinition jobDefinition) {
        schedulingManager.scheduleNewJob(jobDefinition);
    }

}
