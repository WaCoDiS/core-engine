/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
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
package de.wacodis.coreengine.executor.process;

import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEvent;
import de.wacodis.coreengine.executor.exception.JobProcessCompletionException;
import de.wacodis.coreengine.executor.exception.JobProcessException;
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
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne
 */
public class AsynchronousWacodisJobExecutor implements WacodisJobExecutor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AsynchronousWacodisJobExecutor.class);

    private static final String EXECUTIONFINISHEDTIMESTAMP_KEY = "endTimeMillis";

    private final ExecutorService subProcessExecutorService;
    private JobProcessStartedEventHandler processStartedHandler;
    private JobProcessExecutedEventHandler processExecutedHandler;
    private JobProcessFailedEventHandler processFailedHandler;
    private WacodisJobExecutionEventHandler firstProcessStartedHandler;
    private WacodisJobExecutionEventHandler lastProcessFinishedHandler;
    private final Object lockObj = new Object();
    private boolean firedFinalSubProcessEvent = false;

    public AsynchronousWacodisJobExecutor(ExecutorService subProcessExecutorService) {
        this.subProcessExecutorService = subProcessExecutorService;
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
    public void executeAllSubProcesses(List<JobProcess> subProcesses) {

        for (int i = 0; i < subProcesses.size(); i++) {
            Integer subProcessNumber = i;
            JobProcess subProcess = subProcesses.get(i);
            JobProcessExecutor executor = new JobProcessExecutor(subProcess);

            CompletableFuture<JobProcessOutputDescription> processFuture = CompletableFuture.supplyAsync(() -> {
                subProcess.setStatus(JobProcess.Status.STARTED);

                if (subProcessNumber == 0) {
                    WacodisJobExecutionEvent firstProcessStartedEvent = new WacodisJobExecutionEvent(subProcess, subProcesses, WacodisJobExecutionEvent.ProcessExecutionEventType.FIRSTPROCESSSTARTED, this.subProcessExecutorService, this);
                    JobExecutionEventHelper.fireWacodisJobExecutionEvent(firstProcessStartedEvent, this.firstProcessStartedHandler);
                }

                JobProcessStartedEvent processStartedEvent = new JobProcessStartedEvent(subProcess, this);
                JobExecutionEventHelper.fireProcessStartedEvent(processStartedEvent, this.processStartedHandler);

                //execute process
                try {
                    JobProcessOutputDescription processOutput = executor.execute();
                    processOutput.getJobProcess().setStatus(JobProcess.Status.SUCCESSFUL);
                    return processOutput;
                } catch (JobProcessException e) { //catch all exception from async job, rethrow as CompetionException and handle in exceptionally()
                    e.getJobProcess().setStatus(JobProcess.Status.FAILED);
                    throw new JobProcessCompletionException(e.getJobProcess(), e);
                }
            }, this.subProcessExecutorService);

            processFuture.thenAccept((JobProcessOutputDescription processOutput)
                    -> {
                processOutput.getJobProcess().setProcessOutput(processOutput);
                JobProcessExecutedEvent successfulExecutionEvent = new JobProcessExecutedEvent(processOutput.getJobProcess(), processOutput, this, getExecutionFinishedTimestamp(processOutput));
                
                //raise event(s)
                handleSuccessfulSubProcess(subProcesses, processOutput.getJobProcess(), successfulExecutionEvent);
            }).exceptionally((Throwable t) -> { //handle exceptions that were raised by async job
                JobProcessCompletionException e = (JobProcessCompletionException) t; //cast is safe since only JobProcessCompletionException can be thrown in supplyAsync
                e.getJobProcess().setException(e);
                JobProcessFailedEvent processFailedEvent = new JobProcessFailedEvent(e.getJobProcess(), e, this);

                //raise event(s)
                handleFailedSubProcess(subProcesses, e.getJobProcess(), processFailedEvent);

                return null; //satisfy return type
            });

        }

    }

    /**
     * try to get execution finished timestamp from process output, else use
     * DateTime.now()
     *
     * @param output
     * @return
     */
    private DateTime getExecutionFinishedTimestamp(ProcessOutputDescription output) {
        if (output.getAllOutputParameterKeys().contains(EXECUTIONFINISHEDTIMESTAMP_KEY)) {
            String timestampStr = output.getOutputParameter(EXECUTIONFINISHEDTIMESTAMP_KEY);

            try {
                long timestamp = Long.parseLong(timestampStr);
                LOGGER.debug("successfully read execution finished timestamp from process output");
                return new DateTime(timestamp);
            } catch (NumberFormatException e) {
                LOGGER.debug("could not read execution finished timestamp from process output because NumberFormatExeception was raised, use current timestamp instead");
                return DateTime.now();
            }

        } else {
            LOGGER.debug("could not read execution finished timestamp from process output because process output does not contain key {}, use current timestamp instead", EXECUTIONFINISHEDTIMESTAMP_KEY);
            return DateTime.now();
        }
    }

    /**
     * raise event if final subProcess
     *
     * @param subProcesses
     * @param subProcess
     */
    private void handleSuccessfulSubProcess(List<JobProcess> subProcesses, JobProcess subProcess, JobProcessExecutedEvent event) {
        synchronized (this.lockObj) {
            //make sure event is only fired once
            //check if final job process
            if (isFinalJobProcess(subProcesses)) {
                //is final job process
                //fire process execution event
                JobExecutionEventHelper.fireProcessExecutedEvent(event, this.processExecutedHandler, true);
                //fire job completed event
                fireJobExecutionCompletedEvent(subProcess, subProcesses);
                this.firedFinalSubProcessEvent = true;
            } else {
                //not final job process
                //only fire process execution event
                JobExecutionEventHelper.fireProcessExecutedEvent(event, this.processExecutedHandler, false);
            }
        }
    }

    private void handleFailedSubProcess(List<JobProcess> subProcesses, JobProcess subProcess, JobProcessFailedEvent event) {
        synchronized (this.lockObj) {
            //make sure event is only fired once
            //check if final job process
            if (isFinalJobProcess(subProcesses)) {
                //is final job process
                //fire process failed event
                JobExecutionEventHelper.fireProcessFailedEvent(event, this.processFailedHandler, true);
                //fire job completed event
                fireJobExecutionCompletedEvent(subProcess, subProcesses);
                this.firedFinalSubProcessEvent = true;
            } else {
                //not final job process
                //only fire process failed event
                JobExecutionEventHelper.fireProcessFailedEvent(event, this.processFailedHandler, false);
            }
        }
    }

    private void fireJobExecutionCompletedEvent(JobProcess finalJobProcess, List<JobProcess> allJobProcesses) {
        WacodisJobExecutionEvent finalProcessFinishedEvent = new WacodisJobExecutionEvent(finalJobProcess, allJobProcesses, WacodisJobExecutionEvent.ProcessExecutionEventType.FINALPROCESSFINISHED, this.subProcessExecutorService, this);
        JobExecutionEventHelper.fireWacodisJobExecutionEvent(finalProcessFinishedEvent, this.lastProcessFinishedHandler);
    }

    /**
     * return jobProcesses that not finished successfully or failed
     *
     * @param jobProcesses
     * @return
     */
    private List<JobProcess> getUnfinishedJobProcesses(List<JobProcess> jobProcesses) {
        return jobProcesses.stream().filter(p -> (!p.getStatus().equals(JobProcess.Status.SUCCESSFUL) && !p.getStatus().equals(JobProcess.Status.FAILED))).collect(Collectors.toList());
    }
    
    private boolean isFinalJobProcess(List<JobProcess> jobProcesses){
        return getUnfinishedJobProcesses(jobProcesses).isEmpty() && !this.firedFinalSubProcessEvent;
    }

}
