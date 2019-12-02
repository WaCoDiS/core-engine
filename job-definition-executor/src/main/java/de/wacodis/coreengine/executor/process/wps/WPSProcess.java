/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.extension.staticresource.StaticDummyResource;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessOutputDescription;
import de.wacodis.coreengine.executor.process.ResourceDescription;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang.NotImplementedException;
import org.n52.geoprocessing.wps.client.ExecuteRequestBuilder;
import org.n52.geoprocessing.wps.client.WPSClientException;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.n52.geoprocessing.wps.client.model.BoundingBoxInputDescription;
import org.n52.geoprocessing.wps.client.model.ComplexInputDescription;
import org.n52.geoprocessing.wps.client.model.ExceptionReport;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.OWSExceptionElement;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.Result;
import org.n52.geoprocessing.wps.client.model.StatusInfo;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.slf4j.LoggerFactory;

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
        WPSProcessInput processInput = buildExecuteRequest(context);
        Execute executeRequest = processInput.getExecute();
        //submit execute request

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

    private WPSProcessInput buildExecuteRequest(ProcessContext context) {
        ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(this.wpsProcessDescription);
        List <String> originDataEnvelopes = setWPSInputs(context.getInputResources(), executeRequestBuilder); //register inputs
        setExpectedWPSOutputs(context.getExpectedOutputs(), executeRequestBuilder); //register expected outputs
        executeRequestBuilder.setAsynchronousExecute(); //make execute request asynchronous
        Execute execute = executeRequestBuilder.getExecute();

        return new WPSProcessInput(execute, originDataEnvelopes);
    }

    /**
     * register inputs for wps process
     *
     * @param availableInputs
     * @param executeRequestBuilder
     */
    private List<String> setWPSInputs(Map<String, List<ResourceDescription>> providedInputResources, ExecuteRequestBuilder executeRequestBuilder) {
        List<String> originDataEnvelopes = new ArrayList<>();
        List<InputDescription> processInputs = this.wpsProcessDescription.getInputs();
        List<ResourceDescription> providedResourcesForProcessInput;
        String inputID;

        for (InputDescription processInput : processInputs) {
            inputID = processInput.getId();

            if (!providedInputResources.containsKey(inputID)) { //skip if no resource provided for specific wps input
                continue;
            }

            providedResourcesForProcessInput = providedInputResources.get(inputID);

            if (processInput instanceof LiteralInputDescription) {
                List<String> literalInputOrigins = setLiteralInputData((LiteralInputDescription) processInput, providedResourcesForProcessInput, executeRequestBuilder);
                originDataEnvelopes.addAll(literalInputOrigins);
            } else if (processInput instanceof ComplexInputDescription) {
                List<String> complexInputOrigins = setComplexInputData((ComplexInputDescription) processInput, providedResourcesForProcessInput, executeRequestBuilder);
                originDataEnvelopes.addAll(complexInputOrigins);
            } else if (processInput instanceof BoundingBoxInputDescription) {
                //ToDo
                throw new NotImplementedException("cannot handle wps input " + inputID + ": BoundingBoxInput not supported");
                //originDataEnvelopes.addAll(bboxInputOrigins);
            } else {
                throw new IllegalArgumentException("cannot handle wps input " + inputID + ": input is of unknown type " + processInput.getClass().getSimpleName());
            }

        }
        
        
        return removeDuplicates(originDataEnvelopes);
    }

    /**
     * @param literalProcessInput
     * @param providedResources
     * @param executeRequestBuilder
     * @return list of data envelope id's that correspond with the used input resources
     */
    private List<String> setLiteralInputData(LiteralInputDescription literalProcessInput, List<ResourceDescription> providedResources, ExecuteRequestBuilder executeRequestBuilder) {
        String literalValue;
        ResourceDescription providedResource;
        List<String> originDataEnvelopes = new ArrayList<>();

        for (int i = 0; i < providedResources.size(); i++) {
            if (literalProcessInput.getMaxOccurs() < (i + 1)) {
                LOGGER.warn("Max occurs ({}) for input {} reached, provided {} resources.  Skipping surplus candidates.", literalProcessInput.getMaxOccurs(), literalProcessInput.getId(), providedResources.size());
                break;
            }

            providedResource = providedResources.get(i);
            literalValue = getValueFromResource(providedResource);
            executeRequestBuilder.addLiteralData(literalProcessInput.getId(), literalValue, "", "", providedResource.getMimeType()); //input id, value, schema, encoding, mime type 
            
            originDataEnvelopes.add(providedResource.getResource().getDataEnvelopeId()); //add data envelope id to list of origins of process result
        }
        
        return originDataEnvelopes;
    }

    private List<String> setComplexInputData(ComplexInputDescription complexProcessInput, List<ResourceDescription> providedResources, ExecuteRequestBuilder executeRequestBuilder) {
        ResourceDescription providedResource;
        List<String> originDataEnvelopes = new ArrayList<>();

        for (int i = 0; i < providedResources.size(); i++) {
            if (complexProcessInput.getMaxOccurs() < (i + 1)) {
                LOGGER.warn("Max occurs ({}) for input {} reached, provided {} resources.  Skipping surplus candidates.", complexProcessInput.getMaxOccurs(), providedResources.size(), complexProcessInput.getId());
                break;
            }

            providedResource = providedResources.get(i);

            if (StaticDummyResource.class
                    .isAssignableFrom(providedResource.getResource().getClass())) {
                throw new IllegalArgumentException(
                        "illegal ressource type for complex wps input, resource for input " + complexProcessInput.getId() + " is a static resource, only non-static resources are allowed for complex inputs");
            }

            try {
                executeRequestBuilder.addComplexDataReference(complexProcessInput.getId(), providedResource.getResource().getUrl(), providedResource.getSchema().getSchemaLocation(), null, providedResource.getMimeType());
            } catch (MalformedURLException ex) {
                throw new IllegalArgumentException("cannot add complex input " + complexProcessInput.getId() + " as reference, invalid reference, malformed url" + providedResource.getResource().getUrl(), ex);
            }
            
            originDataEnvelopes.add(providedResource.getResource().getDataEnvelopeId());
        }
        
        return originDataEnvelopes;
    }

    /**
     * register each expected output
     *
     * @param executeRequestBuilder
     * @param expectedOutputs
     */
    private void setExpectedWPSOutputs(List<ExpectedProcessOutput> expectedOutputs, ExecuteRequestBuilder executeRequestBuilder) {
        for (ExpectedProcessOutput expectedOutput : expectedOutputs) {
            executeRequestBuilder.setResponseDocument(expectedOutput.getIdentifier(), "", "", expectedOutput.getMimeType() /*mime type*/);
            executeRequestBuilder.setAsReference(expectedOutput.getIdentifier(), true);
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

    /**
     * @return url for non-static resources, value for static resources
     */
    private String getValueFromResource(ResourceDescription input) {
        String literalValue;
        AbstractResource resource = input.getResource();

        if (StaticDummyResource.class
                .isAssignableFrom(resource.getClass())) { //StaticResource or subtype
            StaticDummyResource staticResource = (StaticDummyResource) resource;
            literalValue = staticResource.getValue();
        } else {
            literalValue = resource.getUrl();
        }

        return literalValue;
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
    
    /**
     * @param <T>
     * @param list
     * @return list of unique items
     */
    private <T> List<T> removeDuplicates(List<T> list){
        return list.stream().distinct().collect(Collectors.toList());
    }
    
    
    /**
     * helper class to wrap execute request and origin data envelopes
     */
    private class WPSProcessInput{
        private final Execute execute;
        private final List<String> originDataEnvelopes;

        public WPSProcessInput(Execute execute, List<String> originDataEnvelopes) {
            this.execute = execute;
            this.originDataEnvelopes = originDataEnvelopes;
        }

        public Execute getExecute() {
            return execute;
        }

        public List<String> getOriginDataEnvelopes() {
            return originDataEnvelopes;
        }
    }
}
