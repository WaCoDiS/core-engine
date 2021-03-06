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

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ProcessOutputDescription {

    private String processIdentifier;
    private List<String> originDataEnvelopes;
    private final HashMap<String, String> outputParmeters;

    public ProcessOutputDescription() {
        this.outputParmeters = new HashMap<>();
    }

    /**
     * @param processIdentifier ID of the specific process (e.g. WPS jobID)
     */
    public ProcessOutputDescription(String processIdentifier) {
        this();
        this.processIdentifier = processIdentifier;
    }

    /**
     * @param processIdentifier ID of the specific process (e.g. WPS jobID)
     * @param outputParameters generic map of process outputs
     */
    public ProcessOutputDescription(String processIdentifier, HashMap<String, String> outputParameters) {
        this.processIdentifier = processIdentifier;
        this.outputParmeters = outputParameters;
    }

    /**
     *
     * @param processIdentifier ID of the specific process (e.g. WPS jobID)
     * @param outputParmeters generic map of process outputs
     * @param originDataEnvelopes IDs of DataEnvelopes that describe the data sets that were used to genereate to process output
     */
    public ProcessOutputDescription(String processIdentifier, HashMap<String, String> outputParmeters, List<String> originDataEnvelopes) {
        this.processIdentifier = processIdentifier;
        this.originDataEnvelopes = originDataEnvelopes;
        this.outputParmeters = outputParmeters;
    }

    /**
     * @return ID of the specific process (e.g. WPS jobID)
     */
    public String getProcessIdentifier() {
        return processIdentifier;
    }

    /**
     * @param processIdentifier ID of the specific process (e.g. WPS jobID)
     */
    public void setProcessIdentifier(String processIdentifier) {
        this.processIdentifier = processIdentifier;
    }

    /**
     * add output parameter
     *
     * @param key
     * @param value
     */
    public void addOutputParameter(String key, String value) {
        this.outputParmeters.put(key, value);
    }

    /**
     * get ouput paramter
     *
     * @param key
     * @return return value or null if no value for specific key available
     */
    public String getOutputParameter(String key) {
        return this.outputParmeters.get(key);
    }

    /**
     * remove output parameter
     *
     * @param key
     */
    public void removeOutputParameter(String key) {
        this.outputParmeters.remove(key);
    }

    /**
     * replace value for specific key
     *
     * @param key
     * @param value
     */
    public void replaceOutputParameter(String key, String value) {
        this.outputParmeters.replace(key, value);
    }

    /**
     * add output parameter only if no value for specific key available
     *
     * @param key
     * @param value
     */
    public void addOutputParameterIfAbsent(String key, String value) {
        this.outputParmeters.putIfAbsent(key, value);
    }

    /**
     * get all used parameter keys
     *
     * @return
     */
    public Set<String> getAllOutputParameterKeys() {
        return this.outputParmeters.keySet();
    }

    /**
     * @return IDs of DataEnvelopes that describe the data sets that were used to genereate to process output
     */
    public List<String> getOriginDataEnvelopes() {
        return originDataEnvelopes;
    }

    /**
     * @param originDataEnvelopes IDs of DataEnvelopes that describe the data sets that were used to genereate to process output
     */
    public void setOriginDataEnvelopes(List<String> originDataEnvelopes) {
        this.originDataEnvelopes = originDataEnvelopes;
    }

    public HashMap<String, String> getOutputParmeters() {
        return outputParmeters;
    }


    @Override
    public String toString() {
        return "ProcessOutputDescription{" + "processIdentifier=" + processIdentifier + ", originDataEnvelopes=" + originDataEnvelopes + ", outputParmeters=" + outputParmeters + '}';
    }
}
