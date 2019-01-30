/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
