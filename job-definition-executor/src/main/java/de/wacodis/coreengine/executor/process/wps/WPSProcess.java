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
import java.util.Set;
import java.util.stream.Collectors;
import org.n52.geoprocessing.wps.client.ExecuteRequestBuilder;
import org.n52.geoprocessing.wps.client.WPSClientException;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.n52.geoprocessing.wps.client.model.ComplexInputDescription;
import org.n52.geoprocessing.wps.client.model.ExceptionReport;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.OWSExceptionElement;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.Result;
import org.n52.geoprocessing.wps.client.model.StatusInfo;
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

    private final String processID;
    private final String wpsURL;
    private final String wpsVersion;
    private final WPSClientSession wpsClient;
    private Process wpsProcessDescription;
    private boolean isInitialized;
    private boolean isConnected;

    public WPSProcess(WPSClientSession wpsClient, String wpsURL, String wpsVersion, String processID) {
        this.processID = processID;
        this.wpsURL = wpsURL;
        this.wpsVersion = wpsVersion;
        this.wpsClient = wpsClient;
        this.isInitialized = false;
        this.isConnected = false;
    }

    public String getProcessID() {
        return processID;
    }

    public String getWpsURL() {
        return wpsURL;
    }

    public void init() {
        if (!isConnected) {
            connectWPS();
        }

        this.wpsProcessDescription = this.wpsClient.getProcessDescription(this.wpsURL, this.processID, this.wpsVersion);
        LOGGER.info("retrieved process description {}", this.wpsProcessDescription);
        this.isInitialized = true;
    }

    @Override
    public ProcessOutputDescription execute(ProcessContext context) throws ExecutionException {
        LOGGER.info("start execution of process " + context.getProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.processID);

        if (!this.isInitialized || !this.isConnected) {
            init();
        }

        LOGGER.info("Building WPS Execute request");
        //get execute request
        Execute executeRequest = buildExecuteRequest(context);
        //submit execute request
        try {
            LOGGER.info("Executing request: {}", executeRequest);
            Object wpsOutput = this.wpsClient.execute(this.wpsURL, executeRequest, this.wpsVersion); //submit process to wps
            LOGGER.info("Processing result: {}", wpsOutput);
            Result wpsProcessResult = parseWPSOutput(wpsOutput);
            ProcessOutputDescription outputDescription = buildProcessOutput(context, wpsProcessResult);

            LOGGER.info("successfully executed process " + context.getProcessID());
            return outputDescription;
        } catch (WPSClientException | IOException ex) {
            throw new ExecutionException("execution of wps process for wacodis job " + this.processID + " failed", ex);
        }
    }

    private Execute buildExecuteRequest(ProcessContext context) {
        ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(this.wpsProcessDescription); //build execute request
        setWPSInputs(context.getInputResources(), executeRequestBuilder); //register inputs
        setWPSOutputs(context.getExpectedOutputs(), executeRequestBuilder); //register outputs
        executeRequestBuilder.setAsynchronousExecute(); //make execute request asynchronous

        return executeRequestBuilder.getExecute();
    }

    /**
     * register inputs for wps process
     *
     * @param availableInputs
     * @param executeRequestBuilder
     */
    private void setWPSInputs(Map<String, List<ResourceDescription>> availableInputs, ExecuteRequestBuilder executeRequestBuilder) {
        List<InputDescription> processInputs = this.wpsProcessDescription.getInputs();
        List<ResourceDescription> availableResources;
        String inputID;
        String mimeType;

        for (InputDescription processInput : processInputs) {
            inputID = processInput.getId();
            availableResources = availableInputs.get(inputID); //get corresponding input resources

            //handle missing mandatory inputs
            if (!isInputOptional(processInput) && availableResources == null) {
                throw new IllegalArgumentException("cannot set inputs: no resource available for input " + processInput.getId() + " of process " + this.wpsProcessDescription.getId());
            }
            
            int addedOccurs = 0;

            //set wpsClient input
            //ToDo properley handle body of PostResource
            if (availableResources != null) {

                for (ResourceDescription resource : availableResources) {
                    
                    if (addedOccurs >= processInput.getMaxOccurs()) {
                        LOGGER.info("Max occurs ({}) for input {} reached. Skipping other candidates", processInput.getMaxOccurs(), processInput.getId());
                        break;
                    }
                    
                    mimeType = resource.getMimeType();

                    if (processInput instanceof LiteralInputDescription) { //Literal Input
                       String literalValue = getLiteralInputValue(resource);
                       executeRequestBuilder.addLiteralData(inputID, literalValue, "", "", mimeType); //input id, value, schema, encoding, mime type 
                        
                        addedOccurs++;
                    } else if (processInput instanceof ComplexInputDescription) { // Complex Input
                        //Complex Input (always as reference?)
                        try {
                            if(StaticDummyResource.class.isAssignableFrom(resource.getResource().getClass())){
                                throw new IllegalArgumentException("illegal ressource type for complex wps input, resource for input " + processInput.getId() + " is a static resource, only non-static resources are allowed for complex inputs");
                            }
                            
                            //set input, use url as value
                            executeRequestBuilder.addComplexDataReference(inputID, resource.getResource().getUrl(), GML3SCHEMA, null, mimeType); //ToDo make schema variable
                            addedOccurs++;
                        } catch (MalformedURLException ex) {
                            throw new IllegalArgumentException("cannot add complex input " + inputID + " as reference, invalid reference, malformed url " + resource.getResource().getUrl(), ex);
                        }
                    }//else if (processInput instanceof BoundingBoxInputDescription){} //TODO: handle further input types
                    else {
                        LOGGER.warn("cannot handle input " + inputID + " of type " + processInput.getClass().getSimpleName() + ", ignore input " + inputID);
                    }
                }
            }
        }

    }

    /**
     * register each expected output
     *
     * @param executeRequestBuilder
     * @param expectedOutputs
     */
    private void setWPSOutputs(List<ExpectedProcessOutput> expectedOutputs, ExecuteRequestBuilder executeRequestBuilder) {
        for (ExpectedProcessOutput expectedOutput : expectedOutputs) {
            // METADATA is special. TODO: treat it in a generic way
            if ("METADATA".equalsIgnoreCase(expectedOutput.getIdentifier())) {
                executeRequestBuilder.setResponseDocument(expectedOutput.getIdentifier(), "", "", expectedOutput.getMimeType() /*mime type*/); //output type? mime type?
            } else {
                // TODO parse the mime type from the process description
                executeRequestBuilder.setResponseDocument(expectedOutput.getIdentifier(), "", "", expectedOutput.getMimeType() /*mime type*/); //output type? mime type?
                executeRequestBuilder.setAsReference(expectedOutput.getIdentifier(), true);
            }
        }
    }

    private boolean isInputOptional(InputDescription input) {
        return (input.getMinOccurs() == 0);
    }

    private boolean isResourceAvailable(InputHelper inputHelper) {
        return (inputHelper != null && inputHelper.isResourceAvailable() && inputHelper.getResource().isPresent() && inputHelper.getResource().get().size() > 0);
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
            LOGGER.warn("wps output contained errors, raise ExecutionException");
            List<OWSExceptionElement> exceptions = ((ExceptionReport) wpsOutput).getExceptions();
            throw new ExecutionException("executing process " + this.processID + " raised exceptions:" + System.lineSeparator() + buildExceptionText(exceptions));
        } else {
            if (wpsOutput != null) {
                LOGGER.warn("unexpected response format for wps process " + this.processID + ":" + System.lineSeparator() + wpsOutput.toString());
            }
            throw new ExecutionException("unparsable response for wps process " + this.processID);
        }

        LOGGER.debug("successfully parsed output of wps process " + this.processID + ", WPS Job-ID: " + wpsProcessResult.getJobId() + ")");

        return wpsProcessResult;
    }

    /**
     * @param wpsProcessResult
     * @return
     */
    private ProcessOutputDescription buildProcessOutput(ProcessContext context, Result wpsProcessResult) throws ExecutionException {
        ProcessOutputDescription outputDescription = new ProcessOutputDescription();

        Set<String> availableProcessOutputs = getAvailableOutputIdentifiers(wpsProcessResult);

        //throw exception if expected output is not available
        for (ExpectedProcessOutput expectedOutput : context.getExpectedOutputs()) {
            if (!availableProcessOutputs.contains(expectedOutput.getIdentifier())) {
                throw new ExecutionException("expected process output " + expectedOutput + " not available (Wacodis Job-ID: " + this.processID + ", WPS Job-ID: " + wpsProcessResult.getJobId() + ")");
            }
        }

        outputDescription.setOutputIdentifiers(availableProcessOutputs);
        outputDescription.setProcessIdentifier(wpsProcessResult.getJobId()); //wps job-id

        LOGGER.debug("built process output description for wps process " + this.processID + " (wps Job-ID: " + wpsProcessResult.getJobId() + ")");

        return outputDescription;
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
    private Set<String> getAvailableOutputIdentifiers(Result wpsProcessResult) {
        List<Data> availableOutputs = wpsProcessResult.getOutputs();
        Set<String> availableOutputIdentifiers = availableOutputs.stream().map(output -> output.getId()).collect(Collectors.toSet());

        return availableOutputIdentifiers;
    }
    

    /**
     * @return  url for non-static resources,  value for static resources
     */
    private String getLiteralInputValue(ResourceDescription input){
        String literalValue; 
        AbstractResource resource = input.getResource();
        
        if(StaticDummyResource.class.isAssignableFrom(resource.getClass())){
            StaticDummyResource staticResource = (StaticDummyResource) resource;
            literalValue = staticResource.getValue();
        }else{
            literalValue = resource.getUrl();
        }
        
        return literalValue;
    }

    /**
     * context must include resources for all mandatory process inputs of the
     * wps process, context must not include expected outputs that are not
     * returned by the wps rpcoess, context must include at least one expected
     * output, processID must not be null
     *
     * @param context
     * @return true if context is valid for the wps output
     */
    @Override
    public boolean validateContext(ProcessContext context) {
        if (!this.isInitialized || !this.isConnected) {
            init();
        }
   
        List<String> mandatoryInputs = this.wpsProcessDescription.getInputs().stream().filter(id -> !isInputOptional(id)).map(id -> id.getId()).collect(Collectors.toList());
        List<String> offeredOutputs = this.wpsProcessDescription.getOutputs().stream().map(od -> od.getId()).collect(Collectors.toList());
        Set<String> availableInputs = context.getInputResources().keySet();

        boolean allMandatoryInputsAvailable = availableInputs.containsAll(mandatoryInputs);
        boolean processOffersExpectedOutputs = offeredOutputs.containsAll(context.getExpectedOutputs());

        return (allMandatoryInputsAvailable && processOffersExpectedOutputs && !context.getExpectedOutputs().isEmpty() && context.getProcessID() != null);
    }

}
