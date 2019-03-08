/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.AbstractResource;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ProcessOutput {

    private Map<String, AbstractResource> outputResources;

    public ProcessOutput() {
        this.outputResources = new HashMap<>();
    }

    public Map<String, AbstractResource> getOutputResources() {
        return outputResources;
    }

    public void setOutputResources(Map<String, AbstractResource> outputResources) {
        this.outputResources = outputResources;
    }

    public void addOutputResource(String outputID, AbstractResource outputResource) {
        setOutputResource(outputID, outputResource);
    }

    public void removeOutputResource(String outputID) {
        this.outputResources.remove(outputID);
    }

    public AbstractResource getOutputResource(String outputID) {
        return this.outputResources.get(outputID);
    }

    public void setOutputResource(String outputID, AbstractResource outputResource) {
        this.outputResources.put(outputID, outputResource);
    }

}
