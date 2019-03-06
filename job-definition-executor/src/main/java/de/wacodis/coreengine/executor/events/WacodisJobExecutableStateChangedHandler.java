/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.events;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.EvaluationStatus;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationListener;
import de.wacodis.coreengine.evaluator.WacodisJobExecutableEvent;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.WacodisJobTaskStarter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class WacodisJobExecutableStateChangedHandler implements ApplicationListener<WacodisJobExecutableEvent>{

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobExecutableStateChangedHandler.class);
    
    @Autowired
    private WacodisJobTaskStarter jobExecutor;
    
    @Override
    public void onApplicationEvent(WacodisJobExecutableEvent event) {
        WacodisJobWrapper job = event.getJob();
        WacodisJobDefinition jobDef = job.getJobDefinition();
        
        LOGGER.debug("received " + event.getClass().getSimpleName() + " for job " + jobDef.getId().toString());
        
        if(event.getStatus().equals(EvaluationStatus.EXECUTABLE)){
            LOGGER.info("EvaluationStatus for job " + jobDef.getId().toString() + " is "  + event.getStatus().toString() + ", handle job execution");
        
            //execute wacodis job
            this.jobExecutor.executeWacodisJob(job);
               
        }else{
            LOGGER.warn("EvaluationStatus for job " + jobDef.getId().toString() + " is "  + event.getStatus().toString() + ", excpected " + EvaluationStatus.EXECUTABLE.toString() + ", job is not executed");
        }
    }

}
