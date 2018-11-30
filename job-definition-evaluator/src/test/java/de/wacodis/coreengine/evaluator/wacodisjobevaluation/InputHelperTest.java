/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
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
        InputHelper input = new InputHelper(subset, resource);

        assertEquals(resource, input.getResource().get());
    }

    /**
     * Test of getResource method, of class InputHelper.
     */
    @Test
    public void testGetResource_empty() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        InputHelper input = new InputHelper(subset);

        assertFalse(input.getResource().isPresent());
    }

    /**
     * Test of setResource method, of class InputHelper.
     */
    @Test
    public void testSetResource() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        AbstractResource resource = new AbstractResource();
        InputHelper input = new InputHelper(subset);

        input.setResource(resource);
        assertEquals(resource, input.getResource().get());
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

    /**
     * Test of hasResource method, of class InputHelper.
     */
    @Test
    public void testHasResource() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        AbstractResource resource = new AbstractResource();
        InputHelper input = new InputHelper(subset);

        input.setResource(resource);
        assertTrue(input.hasResource());
    }

    @Test
    public void testSetResourceAvailable() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        InputHelper input = new InputHelper(subset);

        input.setResourceAvailable(true);
        assertTrue(input.isResourceAvailable());
    }

    @Test
    public void testIsResourceAvailable() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        InputHelper input = new InputHelper(subset);

        input.setResourceAvailable(false);
        assertFalse(input.isResourceAvailable());
    }
}
