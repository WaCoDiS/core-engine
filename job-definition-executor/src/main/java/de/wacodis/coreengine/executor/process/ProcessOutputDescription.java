/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ProcessOutputDescription {
    
    private String processIdentifier;
    private final HashMap<String, String> outputParmeters;

    public ProcessOutputDescription() {
        this.outputParmeters = new HashMap<>();
    }
    
    /**
     * @param processIdentifier ID of the specific process (e.g. WPS jobID)
     */
    public ProcessOutputDescription(String processIdentifier){
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
     * @param key
     * @param value 
     */
    public void addOutputParameter(String key, String value){
        this.outputParmeters.put(key, value);
    }
    
    /**
     * get ouput paramter
     * @param key
     * @return return value or null if no value for specific key available
     */
    public String getOutputParameter(String key){
        return this.outputParmeters.get(key);
    }
    
    /**
     * remove output parameter
     * @param key 
     */
    public void removeOutputParameter(String key){
        this.outputParmeters.remove(key);
    }
    
    /**
     * replace value for specific key
     * @param key
     * @param value 
     */
    public void replaceOutputParameter(String key, String value){
        this.outputParmeters.replace(key, value);
    }
    
    /**
     * add output parameter only if no value for specific key available
     * @param key
     * @param value 
     */
    public void addOutputParameterIfAbsent(String key, String value){
        this.outputParmeters.putIfAbsent(key, value);
    } 
    
    /**
     * get all used parameter keys
     * @return 
     */
    public Set<String> getAllOutputParameterKeys(){
        return this.outputParmeters.keySet();
    }

    @Override
    public String toString() {
        return "ProcessOutputDescription{" + "processIdentifier=" + processIdentifier + ", outputParmeters=" + outputParmeters + '}';
    }
}
