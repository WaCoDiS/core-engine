/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.coreengine.executor.process.JobProcess;
import de.wacodis.coreengine.executor.process.JobProcessOutputDescription;
import de.wacodis.coreengine.executor.process.WacodisJobExecutor;

/**
 *
 * @author Arne
 */
public class JobProcessExecutedEvent {
    
    private final JobProcess jobProcess;
    private final JobProcessOutputDescription output;
    private final WacodisJobExecutor executor;

    public JobProcessExecutedEvent(JobProcess jobProcess, JobProcessOutputDescription output, WacodisJobExecutor executor) {
        this.jobProcess = jobProcess;
        this.output = output;
        this.executor = executor;
    }

    public JobProcess getJobProcess() {
        return jobProcess;
    }

    public JobProcessOutputDescription getOutput() {
        return output;
    }

    public WacodisJobExecutor getExecutor() {
        return executor;
    }
}
