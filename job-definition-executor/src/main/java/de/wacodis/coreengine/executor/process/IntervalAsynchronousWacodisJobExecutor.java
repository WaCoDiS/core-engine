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

import de.wacodis.coreengine.executor.exception.JobProcessCompletionException;
import de.wacodis.coreengine.executor.exception.JobProcessException;
import de.wacodis.coreengine.executor.process.events.JobProcessExecutedEvent;
import de.wacodis.coreengine.executor.process.events.JobProcessExecutedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessFailedEvent;
import de.wacodis.coreengine.executor.process.events.JobProcessFailedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessStartedEvent;
import de.wacodis.coreengine.executor.process.events.JobProcessStartedEventHandler;
import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEvent;
import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEventHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne
 */
public class IntervalAsynchronousWacodisJobExecutor implements WacodisJobExecutor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(IntervalAsynchronousWacodisJobExecutor.class);

    private int schedulerCorePoolSize = 3; //default of 3
    private final ScheduledExecutorService subProcessScheduler;
    private final long intialDelay_milliseconds;
    private final long processingPeriod_milliseconds;
    private final int maxParallelSubProcesses;
    private JobProcessStartedEventHandler processStartedHandler;
    private JobProcessExecutedEventHandler processExecutedHandler;
    private JobProcessFailedEventHandler processFailedHandler;
    private WacodisJobExecutionEventHandler firstProcessStartedHandler;
    private WacodisJobExecutionEventHandler lastProcessFinishedHandler;

    public IntervalAsynchronousWacodisJobExecutor(long intialDelay_milliseconds, long processingPeriod_milliseconds, int maxParallelSubProcesses) {
        this.subProcessScheduler = Executors.newScheduledThreadPool(schedulerCorePoolSize);
        this.intialDelay_milliseconds = intialDelay_milliseconds;
        this.processingPeriod_milliseconds = processingPeriod_milliseconds;
        this.maxParallelSubProcesses = maxParallelSubProcesses;
    }

    public IntervalAsynchronousWacodisJobExecutor(long intialDelay_milliseconds, long processingPeriod_milliseconds, int maxParallelSubProcesses, int schedulerCorePoolSize) {
        this.schedulerCorePoolSize = schedulerCorePoolSize;
        this.subProcessScheduler = Executors.newScheduledThreadPool(schedulerCorePoolSize);
        this.intialDelay_milliseconds = intialDelay_milliseconds;
        this.processingPeriod_milliseconds = processingPeriod_milliseconds;
        this.maxParallelSubProcesses = maxParallelSubProcesses;
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
        SubProcessExecutionTask subProcessExecutionTask = new SubProcessExecutionTask(subProcesses, this.subProcessScheduler, this.maxParallelSubProcesses, this);
        this.subProcessScheduler.scheduleAtFixedRate(subProcessExecutionTask, this.intialDelay_milliseconds, this.processingPeriod_milliseconds, TimeUnit.MILLISECONDS);
    }

    private class SubProcessExecutionTask implements Runnable {

        //sync list
        private final Stack<JobProcess> subProcessStack;
        private final ExecutorService subProcessSchedulerService;
        private final JobProcess firstSubProcess;
        private final IntervalAsynchronousWacodisJobExecutor jobExecutor;
        private final Object lockObj = new Object();
        private final List<JobProcess> subProcesses;
        private boolean firedFinalSubProcessEvent = false;
        private final ExecutorService subProcessExecutorService;

        /**
         * @param subProcesses
         * @param subProcessSchedulerService
         * @param threadpoolSize max parallel sub processes
         * @param jobExecutor
         */
        public SubProcessExecutionTask(List<JobProcess> subProcesses, ExecutorService subProcessSchedulerService, int threadpoolSize, IntervalAsynchronousWacodisJobExecutor jobExecutor) {
            this.subProcessStack = getSubProcessesAsStack(subProcesses);
            this.subProcessSchedulerService = subProcessSchedulerService;
            this.firstSubProcess = subProcessStack.peek();
            this.subProcesses = subProcesses;
            this.jobExecutor = jobExecutor;
            this.subProcessExecutorService = Executors.newFixedThreadPool(threadpoolSize);
        }

        @Override
        public void run() {
            JobProcess subProcess;
            //access sub process stack only from one thread at a time
            synchronized (this.lockObj) {
                if (!this.subProcessStack.isEmpty()) {
                    subProcess = this.subProcessStack.pop();
                } else {
                    LOGGER.info("started all sub processes of wacodis job, shutdown executor service");
                    shutdownExecutorService(this.subProcessSchedulerService);
                    return; //nothing do do anymore
                }
            }
            //execute sub process in separate thread to allow parallel execution because scheduler fires only if run (SubProcessExecutionTask) is finished
            this.subProcessExecutorService.submit(() -> {
                JobProcessExecutor subProcessExecutor = new JobProcessExecutor(subProcess);
                try {
                    if (subProcess == firstSubProcess) {
                        WacodisJobExecutionEvent firstStartedEvent = new WacodisJobExecutionEvent(subProcess, new ArrayList<>(this.subProcessStack), WacodisJobExecutionEvent.ProcessExecutionEventType.FIRSTPROCESSSTARTED, this.subProcessSchedulerService, this.jobExecutor);
                        JobExecutionEventHelper.fireWacodisJobExecutionEvent(firstStartedEvent, firstProcessStartedHandler);
                    }

                    subProcess.setStatus(JobProcess.Status.STARTED);
                    JobProcessStartedEvent startedEvent = new JobProcessStartedEvent(subProcess, this.jobExecutor);
                    JobExecutionEventHelper.fireProcessStartedEvent(startedEvent, processStartedHandler);

                    JobProcessOutputDescription output = subProcessExecutor.execute();
                    subProcess.setProcessOutput(output);
                    subProcess.setStatus(JobProcess.Status.SUCCESSFUL);
                    JobProcessExecutedEvent executedEvent = new JobProcessExecutedEvent(subProcess, output, this.jobExecutor);

                    handleSuccessfulSubProcess(subProcesses, output.getJobProcess(), executedEvent, this.subProcessExecutorService);

                } catch (JobProcessException ex) {
                    ex.getJobProcess().setException(ex);
                    ex.getJobProcess().setStatus(JobProcess.Status.FAILED);
                    JobProcessFailedEvent failedEvent = new JobProcessFailedEvent(ex.getJobProcess(), new JobProcessCompletionException(ex.getJobProcess(), ex), this.jobExecutor);
                    //raise event(s)
                    handleFailedSubProcess(subProcesses, ex.getJobProcess(), failedEvent, this.subProcessExecutorService);
                }

            });

        }

        private Stack<JobProcess> getSubProcessesAsStack(List<JobProcess> subProcesses) {
            Stack<JobProcess> subProcessStack = new Stack<>();
            subProcessStack.addAll(subProcesses);
            Collections.reverse(subProcessStack);

            return subProcessStack;
        }

        private void shutdownExecutorService(ExecutorService es) {
            if (!es.isShutdown()) {
                es.shutdown();
            }
        }

        /**
         * raise event if final subProcess
         *
         * @param subProcesses
         * @param subProcess
         */
        private void handleSuccessfulSubProcess(List<JobProcess> subProcesses, JobProcess subProcess, JobProcessExecutedEvent event, ExecutorService subProcessExecutorService) {
            synchronized (this.lockObj) {
                //make sure event is only fired once
                //check if final sub process
                if (isFinalJobProcess(subProcesses)) {
                    //is final sub process
                    //fire process execution event
                    JobExecutionEventHelper.fireProcessExecutedEvent(event, processExecutedHandler, true);
                    //fire job completed event and shutdown threadpool
                    fireJobExecutionCompletedEvent(subProcess, subProcesses);
                    this.firedFinalSubProcessEvent = true;
                    shutdownExecutorService(subProcessExecutorService);
                } else {
                    //not final sub process
                    //only fire process execution event
                    JobExecutionEventHelper.fireProcessExecutedEvent(event, processExecutedHandler, false);
                }
            }
        }

        private void handleFailedSubProcess(List<JobProcess> subProcesses, JobProcess subProcess, JobProcessFailedEvent event, ExecutorService subProcessExecutorService) {
            synchronized (this.lockObj) {
                //make sure event is only fired once
                //check if final sub process
                if (isFinalJobProcess(subProcesses)) {
                    //is final sub process
                    //fire process failed event
                    JobExecutionEventHelper.fireProcessFailedEvent(event, processFailedHandler, true);
                    //fire job completed event and shutdown threadpool
                    fireJobExecutionCompletedEvent(subProcess, subProcesses);
                    this.firedFinalSubProcessEvent = true;
                    shutdownExecutorService(subProcessExecutorService);
                } else {
                    //not final sub process
                    //only fire process failed event
                    JobExecutionEventHelper.fireProcessFailedEvent(event, processFailedHandler, false);
                }
            }
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

        private boolean isFinalJobProcess(List<JobProcess> jobProcesses) {
            return getUnfinishedJobProcesses(jobProcesses).isEmpty() && !this.firedFinalSubProcessEvent;
        }

        private void fireJobExecutionCompletedEvent(JobProcess finalJobProcess, List<JobProcess> allJobProcesses) {
            WacodisJobExecutionEvent finalProcessFinishedEvent = new WacodisJobExecutionEvent(finalJobProcess, allJobProcesses, WacodisJobExecutionEvent.ProcessExecutionEventType.FINALPROCESSFINISHED, this.subProcessExecutorService, IntervalAsynchronousWacodisJobExecutor.this);
            JobExecutionEventHelper.fireWacodisJobExecutionEvent(finalProcessFinishedEvent, lastProcessFinishedHandler);
        }
    }

}
