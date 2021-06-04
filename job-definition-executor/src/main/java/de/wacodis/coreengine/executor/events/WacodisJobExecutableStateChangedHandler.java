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
package de.wacodis.coreengine.executor.events;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.EvaluationStatus;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationListener;
import de.wacodis.coreengine.evaluator.WacodisJobExecutableEvent;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.WacodisJobExecutionStarter;
import javax.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class WacodisJobExecutableStateChangedHandler implements ApplicationListener<WacodisJobExecutableEvent> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobExecutableStateChangedHandler.class);
    
    @Autowired
    private WacodisJobExecutionStarter jobExecutor;

    public WacodisJobExecutionStarter getJobExecutor() {
        return jobExecutor;
    } 
    
    @PostConstruct
    public void init() {
        LOGGER.info("started {}", getClass().getName());
    }
    
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
