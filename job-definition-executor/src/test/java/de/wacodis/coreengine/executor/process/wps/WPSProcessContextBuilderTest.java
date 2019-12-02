/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.configuration.WebProcessingServiceConfiguration;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.Schema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.util.ReflectionTestUtils;

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
        WacodisJobWrapper jobWrapper = new WacodisJobWrapper(jobDef, DateTime.now());
        AbstractResource resource = new AbstractResource();
        List<AbstractResource> resourceList = new ArrayList<>();
        resourceList.add(resource);
        jobWrapper.getInputs().get(0).setResource(resourceList);

        WPSProcessContextBuilder contextBuilder = new WPSProcessContextBuilder();
        ReflectionTestUtils.setField(contextBuilder, "wpsConfig", getWPSConfig()); //inject usually autowired config
        ProcessContext context = contextBuilder.buildProcessContext(jobWrapper);
        
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
        WacodisJobWrapper jobWrapper = new WacodisJobWrapper(jobDef, DateTime.now());

        WPSProcessContextBuilder contextBuilder = new WPSProcessContextBuilder();
        ProcessContext context = contextBuilder.buildProcessContext(jobWrapper);

        assertEquals(processID.toString(), context.getWacodisProcessID());
    }

    private WebProcessingServiceConfiguration getWPSConfig() {
        ExpectedProcessOutput productOutput = new ExpectedProcessOutput("PRODUCT", "image/geotiff");
        ExpectedProcessOutput metadataOutput = new ExpectedProcessOutput("METADATA", "text/json", false);
        WebProcessingServiceConfiguration config = new WebProcessingServiceConfiguration();

        config.setVersion("2.0.0");
        config.setUri("localhost:8080");
        config.setDefaultResourceMimeType("text/xml");
        config.setDefaultResourceSchema(Schema.GML3);
        config.setExpectedProcessOutputs(Arrays.asList(new ExpectedProcessOutput[]{metadataOutput, productOutput}));

        return config;
    }
}
