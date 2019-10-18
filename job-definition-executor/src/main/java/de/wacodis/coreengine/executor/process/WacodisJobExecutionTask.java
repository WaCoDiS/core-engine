/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.ProductDescription;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobExecution;
import de.wacodis.core.models.WacodisJobFailed;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import java.util.concurrent.Callable;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisherChannel;
import java.util.stream.Collectors;

/**
 * execute wacodis job -> execute processing tool and publish messages (started,
 * failed, finished)
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobExecutionTask implements Callable<Void> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobExecutionTask.class);

    private final de.wacodis.coreengine.executor.process.Process toolProcess;
    private final ProcessContext toolContext;
    private final WacodisJobDefinition jobDefinition;
    private ToolMessagePublisherChannel toolMessagePublisher;

    public WacodisJobExecutionTask(Process toolProcess, ProcessContext toolContext, WacodisJobDefinition jobDefinition) {
        this.toolProcess = toolProcess;
        this.toolContext = toolContext;
        this.jobDefinition = jobDefinition;
    }

    public WacodisJobExecutionTask(Process toolProcess, ProcessContext toolContext, WacodisJobDefinition jobDefinition, ToolMessagePublisherChannel newProductPublisher) {
        this.toolProcess = toolProcess;
        this.toolContext = toolContext;
        this.jobDefinition = jobDefinition;
        this.toolMessagePublisher = newProductPublisher;
    }

    @Override
    public Void call() throws Exception {
        LOGGER.debug("new thread started for process " + toolContext.getWacodisProcessID() + ", toolProcess: " + toolProcess);

        // publish message for execution start
        publishToolExecutionStarted(this.jobDefinition);

        //execute processing tool
        ProcessOutputDescription toolOutputDescription;
        try {
            toolOutputDescription = this.toolProcess.execute(this.toolContext);
            LOGGER.debug("Process: " + toolContext.getWacodisProcessID() + ",executed toolProcess " + toolProcess);
        } catch (ExecutionException e) {
            LOGGER.warn("Process execution failed", e.getMessage());
            LOGGER.debug(e.getMessage(), e);

            // publish message with the failure
            publishToolFailure(this.jobDefinition, e.getMessage());

            throw e;
        }

        //publish toolFinished message
        if (this.toolMessagePublisher != null) {
            publishToolFinished(this.jobDefinition, toolOutputDescription);
        } else {
            LOGGER.error("newProductPublisher is null, could not publish ProductDescription message for process " + toolContext.getWacodisProcessID());
        }

        LOGGER.info("Process: " + toolContext.getWacodisProcessID() + ",finished execution");

        return null; //satisfy return type Void
    }

    private void publishToolFinished(WacodisJobDefinition jobDefinition, ProcessOutputDescription outputDescription) {
        //publish newProduct message via broker
        ProductDescription msg = new ProductDescription();
        msg.setJobIdentifier(outputDescription.getProcessIdentifier());
        msg.setProductCollection(jobDefinition.getProductCollection());
        // do not publish the "METADATA" output
        msg.setOutputIdentifiers(outputDescription.getOutputIdentifiers().stream()
                .filter(i -> !"METADATA".equalsIgnoreCase(i))
                .collect(Collectors.toList()));
        Message newProductMessage = MessageBuilder.withPayload(msg).build();
        toolMessagePublisher.toolFinished().send(newProductMessage);
        LOGGER.info("publish toolFinished message " + System.lineSeparator() + newProductMessage.getPayload().toString());
    }

    private void publishToolExecutionStarted(WacodisJobDefinition jobDefinition) {
        //publish toolExecution message via broker
        WacodisJobExecution msg = new WacodisJobExecution();
        msg.setJobIdentifier(jobDefinition.getId().toString());
        msg.setCreated(new DateTime());
        msg.setProcessingTool(jobDefinition.getProcessingTool());
        msg.setProductCollection(jobDefinition.getProductCollection());
        toolMessagePublisher.toolExecution().send(MessageBuilder.withPayload(msg).build());
        LOGGER.info("publish toolExecution message " + System.lineSeparator() + msg.toString());
    }

    private void publishToolFailure(WacodisJobDefinition jobDefinition, String failureMessage) {
        //publish toolFailure message via broker
        WacodisJobFailed msg = new WacodisJobFailed();
        msg.setJobIdentifier(jobDefinition.getId().toString());
        msg.setCreated(new DateTime());
        msg.setReason(failureMessage);
        toolMessagePublisher.toolFailure().send(MessageBuilder.withPayload(msg).build());
        LOGGER.info("publish toolFailure message " + System.lineSeparator() + msg.toString());
    }

}
