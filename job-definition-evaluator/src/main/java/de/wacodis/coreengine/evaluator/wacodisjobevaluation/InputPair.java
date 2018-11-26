/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class InputPair {
    
    private final AbstractSubsetDefinition subsetDefinition;
    private AbstractResource resource;

    public InputPair(AbstractSubsetDefinition subsetDefinition, AbstractResource resource) {
        this.subsetDefinition = subsetDefinition;
        this.resource = resource;
    }
    
    public InputPair(AbstractSubsetDefinition subsetDefinition){
        this.subsetDefinition = subsetDefinition;
        this.resource = null;
    }

    public AbstractResource getResource() {
        return resource;
    }

    public void setResource(AbstractResource resource) {
        this.resource = resource;
    }

    public AbstractSubsetDefinition getSubsetDefinition() {
        return subsetDefinition;
    }
    
    public AbstractSubsetDefinition getKey(){
        return getSubsetDefinition();
    }
    
    public AbstractResource getValue(){
        return getResource();
    }
    
    public void setValue(AbstractResource resource){
        setResource(resource);
    }
    
    public boolean hasValue(){
        return resource != null;
    }
    
    public boolean hasResource(){
        return hasValue();
    }
}
