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
import de.wacodis.core.models.WacodisJobFinished;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisher;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisherChannel;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * execute wacodis job -> execute processing tool and publish messages (started,
 * failed, finished)
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobExecutor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobExecutor.class);

    private final de.wacodis.coreengine.executor.process.Process toolProcess;
    private final ProcessContext toolContext;
    private final WacodisJobDefinition jobDefinition;
    private ToolMessagePublisherChannel toolMessagePublisher;
    private long messagePublishingTimeout_Millis = 10000; //default of ten seconds

    public WacodisJobExecutor(Process toolProcess, ProcessContext toolContext, WacodisJobDefinition jobDefinition, ToolMessagePublisherChannel toolMessagePublisher) {
        this.toolProcess = toolProcess;
        this.toolContext = toolContext;
        this.jobDefinition = jobDefinition;
        this.toolMessagePublisher = toolMessagePublisher;
    }

    public WacodisJobExecutor(Process toolProcess, ProcessContext toolContext, WacodisJobDefinition jobDefinition, ToolMessagePublisherChannel toolMessagePublisher, long messagePublishingTimeout_Millis) {
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

    public ProcessOutputDescription execute() throws Exception {
        LOGGER.debug("start execution of process " + toolContext.getWacodisProcessID() + ", toolProcess: " + toolProcess);

        //publish message for execution start
        ToolMessagePublisher.publishMessageSync(this.toolMessagePublisher.toolExecution(), buildToolExecutionStartedMessage(), this.messagePublishingTimeout_Millis);

        //execute processing tool
        ProcessOutputDescription processOutput;
        try {
            processOutput = this.toolProcess.execute(this.toolContext);
            LOGGER.debug("Process: " + toolContext.getWacodisProcessID() + ",executed toolProcess " + toolProcess);
        } catch (ExecutionException e) {
            LOGGER.error("Process execution failed", e.getMessage());
            LOGGER.warn(e.getMessage(), e);

            //publish message with the failure
            ToolMessagePublisher.publishMessageSync(this.toolMessagePublisher.toolFailure(), buildToolFailureMessage(e.getMessage()), this.messagePublishingTimeout_Millis);

            throw e;
        }

        //publish toolFinished message
        ToolMessagePublisher.publishMessageSync(this.toolMessagePublisher.toolFinished(), buildToolFinishedMessage(processOutput), this.messagePublishingTimeout_Millis);

        LOGGER.info("Process: " + toolContext.getWacodisProcessID() + ",finished execution");

        return processOutput;
    }

    private Message<WacodisJobFinished> buildToolFinishedMessage(ProcessOutputDescription processOuput) {
        WacodisJobFinished msg = new WacodisJobFinished();
        ProductDescription pd = new ProductDescription();
        
        pd.setWpsJobIdentifier(processOuput.getProcessIdentifier());
        pd.setProductCollection(this.jobDefinition.getProductCollection());
        pd.setProcessingTool(this.jobDefinition.getProcessingTool());
        //get list of all IDs of distinct dataenvelopes that correspond with a input resource
        pd.setDataEnvelopeReferences(processOuput.getOriginDataEnvelopes());
        // do not include outputs which should no be published (e.g. Metadata output)
        pd.setOutputIdentifiers(getPublishableExpectedOutputIdentifiers());
        
        msg.setExecutionFinished(DateTime.now()); //set to time of tool finished message publication
        msg.setWacodisJobIdentifier(this.jobDefinition.getId());
        msg.setProductDescription(pd);

        return MessageBuilder.withPayload(msg).build();
    }

    private Message<WacodisJobExecution> buildToolExecutionStartedMessage() {
        WacodisJobExecution msg = new WacodisJobExecution();
        msg.setWacodisJobIdentifier(this.jobDefinition.getId());
        msg.setCreated(new DateTime());
        msg.setProcessingTool(this.jobDefinition.getProcessingTool());
        msg.setProductCollection(this.jobDefinition.getProductCollection());

        return MessageBuilder.withPayload(msg).build();
    }

    private Message<WacodisJobFailed> buildToolFailureMessage(String errorText) {
        WacodisJobFailed msg = new WacodisJobFailed();
        //TODO include wps job identifier (not wacodis job identifier)
        msg.setWpsJobIdentifier(null);
        msg.setCreated(new DateTime());
        msg.setReason(errorText);
        msg.setWacodisJobIdentifier(jobDefinition.getId());
        return MessageBuilder.withPayload(msg).build();
    }

    /**
     * @return a list of all expected outputs (IDs) where isPublishedOutput is
     * true
     */
    private List<String> getPublishableExpectedOutputIdentifiers() {
        List<ExpectedProcessOutput> allExpectedOutputs = this.toolContext.getExpectedOutputs();

        if (allExpectedOutputs == null || allExpectedOutputs.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else { //return all where isPublishedOuput == true
            return allExpectedOutputs.stream()
                    .filter(expectedOutput -> expectedOutput.isPublishedOutput())
                    .map(expectedOuputIdentifier -> expectedOuputIdentifier.getIdentifier())
                    .collect(Collectors.toList());
        }
    }

}
