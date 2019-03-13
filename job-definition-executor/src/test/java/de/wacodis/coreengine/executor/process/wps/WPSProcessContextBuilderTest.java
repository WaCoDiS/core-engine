/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.CatalogueSubsetDefinition;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.executor.process.ProcessContext;
import java.util.ArrayList;
import java.util.List;
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
        WacodisJobWrapper jobWrapper = new WacodisJobWrapper(jobDef, DateTime.now());
        AbstractResource resource = new AbstractResource();
        List<AbstractResource> resourceList = new ArrayList<>();
        resourceList.add(resource);
        jobWrapper.getInputs().get(0).setResource(resourceList);

        WPSProcessContextBuilder contextBuilder = new WPSProcessContextBuilder();
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

        assertEquals(processID.toString(), context.getProcessID());
    }

}
