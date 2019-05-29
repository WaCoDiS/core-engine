/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ProcessOutputDescription {
    
    private String processIdentifier;
    private Set<String> outputIdentifiers;

    public ProcessOutputDescription() {
        this.outputIdentifiers = new HashSet<>();
    }

    /**
     * @param processIdentifier ID of the described process (e.g. WPS jobID)
     * @param outputIdentifiers IDs of outputs produced by the described process
     */
    public ProcessOutputDescription(String processIdentifier, Set outputIdentifiers) {
        this.processIdentifier = processIdentifier;
        this.outputIdentifiers = outputIdentifiers;
    }

    /**
     * @return ID of the described process (e.g. WPS jobID)
     */
    public String getProcessIdentifier() {
        return processIdentifier;
    }

    /**
     * @param processIdentifier ID of the described process (e.g. WPS jobID)
     */
    public void setProcessIdentifier(String processIdentifier) {
        this.processIdentifier = processIdentifier;
    }

    /**
     * @return IDs of outputs produced by the described process
     */
    public Set<String> getOutputIdentifiers() {
        return outputIdentifiers;
    }

    /**
     * @param outputIdentifiers IDs of outputs produced by the described process
     */
    public void setOutputIdentifiers(Set<String> outputIdentifiers) {
        this.outputIdentifiers = outputIdentifiers;
    }
    
    /**
     * @param outputIdentifier
     * @return true if this set did not already contain outputIdentifier
     */
    public boolean addOutputIdentifier(String outputIdentifier){
        return this.outputIdentifiers.add(outputIdentifier);
    }
    
    /**
     * @param outputIdentifier
     * @return true if the set contained outputIdentifier 
     */
    public boolean removeOutputIdentifier(String outputIdentifier){
        return outputIdentifiers.remove(outputIdentifier);
    }
    
}
