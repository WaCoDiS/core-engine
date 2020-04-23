/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.coreengine.executor.exception.JobProcessCompletionException;
import de.wacodis.coreengine.executor.process.JobProcess;
import de.wacodis.coreengine.executor.process.WacodisJobExecutor;

/**
 *
 * @author Arne
 */
public class JobProcessFailedEvent {
    
    private final JobProcess jobProcess;
    private final JobProcessCompletionException exception;
    private final WacodisJobExecutor executor;

    public JobProcessFailedEvent(JobProcess jobProcess, JobProcessCompletionException exception, WacodisJobExecutor executor) {
        this.jobProcess = jobProcess;
        this.exception = exception;
        this.executor = executor;
    }

    public JobProcess getJobProcess() {
        return jobProcess;
    }

    public JobProcessCompletionException getException() {
        return exception;
    }

    public WacodisJobExecutor getExecutor() {
        return executor;
    }
}
