/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobExecution;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisher;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisherChannel;
import de.wacodis.coreengine.executor.process.JobProcess;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 *
 * @author Arne
 */
public class JobExecutionEventHandler implements WacodisJobExecutionEventHandler {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JobProcessEventHandler.class);

    private final ToolMessagePublisherChannel toolMessagePublisher;
    private long messagePublishingTimeout_Millis = 10000; //default of ten seconds

    public JobExecutionEventHandler(ToolMessagePublisherChannel toolMessagePublisher) {
        this.toolMessagePublisher = toolMessagePublisher;
    }

    public JobExecutionEventHandler(ToolMessagePublisherChannel toolMessagePublisher, long messagePublishingTimeout_Millis) {
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
    public void onFirstJobProcessStarted(WacodisJobExecutionEvent e) {
        JobProcess subProcess = e.getCurrentJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();
        
        //publish message on job execution start
        ToolMessagePublisher.publishMessageSync(this.toolMessagePublisher.toolExecution(), buildToolExecutionStartedMessage(subProcess, e.getTimestamp()), this.messagePublishingTimeout_Millis);


        LOGGER.info("execution of wacodis job {} started, first sub process {} started", jobDefinition.getId(), subProcess.getJobProcessIdentifier());
    }

    @Override
    public void onFinalJobProcessFinished(WacodisJobExecutionEvent e) {
        JobProcess subProcess = e.getCurrentJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();

        LOGGER.info("final subprocess ({}) of wacodis job {} finished, shutdown threadpool", subProcess.getJobProcessIdentifier(), jobDefinition.getId());
        if (!e.getExecutorService().isShutdown()) {
            e.getExecutorService().shutdown();
            LOGGER.debug("successfully shut down threadpool for execution of wacodis job {}", jobDefinition.getId());
        } else {
            LOGGER.debug("threadpool for execution of wacodis job {} is already shut down", jobDefinition.getId());
        }
    }

    private Message<WacodisJobExecution> buildToolExecutionStartedMessage(JobProcess subProcess, DateTime timestamp) {
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();

        WacodisJobExecution msg = new WacodisJobExecution();
        msg.setWacodisJobIdentifier(jobDefinition.getId());
        msg.setCreated(timestamp);
        msg.setProcessingTool(jobDefinition.getProcessingTool());
        msg.setProductCollection(jobDefinition.getProductCollection());

        return MessageBuilder.withPayload(msg).build();
    }
}
