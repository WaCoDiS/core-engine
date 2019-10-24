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
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandlingException;

/**
 * execute wacodis job -> execute processing tool and publish messages (started,
 * failed, finished)
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobExecutionTask implements Callable<Void> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobExecutionTask.class);

    private static final String METADATAOUTPUTIDENTIFIER = "METADATA";

    private final de.wacodis.coreengine.executor.process.Process toolProcess;
    private final ProcessContext toolContext;
    private final WacodisJobDefinition jobDefinition;
    private ToolMessagePublisherChannel toolMessagePublisher;
    private long messagePublishingTimeout_Millis = 10000; //default of ten seconds

    public WacodisJobExecutionTask(Process toolProcess, ProcessContext toolContext, WacodisJobDefinition jobDefinition, ToolMessagePublisherChannel toolMessagePublisher) {
        this.toolProcess = toolProcess;
        this.toolContext = toolContext;
        this.jobDefinition = jobDefinition;
        this.toolMessagePublisher = toolMessagePublisher;
    }

    public WacodisJobExecutionTask(Process toolProcess, ProcessContext toolContext, WacodisJobDefinition jobDefinition, ToolMessagePublisherChannel toolMessagePublisher, long messagePublishingTimeout_Millis) {
        this(toolProcess, toolContext, jobDefinition, toolMessagePublisher);
        this.messagePublishingTimeout_Millis = messagePublishingTimeout_Millis;
    }

    public ToolMessagePublisherChannel getToolMessagePublisher() {
        return toolMessagePublisher;
    }

    public void setToolMessagePublisher(ToolMessagePublisherChannel toolMessagePublisher) {
        this.toolMessagePublisher = toolMessagePublisher;
    }

    public long getMessagePublishingTimeout_Millis() {
        return messagePublishingTimeout_Millis;
    }

    public void setMessagePublishingTimeout_Millis(long messagePublishingTimeout_Millis) {
        this.messagePublishingTimeout_Millis = messagePublishingTimeout_Millis;
    }

    @Override
    public Void call() throws Exception {
        LOGGER.debug("new thread started for process " + toolContext.getWacodisProcessID() + ", toolProcess: " + toolProcess);

        //publish message for execution start
         publishMessageSync(this.toolMessagePublisher.toolExecution(), buildToolExecutionStartedMessage());


        //execute processing tool
        ProcessOutputDescription processOutput;
        try {
            processOutput = this.toolProcess.execute(this.toolContext);
            LOGGER.debug("Process: " + toolContext.getWacodisProcessID() + ",executed toolProcess " + toolProcess);
        } catch (ExecutionException e) {
            LOGGER.error("Process execution failed", e.getMessage());
            LOGGER.warn(e.getMessage(), e);

            //publish message with the failure
            publishMessageSync(this.toolMessagePublisher.toolFailure(), buildToolFailureMessage(this.jobDefinition, e.getMessage()));

            throw e;
        }

        //publish toolFinished message
        publishMessageSync(this.toolMessagePublisher.toolFinished(), buildToolFinishedMessage(processOutput));

        LOGGER.info("Process: " + toolContext.getWacodisProcessID() + ",finished execution");

        return null; //satisfy return type Void
    }

    private Message<ProductDescription> buildToolFinishedMessage(ProcessOutputDescription processOuput) {
        ProductDescription msg = new ProductDescription();
        msg.setJobIdentifier(processOuput.getProcessIdentifier());
        msg.setProductCollection(this.jobDefinition.getProductCollection());
        // do not include the metadata output
        msg.setOutputIdentifiers(this.toolContext.getExpectedOutputs().stream()
                .map(expectedOutput -> expectedOutput.getIdentifier())
                .filter(i -> !METADATAOUTPUTIDENTIFIER.equalsIgnoreCase(i))
                .collect(Collectors.toList())); //all output identifiers except metadata

        return MessageBuilder.withPayload(msg).build();
    }

    private Message<WacodisJobExecution> buildToolExecutionStartedMessage() {
        WacodisJobExecution msg = new WacodisJobExecution();
        msg.setJobIdentifier(this.jobDefinition.getId().toString());
        msg.setCreated(new DateTime());
        msg.setProcessingTool(this.jobDefinition.getProcessingTool());
        msg.setProductCollection(this.jobDefinition.getProductCollection());

        return MessageBuilder.withPayload(msg).build();
    }

    private Message<WacodisJobFailed> buildToolFailureMessage(WacodisJobDefinition jobDefinition, String errorText) {
        WacodisJobFailed msg = new WacodisJobFailed();
        msg.setJobIdentifier(jobDefinition.getId().toString());
        msg.setCreated(new DateTime());
        msg.setReason(errorText);
        return MessageBuilder.withPayload(msg).build();
    }

    /**
     * @param publishChannel
     * @param msg
     * @param timeout
     * @return true if message was published succesfully
     */
    private boolean publishMessageSync(MessageChannel publishChannel, Message msg) {
        try {
            boolean isSent = (this.messagePublishingTimeout_Millis >= 0) ? publishChannel.send(msg, this.messagePublishingTimeout_Millis) : publishChannel.send(msg);
            if (isSent) {
                LOGGER.info("published message on channel {}, message: {}", publishChannel.toString(), msg.getPayload().toString());
            } else {
                LOGGER.error("could not publish message on channel {}, exceeded timeout of {}, message: {}", publishChannel.toString(), this.messagePublishingTimeout_Millis, msg.getPayload().toString());
            }

            return isSent;
        } catch (MessageHandlingException e) {
            LOGGER.error("could not publish message on channel " + publishChannel.toString() + ", exception of type "+ e.getClass().getSimpleName() + " occurred, message: " + msg.getPayload().toString(), e);
            return false;
        }
    }
}
