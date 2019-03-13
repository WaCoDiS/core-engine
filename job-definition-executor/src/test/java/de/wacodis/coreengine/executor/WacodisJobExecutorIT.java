/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.EvaluationStatus;
import de.wacodis.coreengine.evaluator.WacodisJobExecutableEvent;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.configuration.WacodisJobExecutorConfiguration;
import de.wacodis.coreengine.executor.configuration.WebProcessingServiceConfiguration;
import de.wacodis.coreengine.executor.events.WacodisJobExecutableStateChangedHandler;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
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
    
    private final static String WPSURL = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";
    private final static String PROCESSTOOL = "org.n52.wps.server.algorithm.test.DummyTestClass";

    @Autowired
    WacodisJobExecutableStateChangedHandler eventHandler;
    
    
    public static void main(String[] args) {
        System.out.println("start executor test application");
        
        SpringApplication.run(WacodisJobExecutorIT.class, args);
    }
    
    @PostConstruct
    private void runTestApp(){
       //Set Configuration manually
        WebProcessingServiceConfiguration wpsConfig = new WebProcessingServiceConfiguration();
        wpsConfig.setVersion("2.0.0");
        wpsConfig.setUri(WPSURL);
        WacodisJobExecutorConfiguration executorConfig = new WacodisJobExecutorConfiguration();
        executorConfig.setCleanUpTool("dummyTool");
        this.eventHandler.getJobExecutor().setWpsConfig(wpsConfig);
        this.eventHandler.getJobExecutor().setExecutorConfig(executorConfig);

       //produce executable job
       WacodisJobWrapper testJob = createJobWrapper();
        
        //simulate event
        WacodisJobExecutableEvent event = new WacodisJobExecutableEvent(this, testJob, EvaluationStatus.EXECUTABLE);
        this.eventHandler.onApplicationEvent(event);
        
    }
    
    
    private WacodisJobWrapper createJobWrapper(){
        UUID jobID = UUID.randomUUID();
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        jobDef.setId(jobID);
        AbstractSubsetDefinition input1 = new AbstractSubsetDefinition();
        input1.setIdentifier("LiteralInputData");
        jobDef.addInputsItem(input1);
        jobDef.setProcessingTool(PROCESSTOOL);
        AbstractResource input1Resource = new AbstractResource();
        input1Resource.setUrl("test input");
        
        WacodisJobWrapper jobWrapper = new WacodisJobWrapper(jobDef, DateTime.now());
        InputHelper literalInputHelper = jobWrapper.getInputs().get(0); //only one input
        List<AbstractResource> input1ResourceList = new ArrayList<>();
        input1ResourceList.add(input1Resource);
        literalInputHelper.setResource(input1ResourceList);
        literalInputHelper.setResourceAvailable(true);
        
        
        return jobWrapper;
    }

}
