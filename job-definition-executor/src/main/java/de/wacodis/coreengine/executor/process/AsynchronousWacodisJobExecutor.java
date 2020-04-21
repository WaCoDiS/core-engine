/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.executor.exception.JobProcessCompletionException;
import de.wacodis.coreengine.executor.exception.JobProcessException;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisherChannel;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import org.joda.time.DateTime;

/**
 *
 * @author Arne
 */
public class AsynchronousWacodisJobExecutor implements WacodisJobExecutor{

    private Process toolProcess;
    private ToolMessagePublisherChannel toolMessagePublisher;
    private long messagePublishingTimeout_Millis = -1;
    private final List<ProcessContext> subProcesses;
    private final WacodisJobDefinition jobDefinition;
    private final ExecutorService subProcessExecutorService;
    private ProcessExecutionHandler processStartedHandler;
    private ProcessExecutionHandler processExecutedHandler;
    private ProcessExecutionHandler processFailedHandler;
    private ProcessExecutionHandler firstProcessStartedHandler;
    private ProcessExecutionHandler lastProcessExecutionHandler;

    public AsynchronousWacodisJobExecutor(Process toolProcess, ToolMessagePublisherChannel toolMessagePublisher, List<ProcessContext> subProcesses, WacodisJobDefinition jobDefinition, ExecutorService subProcessExecutorService) {
        this.toolProcess = toolProcess;
        this.toolMessagePublisher = toolMessagePublisher;
        this.subProcesses = subProcesses;
        this.jobDefinition = jobDefinition;
        this.subProcessExecutorService = subProcessExecutorService;

        //set default handlers
        ProcessExecutionHandler nullHandler = new NullProcessEventHandler();
        this.processStartedHandler = nullHandler;
        this.processExecutedHandler = nullHandler;
        this.processFailedHandler = nullHandler;
        this.firstProcessStartedHandler = nullHandler;
        this.lastProcessExecutionHandler = nullHandler;
    }

    public AsynchronousWacodisJobExecutor(Process toolProcess, ToolMessagePublisherChannel toolMessagePublisher, List<ProcessContext> subProcesses, WacodisJobDefinition jobDefinition, ExecutorService subProcessExecutorService, long messagePublishingTimeout_Millis) {
        this(toolProcess, toolMessagePublisher, subProcesses, jobDefinition, subProcessExecutorService);
        this.messagePublishingTimeout_Millis = messagePublishingTimeout_Millis;
    }

    @Override
    public void setProcessStartedHandler(ProcessExecutionHandler processStartedHandler) {
        this.processStartedHandler = processStartedHandler;
    }

    @Override
    public void setProcessExecutedHandler(ProcessExecutionHandler processExecutedHandler) {
        this.processExecutedHandler = processExecutedHandler;
    }

    @Override
    public void setProcessFailedHandler(ProcessExecutionHandler processFailedHandler) {
        this.processFailedHandler = processFailedHandler;
    }

    @Override
    public void setFirstProcessStartedHandler(ProcessExecutionHandler firstProcessStartedHandler) {
        this.firstProcessStartedHandler = firstProcessStartedHandler;
    }

    @Override
    public void setLastProcessExecutionHandler(ProcessExecutionHandler lastProcessExecutionHandler) {
        this.lastProcessExecutionHandler = lastProcessExecutionHandler;
    }

    @Override
    public void executeAllSubProcesses() {
        String uniqueProcessSuffix = DateTime.now().toString();
        String lastSubProcessId = createSubProcessID(this.subProcesses.size(), this.subProcesses.size(), uniqueProcessSuffix);

        for (int i = 0; i < this.subProcesses.size(); i++) {
            String subProcessId = createSubProcessID(i, this.subProcesses.size(), uniqueProcessSuffix);

            if (i == 0) {
                this.firstProcessStartedHandler.handle(new ProcessExecutionEvent(this.jobDefinition, subProcessId, "", ProcessExecutionEvent.ProcessExecutionEventType.FIRSTPROCESSSTARTED));
            }

            ProcessContext subProcess = this.subProcesses.get(i);
            JobProcessExecutor executor;

            if (this.messagePublishingTimeout_Millis > 0) {
                executor = new JobProcessExecutor(this.toolProcess, subProcess, this.jobDefinition, this.toolMessagePublisher, this.messagePublishingTimeout_Millis);
            } else {
                executor = new JobProcessExecutor(this.toolProcess, subProcess, this.jobDefinition, this.toolMessagePublisher);
            }

            CompletableFuture<JobProcessOutputDescription> processFuture = CompletableFuture.supplyAsync(() -> {
                this.processStartedHandler.handle(new ProcessExecutionEvent(this.jobDefinition, subProcessId, "", ProcessExecutionEvent.ProcessExecutionEventType.PROCESSSTARTED));
                
                try {
                    JobProcessOutputDescription processOutput = executor.execute(subProcessId);
                    return processOutput;
                } catch (JobProcessException e) { //catch all exception from async job, rethrow as CompetionException and handle in exceptionally()
                    throw new JobProcessCompletionException(e.getJobProcessIdentifier(), e.getWacodisJobIdentifier(), e);
                }
            }, this.subProcessExecutorService);

            processFuture.thenAccept((JobProcessOutputDescription processOutput)
                    -> {
                this.processExecutedHandler.handle(new ProcessExecutionEvent(this.jobDefinition, processOutput.getJobProcessIdentifier(), "", ProcessExecutionEvent.ProcessExecutionEventType.PROCESSEXECUTED));

                if (processOutput.getJobProcessIdentifier().equals(lastSubProcessId)) {
                    this.lastProcessExecutionHandler.handle(new ProcessExecutionEvent(this.jobDefinition, processOutput.getJobProcessIdentifier(), "", ProcessExecutionEvent.ProcessExecutionEventType.FINALPROCESSFINISHED));
                }

            }).exceptionally((Throwable t) -> { //handle exceptions that were raised by async job
                JobProcessCompletionException e = (JobProcessCompletionException) t;
                this.processFailedHandler.handle(new ProcessExecutionEvent(this.jobDefinition, e.getJobProcessIdentifier(), e.getMessage(), ProcessExecutionEvent.ProcessExecutionEventType.PROCESSFAILED));

                if (e.getJobProcessIdentifier().equals(lastSubProcessId)) {
                    this.lastProcessExecutionHandler.handle(new ProcessExecutionEvent(this.jobDefinition, e.getJobProcessIdentifier(), e.getMessage(), ProcessExecutionEvent.ProcessExecutionEventType.FINALPROCESSFINISHED));
                }

                return null; //satisfy return type
            });

        }

    }

    private String createSubProcessID(int part, int of, String uniqueProcessSuffix) {
        String subProcessId = this.jobDefinition.getId().toString() + "_" + part + "_" + of + "_" + uniqueProcessSuffix;
        return subProcessId;
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.subProcessExecutorService;
    }

}
