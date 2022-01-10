/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
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
