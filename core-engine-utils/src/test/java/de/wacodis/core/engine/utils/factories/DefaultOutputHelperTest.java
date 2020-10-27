/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.core.engine.utils.factories;

import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.core.models.WacodisJobDefinition;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Arne
 */
public class DefaultOutputHelperTest {

    public DefaultOutputHelperTest() {
    }

    @Test
    public void testGetExepectedOutputsForJob_NoOutputsSpecified() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();

        JobOutputHelper joh = new DefaultOutputHelper();
        List<JobOutputDescriptor> expectedOutputs = joh.getExepectedOutputsForJob(jobDef);

        //expect default outputs
        assertTrue(expectedOutputs.contains(JobOutputDescriptorBuilder.getDefaultProductOutput()));
        assertTrue(expectedOutputs.contains(JobOutputDescriptorBuilder.getDefaultMetadataOutput()));
    }

    @Test
    public void testGetExepectedOutputsForJob_OutputsSpecified() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();

        JobOutputDescriptor someOutput = new JobOutputDescriptor().identifier("abc123").mimeType("text/xml");
        jobDef.setOutputs(Arrays.asList(someOutput));

        JobOutputHelper joh = new DefaultOutputHelper();
        List<JobOutputDescriptor> expectedOutputs = joh.getExepectedOutputsForJob(jobDef);

        //expect default outputs
        assertTrue(expectedOutputs.contains(someOutput));
        //default metadata output should be added since it is not specified in job definition
        assertTrue(expectedOutputs.contains(JobOutputDescriptorBuilder.getDefaultMetadataOutput()));
    }

}
