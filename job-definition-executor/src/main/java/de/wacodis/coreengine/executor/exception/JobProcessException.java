/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.exception;

import de.wacodis.coreengine.executor.process.JobProcess;

/**
 *
 * @author Arne
 */
public class JobProcessException extends Exception{
    
    private final JobProcess jobProcess;

    public JobProcessException(JobProcess jobProcess) {
        this.jobProcess = jobProcess;
    }

    public JobProcessException(JobProcess jobProcess, String message) {
        super(message);
        this.jobProcess = jobProcess;
    }

    public JobProcessException(JobProcess jobProcess, String message, Throwable cause) {
        super(message, cause);
        this.jobProcess = jobProcess;
    }

    public JobProcessException(JobProcess jobProcess, Throwable cause) {
        super(cause);
        this.jobProcess = jobProcess;
    }

    public JobProcessException(JobProcess jobProcess, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.jobProcess = jobProcess;
    }

    public JobProcess getJobProcess() {
        return jobProcess;
    }
}
