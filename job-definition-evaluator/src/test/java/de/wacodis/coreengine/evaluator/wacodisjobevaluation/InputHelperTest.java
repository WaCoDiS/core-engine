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
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class InputHelperTest {

    public InputHelperTest() {
    }

    /**
     * Test of getResource method, of class InputHelper.
     */
    @Test
    public void testGetResource() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        AbstractResource resource = new AbstractResource();
        List<AbstractResource> resourceList = new ArrayList();
        resourceList.add(resource);
        InputHelper input = new InputHelper(subset, resourceList);

        assertTrue(input.getResource().containsAll(resourceList));
    }

    /**
     * Test of getResource method, of class InputHelper.
     */
    @Test
    public void testGetResource_empty() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        InputHelper input = new InputHelper(subset);

        assertTrue(input.getResource().isEmpty());
    }

    /**
     * Test of setResource method, of class InputHelper.
     */
    @Test
    public void testSetResource() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        AbstractResource resource = new AbstractResource();
        List<AbstractResource> resourceList = new ArrayList();
        resourceList.add(resource);
        InputHelper input = new InputHelper(subset, resourceList);

        assertTrue(input.getResource().containsAll(resourceList));
    }

    /**
     * Test of getSubsetDefinition method, of class InputHelper.
     */
    @Test
    public void testGetSubsetDefinition() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        InputHelper input = new InputHelper(subset);

        assertEquals(subset, input.getSubsetDefinition());
    }

    /**
     * Test of getKey method, of class InputHelper.
     */
    @Test
    public void testGetKey() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        InputHelper input = new InputHelper(subset);

        assertEquals(subset, input.getKey());
    }

    /**
     * Test of hasValue method, of class InputHelper.
     */
    @Test
    public void testHasResource_empty() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        InputHelper input = new InputHelper(subset);

        assertFalse(input.hasResource());
    }

    @Test
    public void testGetSubsetDefinitionID() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        InputHelper input = new InputHelper(subset);

        assertEquals(subset.getIdentifier(), input.getSubsetDefinitionIdentifier());
    }

    /**
     * Test of hasResource method, of class InputHelper.
     */
    @Test
    public void testHasResource() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        AbstractResource resource = new AbstractResource();
        List<AbstractResource> resourceList = new ArrayList();
        resourceList.add(resource);
        InputHelper input = new InputHelper(subset);

        input.setResource(resourceList);
        assertTrue(input.hasResource());

        input.removeResource(resourceList);
        assertFalse(input.hasResource());
    }

}
