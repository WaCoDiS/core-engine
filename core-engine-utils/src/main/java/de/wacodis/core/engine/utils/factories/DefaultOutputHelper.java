/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.core.engine.utils.factories;

import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.core.models.WacodisJobDefinition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Arne
 */
public class DefaultOutputHelper implements JobOutputHelper{

    /**
     * returns default outputs if no outputs specified in job definition, else returns specified outputs but adds expected default metadata output (if not already specified)
     * @param jobDef
     * @return 
     */
    @Override
    public List<JobOutputDescriptor> getExepectedOutputsForJob(WacodisJobDefinition jobDef) {
        List<JobOutputDescriptor> jobOutputs = jobDef.getOutputs();
        
        //if not specified in job definition return default outputs
        if(jobOutputs == null || jobOutputs.isEmpty()){
            return Arrays.asList(JobOutputDescriptorBuilder.getDefaultOutputs());
        }else{
            //create copy to remain original job definition
            List<JobOutputDescriptor> expectedJobOutputs = new ArrayList<>(jobOutputs);
            
            JobOutputDescriptor defaultMetadataOuput = JobOutputDescriptorBuilder.getDefaultMetadataOutput();
            String defaultMetadataOutputID = defaultMetadataOuput.getIdentifier();
            
            //see if default metadata output is already specified
            boolean isMetadataOutputSpecified = jobOutputs.stream().anyMatch( jod -> jod.getIdentifier().equals(defaultMetadataOutputID));
            //if not add metadata output since it is always expected
            if(!isMetadataOutputSpecified){
                expectedJobOutputs.add(defaultMetadataOuput);
            }
            
            return expectedJobOutputs;
        }
    }
    
}
