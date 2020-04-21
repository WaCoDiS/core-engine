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

    private String wacodisJobIdentifier;
    private String jobProcessIdentifier;

    public JobProcessOutputDescription() {
        super();
    }

    /**
     * @param processIdentifier
     * @param jobProcessIdentifier
     * @param wacodisJobIdentifier 
     */
    public JobProcessOutputDescription(String processIdentifier, String jobProcessIdentifier, String wacodisJobIdentifier) {
        super(processIdentifier);
        this.jobProcessIdentifier = jobProcessIdentifier;
        this.wacodisJobIdentifier = wacodisJobIdentifier;
    }

    /**
     * @param processIdentifier
     * @param jobProcessIdentifier
     * @param wacodisJobIdentifier
     * @param outputParameters 
     */
    public JobProcessOutputDescription(String processIdentifier, String jobProcessIdentifier, String wacodisJobIdentifier, HashMap<String, String> outputParameters) {
        super(processIdentifier, outputParameters);
        this.jobProcessIdentifier = jobProcessIdentifier;
        this.wacodisJobIdentifier = wacodisJobIdentifier;
    }
    /**
     * @param processIdentifier
     * @param jobProcessIdentifier
     * @param wacodisJobIdentifier
     * @param outputParmeters
     * @param originDataEnvelopes 
     */
    public JobProcessOutputDescription(String processIdentifier, String jobProcessIdentifier, String wacodisJobIdentifier, HashMap<String, String> outputParmeters, List<String> originDataEnvelopes) {
        super(processIdentifier, outputParmeters, originDataEnvelopes);
        this.jobProcessIdentifier = jobProcessIdentifier;
        this.wacodisJobIdentifier = wacodisJobIdentifier;
    }
    
    /**
     * 
     * @param processOutputDescription
     * @param jobProcessIdentifier
     * @param wacodisJobIdentifier 
     */
    public JobProcessOutputDescription(ProcessOutputDescription processOutputDescription, String jobProcessIdentifier, String wacodisJobIdentifier){
        super(processOutputDescription.getProcessIdentifier(), processOutputDescription.getOutputParmeters(), processOutputDescription.getOriginDataEnvelopes());
        this.jobProcessIdentifier = jobProcessIdentifier;
        this.wacodisJobIdentifier = wacodisJobIdentifier;
    }

    public String getWacodisJobIdentifier() {
        return wacodisJobIdentifier;
    }

    public void setWacodisJobIdentifier(String wacodisJobIdentifier) {
        this.wacodisJobIdentifier = wacodisJobIdentifier;
    }

    public String getJobProcessIdentifier() {
        return jobProcessIdentifier;
    }

    public void setJobProcessIdentifier(String jobProcessIdentifier) {
        this.jobProcessIdentifier = jobProcessIdentifier;
    }

}
