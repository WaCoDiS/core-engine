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
package de.wacodis.coreengine.evaluator;

import de.wacodis.core.models.AbstractSubsetDefinition;
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
        
        //need at least one input item, otherwise job would be already executable
        AbstractSubsetDefinition input = new AbstractSubsetDefinition();
        input.setIdentifier("testInput");
        jobDef.addInputsItem(input);
        
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), DateTime.now(), 0), jobDef);
        WacodisJobInputTracker inputTracker = new WacodisJobInputTracker(evaluatorService, new SourceTypeDataEnvelopeMatcher());

        inputTracker.addJob(job);
        
        evaluatorService.setJobEvaluator(new JobEvaluatorRunner() {
            @Override
            public EvaluationStatus evaluateJob(WacodisJobWrapper job) {
                return EvaluationStatus.NOTEXECUTABLE;
            }
        });
        //do not remove job from tracker if job is not executable
        evaluatorService.handleJobEvaluation(job, inputTracker);
        assertTrue(inputTracker.containsJob(job));

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
