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
import de.wacodis.coreengine.executor.process.ProcessContext;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.n52.geoprocessing.wps.client.ExecuteRequestBuilder;
import org.n52.geoprocessing.wps.client.WPSClientException;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.n52.geoprocessing.wps.client.model.ComplexInputDescription;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.OutputDescription;
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
    public ProcessContext execute(ProcessContext context) throws ExecutionException {
        if (!this.isInitialized || !this.isConnected) {
            init();
        }

        ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(this.wpsProcessDescription);
        setWPSInputs(context.getProcessResources(), executeRequestBuilder);
        setWPSOutputs(executeRequestBuilder);
        executeRequestBuilder.setAsynchronousExecute();
        Execute executeRequest = executeRequestBuilder.getExecute();

        ProcessContext outputContext = new ProcessContext();

        try {
            Object output = this.wpsClient.execute(this.wpsURL, executeRequest, this.wpsVersion);
            outputContext.setProcessResources(processWPSOutput(output));
        } catch (WPSClientException | IOException ex) {
            throw new ExecutionException("execution of wps process " + this.processID + " failed", ex);
        }

        return outputContext;
    }

    private void setWPSInputs(Map<String, AbstractResource> availableInputs, ExecuteRequestBuilder executeRequestBuilder) {
        AbstractResource inputResource;
        String inputID;

        for (InputDescription processInput : this.wpsProcessDescription.getInputs()) {
            inputID = processInput.getId();
            inputResource = availableInputs.get(inputID);

            //handle missing mandatory inputs
            if (!isInputOptional(processInput) && inputResource == null) {
                throw new IllegalArgumentException("cannot set inputs: no resource available for input " + processInput.getId() + " of process " + this.wpsProcessDescription.getId());
            }

            //set wpsClient input
            //ToDo properley handle body of PostResource
            if (inputResource != null) {

                String mimeType = (inputResource instanceof PostResource) ? ((PostResource) inputResource).getContentType() : WPSProcess.TEXTPLAIN_MIMETYPE;

                if (processInput instanceof LiteralInputDescription) {
                    executeRequestBuilder.addLiteralData(inputID, inputResource.getUrl(), null, null, mimeType); //input id, value, schema, encoding, mime type
                } else if (processInput instanceof ComplexInputDescription) {
                    try {
                        //complex inputs always by reference?
                        executeRequestBuilder.addComplexDataReference(processInput.getId(), inputResource.getUrl(), WPSProcess.GML3SCHEMA, null, WPSProcess.TEXTXML_MIMETYPE); //schema?
                    } catch (MalformedURLException ex) {
                        throw new IllegalArgumentException("invalid reference, malformed url " + inputResource.getUrl(), ex);
                    }
                }//else if (processInput instanceof BoundingBoxInputDescription){}

            }
        }

    }

    private void setWPSOutputs(ExecuteRequestBuilder executeRequestBuilder) {
        List<OutputDescription> processOutputs = this.wpsProcessDescription.getOutputs();

        if (processOutputs.size() != 1) {
            LOGGER.warn("wps process " + this.wpsProcessDescription.getId() + " has " + processOutputs.size() + " outputs, expected 1 output");
        }

        String outputID;

        for (OutputDescription processOutput : processOutputs) {
            outputID = processOutput.getId();
            executeRequestBuilder.addOutput(outputID, null, null, WPSProcess.TEXTPLAIN_MIMETYPE); //output type?
        }
    }

    private boolean isInputOptional(InputDescription input) {
        return (input.getMinOccurs() > 0);
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

    private Map<String, AbstractResource> processWPSOutput(Object wpsOutput) throws ExecutionException {
        Map<String, AbstractResource> outputResources = new HashMap<>();
        
        Result processResult;
        
        if (wpsOutput instanceof Result) {
            processResult = ((Result) wpsOutput);
        } else if (wpsOutput instanceof StatusInfo) {
            processResult = (((StatusInfo) wpsOutput).getResult());
        }else{
            throw new ExecutionException("unparsable response for wps process " + this.processID);
        }
        
        AbstractResource outputResource;

        for(Data output : processResult.getOutputs()){
            outputResource = new GetResource();
            outputResource.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
            outputResource.setUrl((String) output.getValue());
            outputResources.put(output.getId(), outputResource);
        }

        return outputResources;
    }

    @Override
    public boolean validateContext(ProcessContext context) {
        List<String> mandatoryInputs = this.wpsProcessDescription.getInputs().stream().filter(id -> !isInputOptional(id)).map(id -> id.getId()).collect(Collectors.toList());
        Set<String> availableInputs = context.getProcessResources().keySet();

        boolean allMandatoryInputsAvailable = availableInputs.containsAll(mandatoryInputs);
        return allMandatoryInputsAvailable;
    }

}
