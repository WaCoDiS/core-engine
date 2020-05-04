/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    private JobProcessStartedEventHandler processStartedHandler;
    private JobProcessExecutedEventHandler processExecutedHandler;
    private JobProcessFailedEventHandler processFailedHandler;
    private WacodisJobExecutionEventHandler firstProcessStartedHandler;
    private WacodisJobExecutionEventHandler lastProcessFinishedHandler;

    public IntervalAsynchronousWacodisJobExecutor(long intialDelay_milliseconds, long processingPeriod_milliseconds) {
        this.subProcessScheduler = Executors.newScheduledThreadPool(schedulerCorePoolSize);
        this.intialDelay_milliseconds = intialDelay_milliseconds;
        this.processingPeriod_milliseconds = processingPeriod_milliseconds;
    }

    public IntervalAsynchronousWacodisJobExecutor(long intialDelay_milliseconds, long processingPeriod_milliseconds, int schedulerCorePoolSize) {
        this.schedulerCorePoolSize = schedulerCorePoolSize;
        this.subProcessScheduler = Executors.newScheduledThreadPool(schedulerCorePoolSize);
        this.intialDelay_milliseconds = intialDelay_milliseconds;
        this.processingPeriod_milliseconds = processingPeriod_milliseconds;
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
        SubProcessExecutionTask subProcessExecutionTask = new SubProcessExecutionTask(subProcesses, this.subProcessScheduler, this);
        this.subProcessScheduler.scheduleAtFixedRate(subProcessExecutionTask, this.intialDelay_milliseconds, this.processingPeriod_milliseconds, TimeUnit.MILLISECONDS);
    }

    private class SubProcessExecutionTask implements Runnable {

        //sync list
        private final Stack<JobProcess> subProcessStack;
        private final ExecutorService subProcessExecutorService;
        private final JobProcess firstSubProcess;
        private final JobProcess lastSubProcess;
        private final IntervalAsynchronousWacodisJobExecutor jobExecutor;

        public SubProcessExecutionTask(List<JobProcess> subProcesses, ExecutorService subProcessExecutorService, IntervalAsynchronousWacodisJobExecutor jobExecutor) {
            this.subProcessStack = getSubProcessesAsStack(subProcesses);
            this.subProcessExecutorService = subProcessExecutorService;
            this.firstSubProcess = subProcesses.get(0);
            this.lastSubProcess = subProcesses.get((subProcesses.size() - 1));
            this.jobExecutor = jobExecutor;
        }

        @Override
        public void run() {
            JobProcess subProcess;
            //access sub process stack only from one thread at a time
            synchronized (this) {
                if (!subProcessStack.isEmpty()) {
                    subProcess = this.subProcessStack.pop();
                } else {
                    shutdownExecutorService();
                    return; //nothing do do anymore
                }
            }

            JobProcessExecutor subProcessExecutor = new JobProcessExecutor(subProcess);
            try {
                if (subProcess == firstSubProcess) {
                    WacodisJobExecutionEvent firstStartedEvent = new WacodisJobExecutionEvent(subProcess, new ArrayList<>(this.subProcessStack), WacodisJobExecutionEvent.ProcessExecutionEventType.FIRSTPROCESSSTARTED, this.subProcessExecutorService, this.jobExecutor);
                    JobExecutionEventHelper.fireWacodisJobExecutionEvent(firstStartedEvent,firstProcessStartedHandler);
                }

                JobProcessStartedEvent startedEvent = new JobProcessStartedEvent(subProcess, this.jobExecutor);
                JobExecutionEventHelper.fireProcessStartedEvent(startedEvent, processStartedHandler);

                JobProcessOutputDescription output = subProcessExecutor.execute();
                JobProcessExecutedEvent executedEvent = new JobProcessExecutedEvent(subProcess, output, this.jobExecutor);
                JobExecutionEventHelper.fireProcessExecutedEvent(executedEvent, processExecutedHandler);

                if (output.getJobProcess() == lastSubProcess) {
                    WacodisJobExecutionEvent lastFinishedEvent = new WacodisJobExecutionEvent(output.getJobProcess(), new ArrayList<>(this.subProcessStack), WacodisJobExecutionEvent.ProcessExecutionEventType.FINALPROCESSFINISHED, this.subProcessExecutorService, this.jobExecutor);
                    JobExecutionEventHelper.fireWacodisJobExecutionEvent(lastFinishedEvent, lastProcessFinishedHandler);
                }

            } catch (JobProcessException ex) {
                JobProcessFailedEvent failedEvent = new JobProcessFailedEvent(ex.getJobProcess(), new JobProcessCompletionException(ex.getJobProcess(), ex), this.jobExecutor);
                JobExecutionEventHelper.fireProcessFailedEvent(failedEvent, processFailedHandler);

                if (ex.getJobProcess() == lastSubProcess) {
                    WacodisJobExecutionEvent lastFinishedEvent = new WacodisJobExecutionEvent(ex.getJobProcess(), new ArrayList<>(this.subProcessStack), WacodisJobExecutionEvent.ProcessExecutionEventType.FINALPROCESSFINISHED, this.subProcessExecutorService, this.jobExecutor);
                    JobExecutionEventHelper.fireWacodisJobExecutionEvent(lastFinishedEvent, lastProcessFinishedHandler);
                }
            }

        }

        private Stack<JobProcess> getSubProcessesAsStack(List<JobProcess> subProcesses) {
            Stack<JobProcess> subProcessStack = new Stack<>();
            subProcessStack.addAll(subProcesses);

            return subProcessStack;
        }

        private void shutdownExecutorService() {
            if (!this.subProcessExecutorService.isShutdown()) {
                this.subProcessExecutorService.shutdown();
            }
        }

    }

}
