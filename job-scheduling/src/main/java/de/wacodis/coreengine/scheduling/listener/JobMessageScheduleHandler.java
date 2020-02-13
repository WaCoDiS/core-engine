/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.listener;

import de.wacodis.core.models.SingleJobExecutionEvent;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecution;
import de.wacodis.coreengine.scheduling.manage.QuartSingleExecutionSchedulingManager;
import de.wacodis.coreengine.scheduling.manage.QuartzSchedulingManager;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handler implementation for job messages
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class JobMessageScheduleHandler implements JobMessageHandler {

    @Autowired
    private QuartzSchedulingManager regularExecutionschedulingManager;
    
    @Autowired
    private QuartSingleExecutionSchedulingManager singleExecutionSchedulingManger;

    @Override
    public void handleNewJob(WacodisJobDefinition jobDefinition) {
        WacodisJobDefinitionExecution exec = jobDefinition.getExecution();
        
        if(exec.getPattern() != null && !exec.getPattern().isEmpty()){ //schedule job regularly if cron pattern is provided
            handleRegularExecution(jobDefinition);
        }else if(exec.getEvent() != null){
            handleEventbasedExecution(jobDefinition);
        }else{
            throw new UnsupportedOperationException("unnable to schedule wacodis job " + jobDefinition.getId() + " because neither excution cron pattern nor execution event is provided");
        }
    }
    
    
    private void handleEventbasedExecution(WacodisJobDefinition jobDefinition){
        Object execEvent = jobDefinition.getExecution().getEvent();
        
        if(execEvent instanceof SingleJobExecutionEvent){
            SingleJobExecutionEvent singleExecEvent = (SingleJobExecutionEvent) execEvent;

            if(singleExecEvent.getStartAt() != null){
                Date execAt = singleExecEvent.getStartAt().toDate();
                singleExecutionSchedulingManger.scheduleSingleJobExecutionAt(jobDefinition, execAt); //execute wacodis job once on specified date
            }else{
                singleExecutionSchedulingManger.scheduleSingleJobExecutionImmediately(jobDefinition); //execute immediately if no start date provided
            }
            
        }
    }
    
    private void handleRegularExecution(WacodisJobDefinition jobDefinition){
        regularExecutionschedulingManager.scheduleNewJob(jobDefinition);
    }

}
