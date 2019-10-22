/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.extension.staticresource.StaticDummyResource;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessOutputDescription;
import de.wacodis.coreengine.executor.process.ResourceDescription;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import org.n52.geoprocessing.wps.client.model.OutputDescription;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.Result;
import org.n52.geoprocessing.wps.client.model.StatusInfo;
import org.n52.geoprocessing.wps.client.model.WPSDescriptionParameter;
import org.n52.geoprocessing.wps.client.model.execution.Data;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WPSProcess implements de.wacodis.coreengine.executor.process.Process {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WPSProcess.class);

    private static final String GML3SCHEMA = "http://schemas.opengis.net/gml/3.1.1/base/feature.xsd";

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
     * @param context
     * @return ProcessOutputDescription containing wps job id, parameters 'startTimeMillis' and 'endTimeMillis'
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

        //check if mandatory inputs are provided and provided inputs and its mime types are supported by wps process
        LOGGER.debug(
                "validate provided inputs of process " + context.getWacodisProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID);
        Optional<Throwable> inputValidationResult = validateProvidedInputResources(context.getInputResources());

        if (inputValidationResult.isPresent()) { //optional contains throwable if not valid
            throw new ExecutionException("unable to execute process " + context.getWacodisProcessID() + " because of unsupported provided inputs" + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID, inputValidationResult.get());
        }

        //check if expected outputs and its mime types are supported by wps process
        LOGGER.debug(
                "validate expected outputs of process " + context.getWacodisProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID);
        Optional<Throwable> expectedOutputValidationResult = validateExpectedOutputs(context.getExpectedOutputs());

        if (expectedOutputValidationResult.isPresent()) { //optional contains throwable if not valid
            throw new ExecutionException("unable to execute process " + context.getWacodisProcessID() + " because of unsupported expected outputs" + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID, expectedOutputValidationResult.get());
        }

        LOGGER.debug(
                "building wps execute request for process " + context.getWacodisProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.wpsProcessID);
        //get execute request
        Execute executeRequest = buildExecuteRequest(context);
        //submit execute request

        try {
            //exeute wps process
            LOGGER.debug("execute request for process {}: {}", context.getWacodisProcessID(), executeRequest);
            Object wpsOutput = this.wpsClient.execute(this.wpsURL, executeRequest, this.wpsVersion); //submit process to wps and poll result periodically
            LOGGER.info("processing result for process {}: {}", context.getWacodisProcessID(), wpsOutput);
            LOGGER.debug("start parsing result for process {}", context.getWacodisProcessID());
            //parse wps output
            Result wpsProcessResult = parseWPSOutput(wpsOutput); //throws exception if wps output contains errors
            Optional<Throwable> outputValidationResult = validateProcessOutput(wpsProcessResult, context); //contains exception if invalid
            if (outputValidationResult.isPresent()) {
                throw new ExecutionException("wps process" + this.wpsProcessDescription.getId() + " (wps job id: " + wpsProcessResult.getJobId() + ") returned invalid output", outputValidationResult.get());
            }
            LOGGER.debug("successfully parsed result for process {}", context.getWacodisProcessID());
            
            endTimeMillis = System.currentTimeMillis();
            
            LOGGER.info("successfully executed process " + context.getWacodisProcessID() + " after " + ((endTimeMillis - startTimeMillis)/1000) + " seconds" );
            
            return buildProcessOutput(wpsProcessResult, startTimeMillis, endTimeMillis);
        } catch (WPSClientException | IOException ex) {
            endTimeMillis = System.currentTimeMillis();
            throw new ExecutionException("execution of wps process for wacodis job " + this.wpsProcessID + " failed after " + ((endTimeMillis - startTimeMillis)/1000) + " seconds", ex);
        }
    }

    private Execute buildExecuteRequest(ProcessContext context) {
        ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(this.wpsProcessDescription);
        setWPSInputs(context.getInputResources(), executeRequestBuilder); //register inputs
        setExpectedWPSOutputs(context.getExpectedOutputs(), executeRequestBuilder); //register expected outputs
        executeRequestBuilder.setAsynchronousExecute(); //make execute request asynchronous

        return executeRequestBuilder.getExecute();
    }

    /**
     * register inputs for wps process
     *
     * @param availableInputs
     * @param executeRequestBuilder
     */
    private void setWPSInputs(Map<String, List<ResourceDescription>> providedInputResources, ExecuteRequestBuilder executeRequestBuilder) {
        List<InputDescription> processInputs = this.wpsProcessDescription.getInputs();
        List<ResourceDescription> providedResourcesForProcessInput;
        String inputID;

        for (InputDescription processInput : processInputs) {
            inputID = processInput.getId();
            
            if(!providedInputResources.containsKey(inputID)){ //skip if no resource provided for specific wps input
                continue;
            }
            
            providedResourcesForProcessInput = providedInputResources.get(inputID);

            if (processInput instanceof LiteralInputDescription) {
                setLiteralInputData((LiteralInputDescription) processInput, providedResourcesForProcessInput, executeRequestBuilder);
            } else if (processInput instanceof ComplexInputDescription) {
                setComplexInputData((ComplexInputDescription) processInput, providedResourcesForProcessInput, executeRequestBuilder);
            } else if (processInput instanceof BoundingBoxInputDescription) {
                throw new NotImplementedException("cannot handle wps input " + inputID + ": BoundingBoxInput not supported");
            } else {
                throw new IllegalArgumentException("cannot handle wps input " + inputID + ": input is of unknown type " + processInput.getClass().getSimpleName());
            }

        }

    }

    private void setLiteralInputData(LiteralInputDescription literalProcessInput, List<ResourceDescription> providedResources, ExecuteRequestBuilder executeRequestBuilder) {
        String literalValue;
        ResourceDescription providedResource;

        for (int i = 0; i < providedResources.size(); i++) {
            if (literalProcessInput.getMaxOccurs() < (i + 1)) {
                LOGGER.warn("Max occurs ({}) for input {} reached, provided {} resources.  Skipping surplus candidates.", literalProcessInput.getMaxOccurs(), providedResources.size(), literalProcessInput.getId());
                break;
            }

            providedResource = providedResources.get(i);
            literalValue = getValueFromResource(providedResource);
            executeRequestBuilder.addLiteralData(literalProcessInput.getId(), literalValue, "", "", providedResource.getMimeType()); //input id, value, schema, encoding, mime type 
        }
    }

    private void setComplexInputData(ComplexInputDescription complexProcessInput, List<ResourceDescription> providedResources, ExecuteRequestBuilder executeRequestBuilder) {
        ResourceDescription providedResource;

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
                executeRequestBuilder.addComplexDataReference(complexProcessInput.getId(), providedResource.getResource().getUrl(), GML3SCHEMA, null, providedResource.getMimeType()); //ToDo make schema variable
            } catch (MalformedURLException ex) {
                throw new IllegalArgumentException("cannot add complex input " + complexProcessInput.getId() + " as reference, invalid reference, malformed url" + providedResource.getResource().getUrl(), ex);
            }
        }
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

    private boolean isInputOptional(InputDescription input) {
        return (input.getMinOccurs() == 0);
    }

    private boolean isResourceAvailable(Map<String, List<ResourceDescription>> providedInputResources, String key) { 
        return (providedInputResources.get(key) != null && providedInputResources.get(key).size() > 0);
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


    private ProcessOutputDescription buildProcessOutput(Result wpsProcessResult, long startTimeMillis, long endTimeMillis) { 
        ProcessOutputDescription processOutput = new ProcessOutputDescription();
        
        processOutput.setProcessIdentifier(wpsProcessResult.getJobId()); //wps job-id
        processOutput.addOutputParameter("startTimeMillis", Long.toString(startTimeMillis));
        processOutput.addOutputParameter("endTimeMillis", Long.toString(endTimeMillis));

        LOGGER.debug("built process output description for wps process " + this.wpsProcessID + " (wps Job-ID: " + wpsProcessResult.getJobId() + ")");

        return processOutput;
    }

    /**
     * check if wps output actually contains all expected output parameters
     *
     * @param wpsProcessResult
     * @param context
     * @return Optional.empty() if valid, Optiona.of(Throwable) if invalid
     */
    private Optional<Throwable> validateProcessOutput(Result wpsProcessResult, ProcessContext context) {
        Set<String> missingProcessOutputs = getMissingProcessOutputs(wpsProcessResult, context.getExpectedOutputs());

        if (missingProcessOutputs.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new ExecutionException("wps process " + this.wpsProcessDescription.getId() + " (wps job id: " + wpsProcessResult.getJobId() + ") returned but expected outputs are missing: " + missingProcessOutputs.toString()));
        }
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
     * get all identifiers of outputs included in a wps process result
     *
     * @param wpsProcessResult
     * @return
     */
    private Set<String> getMissingProcessOutputs(Result wpsProcessResult, List<ExpectedProcessOutput> expectedOutputs) {
        List<Data> availableOutputs = wpsProcessResult.getOutputs();
        Set<String> availableOutputIdentifiers = availableOutputs.stream().map(availableOutput -> availableOutput.getId()).collect(Collectors.toSet());
        Set<String> expectedOutputIdentifiers = expectedOutputs.stream().map(expectedOutput -> expectedOutput.getIdentifier()).collect(Collectors.toSet());

        expectedOutputIdentifiers.removeAll(availableOutputIdentifiers); //missing entries remain in the set

        return expectedOutputIdentifiers;
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

    /**
     * validates if expected outputs are actually provided by wps process
     *
     * 1) expected output matches a wps output 2) MimeType for expected output
     * is supported for corresponding wps output
     *
     * @param expectedOutputs
     * @return returns Optional.of(Throwable t) if a expected output or a
     * MimeType of a expected output is not supported, Optinal.empty() if valid
     */
    private Optional<Throwable> validateExpectedOutputs(List<ExpectedProcessOutput> expectedOutputs) {

        for (ExpectedProcessOutput expectedOutput : expectedOutputs) {
            Optional<OutputDescription> supportedOutput = getOutputDescriptionByID(expectedOutput.getIdentifier());

            if (supportedOutput.isPresent()) { //output supported
                boolean isMimeTypeSupported = isMimeTypeSupported(supportedOutput.get(), expectedOutput.getMimeType());

                if (!isMimeTypeSupported) { //MimeType not supported for supported output
                    return Optional.of(new IllegalArgumentException("MimeType  " + expectedOutput.getMimeType() + " is not supported for expected output " + expectedOutput.getIdentifier() + " of wps process " + this.wpsProcessDescription.getTitle() + " (" + this.wpsProcessDescription.getId() + ")"));
                }

            } else { //output not supported
                return Optional.of(new IllegalArgumentException("expected output " + expectedOutput.getIdentifier() + " is not supported by wps process " + this.wpsProcessDescription.getTitle() + " (" + this.wpsProcessDescription.getId() + ")"));
            }
        }

        return Optional.empty(); //return empty if valid
    }

    /**
     * Checks whether the provided input resources are suitable for the wps
     * process.
     *
     * 1) resources for mandatory wps inputs are provided 2) resource key
     * matches a wps input 3) resource mime type is supported for corresponding
     * wps input 4) minimum occurrence of wps inputs is respected
     *
     * @param providedInputResources
     * @return
     */
    private Optional<Throwable> validateProvidedInputResources(Map<String, List<ResourceDescription>> providedInputResources) {
        Set<String> missingMandatoryInputs = getMissingMandatoryInputs(providedInputResources);

        if (!missingMandatoryInputs.isEmpty()) { //mandatory input not provided
            return Optional.of(new IllegalArgumentException("missing mandatory input(s) " + missingMandatoryInputs.toString() + " for wps process " + this.wpsProcessDescription.getTitle() + " (" + this.wpsProcessDescription.getId() + ")"));
        }

        for (String providedInputID : providedInputResources.keySet()) {
            Optional<InputDescription> supportedInput = getInputDescriptionByID(providedInputID);

            if (supportedInput.isPresent()) { //input supported
                boolean isMimeTypeSupported = checkResourcesSupportedMimeType(supportedInput.get(), providedInputResources.get(providedInputID));

                if (!isMimeTypeSupported) { //MimeType not supported for supported input
                    return Optional.of(new IllegalArgumentException("provided MimeType is not supported for input " + supportedInput.get().getId() + " of wps process " + this.wpsProcessDescription.getTitle() + " (" + this.wpsProcessDescription.getId() + ")"));
                }

                if (!validateMinumumInputQuantity(supportedInput.get(), providedInputResources.get(providedInputID))) { //input quantity
                    return Optional.of(new IllegalArgumentException("unsupported quantity " + providedInputResources.get(providedInputID).size() + " of input " + supportedInput.get().getId() + " provided, provided " + providedInputResources.size() + " resources ,minimum occurrence is " + supportedInput.get().getMinOccurs() + " for wps process " + this.wpsProcessDescription.getTitle() + " (" + this.wpsProcessDescription.getId() + ")"));
                }

            } else { //input not supported
                return Optional.of(new IllegalArgumentException("provided input " + providedInputID + " is not supported by wps process " + this.wpsProcessDescription.getTitle() + " (" + this.wpsProcessDescription.getId() + ")"));
            }
        }

        return Optional.empty(); //return empty if valid
    }

    private Optional<OutputDescription> getOutputDescriptionByID(String outputIdentifier) {
        return this.wpsProcessDescription.getOutputs().stream().filter(output -> output.getId().equals(outputIdentifier)).findAny();
    }

    private Optional<InputDescription> getInputDescriptionByID(String inputIdentifier) {
        return this.wpsProcessDescription.getInputs().stream().filter(input -> input.getId().equals(inputIdentifier)).findAny();
    }

    private boolean validateMinumumInputQuantity(InputDescription supportedInput, List<ResourceDescription> providedInput) {
        return (providedInput.size() >= supportedInput.getMinOccurs());
    }

    private Set<String> getMissingMandatoryInputs(Map<String, List<ResourceDescription>> providedInputResources) {
        Set<String> mandatoryInputs = this.wpsProcessDescription.getInputs().stream().filter(supportedInput -> !isInputOptional(supportedInput)).map(supportedInput -> supportedInput.getId()).collect(Collectors.toSet()); //get IDs of all mandatory inputs
        Set<String> providedInputs = providedInputResources.keySet().stream().filter(inputID -> isResourceAvailable(providedInputResources, inputID)).collect(Collectors.toSet()); //get all provided inputs (checks if != null && size is > 0)      
        mandatoryInputs.removeAll(providedInputs); //keep all entries that are not contained in provideInputs

        return mandatoryInputs;
    }

    private boolean isMimeTypeSupported(WPSDescriptionParameter wpsInputOutputParameter, String mimeType) {
        return wpsInputOutputParameter.getFormats().stream().anyMatch(format -> format.getMimeType().equalsIgnoreCase(mimeType));
    }

    private boolean checkResourcesSupportedMimeType(InputDescription supportedInput, List<ResourceDescription> providedResources) {
        for (ResourceDescription providedResource : providedResources) {
            if (!isMimeTypeSupported(supportedInput, providedResource.getMimeType())) {
                return false;
            }
        }

        return true;
    }
}
