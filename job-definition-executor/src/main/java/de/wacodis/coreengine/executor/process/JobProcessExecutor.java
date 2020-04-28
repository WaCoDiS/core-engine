/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.exception.JobProcessException;
import org.slf4j.LoggerFactory;

/**
 * execute single job process
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class JobProcessExecutor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JobProcessExecutor.class);

    private final JobProcess jobProcess;

    public JobProcessExecutor(JobProcess jobProcess) {
        this.jobProcess = jobProcess;
    }

    public WacodisJobDefinition getJobDefinition() {
        return this.jobProcess.getJobDefinition();
    }

    public JobProcessOutputDescription execute() throws JobProcessException {
        Process process = this.jobProcess.getProcess();
        ProcessContext executionContext = this.jobProcess.getExecutionContext();
        LOGGER.debug("start execution of process " + executionContext.getWacodisProcessID() + ", toolProcess: " + process);

        //execute processing tool
        JobProcessOutputDescription jobProcessOutput = null;

        try {
            ProcessOutputDescription wpsProcessOutput = process.execute(executionContext);
            jobProcessOutput = new JobProcessOutputDescription(wpsProcessOutput, this.jobProcess);
            LOGGER.debug("Process: " + executionContext.getWacodisProcessID() + ",executed toolProcess " + process);
        } catch (ExecutionException e) {
            LOGGER.error("Process execution failed", e.getMessage());
            LOGGER.warn(e.getMessage(), e); 

            throw new JobProcessException(this.jobProcess, e);
        }

        LOGGER.info("Process: " + executionContext.getWacodisProcessID() + ",finished execution");
        return jobProcessOutput;
    }
}
