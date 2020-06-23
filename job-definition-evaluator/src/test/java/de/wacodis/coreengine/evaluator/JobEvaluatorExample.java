/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.CopernicusDataEnvelope;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecution;
import de.wacodis.core.models.WacodisJobDefinitionTemporalCoverage;
import de.wacodis.coreengine.evaluator.messaging.listener.DataEnvelopeListener;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import java.util.Arrays;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@SpringBootApplication
@ComponentScan({"de.wacodis.coreengine.evaluator"})
public class JobEvaluatorExample {

    @Autowired
    JobEvaluatorRunner evalRunner;

    @Autowired
    DataEnvelopeListener envListener;

    public static void main(String[] args) {
        System.out.println("start executor test application");

        SpringApplication.run(JobEvaluatorExample.class, args);
    }

    @PostConstruct
    private void runTestApp() {
        WacodisJobWrapper testJob = createJobWrapper();

        //simulate event
        WacodisJobExecutableEvent event = new WacodisJobExecutableEvent(this, testJob, EvaluationStatus.EXECUTABLE);
        this.evalRunner.evaluateJob(testJob, true); //add job to inputtracker
        this.envListener.evaluateDateEnvelope(createDataEnvelope());
    }

    private WacodisJobWrapper createJobWrapper() {
        UUID jobID = UUID.randomUUID();
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        jobDef.setId(jobID);

        AbstractSubsetDefinition input1 = new CopernicusSubsetDefinition();
        input1.setIdentifier("exampleInput");
        input1.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.COPERNICUSSUBSETDEFINITION);
        jobDef.addInputsItem(input1);

        jobDef.setProcessingTool("exampleTool");
        jobDef.setProductCollection("testMosaic");

        WacodisJobDefinitionExecution execution = new WacodisJobDefinitionExecution();
        execution.setPattern("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        WacodisJobDefinitionTemporalCoverage tempCov = new WacodisJobDefinitionTemporalCoverage();
        tempCov.setPreviousExecution(Boolean.TRUE);
        jobDef.setTemporalCoverage(tempCov);
        jobDef.setExecution(execution);

        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.setExtent(Arrays.asList(new Float[]{0f, 1f, 0f, 1f}));
        jobDef.areaOfInterest(aoi);

        WacodisJobWrapper jobWrapper = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), DateTime.now(), 0), jobDef);

        return jobWrapper;
    }

    private AbstractDataEnvelope createDataEnvelope() {
        CopernicusDataEnvelope env = new CopernicusDataEnvelope();
        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.setExtent(Arrays.asList(new Float[]{0f, 1f, 0f, 1f}));
        env.setAreaOfInterest(aoi);
        env.setIdentifier("testEnvelope");
        env.setSourceType(AbstractDataEnvelope.SourceTypeEnum.COPERNICUSDATAENVELOPE);
        
        return env;
        
    }

}
