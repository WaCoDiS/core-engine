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
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

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
    
    @Test
    @DisplayName("check GetInputs returns umodifiable collection")
    public void testGetInputsUnmodifiable(){
        WacodisJobDefinition jobDef = getJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, new DateTime());
        
        //exception expected because collection should be unmodifiable
        assertThrows(java.lang.UnsupportedOperationException.class, () -> {
             job.getInputs().add(new InputHelper(new AbstractSubsetDefinition())); 
        }); 
    }

    /**
     * Test of isExecutable method, of class WacodisJobWrapper.
     */
    @Test
    public void testIsExecutable() {
        WacodisJobWrapper job = new WacodisJobWrapper(getJobDefinition(), new DateTime());
        assertFalse(job.isExecutable());
        
        job.getInputs().get(0).setResourceAvailable(true);      
        assertFalse(job.isExecutable());
        
        job.getInputs().get(1).setResourceAvailable(true);
        assertTrue(job.isExecutable());
        
        job.getInputs().get(1).setResourceAvailable(false);    
        assertFalse(job.isExecutable());
        
        job.getInputs().get(0).setResourceAvailable(false);
        assertFalse(job.isExecutable());
    }
    
    
    /**
     * tests if every input is marked as unavailable initially
     */
    @Test
    @DisplayName("check every resource is unavailable initially")
    public void testSubsetsInitialized(){
        WacodisJobWrapper job = new WacodisJobWrapper(getJobDefinition(), new DateTime());
        
        for(InputHelper pair : job.getInputs()){
            assertFalse(pair.hasResource());
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
