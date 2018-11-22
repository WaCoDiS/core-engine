/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.CatalogueSubsetDefinition;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobWrapperTest {
    
    public WacodisJobWrapperTest() {
    }

    /**
     * Test of getInputs method, of class WacodisJobWrapper.
     */
    @Test
    public void testGetInputs() {
        WacodisJobDefinition jobDef = getJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, new DateTime());
        
        assertEquals(2, job.getInputs().size());
    }
    
    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void testGetInputsUnmodifiable(){
        WacodisJobDefinition jobDef = getJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, new DateTime());
        
        job.getInputs().add(new MutablePair<>(new AbstractSubsetDefinition(), Boolean.FALSE)); //should fail
    }

    /**
     * Test of isExecutable method, of class WacodisJobWrapper.
     */
    @Test
    public void testIsExecutable() {
        WacodisJobWrapper job = new WacodisJobWrapper(getJobDefinition(), new DateTime());
        assertFalse(job.isExecutable());
        
        job.getInputs().get(0).setValue(Boolean.TRUE);      
        assertFalse(job.isExecutable());
        
        job.getInputs().get(1).setValue(Boolean.TRUE);
        assertTrue(job.isExecutable());
        
        job.getInputs().get(1).setValue(Boolean.FALSE);    
        assertFalse(job.isExecutable());
        
        job.getInputs().get(0).setValue(Boolean.FALSE);
        assertFalse(job.isExecutable());
    }
    
    @Test
    /**
     * tests if every input is marked as unavailable initially
     */
    public void testSubsetsInitialized(){
        WacodisJobWrapper job = new WacodisJobWrapper(getJobDefinition(), new DateTime());
        
        for(Pair<AbstractSubsetDefinition, Boolean> input : job.getInputs()){
            assertFalse(input.getValue());
        }
    }
    
    
    private WacodisJobDefinition getJobDefinition(){
        AbstractSubsetDefinition subset1 = new CatalogueSubsetDefinition();
        subset1.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.CATALOGUESUBSETDEFINITION);
        AbstractSubsetDefinition subset2 = new CopernicusSubsetDefinition();
        subset2.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.COPERNICUSSUBSETDEFINITION);       
        List<AbstractSubsetDefinition> subsets = new ArrayList<>();
        subsets.add(subset1);
        subsets.add(subset2);
               
        WacodisJobDefinition jobDefinition = new WacodisJobDefinition();
        jobDefinition.setInputs(subsets);
        
        return jobDefinition;
    }
    
}
