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
public class InputPairTest {

    public InputPairTest() {
    }

    /**
     * Test of getResource method, of class InputPair.
     */
    @Test
    public void testGetResource() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        AbstractResource resource = new AbstractResource();
        InputPair pair = new InputPair(subset, resource);

        assertEquals(resource, pair.getResource());
    }

    /**
     * Test of getResource method, of class InputPair.
     */
    @Test
    public void testGetResource_null() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        InputPair pair = new InputPair(subset);

        assertNull(pair.getResource());
    }

    /**
     * Test of setResource method, of class InputPair.
     */
    @Test
    public void testSetResource() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        AbstractResource resource = new AbstractResource();
        InputPair pair = new InputPair(subset);

        pair.setResource(resource);
        assertEquals(resource, pair.getResource());
    }

    /**
     * Test of getSubsetDefinition method, of class InputPair.
     */
    @Test
    public void testGetSubsetDefinition() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        InputPair pair = new InputPair(subset);

        assertEquals(subset, pair.getSubsetDefinition());
    }

    /**
     * Test of getKey method, of class InputPair.
     */
    @Test
    public void testGetKey() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        InputPair pair = new InputPair(subset);

        assertEquals(subset, pair.getSubsetDefinition());
    }

    /**
     * Test of getValue method, of class InputPair.
     */
    @Test
    public void testGetValue() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        AbstractResource resource = new AbstractResource();
        InputPair pair = new InputPair(subset, resource);

        assertEquals(resource, pair.getResource());
    }

    /**
     * Test of setValue method, of class InputPair.
     */
    @Test
    public void testSetValue() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        AbstractResource resource = new AbstractResource();
        InputPair pair = new InputPair(subset);

        pair.setResource(resource);
        assertEquals(resource, pair.getResource());
    }

    /**
     * Test of hasValue method, of class InputPair.
     */
    @Test
    public void testHasValue() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        AbstractResource resource = new AbstractResource();
        InputPair pair = new InputPair(subset, resource);

        assertTrue(pair.hasValue());
    }

    /**
     * Test of hasValue method, of class InputPair.
     */
    @Test
    public void testHasValue_null() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        InputPair pair = new InputPair(subset);

        assertFalse(pair.hasValue());
    }

    /**
     * Test of hasResource method, of class InputPair.
     */
    @Test
    public void testHasResource() {
        AbstractSubsetDefinition subset = new AbstractSubsetDefinition();
        AbstractResource resource = new AbstractResource();
        InputPair pair = new InputPair(subset);

        pair.setResource(resource);
        assertTrue(pair.hasResource());
    }

}
