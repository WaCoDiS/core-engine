/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * helper class to provide execution information for a process (inputs, expected
 * outputs)
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ProcessContext {

    private Map<String, List<ResourceDescription>> inputResources;
    private List<ExpectedProcessOutput> expectedOutputs;
    private String processID;

    public ProcessContext() {
        this.inputResources = new HashMap<>();
        this.expectedOutputs = new ArrayList<>();
    }

    public String getProcessID() {
        return processID;
    }

    public void setProcessID(String processID) {
        this.processID = processID;
    }

    public Map<String, List<ResourceDescription>> getInputResources() {
        return inputResources;
    }

    public void setInputResources(Map<String, List<ResourceDescription>> inputResources) {
        this.inputResources = inputResources;
    }

    public void addInputResource(String inputID, ResourceDescription inputResource) {
        setInputResource(inputID, inputResource);
    }

    public void removeInputResource(String inputID) {
        this.inputResources.remove(inputID);
    }

    public List<ResourceDescription> getInputResource(String inputID) {
        return this.inputResources.get(inputID);
    }

    public void setInputResource(String inputID, ResourceDescription inputResource) {
        List<ResourceDescription> currentInputResources = this.inputResources.getOrDefault(inputID, new ArrayList());
        currentInputResources.add(inputResource);
        this.inputResources.putIfAbsent(inputID, currentInputResources);
    }


    public List<ExpectedProcessOutput> getExpectedOutputs() {
        return expectedOutputs;
    }

    public void setExpectedOutputs(List<ExpectedProcessOutput> expectedOutputs) {
        this.expectedOutputs = expectedOutputs;
    }

    public void addExpectedOutput(ExpectedProcessOutput expectedOutput) {
        this.expectedOutputs.add(expectedOutput);
    }

    public void removeExpectedOutput(ExpectedProcessOutput expectedOutput) {
        this.expectedOutputs.remove(expectedOutput);
    }

}
