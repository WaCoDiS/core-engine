/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.exception;

import java.util.concurrent.CompletionException;

/**
 *
 * @author Arne
 */
public class JobProcessCompletionException extends CompletionException {

    private final String wacodisJobIdentifier;
    private final String jobProcessIdentifier;

    public JobProcessCompletionException(String wacodisJobIdentifier, String jobProcessIdentifier) {
        this.wacodisJobIdentifier = wacodisJobIdentifier;
        this.jobProcessIdentifier = jobProcessIdentifier;
    }

    public JobProcessCompletionException(String wacodisJobIdentifier, String jobProcessIdentifier, String message) {
        super(message);
        this.wacodisJobIdentifier = wacodisJobIdentifier;
        this.jobProcessIdentifier = jobProcessIdentifier;
    }

    public JobProcessCompletionException(String wacodisJobIdentifier, String jobProcessIdentifier, String message, Throwable cause) {
        super(message, cause);
        this.wacodisJobIdentifier = wacodisJobIdentifier;
        this.jobProcessIdentifier = jobProcessIdentifier;
    }

    public JobProcessCompletionException(String wacodisJobIdentifier, String jobProcessIdentifier, Throwable cause) {
        super(cause);
        this.wacodisJobIdentifier = wacodisJobIdentifier;
        this.jobProcessIdentifier = jobProcessIdentifier;
    }

    public String getWacodisJobIdentifier() {
        return wacodisJobIdentifier;
    }

    public String getJobProcessIdentifier() {
        return jobProcessIdentifier;
    }
}
