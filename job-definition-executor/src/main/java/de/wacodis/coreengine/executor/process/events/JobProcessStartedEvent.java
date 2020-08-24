/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.coreengine.executor.process.JobProcess;
import de.wacodis.coreengine.executor.process.WacodisJobExecutor;
import org.joda.time.DateTime;

/**
 *
 * @author Arne
 */
public class JobProcessStartedEvent {
    
    private final JobProcess jobProcess;
    private final WacodisJobExecutor executor;
    private final DateTime timestamp;

    public JobProcessStartedEvent(JobProcess jobProcess, WacodisJobExecutor executor) {
        this(jobProcess, executor, DateTime.now());
    }

    public JobProcessStartedEvent(JobProcess jobProcess, WacodisJobExecutor executor, DateTime timestamp) {
        this.jobProcess = jobProcess;
        this.executor = executor;
        this.timestamp = timestamp;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public JobProcess getJobProcess() {
        return jobProcess;
    }

    public WacodisJobExecutor getExecutor() {
        return executor;
    } 
}
