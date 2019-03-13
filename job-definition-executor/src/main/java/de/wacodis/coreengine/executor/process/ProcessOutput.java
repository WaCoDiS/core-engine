/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ProcessOutput {

    private Map<String, List<ResourceDescription>> outputResources;

    public ProcessOutput() {
        this.outputResources = new HashMap<>();
    }

    public Map<String, List<ResourceDescription>> getOutputResources() {
        return outputResources;
    }

    public void setOutputResources(Map<String, List<ResourceDescription>> outputResources) {
        this.outputResources = outputResources;
    }

    public void addOutputResource(String outputID, ResourceDescription outputResource) {
        setOutputResource(outputID, outputResource);
    }

    public void removeOutputResource(String outputID) {
        this.outputResources.remove(outputID);
    }

    public List<ResourceDescription> getOutputResource(String outputID) {
        return this.outputResources.get(outputID);
    }

    public void setOutputResource(String outputID, ResourceDescription outputResource) {
        List<ResourceDescription> currentOutputResources = this.outputResources.getOrDefault(outputID, new ArrayList());
        currentOutputResources.add(outputResource);
        this.outputResources.put(outputID, currentOutputResources);
    }

}
