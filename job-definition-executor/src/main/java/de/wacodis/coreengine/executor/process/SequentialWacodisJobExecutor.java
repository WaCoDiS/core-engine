/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.coreengine.executor.process.events.JobProcessExecutedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessFailedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessStartedEventHandler;
import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEventHandler;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Arne
 */
public class SequentialWacodisJobExecutor implements WacodisJobExecutor {

    //single thread executor makes async process executor effectively sequential
    ExecutorService singleThreadService = Executors.newSingleThreadExecutor();;

    private final AsynchronousWacodisJobExecutor wacodisJobExecutor;

    public SequentialWacodisJobExecutor(List<JobProcess> subProcesses) {
        this.wacodisJobExecutor = new AsynchronousWacodisJobExecutor(subProcesses, this.singleThreadService);
    }
    
    @Override
    public void executeAllSubProcesses() {
        this.wacodisJobExecutor.executeAllSubProcesses();
    }

    @Override
    public void setProcessStartedHandler(JobProcessStartedEventHandler handler) {
        this.wacodisJobExecutor.setProcessStartedHandler(handler);
    }

    @Override
    public void setProcessExecutedHandler(JobProcessExecutedEventHandler handler) {
        this.wacodisJobExecutor.setProcessExecutedHandler(handler);
    }

    @Override
    public void setProcessFailedHandler(JobProcessFailedEventHandler handler) {
        this.wacodisJobExecutor.setProcessFailedHandler(handler);
    }

    @Override
    public void setFirstProcessStartedHandler(WacodisJobExecutionEventHandler handler) {
        this.wacodisJobExecutor.setFirstProcessStartedHandler(handler);
    }

    @Override
    public void setFinalProcessFinishedHandler(WacodisJobExecutionEventHandler handler) {
        this.wacodisJobExecutor.setFinalProcessFinishedHandler(handler);
    }



}
