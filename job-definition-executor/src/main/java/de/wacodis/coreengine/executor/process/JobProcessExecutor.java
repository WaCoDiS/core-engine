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
import de.wacodis.coreengine.executor.exception.JobProcessException;
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
public class JobProcessExecutor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JobProcessExecutor.class);

    private final JobProcess jobProcess;
    private ToolMessagePublisherChannel toolMessagePublisher;
    private long messagePublishingTimeout_Millis = 10000; //default of ten seconds

    public JobProcessExecutor(JobProcess jobProcess, ToolMessagePublisherChannel toolMessagePublisher) {
        this.jobProcess = jobProcess;
        this.toolMessagePublisher = toolMessagePublisher;
    }

    public JobProcessExecutor(JobProcess jobProcess, ToolMessagePublisherChannel toolMessagePublisher, long messagePublishingTimeout_Millis) {
        this(jobProcess, toolMessagePublisher);
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

    public WacodisJobDefinition getJobDefinition() {
        return this.jobProcess.getJobDefinition();
    }

    public JobProcessOutputDescription execute() throws JobProcessException {
        Process process = this.jobProcess.getProcess();
        ProcessContext executionContext = this.jobProcess.getExecutionContext();
        LOGGER.debug("start execution of process " + executionContext.getWacodisProcessID() + ", toolProcess: " + process);

        //publish message for execution start
        ToolMessagePublisher.publishMessageSync(this.toolMessagePublisher.toolExecution(), buildToolExecutionStartedMessage(), this.messagePublishingTimeout_Millis);

        //execute processing tool
        JobProcessOutputDescription jobProcessOutput = null;

        try {
            ProcessOutputDescription wpsProcessOutput = process.execute(executionContext);
            jobProcessOutput = new JobProcessOutputDescription(wpsProcessOutput, this.jobProcess);
            LOGGER.debug("Process: " + executionContext.getWacodisProcessID() + ",executed toolProcess " + process);
        } catch (ExecutionException e) {
            LOGGER.error("Process execution failed", e.getMessage());
            LOGGER.warn(e.getMessage(), e);

            //publish message with the failure
            ToolMessagePublisher.publishMessageSync(this.toolMessagePublisher.toolFailure(), buildToolFailureMessage(e.getMessage()), this.messagePublishingTimeout_Millis);

            throw new JobProcessException(this.jobProcess, e);
        }

        //publish toolFinished message
        ToolMessagePublisher.publishMessageSync(this.toolMessagePublisher.toolFinished(), buildToolFinishedMessage(jobProcessOutput), this.messagePublishingTimeout_Millis);

        LOGGER.info("Process: " + executionContext.getWacodisProcessID() + ",finished execution");

        return jobProcessOutput;
    }

    private Message<WacodisJobFinished> buildToolFinishedMessage(ProcessOutputDescription processOuput) {
        WacodisJobDefinition jobDefinition = this.jobProcess.getJobDefinition();
        WacodisJobFinished msg = new WacodisJobFinished();
        ProductDescription pd = new ProductDescription();

        pd.setWpsJobIdentifier(processOuput.getProcessIdentifier());
        pd.setProductCollection(jobDefinition.getProductCollection());
        pd.setProcessingTool(jobDefinition.getProcessingTool());
        //get list of all IDs of distinct dataenvelopes that correspond with a input resource
        pd.setDataEnvelopeReferences(processOuput.getOriginDataEnvelopes());
        // do not include outputs which should no be published (e.g. Metadata output)
        pd.setOutputIdentifiers(getPublishableExpectedOutputIdentifiers());

        msg.setExecutionFinished(DateTime.now()); //set to time of tool finished message publication
        msg.setWacodisJobIdentifier(jobDefinition.getId());
        msg.setProductDescription(pd);

        return MessageBuilder.withPayload(msg).build();
    }

    private Message<WacodisJobExecution> buildToolExecutionStartedMessage() {
        WacodisJobDefinition jobDefinition = this.jobProcess.getJobDefinition();
        
        WacodisJobExecution msg = new WacodisJobExecution();
        msg.setWacodisJobIdentifier(jobDefinition.getId());
        msg.setCreated(new DateTime());
        msg.setProcessingTool(jobDefinition.getProcessingTool());
        msg.setProductCollection(jobDefinition.getProductCollection());

        return MessageBuilder.withPayload(msg).build();
    }

    private Message<WacodisJobFailed> buildToolFailureMessage(String errorText) {
        WacodisJobFailed msg = new WacodisJobFailed();
        //TODO include wps job identifier (not wacodis job identifier)
        msg.setWpsJobIdentifier(null);
        msg.setCreated(new DateTime());
        msg.setReason(errorText);
        msg.setWacodisJobIdentifier(this.jobProcess.getJobDefinition().getId());
        return MessageBuilder.withPayload(msg).build();
    }

    /**
     * @return a list of all expected outputs (IDs) where isPublishedOutput is
     * true
     */
    private List<String> getPublishableExpectedOutputIdentifiers() {
        List<ExpectedProcessOutput> allExpectedOutputs = this.jobProcess.getExecutionContext().getExpectedOutputs();

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
