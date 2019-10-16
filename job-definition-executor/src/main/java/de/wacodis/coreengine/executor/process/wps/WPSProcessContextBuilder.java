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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  build ProcessContext for a Wacodis Job applicable for a WPSProcess
 * 
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WPSProcessContextBuilder implements ProcessContextBuilder {
    
    private static final Logger LOG = LoggerFactory.getLogger(WPSProcessContextBuilder.class);
    
    private static final String DEFAULT_MIME_TYPE = "text/xml";
    

    @Override
    public ProcessContext buildProcessContext(WacodisJobWrapper job, ExpectedProcessOutput... expectedProcessOutputs) {
        ProcessContext context = new ProcessContext();

        context.setProcessID(job.getJobDefinition().getId().toString());
        
        List<InputHelper> jobInputs = job.getInputs();
        
        for(InputHelper jobInput : jobInputs){ //set inputs
            LOG.info("Processing input: {}", jobInput);
            if(jobInput.hasResource()){
                String mimeType = DEFAULT_MIME_TYPE; //TODO handle mimeTypes
                
                for(AbstractResource resource : jobInput.getResource().get()){
                    LOG.info("Setting input resource: {} - {}, {}", jobInput.getSubsetDefinitionIdentifier(), resource, mimeType);
                    context.setInputResource(jobInput.getSubsetDefinitionIdentifier(), new ResourceDescription(resource, mimeType));
                }  
            }        
        }
   
        context.setExpectedOutputs(Arrays.asList(expectedProcessOutputs)); //set expected outputs

        return context;
    }
    
}
