/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.GetResource;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.extension.staticresource.StaticDummyResource;
import de.wacodis.coreengine.evaluator.EvaluationStatus;
import de.wacodis.coreengine.evaluator.WacodisJobExecutableEvent;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.events.WacodisJobExecutableStateChangedHandler;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
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
@ComponentScan({"de.wacodis.coreengine.executor"})
public class WacodisJobExecutorIT {

    private final static String PROCESSTOOL = "org.n52.wps.server.algorithm.test.DummyTestClass";
    private final static ExpectedProcessOutput[] EXPECTEDOUTPUTS = new ExpectedProcessOutput[]{
        new ExpectedProcessOutput("LiteralOutputData", "text/xml", true)
    };

    @Autowired
    WacodisJobExecutableStateChangedHandler eventHandler;

    public static void main(String[] args) {
        System.out.println("start executor test application");

        SpringApplication.run(WacodisJobExecutorIT.class, args);
    }

    @PostConstruct
    private void runTestApp() {
        //set outputs
        this.eventHandler.getJobExecutor().setExpectedProcessOutputs(Arrays.asList(EXPECTEDOUTPUTS));
        //produce executable job
        WacodisJobWrapper testJob = createJobWrapper();

        //simulate event
        WacodisJobExecutableEvent event = new WacodisJobExecutableEvent(this, testJob, EvaluationStatus.EXECUTABLE);
        this.eventHandler.onApplicationEvent(event);
    }

    private WacodisJobWrapper createJobWrapper() {
        UUID jobID = UUID.randomUUID();
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        jobDef.setId(jobID);
        AbstractSubsetDefinition input1 = new AbstractSubsetDefinition();
        input1.setIdentifier("LiteralInputData");
        CopernicusSubsetDefinition input2 = new CopernicusSubsetDefinition();
        input2.setIdentifier("copernicusInput");
        input2.setSatellite(CopernicusSubsetDefinition.SatelliteEnum._2);
        input2.setMaximumCloudCoverage(50.0f);
        jobDef.addInputsItem(input1);
        jobDef.addInputsItem(input2);
        jobDef.setProcessingTool(PROCESSTOOL);
        jobDef.setProductCollection("testMosaic");
        StaticDummyResource staticResource = new StaticDummyResource();
        staticResource.setValue("this is a static resource");
        staticResource.setDataEnvelopeId("someDataEnvelopeID");
        AbstractResource abstractResource = new AbstractResource();
        abstractResource.setUrl("test input");
        abstractResource.setDataEnvelopeId("someOtherDataEnvelopeID");
        WacodisJobExecutionContext context = new WacodisJobExecutionContext(UUID.randomUUID(), DateTime.now(), 0);

        WacodisJobWrapper jobWrapper = new WacodisJobWrapper(context, jobDef);
        InputHelper literalInputHelper = jobWrapper.getInputs().get(0);
        List<AbstractResource> input1ResourceList = new ArrayList<>();
        input1ResourceList.add(staticResource);
        input1ResourceList.add(abstractResource);
        literalInputHelper.setResource(input1ResourceList);

        GetResource copernicusResource1 = new GetResource();
        copernicusResource1.dataEnvelopeId("abcdefg");
        copernicusResource1.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        copernicusResource1.setUrl("https://scihub.copernicus.eu/dhus/odata/v1/Products('21eb3657-9a68-46cf-979a-9b731dfedf24')/$value");
        GetResource copernicusResource2 = new GetResource();
        copernicusResource2.dataEnvelopeId("vwxyz");
        copernicusResource2.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        copernicusResource2.setUrl("https://scihub.copernicus.eu/dhus/odata/v1/Products('21eb3657-9a68-46cf-979a-9b731dfedf24')/$value");
        
        InputHelper copernicusInputHelper = jobWrapper.getInputs().get(1);
        List<AbstractResource> copernicusResources = new ArrayList<>();
        copernicusResources.add(copernicusResource1);
        copernicusResources.add(copernicusResource2);
        copernicusInputHelper.setResource(copernicusResources);

        
        return jobWrapper;
    }

}
