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
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.core.models.extension.staticresource.StaticDummyResource;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessOutputDescription;
import de.wacodis.coreengine.executor.process.ResourceDescription;
import de.wacodis.coreengine.executor.process.wps.exception.WPSException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
import org.n52.geoprocessing.wps.client.model.async.FutureResult;
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
    private final Object lockObj = new Object();
    private final boolean validateInputs;
    
    public WPSProcess(WPSClientSession wpsClient, String wpsURL, String wpsVersion, String wpsProcessID, boolean validateInputs) {
        this.wpsProcessID = wpsProcessID;
        this.wpsURL = wpsURL;
        this.wpsVersion = wpsVersion;
        this.wpsClient = wpsClient;
        this.validateInputs = validateInputs;
    }

    /**
     * validateInputs == true
     *
     * @param wpsClient
     * @param wpsURL
     * @param wpsVersion
     * @param wpsProcessID
     */
    public WPSProcess(WPSClientSession wpsClient, String wpsURL, String wpsVersion, String wpsProcessID) {
        this(wpsClient, wpsURL, wpsVersion, wpsProcessID, true);
    }
    
    public boolean isValidateInputs() {
        return validateInputs;
    }
    
    public String getProcessID() {
        return wpsProcessID;
    }
    
    public String getWpsURL() {
        return wpsURL;
    }

    /**
     * connect to wps and get process description for this.wpsProcessID
     *
     * @param context
     * @return process description
     * @throws ExecutionException failed to connect to wps
     */
    public Process initWPS(ProcessContext context) throws ExecutionException {
        synchronized (this.lockObj) {
            connectWPS(context);
        }
        
        Process wpsProcessDescription = this.wpsClient.getProcessDescription(this.wpsURL, this.wpsProcessID, this.wpsVersion);
        LOGGER.info("retrieved process description {}", wpsProcessDescription);
        
        return wpsProcessDescription;
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
        String wpsProcessId = null;
        Process wpsProcessDescription;
        
        startTimeMillis = System.currentTimeMillis();
        
        LOGGER.info(
                "start execution of process " + context.getWacodisProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID);
        
        wpsProcessDescription = initWPS(context);
        
        if (this.validateInputs) {
            Optional<ExecutionException> preValidationResult = validatePreExecution(wpsProcessDescription, context);
            if (preValidationResult.isPresent()) { //throw exception if provided inputs or expected outputs are not valid
                throw preValidationResult.get();
            }
        }
        
        LOGGER.debug(
                "building wps execute request for process " + context.getWacodisProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID);
        //build execute request
        WPSProcessInput processInput = buildExecuteRequest(wpsProcessDescription, context);
        Execute executeRequest = processInput.getExecute();
        //submit execute request
        LOGGER.info("Executing WPS: {}", ToStringHelper.executeToString(executeRequest));
        
        try {
            //exeute wps process
            LOGGER.debug("execute request for process {}: {}", context.getWacodisProcessID(), executeRequest);
            FutureResult wpsOutputFuture = (FutureResult) this.wpsClient.executeAsync(this.wpsURL, executeRequest, this.wpsVersion); //submit process to wps and poll result periodically
            wpsProcessId = wpsOutputFuture.getJobId();
            LOGGER.info("started wps process {} to execute wacodis job {}", wpsProcessId, context.getWacodisProcessID());
            LOGGER.debug("waiting for result of wps process {}", wpsProcessId);
            
            long timeout_Millies = (long) context.getAdditionalParameters().getOrDefault(WPSProcessContextBuilder.getTIMEOUT_MILLIES_KEY(), -1l);
            Object wpsOutput;
            //wait until wps process return (blocking)
            if (timeout_Millies > 0) {  //respect timeout
                wpsOutput = wpsOutputFuture.getFutureResult().get(timeout_Millies, TimeUnit.MILLISECONDS);
            } else {
                wpsOutput = wpsOutputFuture.getFutureResult().get();
            }
            
            LOGGER.debug("wps process {} returned result of type {}", wpsProcessId, wpsOutput.getClass().getSimpleName());
            LOGGER.info("processing result for process {}: {}", context.getWacodisProcessID(), wpsOutput);
            LOGGER.debug("start parsing result for process {}", context.getWacodisProcessID());
            //parse wps output
            Result wpsProcessResult = parseWPSOutput(wpsOutput); //throws exception if wps output contains errors

            Optional<ExecutionException> postValidationResult = validatePostExecution(wpsProcessResult, wpsProcessDescription, context);
            
            if (postValidationResult.isPresent()) {
                throw postValidationResult.get(); //throw exception if excepted outputs are missing in received result
            }
            LOGGER.debug("successfully parsed result for process {}", context.getWacodisProcessID());
            
            endTimeMillis = System.currentTimeMillis();
            
            LOGGER.info("successfully executed process " + context.getWacodisProcessID() + " after " + ((endTimeMillis - startTimeMillis) / 1000) + " seconds");
            
            return buildProcessOutput(wpsProcessResult, processInput.getOriginDataEnvelopes(), startTimeMillis, endTimeMillis);
        } catch (Exception ex) {
            endTimeMillis = System.currentTimeMillis();
            ExecutionException eEx = new ExecutionException(UUID.fromString(context.getWacodisProcessID()), "execution of wps process for wacodis job " + this.wpsProcessID + " failed after " + ((endTimeMillis - startTimeMillis) / 1000) + " seconds", ex);
            if (wpsProcessId != null) {
                eEx.setProcessId(wpsProcessId);
            }
            
            throw eEx;
        }
    }
    
    private WPSProcessInput buildExecuteRequest(Process processDescription, ProcessContext context) {
        ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(processDescription);
        List<String> originDataEnvelopes = setWPSInputs(processDescription, context.getInputResources(), executeRequestBuilder); //register inputs
        setExpectedWPSOutputs(context.getExpectedOutputs(), executeRequestBuilder); //register expected outputs
        executeRequestBuilder.setAsynchronousExecute(); //make execute request asynchronous
        Execute execute = executeRequestBuilder.getExecute();
        
        return new WPSProcessInput(execute, originDataEnvelopes);
    }

    /**
     * register inputs for wps process
     *
     * @param providedInputResources
     * @param executeRequestBuilder
     */
    private List<String> setWPSInputs(Process processDescription, Map<String, List<ResourceDescription>> providedInputResources, ExecuteRequestBuilder executeRequestBuilder) {
        List<String> originDataEnvelopes = new ArrayList<>();
        List<InputDescription> processInputs = processDescription.getInputs();
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
        
        return removeDuplicates(removeNullorEmpty(originDataEnvelopes));
    }

    /**
     * @param literalProcessInput
     * @param providedResources
     * @param executeRequestBuilder
     * @return list of data envelope id's that correspond with the used input
     * resources
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
        String inputValue;
        List<String> originDataEnvelopes = new ArrayList<>();
        
        for (int i = 0; i < providedResources.size(); i++) {
            if (complexProcessInput.getMaxOccurs() < (i + 1)) {
                LOGGER.warn("Max occurs ({}) for input {} reached, provided {} resources.  Skipping surplus candidates.", complexProcessInput.getMaxOccurs(), providedResources.size(), complexProcessInput.getId());
                break;
            }
            
            providedResource = providedResources.get(i);
            inputValue = getValueFromResource(providedResource);
            
            try {
                executeRequestBuilder.addComplexDataReference(complexProcessInput.getId(), inputValue, providedResource.getSchema().getSchemaLocation(), null, providedResource.getMimeType());
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
    private void setExpectedWPSOutputs(List<JobOutputDescriptor> expectedOutputs, ExecuteRequestBuilder executeRequestBuilder) {
        for (JobOutputDescriptor expectedOutput : expectedOutputs) {
            executeRequestBuilder.setResponseDocument(expectedOutput.getIdentifier(), "", "", expectedOutput.getMimeType() /*mime type*/);
            executeRequestBuilder.setAsReference(expectedOutput.getIdentifier(), expectedOutput.getAsReference());
        }
    }
    
    private boolean connectWPS(ProcessContext context) throws ExecutionException {
        try {
            boolean isConnected = this.wpsClient.connect(this.wpsURL, this.wpsVersion);
            LOGGER.info("Connected to WPS at {}", this.wpsURL);
            return isConnected;
        } catch (WPSClientException ex) {
            throw new ExecutionException(UUID.fromString(context.getWacodisProcessID()), "could not connect to web processing service with url: " + this.wpsURL, ex);
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
    private Result parseWPSOutput(Object wpsOutput) throws WPSException {
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
            throw new WPSException("executing process " + this.wpsProcessID + " raised exceptions:" + System.lineSeparator() + buildExceptionText(exceptions));
        } else {
            if (wpsOutput != null) {
                LOGGER.warn("unexpected response format for wps process " + this.wpsProcessID + ":" + System.lineSeparator() + wpsOutput.toString());
            }
            throw new WPSException("unparsable response for wps process " + this.wpsProcessID);
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
    
    private Optional<ExecutionException> validatePreExecution(Process processDescription, ProcessContext context) {
        WPSInputOutputValidator validator = new WPSInputOutputValidator(processDescription);

        //check if mandatory inputs are provided and provided inputs and its mime types are supported by wps process
        LOGGER.debug(
                "validate provided inputs of process " + context.getWacodisProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID);
        Optional<Throwable> inputValidationResult = validator.validateProvidedInputResources(context.getInputResources());
        
        if (inputValidationResult.isPresent()) { //optional contains throwable if not valid
            return Optional.of(new ExecutionException(UUID.fromString(context.getWacodisProcessID()), "unable to execute process " + context.getWacodisProcessID() + " because of unsupported provided inputs" + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID, inputValidationResult.get()));
        }

        //check if expected outputs and its mime types are supported by wps process
        LOGGER.debug(
                "validate expected outputs of process " + context.getWacodisProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID);
        Optional<Throwable> expectedOutputValidationResult = validator.validateExpectedOutputs(context.getExpectedOutputs());
        
        if (expectedOutputValidationResult.isPresent()) { //optional contains throwable if not valid
            return Optional.of(new ExecutionException(UUID.fromString(context.getWacodisProcessID()), "unable to execute process " + context.getWacodisProcessID() + " because of unsupported expected outputs" + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID, expectedOutputValidationResult.get()));
        }
        
        return Optional.empty();
    }
    
    private Optional<ExecutionException> validatePostExecution(Result receivedResult, Process processDescription, ProcessContext context) {
        WPSResultValidator validator = new WPSResultValidator(processDescription);
        
        Optional<Throwable> outputValidationResult = validator.validateProcessOutput(receivedResult, context); //contains exception if invalid
        if (outputValidationResult.isPresent()) {
            return Optional.of(new ExecutionException(UUID.fromString(context.getWacodisProcessID()), "wps process" + processDescription.getId() + " (wps job id: " + receivedResult.getJobId() + ") returned invalid output", outputValidationResult.get()));
        }
        
        return Optional.empty();
    }

    /**
     * @param <T>
     * @param list
     * @return list of unique items
     */
    private <T> List<T> removeDuplicates(List<T> list) {
        return list.stream().distinct().collect(Collectors.toList());
    }

    /**
     * @param list
     * @return list of string without null references or empty strings
     */
    private List<String> removeNullorEmpty(List<String> list) {
        return list.stream().filter(str -> str != null && !str.isEmpty()).collect(Collectors.toList());
    }

    /**
     * helper class to wrap execute request and origin data envelopes
     */
    private class WPSProcessInput {
        
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
