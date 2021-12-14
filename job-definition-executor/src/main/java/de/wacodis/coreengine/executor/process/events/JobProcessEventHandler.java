/*
 * Copyright 2018-2021 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.core.models.ProductDescription;
import de.wacodis.core.models.SingleJobExecutionEvent;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobFailed;
import de.wacodis.core.models.WacodisJobFinished;
import de.wacodis.coreengine.executor.exception.JobProcessCompletionException;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisher;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisherChannel;
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
    private final ToolMessagePublisherChannel toolMessagePublisher;
    private long messagePublishingTimeout_Millis = 10000; //default of ten seconds

    public JobProcessEventHandler(ToolMessagePublisherChannel toolMessagePublisher, ApplicationEventPublisher jobProcessFailedPublisher) {
        this.jobProcessFailedPublisher = jobProcessFailedPublisher;
        this.toolMessagePublisher = toolMessagePublisher;
    }

    public JobProcessEventHandler(ToolMessagePublisherChannel toolMessagePublisher, ApplicationEventPublisher jobProcessFailedPublisher, long messagePublishingTimeout_Millis) {
        this.jobProcessFailedPublisher = jobProcessFailedPublisher;
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
    public void onJobProcessFinished(JobProcessExecutedEvent e, boolean isFinalJobProcess) {
        JobProcess subProcess = e.getJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();
        boolean isSingleExecutionJob = isSingleExecutionJob(jobDefinition);

        //publish message on succesful processing
        ToolMessagePublisher.publishMessageSync(this.toolMessagePublisher.toolFinished(), buildProcessFinishedMessage(e.getOutput(), e.getTimestamp(), isFinalJobProcess, isSingleExecutionJob), this.messagePublishingTimeout_Millis);

        LOGGER.info("sub process {} of wacodis job {} finished sucessfully", subProcess.getJobProcessIdentifier(), jobDefinition.getId());
    }

    @Override
    public void onJobProcessFailed(JobProcessFailedEvent e, boolean isFinalJobProcess) {
        JobProcess subProcess = e.getJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();
        boolean isSingleExecutionJob = isSingleExecutionJob(jobDefinition);

        //publish message on failed processing
        ToolMessagePublisher.publishMessageSync(this.toolMessagePublisher.toolFailure(), buildToolFailureMessage(e.getException(), e.getTimestamp(), isFinalJobProcess, isSingleExecutionJob), this.messagePublishingTimeout_Millis);

        LOGGER.error("execution of  sub process " + subProcess.getJobProcessIdentifier() + " of wacodis job " + jobDefinition.getId() + " failed", e.getException());
    }

    @Override
    public void onJobProcessStarted(JobProcessStartedEvent e) {
        JobProcess subProcess = e.getJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();

        LOGGER.info("execution of sub process {} of wacodis job {} started", subProcess.getJobProcessIdentifier(), jobDefinition.getId());
    }

    private Message<WacodisJobFinished> buildProcessFinishedMessage(JobProcessOutputDescription processOuput, DateTime timestamp, boolean isFinalJobProcess, boolean isSingleExecutionJob) {
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

        msg.setExecutionFinished(timestamp); //set to time of tool finished message publication
        msg.setWacodisJobIdentifier(jobDefinition.getId());
        msg.setProductDescription(pd);
        msg.setFinalJobProcess(isFinalJobProcess);
        msg.setSingleExecutionJob(isSingleExecutionJob);

        return MessageBuilder.withPayload(msg).build();
    }

    private Message<WacodisJobFailed> buildToolFailureMessage(JobProcessCompletionException exception, DateTime timestamp, boolean isFinalJobProcess, boolean isSingleExecutionJob) {
        WacodisJobFailed msg = new WacodisJobFailed();
        //TODO include wps job identifier (not wacodis job identifier)
        msg.setWpsJobIdentifier(null);
        msg.setCreated(timestamp);
        msg.setReason(exception.getMessage());
        msg.setWacodisJobIdentifier(exception.getJobProcess().getJobDefinition().getId());
        msg.setFinalJobProcess(isFinalJobProcess);
        msg.setSingleExecutionJob(isSingleExecutionJob);
        
        return MessageBuilder.withPayload(msg).build();
    }

    /**
     * @return a list of all expected outputs (IDs) where isPublishedOutput is
     * true
     */
    private List<String> getPublishableExpectedOutputIdentifiers(JobProcess subProcess) {
        List<JobOutputDescriptor> allExpectedOutputs = subProcess.getExecutionContext().getExpectedOutputs();

        if (allExpectedOutputs == null || allExpectedOutputs.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else { //return all where isPublishedOuput == true
            return allExpectedOutputs.stream()
                    .filter(expectedOutput -> expectedOutput.getPublishedOutput())
                    .map(expectedOuputIdentifier -> expectedOuputIdentifier.getIdentifier())
                    .collect(Collectors.toList());
        }
    }

    private boolean isSingleExecutionJob(WacodisJobDefinition jobDefinition) {
        return (jobDefinition.getExecution().getEvent() != null && jobDefinition.getExecution().getEvent() instanceof SingleJobExecutionEvent);
    }
}
