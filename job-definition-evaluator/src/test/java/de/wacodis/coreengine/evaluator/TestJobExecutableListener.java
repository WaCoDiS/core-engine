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
package de.wacodis.coreengine.evaluator;

import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * checks if event is published by JobEvaluatorRunner if job that was added to InputTracker is executable
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class TestJobExecutableListener implements ApplicationListener<WacodisJobExecutableEvent> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TestJobExecutableListener.class);

    private JobEvaluatorRunner evalutor;

    public JobEvaluatorRunner getEvalutor() {
        return evalutor;
    }

    public void setEvalutor(JobEvaluatorRunner evalutor) {
        this.evalutor = evalutor;
    }

    @Override
    public void onApplicationEvent(WacodisJobExecutableEvent event) {
        LOGGER.info("TestJobExecutableListener notified at timestamp"+ event.getTimestamp() +" , Event: "+ System.lineSeparator() +"Job: " + event.getJob().getJobDefinition().toString() + ", Status: " + event.getStatus());
        //event was published if this code is executed           
        assertAll(
                () -> assertEquals(this.evalutor, event.getSource()),
                () -> assertEquals(EvaluationStatus.EXECUTABLE, event.getStatus())
        );
    }

}
