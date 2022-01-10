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
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecutionSettings;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.configuration.WebProcessingServiceConfiguration;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.Schema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WPSProcessContextBuilderTest {

    public WPSProcessContextBuilderTest() {
    }

    /**
     * Test of buildProcessContext method, of class WPSProcessContextBuilder.
     */
    @Test
    @DisplayName("assert all inputs are created")
    public void testBuildProcessContext_Inputs_Created() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        UUID processID = UUID.randomUUID();
        jobDef.setId(processID);
        AbstractSubsetDefinition input = new AbstractSubsetDefinition();
        input.setIdentifier("input1");
        jobDef.addInputsItem(input);
        WacodisJobWrapper jobWrapper = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), DateTime.now(), 0), jobDef);
        AbstractResource resource = new AbstractResource();
        List<AbstractResource> resourceList = new ArrayList<>();
        resourceList.add(resource);
        jobWrapper.getInputs().get(0).setResource(resourceList);

        WPSProcessContextBuilder contextBuilder = new WPSProcessContextBuilder(getWPSConfig());
        ProcessContext context = contextBuilder.buildProcessContext(jobWrapper, new HashMap<>());

        Set<String> contextInputs = context.getInputResources().keySet();
        List<String> jobWrapperInputs = jobWrapper.getInputs().stream().map(helper -> helper.getSubsetDefinitionIdentifier()).collect(Collectors.toList());
        assertTrue(context.getInputResources().keySet().containsAll(jobWrapperInputs));
    }

    @Test
    @DisplayName("assert process id is used for context")
    public void testBuildProcessContext_ProcessID() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        UUID processID = UUID.randomUUID();
        jobDef.setId(processID);
        WacodisJobWrapper jobWrapper = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), DateTime.now(), 0), jobDef);

        WPSProcessContextBuilder contextBuilder = new WPSProcessContextBuilder();
        ProcessContext context = contextBuilder.buildProcessContext(jobWrapper, new HashMap<>());

        assertEquals(processID.toString(), context.getWacodisProcessID());
    }

    @Test
    @DisplayName("assert additional parameters are carried over correctly")
    public void testBuildProcessContext_AdditionalParameters(){
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        UUID processID = UUID.randomUUID();
        jobDef.setId(processID);
        WacodisJobDefinitionExecutionSettings execSettings = new WacodisJobDefinitionExecutionSettings();
        jobDef.setExecutionSettings(execSettings);
        execSettings.setTimeoutMillies(Long.MAX_VALUE);
        WacodisJobWrapper jobWrapper = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), DateTime.now(), 0), jobDef);

        WPSProcessContextBuilder contextBuilder = new WPSProcessContextBuilder();
        
        Map<String, Object> additionalParams = new HashMap<>();
        //set timeout parameter
        additionalParams.put(WPSProcessContextBuilder.getTIMEOUT_MILLIES_KEY(),jobDef.getExecutionSettings().getTimeoutMillies());
        ProcessContext context = contextBuilder.buildProcessContext(jobWrapper, additionalParams);

        assertEquals(jobDef.getExecutionSettings().getTimeoutMillies(), context.getAdditionalParameter(WPSProcessContextBuilder.getTIMEOUT_MILLIES_KEY()));
    }

    private WebProcessingServiceConfiguration getWPSConfig() {
        WebProcessingServiceConfiguration config = new WebProcessingServiceConfiguration();

        config.setVersion("2.0.0");
        config.setUri("localhost:8080");
        config.setDefaultResourceMimeType("text/xml");
        config.setDefaultResourceSchema(Schema.GML3);

        return config;
    }
}
