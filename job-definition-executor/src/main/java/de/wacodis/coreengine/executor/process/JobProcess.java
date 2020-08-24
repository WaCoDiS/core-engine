/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.WacodisJobDefinition;
import java.util.Optional;

/**
 *
 * @author Arne
 */
public class JobProcess {

    private final String jobProcessIdentifier;
    private final WacodisJobDefinition jobDefinition;
    private final Process process;
    private final ProcessContext executionContext;
    private Optional<JobProcessOutputDescription> processOutput;
    private Optional<Exception> exception; 
    private Status status;

    public JobProcess(String jobProcessIdentifier, WacodisJobDefinition jobDefinition, Process process, ProcessContext executionContext) {
        this.jobProcessIdentifier = jobProcessIdentifier;
        this.jobDefinition = jobDefinition;
        this.process = process;
        this.executionContext = executionContext;
        this.status = Status.NOTSTARTED;  //by default job process is not started 
        this.exception = Optional.empty();
        this.processOutput = Optional.empty();
    }

    public String getJobProcessIdentifier() {
        return jobProcessIdentifier;
    }

    public WacodisJobDefinition getJobDefinition() {
        return jobDefinition;
    }

    public Process getProcess() {
        return process;
    }

    public ProcessContext getExecutionContext() {
        return executionContext;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public Optional<JobProcessOutputDescription> getProcessOutput() {
        return processOutput;
    }

    public void setProcessOutput(JobProcessOutputDescription processOutput) {
        this.processOutput = Optional.ofNullable(processOutput);
    }

    public Optional<Exception> getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = Optional.ofNullable(exception);
    }
    
    

    public enum Status {
        NOTSTARTED, STARTED, FAILED, SUCCESSFUL
    }
}
