/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.ProductDescription;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.executor.messaging.NewProductPublisherChannel;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.List;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * execute wacodis job, 1) execute processing tool, 2) execute cleanUp tool
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobExecutionTask implements Callable<WacodisJobExecutionOutput> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobExecutionTask.class);
    
    private final de.wacodis.coreengine.executor.process.Process toolProcess;
    private final de.wacodis.coreengine.executor.process.Process cleanUpProcess;
    private final ProcessContext toolContext;
    private final WacodisJobDefinition jobDefinition;
    private NewProductPublisherChannel newProductPublisher;

    public WacodisJobExecutionTask(Process toolProcess, ProcessContext toolContext, Process cleanUpProcess, WacodisJobDefinition jobDefinition) {
        this.toolProcess = toolProcess;
        this.cleanUpProcess = cleanUpProcess;
        this.toolContext = toolContext;
        this.jobDefinition = jobDefinition;
    }

    public WacodisJobExecutionTask(Process toolProcess,  ProcessContext toolContext, Process cleanUpProcess, WacodisJobDefinition jobDefinition, NewProductPublisherChannel newProductPublisher) {
        this.toolProcess = toolProcess;
        this.cleanUpProcess = cleanUpProcess;
        this.toolContext = toolContext;
        this.jobDefinition = jobDefinition;
        this.newProductPublisher = newProductPublisher;
    }
    
    
    @Override
    public WacodisJobExecutionOutput call() throws Exception {
        LOGGER.debug("new thread started for process " + toolContext.getProcessID() + ", toolProcess: " + toolProcess + " cleaUpProcess: " + cleanUpProcess);

        //execute processing tool
        ProcessOutput toolOutput = this.toolProcess.execute(this.toolContext);
        LOGGER.debug("Process: " + toolContext.getProcessID() + ",executed toolProcess " + toolProcess);

        if(this.newProductPublisher != null){
            //publish newProduct message via broker
            Message newProductMessage = MessageBuilder.withPayload(createMessagePayload(toolOutput, this.jobDefinition)).build();
            newProductPublisher.newProduct().send(newProductMessage);
            LOGGER.info("publish newProduct message " + System.lineSeparator() + newProductMessage.getPayload().toString());
        }
        
        //execute cleanup tool
        ProcessContext cleanUpContext = buildCleanUpToolContext(toolOutput);
        ProcessOutput cleanUpOutput = this.cleanUpProcess.execute(cleanUpContext);
        LOGGER.debug("Process: " + toolContext.getProcessID() + ",executed cleanUpProcess " + cleanUpProcess);

        WacodisJobExecutionOutput jobOutput = buildJobExecutionOutput(toolOutput, cleanUpOutput);

        LOGGER.info("Process: " + toolContext.getProcessID() + ",finished execution");

        return jobOutput;
    }

    private ProcessContext buildCleanUpToolContext(ProcessOutput toolOutput) {
        ProcessContext cleanUpContext = new ProcessContext();
        
        Map<String, List<ResourceDescription>> outputs = toolOutput.getOutputResources(); //use tool outputs as cleanUp inputs
        cleanUpContext.setInputResources(outputs);

        //ToDo: expected outputs cleanUp process
        return cleanUpContext;
    }

    private WacodisJobExecutionOutput buildJobExecutionOutput(ProcessOutput toolOutput, ProcessOutput cleanUpOutput) {
        return new WacodisJobExecutionOutput();
    }
    
    private ProductDescription createMessagePayload(ProcessOutput output, WacodisJobDefinition jobDefinition){
        ProductDescription payload = new ProductDescription();
        
        List<String> outputIdentifiers = new ArrayList<>(output.getOutputResources().keySet());
        
        payload.setJobIdentifier(output.getJobIdentifier());
        payload.setProductCollection(jobDefinition.getProductCollection());
        payload.setOutputIdentifiers(outputIdentifiers);
        
        return payload;
    }
}
