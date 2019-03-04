/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import java.util.List;
import java.util.Optional;

/**
 *
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class InputHelper {

    private final AbstractSubsetDefinition subsetDefinition;
    private boolean resourceAvailable;
    private Optional<List<AbstractResource>> resource;

    public InputHelper(AbstractSubsetDefinition subsetDefinition, Optional<List<AbstractResource>> resource) {
        this.subsetDefinition = subsetDefinition;
        this.resource = resource;
        this.resourceAvailable = false;
    }

    public InputHelper(AbstractSubsetDefinition subsetDefinition, List<AbstractResource> resource) {
        this(subsetDefinition, Optional.ofNullable(resource)) ;
    }

    /**
     * creates instance with empty resource
     *
     * @param subsetDefinition
     */
    public InputHelper(AbstractSubsetDefinition subsetDefinition) {
        this(subsetDefinition, Optional.empty());
    }

    public Optional<List<AbstractResource>> getResource() {
        return resource;
    }

    public void setResource(List<AbstractResource> resource) {
        setResource(Optional.ofNullable(resource));
    }

    public void setResource(Optional<List<AbstractResource>> resource) {
        this.resource = resource;
    }

    public AbstractSubsetDefinition getSubsetDefinition() {
        return subsetDefinition;
    }

    /**
     * alias for getSubsetDefinition
     *
     * @return
     */
    public AbstractSubsetDefinition getKey() {
        return getSubsetDefinition();
    }

    /**
     * returns true if resource is not null
     *
     * @return
     */
    public boolean hasResource() {
        return resource.isPresent();
    }

    /**
     * true if a resource matching the SubsetDefinition is available
     * @return 
     */
    public boolean isResourceAvailable() {
        return resourceAvailable;
    }

    /**
     * mark if a resource matching the SubsetDefinition is available
     * @param resourceAvailable
     */
    public void setResourceAvailable(boolean resourceAvailable) {
        this.resourceAvailable = resourceAvailable;
    }
    
    /**
     * returns the ID of the wrapped SubsetDefinition
     * @return 
     */
    public String getSubsetDefinitionIdentifier(){
        return this.subsetDefinition.getIdentifier();
    }
}
