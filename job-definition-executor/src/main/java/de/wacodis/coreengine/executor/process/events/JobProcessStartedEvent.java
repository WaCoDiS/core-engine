/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.coreengine.executor.process.JobProcess;
import de.wacodis.coreengine.executor.process.WacodisJobExecutor;

/**
 *
 * @author Arne
 */
public class JobProcessStartedEvent {
    
    private final JobProcess jobProcess;
    private final WacodisJobExecutor executor;

    public JobProcessStartedEvent(JobProcess jobProcess, WacodisJobExecutor executor) {
        this.jobProcess = jobProcess;
        this.executor = executor;
    }

    public JobProcess getJobProcess() {
        return jobProcess;
    }

    public WacodisJobExecutor getExecutor() {
        return executor;
    } 
}
