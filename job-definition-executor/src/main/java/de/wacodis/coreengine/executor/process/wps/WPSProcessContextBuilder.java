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
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessContextBuilder;
import java.util.List;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.executor.configuration.WebProcessingServiceConfiguration;
import de.wacodis.coreengine.executor.process.ResourceDescription;
import de.wacodis.coreengine.executor.process.Schema;
import java.util.Arrays;
import java.util.Map;
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
    private static final String TIMEOUT_MILLIES_KEY = "timeout_millies";

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

    public static String getDEFAULT_MIME_TYPE() {
        return DEFAULT_MIME_TYPE;
    }

    public static Schema getDEFAULT_SCHEMA() {
        return DEFAULT_SCHEMA;
    }

    public static String getTIMEOUT_MILLIES_KEY() {
        return TIMEOUT_MILLIES_KEY;
    }

    @Override
    public ProcessContext buildProcessContext(WacodisJobWrapper job, Map<String, Object> additionalParameters, JobOutputDescriptor... expectedProcessOutputs) {
        ProcessContext context = new ProcessContext();

        context.setWacodisProcessID(job.getJobDefinition().getId().toString());

        List<InputHelper> jobInputs = job.getInputs();

        for (InputHelper jobInput : jobInputs) { //set inputs
            if (jobInput.hasResource()) {
                String mimeType = getDefaultMimeType(); //mime type currently the same for every resource, no mime type information available
                Schema schema = getDefaultSchema(); //schema type currently the same for every resource, no schema information available

                for (AbstractResource resource : jobInput.getResource()) {
                    context.setInputResource(jobInput.getSubsetDefinitionIdentifier(), new ResourceDescription(resource, mimeType, schema));
                }
            }
        }

        context.setExpectedOutputs(Arrays.asList(expectedProcessOutputs)); //set expected outputs
        context.setAdditionalParameters(additionalParameters); //carry over parameters

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
