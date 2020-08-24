/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.exception;

import de.wacodis.coreengine.executor.process.JobProcess;
import java.util.concurrent.CompletionException;

/**
 *
 * @author Arne
 */
public class JobProcessCompletionException extends CompletionException {

    private final JobProcess jobProcess;

    public JobProcessCompletionException(JobProcess jobProcess) {
        this.jobProcess = jobProcess;
    }

    public JobProcessCompletionException(JobProcess jobProcess, String message) {
        super(message);
        this.jobProcess = jobProcess;
    }

    public JobProcessCompletionException(JobProcess jobProcess, String message, Throwable cause) {
        super(message, cause);
        this.jobProcess = jobProcess;
    }

    public JobProcessCompletionException(JobProcess jobProcess, Throwable cause) {
        super(cause);
        this.jobProcess = jobProcess;
    }

    public JobProcess getJobProcess() {
        return jobProcess;
    }
}
