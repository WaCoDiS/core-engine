/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessContextBuilder;
import java.util.List;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.ResourceDescription;
import java.util.Arrays;

/**
 *  build ProcessContext for a Wacodis Job applicable for a WPSProcess
 * 
 * TODO: Handle MimeType, handle outputs
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WPSProcessContextBuilder implements ProcessContextBuilder {
    
    private static final String DEFAULT_MIME_TYPE = "text/xml";

    @Override
    public ProcessContext buildProcessContext(WacodisJobWrapper job, String... expectedProcessOutputIdentifiers) {
        ProcessContext context = new ProcessContext();

        context.setProcessID(job.getJobDefinition().getId().toString());
        
        List<InputHelper> jobInputs = job.getInputs();
        
        for(InputHelper jobInput : jobInputs){ //set inputs
            if(jobInput.hasResource()){
                String mimeType = DEFAULT_MIME_TYPE; //TODO handle mimeTypes
                
                for(AbstractResource resource : jobInput.getResource().get()){
                    context.setInputResource(jobInput.getSubsetDefinitionIdentifier(), new ResourceDescription(resource, mimeType));
                }  
            }        
        }
   
        context.setExpectedOutputs(Arrays.asList(expectedProcessOutputIdentifiers)); //set expected outputs

        return context;
    }
}
