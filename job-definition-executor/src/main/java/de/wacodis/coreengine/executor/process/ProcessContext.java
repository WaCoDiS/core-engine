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
public class ProcessContext {
    
    private Map<String, AbstractResource> processResources;

    public ProcessContext() {
        this.processResources = new HashMap<>();
    }

    public Map<String, AbstractResource> getProcessResources() {
        return processResources;
    }

    public void setProcessResources(Map<String, AbstractResource> processResources) {
        this.processResources = processResources;
    }
    
    public void addProcessResource(String inputID, AbstractResource inputResource){
        setProcessResource(inputID, inputResource);
    }
    
    public void removeProcessResource(String inputID){
        this.processResources.remove(inputID);
    }
    
    public AbstractResource getProcessResource(String inputID){
        return this.processResources.get(inputID);
    }
    
    public void setProcessResource(String inputID, AbstractResource inputResource){
        this.processResources.put(inputID, inputResource);
    }
   
}
