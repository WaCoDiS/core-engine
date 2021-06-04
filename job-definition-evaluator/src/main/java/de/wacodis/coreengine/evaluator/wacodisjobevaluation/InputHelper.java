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
        return (this.resourceList != null && this.resourceList.size() > 0);
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
