/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobExecution;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.events.WacodisJobExecutionFailedEvent;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisher;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisherChannel;
import de.wacodis.coreengine.executor.process.JobProcess;
import de.wacodis.coreengine.executor.process.ProcessOutputDescription;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;

/**
 *
 * @author Arne
 */
public class JobExecutionEventHandler implements WacodisJobExecutionEventHandler {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JobExecutionEventHandler.class);

    private final ToolMessagePublisherChannel toolMessagePublisher;
    private final ApplicationEventPublisher jobFailedEventPublisher;
    private final WacodisJobWrapper job;
    private long messagePublishingTimeout_Millis = 10000; //default of ten seconds
    private int maxFailedJobProcesses = 0;

    public JobExecutionEventHandler(WacodisJobWrapper job, ToolMessagePublisherChannel toolMessagePublisher, ApplicationEventPublisher jobFailedEventPublisher) {
        this.job = job;
        this.toolMessagePublisher = toolMessagePublisher;
        this.jobFailedEventPublisher = jobFailedEventPublisher;
    }

    public JobExecutionEventHandler(WacodisJobWrapper job, ToolMessagePublisherChannel toolMessagePublisher, ApplicationEventPublisher jobFailedEventPublisher, long messagePublishingTimeout_Millis) {
        this.job = job;
        this.toolMessagePublisher = toolMessagePublisher;
        this.messagePublishingTimeout_Millis = messagePublishingTimeout_Millis;
        this.jobFailedEventPublisher = jobFailedEventPublisher;
    }

    public JobExecutionEventHandler(WacodisJobWrapper job, ToolMessagePublisherChannel toolMessagePublisher, ApplicationEventPublisher jobFailedEventPublisher, long messagePublishingTimeout_Millis, int maxFailedJobProcesses) {
        this.job = job;
        this.toolMessagePublisher = toolMessagePublisher;
        this.messagePublishingTimeout_Millis = messagePublishingTimeout_Millis;
        this.maxFailedJobProcesses = maxFailedJobProcesses;
        this.jobFailedEventPublisher = jobFailedEventPublisher;
    }

    public JobExecutionEventHandler(WacodisJobWrapper job, ToolMessagePublisherChannel toolMessagePublisher, ApplicationEventPublisher jobFailedEventPublisher, int maxFailedJobProcesses) {
        this.job = job;
        this.toolMessagePublisher = toolMessagePublisher;
        this.maxFailedJobProcesses = maxFailedJobProcesses;
        this.jobFailedEventPublisher = jobFailedEventPublisher;
    }

    public long getMessagePublishingTimeout_Millis() {
        return messagePublishingTimeout_Millis;
    }

    public void setMessagePublishingTimeout_Millis(long messagePublishingTimeout_Millis) {
        this.messagePublishingTimeout_Millis = messagePublishingTimeout_Millis;
    }

    public int getMaxFailedJobProcesses() {
        return maxFailedJobProcesses;
    }

    public void setMaxFailedJobProcesses(int maxFailedJobProcesses) {
        this.maxFailedJobProcesses = maxFailedJobProcesses;
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
        String subProcessReport = getSubProcessReport(e.getAllJobProcesses());
        int subProcessCount = e.getAllJobProcesses().size();
        int failedSubProcessesCount = getJobProcessesByStatus(e.getAllJobProcesses(), JobProcess.Status.FAILED).size();

        LOGGER.info("final subprocess ({}) of wacodis job {} finished, {} of {} subprocesses failed", subProcess.getJobProcessIdentifier(), jobDefinition.getId(), failedSubProcessesCount, subProcessCount);
        if (failedSubProcessesCount > this.maxFailedJobProcesses) {
            //handle retry
            if (this.job.getExecutionContext().getRetryCount() < this.job.getJobDefinition().getRetrySettings().getMaxRetries()) { //check if max retries exceeded
                LOGGER.info("{} subprocesses of wacodis job {} failed (max failed subprocesses: {}), initiate retry", failedSubProcessesCount, jobDefinition.getId(), this.maxFailedJobProcesses);
                //fire event to schedule retry attempt
                int retries = this.job.incrementRetryCount();
                LOGGER.info("publish jobExecutionFailedEvent to schedule retry attempt {} of {}, WacodisJobID {}, toolID: {}", retries, job.getJobDefinition().getRetrySettings().getMaxRetries(), jobDefinition.getId(), subProcess.getExecutionContext().getWacodisProcessID());
                WacodisJobExecutionFailedEvent failEvent = new WacodisJobExecutionFailedEvent(this, job, new ExecutionException(jobDefinition.getId(), "Execution of Wacodis Job " + job.getJobDefinition().getId() + "failed, subprocess: " + System.lineSeparator() + subProcessReport));
                this.jobFailedEventPublisher.publishEvent(failEvent);
            } else {
                //retries exceeded, job failed ultimately
                LOGGER.error("execution of wacodis job failed ultimately after " + job.getExecutionContext().getRetryCount() + " of " + job.getJobDefinition().getRetrySettings().getMaxRetries() + " retries, WacodisJobID: " + jobDefinition.getId().toString() + ",toolID: " + subProcess.getExecutionContext().getWacodisProcessID());
            }
        } else {
            LOGGER.info("execution of wacodis job {} successfully finished, {} sub processes failed (max failed subprocesses: {})", jobDefinition.getId(), failedSubProcessesCount, this.maxFailedJobProcesses);
        }

        LOGGER.info("final subprocess ({}) of wacodis job {} finished, subprocess: " + System.lineSeparator() + subProcessReport, subProcess.getJobProcessIdentifier(), jobDefinition.getId());
        LOGGER.info("final subprocess ({}) of wacodis job {} finished, shutdown threadpool", subProcess.getJobProcessIdentifier(), jobDefinition.getId());
        shutdownThreadpool(e.getExecutorService(), jobDefinition);
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

    private List<JobProcess> getJobProcessesByStatus(List<JobProcess> processes, JobProcess.Status status) {
        return processes.stream().filter(p -> p.getStatus().equals(status)).collect(Collectors.toList());
    }

    private void shutdownThreadpool(ExecutorService es, WacodisJobDefinition jobDefinition) {
        if (!es.isShutdown()) {
            es.shutdown();
            LOGGER.debug("successfully shut down threadpool for execution of wacodis job {}", jobDefinition.getId());
        } else {
            LOGGER.debug("threadpool for execution of wacodis job {} is already shut down", jobDefinition.getId());
        }
    }

    private String getSubProcessReport(List<JobProcess> subProcesses) {
        StringBuilder report = new StringBuilder();

        for (JobProcess process : subProcesses) {
            report.append("subprocess: ").append(process.getJobProcessIdentifier()).append(", wacodis Job: ").append(process.getJobDefinition().getId()).append(", processing tool: ").append(process.getJobDefinition().getProcessingTool()).append(", status: ").append(process.getStatus().toString());

            if (process.getProcessOutput().isPresent()) {
                ProcessOutputDescription output = process.getProcessOutput().get();
                report.append(", output parameters: ").append(process.getExecutionContext().getExpectedOutputs());
            }
            if (process.getException().isPresent()) {
                Exception exception = process.getException().get();
                report.append(", exception message: ").append(exception.getMessage());
            }

            report.append(System.lineSeparator());
        }

        return report.toString();
    }
}
