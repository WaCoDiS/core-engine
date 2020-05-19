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
import de.wacodis.coreengine.executor.configuration.WebProcessingServiceConfiguration;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.ResourceDescription;
import de.wacodis.coreengine.executor.process.Schema;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * build ProcessContext for a Wacodis Job applicable for a WPSProcess
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WPSProcessContextBuilder implements ProcessContextBuilder {
    
    private static final Logger LOG = LoggerFactory.getLogger(WPSProcessContextBuilder.class);
    
    private static final String DEFAULT_MIME_TYPE = "text/xml";
    private static final Schema DEFAULT_SCHEMA = Schema.GML3;
    
    private WebProcessingServiceConfiguration wpsConfig;
    
    public WPSProcessContextBuilder(WebProcessingServiceConfiguration wpsConfig) {
        this.wpsConfig = wpsConfig;
    }
    
    public WPSProcessContextBuilder() {
    }
    
    public WebProcessingServiceConfiguration getWpsConfig() {
        return wpsConfig;
    }
    
    public void setWpsConfig(WebProcessingServiceConfiguration wpsConfig) {
        this.wpsConfig = wpsConfig;
    }
    
    @Override
    public ProcessContext buildProcessContext(WacodisJobWrapper job, ExpectedProcessOutput... expectedProcessOutputs) {        
        ProcessContext context = new ProcessContext();
        
        context.setWacodisProcessID(job.getJobDefinition().getId().toString());
        
        List<InputHelper> jobInputs = job.getInputs();
        
        for (InputHelper jobInput : jobInputs) { //set inputs
            if (jobInput.hasResource()) {
                String mimeType = getDefaultMimeType(); //mime type currently the same for every resource, no mime type information available
                Schema schema = getDefaultSchema(); //schema type currently the same for every resource, no schema information available
                
                for (AbstractResource resource : jobInput.getResource().get()) {
                    context.setInputResource(jobInput.getSubsetDefinitionIdentifier(), new ResourceDescription(resource, mimeType, schema));
                }                
            }            
        }
        
        context.setExpectedOutputs(Arrays.asList(expectedProcessOutputs)); //set expected outputs

        LOG.info("built wps process context ({}) for wacodis job {}", this.getClass().getSimpleName(), job.getJobDefinition().getId());
        LOG.debug("wps process context for wacodis job {}:{}{}", job.getJobDefinition().getId(), System.lineSeparator(), context);
        
        return context;
    }
    
    private String getDefaultMimeType() {
        return (this.wpsConfig != null && this.wpsConfig.getDefaultResourceMimeType() != null && !this.wpsConfig.getDefaultResourceMimeType().isEmpty()) ? this.wpsConfig.getDefaultResourceMimeType() : DEFAULT_MIME_TYPE;
    }
    
    private Schema getDefaultSchema() {
        return (this.wpsConfig != null && this.wpsConfig.getDefaultResourceSchema() != null) ? this.wpsConfig.getDefaultResourceSchema() : DEFAULT_SCHEMA;
    }
}
