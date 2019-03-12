/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.GetResource;
import de.wacodis.core.models.PostResource;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessOutput;
import de.wacodis.coreengine.executor.process.ResourceDescription;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final String TEXTPLAIN_MIMETYPE = "text/plain";
    private static final String TEXTXML_MIMETYPE = "text/xml";

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
        this.isInitialized = true;
    }

    @Override
    public ProcessOutput execute(ProcessContext context) throws ExecutionException {
        LOGGER.info("start execution of process " + context.getProcessID() + System.lineSeparator() + "wps url: " + this.wpsURL + ", wps version: " + this.wpsVersion + " wps process: " + this.processID);

        if (!this.isInitialized || !this.isConnected) {
            init();
        }

        //get execute request
        Execute executeRequest = buildExecuteRequest(context);

        //submit execute request
        try {
            Object wpsOutput = this.wpsClient.execute(this.wpsURL, executeRequest, this.wpsVersion); //submit 
            Result wpsProcessResult = parseWPSOutput(wpsOutput);
            ProcessOutput processOutput = buildProcessOutput(context, wpsProcessResult);

            LOGGER.info("successfully executed process " + context.getProcessID());
            return processOutput;
        } catch (WPSClientException | IOException ex) {
            throw new ExecutionException("execution of wps process " + this.processID + " failed", ex);
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

            //set wpsClient input
            //ToDo properley handle body of PostResource
            if (availableResources != null) {

                for (ResourceDescription resource : availableResources) {

                    mimeType = (resource.getMimeType() != null) ? resource.getMimeType() : "";

                    if (processInput instanceof LiteralInputDescription) { //Literal Input
                        //set input, use url as value
                        executeRequestBuilder.addLiteralData(inputID, resource.getResource().getUrl(), "", "", mimeType); //input id, value, schema, encoding, mime type
                    } else if (processInput instanceof ComplexInputDescription) { // Complex Input
                            //Complex Input (always as reference?)
                    try {
                        //set input, use url as value
                        executeRequestBuilder.addComplexDataReference(inputID, resource.getResource().getUrl(), WPSProcess.GML3SCHEMA, null, WPSProcess.TEXTXML_MIMETYPE); //schema?, mimety
                    } catch (MalformedURLException ex) {
                        throw new IllegalArgumentException("cannot add complex input " + inputID + " as reference, invalid reference, malformed url " + resource.getResource().getUrl(), ex);
                    }
                    }//else if (processInput instanceof BoundingBoxInputDescription){} //TODO: handle further input types
                    else{
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
           executeRequestBuilder.setResponseDocument(expectedOutput.getIdentifier(), "", "", expectedOutput.getMimeType()); //output type? mime type?
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

        if (wpsOutput instanceof Result) {
            wpsProcessResult = ((Result) wpsOutput);
        } else if (wpsOutput instanceof StatusInfo) {
            wpsProcessResult = (((StatusInfo) wpsOutput).getResult());
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

        LOGGER.debug("successfully parsed output of wps process " + this.processID);

        return wpsProcessResult;
    }

    /**
     * TODO: handle PostResouces
     *
     * @param wpsProcessResult
     * @return
     */
    private ProcessOutput buildProcessOutput(ProcessContext context, Result wpsProcessResult) {
        ProcessOutput processOutput = new ProcessOutput();
        AbstractResource outputResource;
        String actualMimeType;
        String expectedMimeType;

        //create resource for each wps process output 
        for (Data output : wpsProcessResult.getOutputs()) {
            //create Resource for Ouput
            outputResource = new GetResource();
            outputResource.setMethod(AbstractResource.MethodEnum.GETRESOURCE); //ToDo PostResources
            outputResource.setUrl((String) output.getValue());
            //set mime type
            actualMimeType = (output.getFormat() != null && output.getFormat().getMimeType() != null) ? output.getFormat().getMimeType() : "";
            expectedMimeType = getExpectedMimeType(output.getId(), context);
            
            if(!actualMimeType.equalsIgnoreCase(expectedMimeType)){
                LOGGER.warn("unexpected mime type for output " + output.getId() + ", expected: " + expectedMimeType + ", actual: " + actualMimeType + System.lineSeparator() + "set mime type to " + actualMimeType);
            }
            
            //add Resource to ProcessOutput
            processOutput.addOutputResource(output.getId(), new ResourceDescription(outputResource, actualMimeType));
        }

        LOGGER.debug("built process output for wps process " + this.processID);

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
    
    
    private ExpectedProcessOutput findExpectedProcessOutputByID(ProcessContext context, String id){
        ExpectedProcessOutput output = null;
        
        for(ExpectedProcessOutput o : context.getExpectedOutputs()){
            if(o.getIdentifier().equals(id)){
                output = o;
                break;
            }
        }
        
        return output;
    }
    
    private String getExpectedMimeType(String outputID, ProcessContext context){
        //get expected mime type
        ExpectedProcessOutput expectedOutput = findExpectedProcessOutputByID(context, outputID);
        String expectedMimeType = expectedOutput.getMimeType();
        
        return expectedMimeType;
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
        List<String> mandatoryInputs = this.wpsProcessDescription.getInputs().stream().filter(id -> !isInputOptional(id)).map(id -> id.getId()).collect(Collectors.toList());
        List<String> offeredOutputs = this.wpsProcessDescription.getOutputs().stream().map(od -> od.getId()).collect(Collectors.toList());
        Set<String> availableInputs = context.getInputResources().keySet();

        boolean allMandatoryInputsAvailable = availableInputs.containsAll(mandatoryInputs);
        boolean processOffersExpectedOutputs = offeredOutputs.containsAll(context.getExpectedOutputs());

        return (allMandatoryInputsAvailable && processOffersExpectedOutputs && !context.getExpectedOutputs().isEmpty() && context.getProcessID() != null);
    }

}
