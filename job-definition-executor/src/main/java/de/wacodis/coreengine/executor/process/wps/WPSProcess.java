/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessOutputDescription;
import org.n52.geoprocessing.wps.client.WPSClientException;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.n52.geoprocessing.wps.client.model.ExceptionReport;
import org.n52.geoprocessing.wps.client.model.OWSExceptionElement;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.Result;
import org.n52.geoprocessing.wps.client.model.StatusInfo;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WPSProcess implements de.wacodis.coreengine.executor.process.Process {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WPSProcess.class);

    private final String wpsProcessID;
    private final String wpsURL;
    private final String wpsVersion;
    private final WPSClientSession wpsClient;
    private Process wpsProcessDescription;
    private boolean isInitialized;
    private boolean isConnected;

    public WPSProcess(WPSClientSession wpsClient, String wpsURL, String wpsVersion, String wpsProcessID) {
        this.wpsProcessID = wpsProcessID;
        this.wpsURL = wpsURL;
        this.wpsVersion = wpsVersion;
        this.wpsClient = wpsClient;
        this.isInitialized = false;
        this.isConnected = false;
    }

    public String getProcessID() {
        return wpsProcessID;
    }

    public String getWpsURL() {
        return wpsURL;
    }

    public void init() {
        if (!isConnected) {
            connectWPS();
        }

        this.wpsProcessDescription = this.wpsClient.getProcessDescription(this.wpsURL, this.wpsProcessID, this.wpsVersion);
        LOGGER.info("retrieved process description {}", this.wpsProcessDescription);
        this.isInitialized = true;
    }

    /**
     * execute wps process asynchronously
     *
     * @param context
     * @return ProcessOutputDescription containing wps job id, parameters
     * 'startTimeMillis' and 'endTimeMillis'
     * @throws ExecutionException
     */
    @Override
    public ProcessOutputDescription execute(ProcessContext context) throws ExecutionException {
        long startTimeMillis, endTimeMillis;

        startTimeMillis = System.currentTimeMillis();

        LOGGER.info(
                "start execution of process " + context.getWacodisProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID);

        if (!this.isInitialized || !this.isConnected) {
            init();
        }

        Optional<ExecutionException> preValidationResult = validatePreExecution(context);
        if (preValidationResult.isPresent()) { //throw exception if provided inputs or expected outputs are not valid
            throw preValidationResult.get();
        }

        LOGGER.debug(
                "building wps execute request for process " + context.getWacodisProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID);
        //build execute request
        WPSProcessInput processInput = new ExecuteBuilder(this.wpsProcessDescription).buildExecuteRequest(context);
        Execute executeRequest = processInput.getExecute();
        //submit execute request
        LOGGER.info("Executing WPS: {}", ToStringHelper.executeToString(executeRequest));

        try {
            //exeute wps process
            LOGGER.debug("execute request for process {}: {}", context.getWacodisProcessID(), executeRequest);
            Object wpsOutput = this.wpsClient.execute(this.wpsURL, executeRequest, this.wpsVersion); //submit process to wps and poll result periodically
            LOGGER.info("processing result for process {}: {}", context.getWacodisProcessID(), wpsOutput);
            LOGGER.debug("start parsing result for process {}", context.getWacodisProcessID());
            //parse wps output
            Result wpsProcessResult = parseWPSOutput(wpsOutput); //throws exception if wps output contains errors
            
            Optional<ExecutionException> postValidationResult = validatePostExecution(wpsProcessResult, context);
            if(postValidationResult.isPresent()){
                throw postValidationResult.get(); //throw exception if excepted outputs are missing in received result
            }
            LOGGER.debug("successfully parsed result for process {}", context.getWacodisProcessID());

            endTimeMillis = System.currentTimeMillis();

            LOGGER.info("successfully executed process " + context.getWacodisProcessID() + " after " + ((endTimeMillis - startTimeMillis) / 1000) + " seconds");

            return buildProcessOutput(wpsProcessResult, processInput.getOriginDataEnvelopes(), startTimeMillis, endTimeMillis);
        } catch (WPSClientException | IOException ex) {
            endTimeMillis = System.currentTimeMillis();
            throw new ExecutionException("execution of wps process for wacodis job " + this.wpsProcessID + " failed after " + ((endTimeMillis - startTimeMillis) / 1000) + " seconds", ex);
        }
    }

    private void connectWPS() {
        try {
            this.isConnected = this.wpsClient.connect(this.wpsURL, this.wpsVersion);
            LOGGER.info("Connected to WPS at {}", this.wpsURL);
        } catch (WPSClientException ex) {
            this.isConnected = false;
            throw new IllegalStateException("could not connect to web processing service with url: " + this.wpsURL, ex);
        }
    }

    /**
     * check type of wps output, apply type cast and return wps result
     *
     * @param wpsOutput
     * @return
     * @throws ExecutionException if wps process raised exceptions during
     * processing or wps output is not unparsable
     */
    private Result parseWPSOutput(Object wpsOutput) throws ExecutionException {
        Result wpsProcessResult;

        if (wpsOutput instanceof StatusInfo) {
            wpsProcessResult = ((StatusInfo) wpsOutput).getResult();
            if (wpsProcessResult.getJobId() == null) {
                wpsProcessResult.setJobId(((StatusInfo) wpsOutput).getJobId());
            }
        } else if (wpsOutput instanceof Result) {
            wpsProcessResult = ((Result) wpsOutput);
        } else if (wpsOutput instanceof ExceptionReport) {
            LOGGER.warn("wps output contains errors, raise ExecutionException");
            List<OWSExceptionElement> exceptions = ((ExceptionReport) wpsOutput).getExceptions();
            throw new ExecutionException("executing process " + this.wpsProcessID + " raised exceptions:" + System.lineSeparator() + buildExceptionText(exceptions));
        } else {
            if (wpsOutput != null) {
                LOGGER.warn("unexpected response format for wps process " + this.wpsProcessID + ":" + System.lineSeparator() + wpsOutput.toString());
            }
            throw new ExecutionException("unparsable response for wps process " + this.wpsProcessID);
        }

        LOGGER.debug("successfully parsed output of wps process " + this.wpsProcessID + ", WPS Job-ID: " + wpsProcessResult.getJobId() + ")");

        return wpsProcessResult;
    }

    private ProcessOutputDescription buildProcessOutput(Result wpsProcessResult, List<String> originDataEnvelopes, long startTimeMillis, long endTimeMillis) {
        ProcessOutputDescription processOutput = new ProcessOutputDescription();

        processOutput.setProcessIdentifier(wpsProcessResult.getJobId()); //wps job-id
        processOutput.setOriginDataEnvelopes(originDataEnvelopes);
        processOutput.addOutputParameter("startTimeMillis", Long.toString(startTimeMillis));
        processOutput.addOutputParameter("endTimeMillis", Long.toString(endTimeMillis));

        LOGGER.debug("built process output description for wps process " + this.wpsProcessID + " (wps Job-ID: " + wpsProcessResult.getJobId() + ")");

        return processOutput;
    }

    /**
     * append exception messages
     *
     * @param exceptions
     * @return
     */
    private String buildExceptionText(List<OWSExceptionElement> exceptions) {
        StringBuilder exceptionTextBuilder = new StringBuilder();

        for (int i = 0; i < exceptions.size(); i++) {
            exceptionTextBuilder.append("Exception ").append((i + 1)).append(":").append(System.lineSeparator()).
                    append("code: ").append(exceptions.get(i).getExceptionCode()).append(System.lineSeparator()).
                    append(exceptions.get(i).getExceptionText());

            if (i != (exceptions.size() - 1)) { //no linebreak after last exception
                exceptionTextBuilder.append(System.lineSeparator());
            }
        }

        return exceptionTextBuilder.toString();
    }


    private Optional<ExecutionException> validatePreExecution(ProcessContext context) {
        WPSInputOutputValidator validator = new WPSInputOutputValidator(this.wpsProcessDescription);

        //check if mandatory inputs are provided and provided inputs and its mime types are supported by wps process
        LOGGER.debug(
                "validate provided inputs of process " + context.getWacodisProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID);
        Optional<Throwable> inputValidationResult = validator.validateProvidedInputResources(context.getInputResources());

        if (inputValidationResult.isPresent()) { //optional contains throwable if not valid
            return Optional.of(new ExecutionException("unable to execute process " + context.getWacodisProcessID() + " because of unsupported provided inputs" + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID, inputValidationResult.get()));
        }

        //check if expected outputs and its mime types are supported by wps process
        LOGGER.debug(
                "validate expected outputs of process " + context.getWacodisProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID);
        Optional<Throwable> expectedOutputValidationResult = validator.validateExpectedOutputs(context.getExpectedOutputs());

        if (expectedOutputValidationResult.isPresent()) { //optional contains throwable if not valid
            return Optional.of(new ExecutionException("unable to execute process " + context.getWacodisProcessID() + " because of unsupported expected outputs" + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID, expectedOutputValidationResult.get()));
        }

        return Optional.empty();
    }

    private Optional<ExecutionException> validatePostExecution(Result receivedResult, ProcessContext context) {
        WPSResultValidator validator = new WPSResultValidator(this.wpsProcessDescription);

        Optional<Throwable> outputValidationResult = validator.validateProcessOutput(receivedResult, context); //contains exception if invalid
        if (outputValidationResult.isPresent()) {
            return Optional.of(new ExecutionException("wps process" + this.wpsProcessDescription.getId() + " (wps job id: " + receivedResult.getJobId() + ") returned invalid output", outputValidationResult.get()));
        }
        
        return Optional.empty();
    }

}
