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
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.JobOutputDescriptor;
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
    private List<JobOutputDescriptor> expectedOutputs;
    private String wacodisProcessID;
    private Map<String, Object> additionalParameters;

    public ProcessContext() {
        this.inputResources = new HashMap<>();
        this.expectedOutputs = new ArrayList<>();
        this.additionalParameters = new HashMap<>();
    }

    public String getWacodisProcessID() {
        return wacodisProcessID;
    }

    public void setWacodisProcessID(String wacodisProcessID) {
        this.wacodisProcessID = wacodisProcessID;
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

    public List<JobOutputDescriptor> getExpectedOutputs() {
        return expectedOutputs;
    }

    public void setExpectedOutputs(List<JobOutputDescriptor> expectedOutputs) {
        this.expectedOutputs = expectedOutputs;
    }

    public void addExpectedOutput(JobOutputDescriptor expectedOutput) {
        this.expectedOutputs.add(expectedOutput);
    }

    public void removeExpectedOutput(JobOutputDescriptor expectedOutput) {
        this.expectedOutputs.remove(expectedOutput);
    }

    public Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(Map<String, Object> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    public void addAdditionalParameter(String key, Object value) {
        this.additionalParameters.put(key, value);
    }

    public Object getAdditionalParameter(String key) {
        return this.additionalParameters.get(key);
    }

    @Override
    public String toString() {
        return "ProcessContext{" + "inputResources=" + inputResources + ", expectedOutputs=" + expectedOutputs + ", wacodisProcessID=" + wacodisProcessID + ", additionalParameters=" + additionalParameters + '}';
    }
}
