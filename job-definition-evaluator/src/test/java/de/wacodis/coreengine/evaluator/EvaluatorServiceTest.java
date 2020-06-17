/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.SourceTypeDataEnvelopeMatcher;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobInputTracker;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import java.util.UUID;
import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

/**
 *
 * @author Arne
 */
public class EvaluatorServiceTest {

    public EvaluatorServiceTest() {
    }

    /**
     * Test of handleJobEvaluation method, of class EvaluatorService.
     */
    @Test
    public void testHandleJobEvaluation_CheckJobRemoved() {
        EvaluatorService evaluatorService = new EvaluatorService();
        //mock event publisher
        evaluatorService.setPublisher((Mockito.mock(ApplicationEventPublisher.class)));

        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        jobDef.setId(UUID.randomUUID());
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), DateTime.now(), 0), jobDef);
        WacodisJobInputTracker inputTracker = new WacodisJobInputTracker(evaluatorService, new SourceTypeDataEnvelopeMatcher());

        evaluatorService.setJobEvaluator(new JobEvaluatorRunner() {
            @Override
            public EvaluationStatus evaluateJob(WacodisJobWrapper job) {
                return EvaluationStatus.NOTEXECUTABLE;
            }
        });
        //do not remove job from tracker if job is not executable
        evaluatorService.handleJobEvaluation(job, inputTracker);
        assertFalse(inputTracker.containsJob(job));

        evaluatorService.setJobEvaluator(new JobEvaluatorRunner() {
            @Override
            public EvaluationStatus evaluateJob(WacodisJobWrapper job) {
                return EvaluationStatus.EXECUTABLE;
            }
        });
        //remove job from tracker if job is executable
        evaluatorService.handleJobEvaluation(job, inputTracker);
        assertFalse(inputTracker.containsJob(job));

    }

}
