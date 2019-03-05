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
import de.wacodis.coreengine.executor.JobExecutor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class WacodisJobExecutableStateChangedHandler implements ApplicationListener<WacodisJobExecutableEvent>{

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobExecutableStateChangedHandler.class);
    

    @Override
    public void onApplicationEvent(WacodisJobExecutableEvent event) {
        WacodisJobWrapper job = event.getJob();
        WacodisJobDefinition jobDef = job.getJobDefinition();
    }

    
    
}
