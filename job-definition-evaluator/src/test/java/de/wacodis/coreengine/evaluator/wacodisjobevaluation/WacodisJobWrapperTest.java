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
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.AbstractSubsetDefinitionTemporalCoverage;
import de.wacodis.core.models.CatalogueSubsetDefinition;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecution;
import de.wacodis.core.models.WacodisJobDefinitionTemporalCoverage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobWrapperTest {

    public WacodisJobWrapperTest() {
    }

    /**
     * Test of getInputs method, of class WacodisJobWrapper.
     */
    @Test
    public void testGetInputs() {
        WacodisJobDefinition jobDef = getJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), new DateTime(), 0), jobDef);

        assertEquals(2, job.getInputs().size());
    }

    @Test
    @DisplayName("check GetInputs returns umodifiable collection")
    public void testGetInputsUnmodifiable() {
        WacodisJobDefinition jobDef = getJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), new DateTime(), 0), jobDef);

        //exception expected because collection should be unmodifiable
        assertThrows(java.lang.UnsupportedOperationException.class, () -> {
            job.getInputs().add(new InputHelper(new AbstractSubsetDefinition()));
        });
    }

    /**
     * Test of isExecutable method, of class WacodisJobWrapper.
     */
    @Test
    public void testIsExecutable() {
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), new DateTime(), 0), getJobDefinition());
        InputHelper input = new InputHelper(new AbstractSubsetDefinition());
        List<AbstractResource> resource = new ArrayList<>();
        resource.add(new AbstractResource());

        assertFalse(job.isExecutable());

        job.getInputs().get(0).setResource(resource);
        assertFalse(job.isExecutable());

        job.getInputs().get(1).setResource(resource);
        assertTrue(job.isExecutable());

        job.getInputs().get(1).removeResource(resource);
        assertFalse(job.isExecutable());

        job.getInputs().get(0).removeResource(resource);
        assertFalse(job.isExecutable());
    }

    /**
     * tests if every input is marked as unavailable initially
     */
    @Test
    @DisplayName("check every resource is unavailable initially")
    public void testSubsetsInitialized() {
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), new DateTime(), 0), getJobDefinition());

        for (InputHelper pair : job.getInputs()) {
            assertFalse(pair.hasResource());
        }
    }

    @Test
    public void testCalculateInputRelevancyTimeFrame_PrevExecution() {
        DateTime execTime = DateTime.parse("2000-01-15T00:00:00Z");
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), execTime, 0), getJobDefinition());
        WacodisJobDefinitionExecution exec = new WacodisJobDefinitionExecution();
        WacodisJobDefinitionTemporalCoverage tmpCov = new WacodisJobDefinitionTemporalCoverage();
        tmpCov.setPreviousExecution(true);
        exec.setPattern("0 0 1 * *"); //executes on the 1st day of each month (xxxx-xx-01:00:00:00)
        job.getJobDefinition().setTemporalCoverage(tmpCov);
        job.getJobDefinition().setExecution(exec);

        assertEquals(DateTime.parse("2000-01-01T00:00:00Z"), job.calculateInputRelevancyTimeFrame().getStart());
    }

    @Test
    public void testCalculateInputRelevancyTimeFrame_Period() {
        DateTime execTime = DateTime.parse("2000-02-01T00:00:00Z");
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), execTime, 0), getJobDefinition());
        WacodisJobDefinitionTemporalCoverage tmpCov = new WacodisJobDefinitionTemporalCoverage();
        tmpCov.setPreviousExecution(false);
        tmpCov.setDuration("P1M"); //one month
        job.getJobDefinition().setTemporalCoverage(tmpCov);

        assertEquals(DateTime.parse("2000-01-01T00:00:00Z"), job.calculateInputRelevancyTimeFrame().getStart());
    }

    @Test
    public void testCalculateInputRelevancyTimeFrame_EndTime() {
        DateTime execTime = DateTime.parse("2000-02-01T00:00:00Z");
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), execTime, 0), getJobDefinition());
        WacodisJobDefinitionTemporalCoverage tmpCov = new WacodisJobDefinitionTemporalCoverage();
        tmpCov.setPreviousExecution(false);
        tmpCov.setDuration("P1M"); //one month
        job.getJobDefinition().setTemporalCoverage(tmpCov);

        assertEquals(execTime, job.calculateInputRelevancyTimeFrame().getEnd());
    }

    @Test
    public void testCalculateInputRelevancyTimeFrame_PeriodWithOffset() {
        DateTime execTime = DateTime.parse("2000-02-01T00:00:00Z");
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), execTime, 0), getJobDefinition());
        WacodisJobDefinitionTemporalCoverage tmpCov = new WacodisJobDefinitionTemporalCoverage();
        tmpCov.setPreviousExecution(false);
        tmpCov.setDuration("P1M"); //one month
        tmpCov.setOffset("P2M"); //two months offset
        job.getJobDefinition().setTemporalCoverage(tmpCov);

        assertEquals(DateTime.parse("1999-11-01T00:00:00Z"), job.calculateInputRelevancyTimeFrame().getStart());
        assertEquals(DateTime.parse("1999-12-01T00:00:00Z"), job.calculateInputRelevancyTimeFrame().getEnd());
    }

    @Test
    public void testCalculateInputRelevancyTimeFrame_PrevExecutionWithOfsset() {
        DateTime execTime = DateTime.parse("2000-01-15T00:00:00Z");
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), execTime, 0), getJobDefinition());
        WacodisJobDefinitionExecution exec = new WacodisJobDefinitionExecution();
        WacodisJobDefinitionTemporalCoverage tmpCov = new WacodisJobDefinitionTemporalCoverage();
        tmpCov.setPreviousExecution(true);
        tmpCov.setOffset("P5D"); //five days offset
        exec.setPattern("0 0 1 * *"); //executes on the 1st day of each month (xxxx-xx-01:00:00:00)
        job.getJobDefinition().setTemporalCoverage(tmpCov);
        job.getJobDefinition().setExecution(exec);

        assertEquals(DateTime.parse("2000-01-01T00:00:00Z"), job.calculateInputRelevancyTimeFrame().getStart());
        assertEquals(DateTime.parse("2000-01-10T00:00:00Z"), job.calculateInputRelevancyTimeFrame().getEnd());
    }

    @Test
    public void testCalculateInputRelevancyTimeFrame_Input_NoneTempCov() {
        DateTime execTime = DateTime.parse("2000-01-15T00:00:00Z");
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), execTime, 0), getJobDefinition());
        WacodisJobDefinitionExecution exec = new WacodisJobDefinitionExecution();
        WacodisJobDefinitionTemporalCoverage tmpCov = new WacodisJobDefinitionTemporalCoverage();
        exec.setPattern("0 0 1 * *"); //executes on the 1st day of each month (xxxx-xx-01:00:00:00)
        tmpCov.setPreviousExecution(false);
        tmpCov.setDuration("P1M"); //one month
        job.getJobDefinition().setTemporalCoverage(tmpCov);
        job.getJobDefinition().setExecution(exec);

        AbstractSubsetDefinition input = new AbstractSubsetDefinition();
        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.DWDSUBSETDEFINITION);
        input.setIdentifier("input1");
        // temp cov is null
        input.setTemporalCoverage(null);

        //must be equal to job's inputRelevanceTimeFrame if no temporal coverage is provide for input
        assertEquals(job.calculateInputRelevancyTimeFrame(), job.calculateInputRelevancyTimeFrameForInput(input));
    }

    @Test
    public void testCalculateInputRelevancyTimeFrame_Input_TempCov() {
        DateTime execTime = DateTime.parse("2000-01-15T00:00:00Z");
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), execTime, 0), getJobDefinition());
        WacodisJobDefinitionExecution exec = new WacodisJobDefinitionExecution();
        WacodisJobDefinitionTemporalCoverage tmpCov = new WacodisJobDefinitionTemporalCoverage();
        exec.setPattern("0 0 1 * *"); //executes on the 1st day of each month (xxxx-xx-01:00:00:00)
        tmpCov.setPreviousExecution(false);
        tmpCov.setDuration("P1M"); //one month
        job.getJobDefinition().setTemporalCoverage(tmpCov);
        job.getJobDefinition().setExecution(exec);

        AbstractSubsetDefinition input = new AbstractSubsetDefinition();
        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.DWDSUBSETDEFINITION);
        input.setIdentifier("input1");
        AbstractSubsetDefinitionTemporalCoverage inputTmpCov = new AbstractSubsetDefinitionTemporalCoverage();
        inputTmpCov.setDuration("P2M"); //2 month, must override job's temporal coverage
        input.setTemporalCoverage(inputTmpCov);

        //temporal coverage must override job's temporal coverage
        Interval expectedTimeFrame = new Interval(DateTime.parse("1999-11-15T00:00:00Z"), execTime);
        assertEquals(expectedTimeFrame, job.calculateInputRelevancyTimeFrameForInput(input));
    }

    @Test
    public void testCalculateInputRelevancyTimeFrame_Input_TempCovWithOffset() {
        DateTime execTime = DateTime.parse("2000-01-15T00:00:00Z");
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), execTime, 0), getJobDefinition());
        WacodisJobDefinitionExecution exec = new WacodisJobDefinitionExecution();
        WacodisJobDefinitionTemporalCoverage tmpCov = new WacodisJobDefinitionTemporalCoverage();
        exec.setPattern("0 0 1 * *"); //executes on the 1st day of each month (xxxx-xx-01:00:00:00)
        tmpCov.setPreviousExecution(false);
        tmpCov.setDuration("P1M"); //one month
        job.getJobDefinition().setTemporalCoverage(tmpCov);
        job.getJobDefinition().setExecution(exec);

        AbstractSubsetDefinition input = new AbstractSubsetDefinition();
        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.DWDSUBSETDEFINITION);
        input.setIdentifier("input1");
        AbstractSubsetDefinitionTemporalCoverage inputTmpCov = new AbstractSubsetDefinitionTemporalCoverage();
        inputTmpCov.setDuration("P2M"); //2 month, must override job's temporal coverage
        inputTmpCov.setOffset("P1M"); //offset of 1 month
        input.setTemporalCoverage(inputTmpCov);

        //temporal coverage must override job's temporal coverage (consider offset of 1 month)
        Interval expectedTimeFrame = new Interval(DateTime.parse("1999-10-15T00:00:00Z"), DateTime.parse("1999-12-15T00:00:00Z"));
        assertEquals(expectedTimeFrame, job.calculateInputRelevancyTimeFrameForInput(input));
    }

    private WacodisJobDefinition getJobDefinition() {
        AbstractSubsetDefinition subset1 = new CatalogueSubsetDefinition();
        subset1.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.CATALOGUESUBSETDEFINITION);
        AbstractSubsetDefinition subset2 = new CopernicusSubsetDefinition();
        subset2.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.COPERNICUSSUBSETDEFINITION);
        List<AbstractSubsetDefinition> subsets = new ArrayList<>();
        subsets.add(subset1);
        subsets.add(subset2);

        WacodisJobDefinition jobDefinition = new WacodisJobDefinition();
        jobDefinition.setInputs(subsets);

        return jobDefinition;
    }

}
