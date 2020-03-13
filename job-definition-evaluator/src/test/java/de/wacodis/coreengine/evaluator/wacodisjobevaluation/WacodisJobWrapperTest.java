/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.CatalogueSubsetDefinition;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.StaticSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecution;
import de.wacodis.core.models.WacodisJobDefinitionTemporalCoverage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
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
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(),new DateTime(), 0), jobDef);

        assertEquals(3, job.getInputs().size());
        InputHelper staticInput = job.getInputs().get(2);
        assertTrue(staticInput.getSubsetDefinition().getSourceType().equals(AbstractSubsetDefinition.SourceTypeEnum.STATICSUBSETDEFINITION));
    }

    @Test
    @DisplayName("check GetInputs returns umodifiable collection")
    public void testGetInputsUnmodifiable() {
        WacodisJobDefinition jobDef = getJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(),new DateTime(), 0), jobDef);

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
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(),new DateTime(), 0), getJobDefinition());
        assertFalse(job.isExecutable());

        job.getInputs().get(0).setResourceAvailable(true);
        assertFalse(job.isExecutable());

        job.getInputs().get(1).setResourceAvailable(true);
        assertTrue(job.isExecutable());

        job.getInputs().get(1).setResourceAvailable(false);
        assertFalse(job.isExecutable());

        job.getInputs().get(0).setResourceAvailable(false);
        assertFalse(job.isExecutable());
    }

    /**
     * tests if every input is marked as unavailable initially
     */
    @Test
    @DisplayName("check every resource is unavailable initially")
    public void testSubsetsInitialized() {
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(),new DateTime(), 0), getJobDefinition());

        job.getInputs().stream()
                .filter(i -> i.getSubsetDefinition().getSourceType() != AbstractSubsetDefinition.SourceTypeEnum.STATICSUBSETDEFINITION)
                .map(InputHelper::hasResource).forEach(Assertions::assertFalse);
    }

    @Test
    public void testCalculateInputRelevancyTimeFrame_PrevExecution() {
        DateTime execTime = DateTime.parse("2000-01-15T00:00:00Z");
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(),execTime, 0), getJobDefinition());
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
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(),execTime, 0), getJobDefinition());
        WacodisJobDefinitionTemporalCoverage tmpCov = new WacodisJobDefinitionTemporalCoverage();
        tmpCov.setPreviousExecution(false);
        tmpCov.setDuration("P1M"); //one month
        job.getJobDefinition().setTemporalCoverage(tmpCov);

        assertEquals(DateTime.parse("2000-01-01T00:00:00Z"), job.calculateInputRelevancyTimeFrame().getStart());
    }

    @Test
    public void testCalculateInputRelevancyTimeFrame_EndTime() {
        DateTime execTime = DateTime.parse("2000-02-01T00:00:00Z");
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(),execTime, 0), getJobDefinition());
        WacodisJobDefinitionTemporalCoverage tmpCov = new WacodisJobDefinitionTemporalCoverage();
        tmpCov.setPreviousExecution(false);
        tmpCov.setDuration("P1M"); //one month
        job.getJobDefinition().setTemporalCoverage(tmpCov);

        assertEquals(execTime, job.calculateInputRelevancyTimeFrame().getEnd());
    }

    private WacodisJobDefinition getJobDefinition() {
        AbstractSubsetDefinition subset1 = new CatalogueSubsetDefinition();
        subset1.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.CATALOGUESUBSETDEFINITION);
        AbstractSubsetDefinition subset2 = new CopernicusSubsetDefinition();
        subset2.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.COPERNICUSSUBSETDEFINITION);
        StaticSubsetDefinition subset3 = new StaticSubsetDefinition();
        subset3.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.STATICSUBSETDEFINITION);
        subset3.setDataType(StaticSubsetDefinition.DataTypeEnum.TEXT);
        subset3.setValue("http://remote-resource.url");

        List<AbstractSubsetDefinition> subsets = new ArrayList<>();
        subsets.add(subset1);
        subsets.add(subset2);
        subsets.add(subset3);

        WacodisJobDefinition jobDefinition = new WacodisJobDefinition();
        jobDefinition.setInputs(subsets);

        return jobDefinition;
    }

}
