/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.coreengine.executor.process.JobProcess;
import de.wacodis.coreengine.executor.process.JobProcessOutputDescription;
import de.wacodis.coreengine.executor.process.WacodisJobExecutor;
import org.joda.time.DateTime;

/**
 * raised after succesful execution of a single job process
 * @author Arne
 */
public class JobProcessExecutedEvent {
    
    private final JobProcess jobProcess;
    private final JobProcessOutputDescription output;
    private final WacodisJobExecutor executor;
    private final DateTime timestamp;

    public JobProcessExecutedEvent(JobProcess jobProcess, JobProcessOutputDescription output, WacodisJobExecutor executor) {
        this(jobProcess, output, executor, DateTime.now());
    }

    public JobProcessExecutedEvent(JobProcess jobProcess, JobProcessOutputDescription output, WacodisJobExecutor executor, DateTime timestamp) {
        this.jobProcess = jobProcess;
        this.output = output;
        this.executor = executor;
        this.timestamp = timestamp;
    }


    public DateTime getTimestamp() {
        return timestamp;
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
