/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.WacodisJobDefinition;

/**
 *
 * @author Arne
 */
public class JobProcess {
    
    private final String jobProcessIdentifier;
    private final WacodisJobDefinition jobDefinition;
    private final Process process;
    private final ProcessContext executionContext;

    public JobProcess(String jobProcessIdentifier, WacodisJobDefinition jobDefinition, Process process, ProcessContext executionContext) {
        this.jobProcessIdentifier = jobProcessIdentifier;
        this.jobDefinition = jobDefinition;
        this.process = process;
        this.executionContext = executionContext;
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
}
