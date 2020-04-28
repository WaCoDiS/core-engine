/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.core.models.ProductDescription;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobFailed;
import de.wacodis.core.models.WacodisJobFinished;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.events.WacodisJobExecutionFailedEvent;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.exception.JobProcessCompletionException;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisher;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisherChannel;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.JobProcess;
import de.wacodis.coreengine.executor.process.JobProcessOutputDescription;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 *
 * @author Arne
 */
public class JobProcessEventHandler implements JobProcessExecutedEventHandler, JobProcessFailedEventHandler, JobProcessStartedEventHandler {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JobProcessEventHandler.class);

    private final ApplicationEventPublisher jobProcessFailedPublisher;
    private final WacodisJobWrapper job;
    private final ToolMessagePublisherChannel toolMessagePublisher;
    private long messagePublishingTimeout_Millis = 10000; //default of ten seconds

    public JobProcessEventHandler(WacodisJobWrapper job, ToolMessagePublisherChannel toolMessagePublisher, ApplicationEventPublisher jobProcessFailedPublisher) {
        this.jobProcessFailedPublisher = jobProcessFailedPublisher;
        this.job = job;
        this.toolMessagePublisher = toolMessagePublisher;
    }

    public JobProcessEventHandler(WacodisJobWrapper job, ToolMessagePublisherChannel toolMessagePublisher, ApplicationEventPublisher jobProcessFailedPublisher, long messagePublishingTimeout_Millis) {
        this.jobProcessFailedPublisher = jobProcessFailedPublisher;
        this.job = job;
        this.toolMessagePublisher = toolMessagePublisher;
        this.messagePublishingTimeout_Millis = messagePublishingTimeout_Millis;
    }

    public long getMessagePublishingTimeout_Millis() {
        return messagePublishingTimeout_Millis;
    }

    public void setMessagePublishingTimeout_Millis(long messagePublishingTimeout_Millis) {
        this.messagePublishingTimeout_Millis = messagePublishingTimeout_Millis;
    }

    @Override
    public void onJobProcessFinished(JobProcessExecutedEvent e) {
        JobProcess subProcess = e.getJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();
        
        //publish message on succesful processing
        ToolMessagePublisher.publishMessageSync(this.toolMessagePublisher.toolFinished(), buildProcessFinishedMessage(e.getOutput()), this.messagePublishingTimeout_Millis);

        LOGGER.info("sub process {} of wacodis job {} finished sucessfully", subProcess.getJobProcessIdentifier(), jobDefinition.getId());
    }

    @Override
    public void onJobProcessFailed(JobProcessFailedEvent e) {
        JobProcess subProcess = e.getJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();
        
        //publish message on failed processing
         ToolMessagePublisher.publishMessageSync(this.toolMessagePublisher.toolFailure(), buildToolFailureMessage(e.getException()), this.messagePublishingTimeout_Millis);
        
        LOGGER.error("execution of  sub process " + subProcess.getJobProcessIdentifier() + " of wacodis job " + jobDefinition.getId() + " failed", e.getException());
        //trigger job execution failed event
        if (this.job.getExecutionContext().getRetryCount() < this.job.getJobDefinition().getRetrySettings().getMaxRetries()) { //check if retry
            //fire event to schedule retry attempt
            int retries = this.job.incrementRetryCount();
            LOGGER.info("publish jobExecutionFailedEvent to schedule retry attempt {} of {}, WacodisJobID {}, toolID: {}", retries, job.getJobDefinition().getRetrySettings().getMaxRetries(), jobDefinition.getId(), subProcess.getExecutionContext().getWacodisProcessID());
            WacodisJobExecutionFailedEvent failEvent = new WacodisJobExecutionFailedEvent(this, job, new ExecutionException(e.getException()));
            this.jobProcessFailedPublisher.publishEvent(failEvent);
        } else {
            //retries exceeded, job failed ultimately
            LOGGER.error("execution of wacodis job failed ultimately after " + job.getExecutionContext().getRetryCount() + " of " + job.getJobDefinition().getRetrySettings().getMaxRetries() + " retries, WacodisJobID: " + jobDefinition.getId().toString() + ",toolID: " + subProcess.getExecutionContext().getWacodisProcessID());
        }
    }

    @Override
    public void onJobProcessStarted(JobProcessStartedEvent e) {
        JobProcess subProcess = e.getJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();

        LOGGER.info("execution of sub process {} of wacodis job {} started", subProcess.getJobProcessIdentifier(), jobDefinition.getId());
    }

    private Message<WacodisJobFinished> buildProcessFinishedMessage(JobProcessOutputDescription processOuput) {
        WacodisJobDefinition jobDefinition = processOuput.getJobProcess().getJobDefinition();
        WacodisJobFinished msg = new WacodisJobFinished();
        ProductDescription pd = new ProductDescription();

        pd.setWpsJobIdentifier(processOuput.getProcessIdentifier());
        pd.setProductCollection(jobDefinition.getProductCollection());
        pd.setProcessingTool(jobDefinition.getProcessingTool());
        //get list of all IDs of distinct dataenvelopes that correspond with a input resource
        pd.setDataEnvelopeReferences(processOuput.getOriginDataEnvelopes());
        // do not include outputs which should no be published (e.g. Metadata output)
        pd.setOutputIdentifiers(getPublishableExpectedOutputIdentifiers(processOuput.getJobProcess()));

        msg.setExecutionFinished(DateTime.now()); //set to time of tool finished message publication
        msg.setWacodisJobIdentifier(jobDefinition.getId());
        msg.setProductDescription(pd);

        return MessageBuilder.withPayload(msg).build();
    }

    private Message<WacodisJobFailed> buildToolFailureMessage(JobProcessCompletionException exception) {
        WacodisJobFailed msg = new WacodisJobFailed();
        //TODO include wps job identifier (not wacodis job identifier)
        msg.setWpsJobIdentifier(null);
        msg.setCreated(new DateTime());
        msg.setReason(exception.getMessage());
        msg.setWacodisJobIdentifier(exception.getJobProcess().getJobDefinition().getId());
        return MessageBuilder.withPayload(msg).build();
    }

    /**
     * @return a list of all expected outputs (IDs) where isPublishedOutput is
     * true
     */
    private List<String> getPublishableExpectedOutputIdentifiers(JobProcess subProcess) {
        List<ExpectedProcessOutput> allExpectedOutputs = subProcess.getExecutionContext().getExpectedOutputs();

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
