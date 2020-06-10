/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class InputHelper {

    private final AbstractSubsetDefinition subsetDefinition;
    private final List<AbstractResource> resourceList ;


    public InputHelper(AbstractSubsetDefinition subsetDefinition) {
        this.subsetDefinition = subsetDefinition;
        this.resourceList = new ArrayList<>();
    }

    public InputHelper(AbstractSubsetDefinition subsetDefinition, List<AbstractResource> resource) {
        this(subsetDefinition);    
        this.resourceList.addAll(resource);
    }
  


    /**
     * @return unmodifable list of registered resources
     */
    public List<AbstractResource> getResource() {
        return Collections.unmodifiableList(this.resourceList);
    }

    /**
     * register resources, override current resources
     * @param resource 
     */
    public void setResource(List<AbstractResource> resource) {
        this.resourceList.clear();
        this.resourceList.addAll(resource);
    }

    /**
     * register resource, add to current resources
     * @param resource 
     */
    public void addResource(List<AbstractResource> resource) {
        this.resourceList.addAll(resource);
    }
    
    /**
     * @param resource
     * @return true if at least one item is removed from item list
     */
    public boolean removeResource(List<AbstractResource> resource){
        return this.resourceList.removeAll(resource);
    }
    
    public void clearResource(){
        this.resourceList.clear();
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
        return (this.resourceList.size() > 0);
    }

    /**
     * returns the ID of the wrapped SubsetDefinition
     *
     * @return
     */
    public String getSubsetDefinitionIdentifier() {
        return this.subsetDefinition.getIdentifier();
    }

    @Override
    public String toString() {
        return "InputHelper{" + "subsetDefinition=" + subsetDefinition + ", resourceAvailable=" + hasResource() + ", resource=" + resourceList + '}';
    }

}
