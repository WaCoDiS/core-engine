/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Arne
 */
public class JobProcessOutputDescription extends ProcessOutputDescription {

    private JobProcess jobProcess;

    public JobProcessOutputDescription() {
        super();
    }

    /**
     * @param processIdentifier
     * @param jobProcess
     */
    public JobProcessOutputDescription(String processIdentifier, JobProcess jobProcess) {
        super(processIdentifier);
        this.jobProcess = jobProcess;
    }

    /**
     * @param processIdentifier
     * @param jobProcess
     * @param outputParameters
     */
    public JobProcessOutputDescription(String processIdentifier, JobProcess jobProcess, HashMap<String, String> outputParameters) {
        super(processIdentifier, outputParameters);
        this.jobProcess = jobProcess;
    }

    /**
     * @param processIdentifier
     * @param jobProcess
     * @param outputParmeters
     * @param originDataEnvelopes
     */
    public JobProcessOutputDescription(String processIdentifier, JobProcess jobProcess, HashMap<String, String> outputParmeters, List<String> originDataEnvelopes) {
        super(processIdentifier, outputParmeters, originDataEnvelopes);
        this.jobProcess = jobProcess;
    }

    /**
     *
     * @param processOutputDescription
     * @param jobProcess
     */
    public JobProcessOutputDescription(ProcessOutputDescription processOutputDescription, JobProcess jobProcess) {
        super(processOutputDescription.getProcessIdentifier(), processOutputDescription.getOutputParmeters(), processOutputDescription.getOriginDataEnvelopes());
        this.jobProcess = jobProcess;
    }

    /**
     * @return 
     */
    public JobProcess getJobProcess() {
        return jobProcess;
    }

    /**
     * @param jobProcess 
     */
    public void setJobProcess(JobProcess jobProcess) {
        this.jobProcess = jobProcess;
    }

}
