/*
 * Copyright 2018-2021 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
