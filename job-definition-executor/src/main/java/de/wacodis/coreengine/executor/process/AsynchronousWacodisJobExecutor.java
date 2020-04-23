/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEvent;
import de.wacodis.coreengine.executor.exception.JobProcessCompletionException;
import de.wacodis.coreengine.executor.exception.JobProcessException;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisherChannel;
import de.wacodis.coreengine.executor.process.events.JobProcessExecutedEvent;
import de.wacodis.coreengine.executor.process.events.JobProcessExecutedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessFailedEvent;
import de.wacodis.coreengine.executor.process.events.JobProcessFailedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessStartedEvent;
import de.wacodis.coreengine.executor.process.events.JobProcessStartedEventHandler;
import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEventHandler;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne
 */
public class AsynchronousWacodisJobExecutor implements WacodisJobExecutor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AsynchronousWacodisJobExecutor.class);

    private final ToolMessagePublisherChannel toolMessagePublisher;
    private long messagePublishingTimeout_Millis = -1;
    private final List<JobProcess> subProcesses;
    private final ExecutorService subProcessExecutorService;
    private JobProcessStartedEventHandler processStartedHandler;
    private JobProcessExecutedEventHandler processExecutedHandler;
    private JobProcessFailedEventHandler processFailedHandler;
    private WacodisJobExecutionEventHandler firstProcessStartedHandler;
    private WacodisJobExecutionEventHandler lastProcessFinishedHandler;

    public AsynchronousWacodisJobExecutor(List<JobProcess> subProcesses, ToolMessagePublisherChannel toolMessagePublisher, ExecutorService subProcessExecutorService) {
        this.toolMessagePublisher = toolMessagePublisher;
        this.subProcesses = subProcesses;
        this.subProcessExecutorService = subProcessExecutorService;
    }

    public AsynchronousWacodisJobExecutor(List<JobProcess> subProcesses, ToolMessagePublisherChannel toolMessagePublisher, ExecutorService subProcessExecutorService, long messagePublishingTimeout_Millis) {
        this(subProcesses, toolMessagePublisher, subProcessExecutorService);
        this.messagePublishingTimeout_Millis = messagePublishingTimeout_Millis;
    }

    @Override
    public void setProcessStartedHandler(JobProcessStartedEventHandler handler) {
        this.processStartedHandler = handler;
    }

    @Override
    public void setProcessExecutedHandler(JobProcessExecutedEventHandler handler) {
        this.processExecutedHandler = handler;
    }

    @Override
    public void setProcessFailedHandler(JobProcessFailedEventHandler handler) {
        this.processFailedHandler = handler;
    }

    @Override
    public void setFirstProcessStartedHandler(WacodisJobExecutionEventHandler handler) {
        this.firstProcessStartedHandler = handler;
    }

    @Override
    public void setFinalProcessFinishedHandler(WacodisJobExecutionEventHandler handler) {
        this.lastProcessFinishedHandler = handler;
    }

    @Override
    public void executeAllSubProcesses() {
        final String lastSubProcessId = this.subProcesses.get((this.subProcesses.size() - 1)).getJobProcessIdentifier();

        for (int i = 0; i < this.subProcesses.size(); i++) {
            Integer subProcessNumber = i;
            JobProcess subProcess = this.subProcesses.get(i);
            JobProcessExecutor executor;

            if (this.messagePublishingTimeout_Millis > 0) {
                executor = new JobProcessExecutor(subProcess, this.toolMessagePublisher, this.messagePublishingTimeout_Millis);
            } else {
                executor = new JobProcessExecutor(subProcess, this.toolMessagePublisher);
            }

            CompletableFuture<JobProcessOutputDescription> processFuture = CompletableFuture.supplyAsync(() -> {
                if (subProcessNumber == 0) {
                    WacodisJobExecutionEvent firstProcessStartedEvent = new WacodisJobExecutionEvent(subProcess, this.subProcesses, WacodisJobExecutionEvent.ProcessExecutionEventType.FIRSTPROCESSSTARTED, this.subProcessExecutorService, this);
                    fireWacodisJobExecutionEvent(firstProcessStartedEvent, this.firstProcessStartedHandler);
                }

                JobProcessStartedEvent processStartedEvent = new JobProcessStartedEvent(subProcess, this);
                fireProcessStartedEvent(processStartedEvent);

                try {
                    JobProcessOutputDescription processOutput = executor.execute();
                    return processOutput;
                } catch (JobProcessException e) { //catch all exception from async job, rethrow as CompetionException and handle in exceptionally()
                    throw new JobProcessCompletionException(e.getJobProcess(), e);
                }
            }, this.subProcessExecutorService);

            processFuture.thenAccept((JobProcessOutputDescription processOutput)
                    -> {
                JobProcessExecutedEvent succesfulExecutionEvent = new JobProcessExecutedEvent(processOutput.getJobProcess(), processOutput, this);
                fireProcessExecutedEvent(succesfulExecutionEvent);

                //ceck if final sub process
                if (processOutput.getJobProcess().getJobProcessIdentifier().equals(lastSubProcessId)) {
                    WacodisJobExecutionEvent finalProcessFinishedEvent = new WacodisJobExecutionEvent(processOutput.getJobProcess(), this.subProcesses, WacodisJobExecutionEvent.ProcessExecutionEventType.FINALPROCESSFINISHED, this.subProcessExecutorService, this);
                    fireWacodisJobExecutionEvent(finalProcessFinishedEvent, this.lastProcessFinishedHandler);
                }

            }).exceptionally((Throwable t) -> { //handle exceptions that were raised by async job
                JobProcessCompletionException e = (JobProcessCompletionException) t; //cast is safe since only JobProcessCompletionException can be thrown in supplyAsync
                JobProcessFailedEvent processFailedEvent = new JobProcessFailedEvent(e.getJobProcess(), e, this);
                fireProcessFailedEvent(processFailedEvent);

                  //ceck if final sub process
                if (e.getJobProcess().getJobProcessIdentifier().equals(lastSubProcessId)) {
                    WacodisJobExecutionEvent finalProcessFinishedEvent = new WacodisJobExecutionEvent(e.getJobProcess(), this.subProcesses, WacodisJobExecutionEvent.ProcessExecutionEventType.FINALPROCESSFINISHED, this.subProcessExecutorService, this);
                    fireWacodisJobExecutionEvent(finalProcessFinishedEvent, this.lastProcessFinishedHandler);
                }

                return null; //satisfy return type
            });

        }

    }

    private void fireProcessStartedEvent(JobProcessStartedEvent e) {
        String subProcessId = e.getJobProcess().getJobProcessIdentifier();
        String wacodisJobId = e.getJobProcess().getJobDefinition().getId().toString();

        if (this.processStartedHandler != null) {
            LOGGER.debug("fire event of type {} for job process {} of wacodis job {}", e.getClass().getSimpleName(), subProcessId, wacodisJobId);
            this.processStartedHandler.onJobProcessStarted(e);
        } else {
            LOGGER.debug("cannot fire event of type {}, event handler is null", e.getClass().getSimpleName());
        }
    }

    private void fireProcessExecutedEvent(JobProcessExecutedEvent e) {
        String subProcessId = e.getJobProcess().getJobProcessIdentifier();
        String wacodisJobId = e.getJobProcess().getJobDefinition().getId().toString();

        if (this.processStartedHandler != null) {
            LOGGER.debug("fire event of type {} for job process {} of wacodis job {}", e.getClass().getSimpleName(), subProcessId, wacodisJobId);
            this.processExecutedHandler.onJobProcessFinished(e);
        } else {
            LOGGER.debug("cannot fire event of type {}, event handler is null", e.getClass().getSimpleName());
        }
    }

    private void fireProcessFailedEvent(JobProcessFailedEvent e) {
        String subProcessId = e.getJobProcess().getJobProcessIdentifier();
        String wacodisJobId = e.getJobProcess().getJobDefinition().getId().toString();

        if (this.processStartedHandler != null) {
            LOGGER.debug("fire event of type {} for job process {} of wacodis job {}", e.getClass().getSimpleName(), subProcessId, wacodisJobId);
            this.processFailedHandler.onJobProcessFailed(e);
        } else {
            LOGGER.debug("cannot fire event of type {}, event handler is null", e.getClass().getSimpleName());
        }
    }

    private void fireWacodisJobExecutionEvent(WacodisJobExecutionEvent e, WacodisJobExecutionEventHandler eventHandler) {
        String wacodisJobId = e.getCurrentJobProcess().getJobDefinition().getId().toString();

        if (eventHandler != null) {
            LOGGER.debug("fire event {} of type {} for of wacodis job {}", e.getClass().getSimpleName(), wacodisJobId);

            switch (e.getEventType()) {
                case FINALPROCESSFINISHED:
                    eventHandler.onFinalJobProcessFinished(e);
                    break;
                case FIRSTPROCESSSTARTED:
                    eventHandler.onFirstJobProcessStarted(e);
                    break;
                default:
                    LOGGER.error("cannot fire event {} of type {}, event type {} is unknown", e.getEventType(), e.getClass().getSimpleName(), e.getEventType());
                    break;
            }

        } else {
            LOGGER.debug("cannot fire event {} of type {}, event handler is null", e.getEventType(), e.getClass().getSimpleName());
        }
    }

}
