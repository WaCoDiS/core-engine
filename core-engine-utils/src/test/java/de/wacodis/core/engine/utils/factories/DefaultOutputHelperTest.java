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
