/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.PostResource;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.process.ProcessContext;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
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
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WPSProcess implements de.wacodis.coreengine.executor.process.Process{

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WPSProcess.class);
    
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
        setWPSInputs(context, executeRequestBuilder);
        setWPSOutputs(executeRequestBuilder);
        executeRequestBuilder.setAsynchronousExecute();
        Execute executeRequest = executeRequestBuilder.getExecute();
        
        
        
        
        try {
            this.wpsClient.execute(this.wpsURL, executeRequest, this.wpsVersion);
        } catch (WPSClientException | IOException ex) {
            throw new ExecutionException("execution of wps process " + this.processID +  " failed", ex);
        }
        
        return new ProcessContext();
    }

    private void setWPSInputs(ProcessContext context, ExecuteRequestBuilder executeRequestBuilder) {


        for (InputDescription processInput : this.wpsProcessDescription.getInputs()) {
            AbstractResource resource = context.getProcessResource(processInput.getId());

            //handle missing mandatory inputs
            if (!isInputOptional(processInput) && resource == null) {
                throw new IllegalArgumentException("cannot set inputs: no resource available for input " + processInput.getId() + " of process " + this.wpsProcessDescription.getId());
            }

            //set wpsClient input
            if (resource != null) {

     
                    String mimeType = (resource instanceof PostResource)? ((PostResource)resource).getContentType() : "text/plain";

                    //how to handle input types
                    if (processInput instanceof LiteralInputDescription) {
                        executeRequestBuilder.addLiteralData(processInput.getId(),resource.getUrl(), null, null, mimeType); //input id, value, schema, encoding, mime type
                    }else if(processInput instanceof ComplexInputDescription){
                        try {
                            //ToDo properley handle body of PostResource
                            executeRequestBuilder.addComplexDataReference(processInput.getId(), resource.getUrl(), null, null, mimeType); //schema?
                        } catch (MalformedURLException ex) {
                            throw new IllegalArgumentException("invalid reference, malformed url " + resource.getUrl(), ex);
                        }
                    }//else if (processInput instanceof BoundingBoxInputDescription){}

                }
        }

    }
    
    private void setWPSOutputs(ExecuteRequestBuilder executeRequestBuilder){
        List<OutputDescription> processOutputs = this.wpsProcessDescription.getOutputs();
        
        if(processOutputs.size() != 1){
            LOGGER.warn("wps process " + this.wpsProcessDescription.getId() + " has " + processOutputs.size() + " outputs, expected 1 output");
        }
        
        for(OutputDescription processOutput : processOutputs){
            String id = processOutput.getId();
            executeRequestBuilder.addOutput(id, null, null, "text/plain");
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



    @Override
    public boolean validateContext(ProcessContext context) {
        List<String> mandatoryInputs = this.wpsProcessDescription.getInputs().stream().filter(id -> !isInputOptional(id)).map(id -> id.getId()).collect(Collectors.toList());
        Set<String> availableInputs = context.getProcessResources().keySet();
        
        boolean allMandatoryInputsAvailable = availableInputs.containsAll(mandatoryInputs);
        return allMandatoryInputsAvailable;   
    }

}
