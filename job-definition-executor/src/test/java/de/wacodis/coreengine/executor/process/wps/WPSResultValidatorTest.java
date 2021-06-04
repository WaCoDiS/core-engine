/*
 * Copyright 2018-2021 52°North Spatial Information Research GmbH
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

import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.coreengine.executor.process.ProcessContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.*;
import org.n52.geoprocessing.wps.client.model.execution.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Arne
 */
public class WPSResultValidatorTest {

    private Process wpsProcessDescription;
    private Result wpsResult;
    private ProcessContext processContext;

    private static Format txtJson;
    private static Format txtXML;
    private static Format txtPlain;

    public WPSResultValidatorTest() {
    }

    @BeforeEach
    public void setUp() {
        //init mock wps process description
        this.wpsProcessDescription = new Process();

        this.wpsProcessDescription.setAbstract("this is a test process");
        this.wpsProcessDescription.setId("testProcessIdentifier");
        this.wpsProcessDescription.setTitle("testWpsProcess");
        this.wpsProcessDescription.setReferenceSupported(true);
        this.wpsProcessDescription.setStatusSupported(true);

        //init inputs
        LiteralInputDescription literalInput = new LiteralInputDescription();
        literalInput.setAbstract("test literal input");
        literalInput.setId("literalInput");
        literalInput.setTitle("literalInput");
        literalInput.setFormats(Arrays.asList(new Format[]{txtJson, txtXML, txtPlain}));
        literalInput.setMinOccurs(1); //exactly once
        literalInput.setMaxOccurs(1);
        this.wpsProcessDescription.setInputs(Arrays.asList(new InputDescription[]{literalInput}));

        //init outputs
        LiteralOutputDescription literalOutput = new LiteralOutputDescription();
        literalOutput.setAbstract("test literal output");
        literalOutput.setId("literalOutput");
        literalOutput.setTitle("literalOutput");
        literalOutput.setFormats(Arrays.asList(new Format[]{txtJson, txtXML, txtPlain}));
        this.wpsProcessDescription.setOutputs(Arrays.asList(new OutputDescription[]{literalOutput}));

        //init mock wps result
        this.wpsResult = new Result();
        this.wpsResult.setJobId("testJobIdentifier");

        //init outputs
        Data literalResult = new Data();
        literalResult.setId("literalOutput");
        literalResult.setValue("test output");
        literalResult.setTitle("literalOutput");
        this.wpsResult.setOutputs(Arrays.asList(new Data[]{literalResult}));

        //init process context
        this.processContext = new ProcessContext();
        this.processContext.setWacodisProcessID("testProcess");
        JobOutputDescriptor expectedLiteralOutput = new JobOutputDescriptor().identifier("literalOutput").mimeType("text/json");
        this.processContext.setExpectedOutputs(Arrays.asList(new JobOutputDescriptor[]{expectedLiteralOutput}));
    }

    /**
     * Test of validateProcessOutput method, of class WPSResultValidator.
     */
    @Test
    @DisplayName("validateProcessOutput valid")
    public void testValidateProcessOutput() {
        System.out.println("validateProcessOutput valid");

        WPSResultValidator instance = new WPSResultValidator(this.wpsProcessDescription);
        Optional<Throwable> result = instance.validateProcessOutput(this.wpsResult, this.processContext);

        assertEquals(Optional.empty(), result);
    }

    /**
     * Test of validateProcessOutput method, of class WPSResultValidator.
     */
    @Test
    @DisplayName("validateProcessOutput invalid result missing output")
    public void testValidateProcessOutput_ResultMissingOutput() {
        System.out.println("validateProcessOutput invalid result missing output");

        this.wpsResult.setOutputs(new ArrayList<>());

        WPSResultValidator instance = new WPSResultValidator(this.wpsProcessDescription);
        Optional<Throwable> result = instance.validateProcessOutput(this.wpsResult, this.processContext);

        assertTrue(result.isPresent());
        System.out.println("validation exception:");
        System.out.println(result.get());
    }

    /**
     * Test of validateProcessOutput method, of class WPSResultValidator.
     */
    @Test
    @DisplayName("validateProcessOutput invalid result missing output")
    public void testValidateProcessOutput_ResultAdditionalOutput() {
        System.out.println("validateProcessOutput invalid result additional output");

        JobOutputDescriptor additionalExpectedOutput = new JobOutputDescriptor().identifier("additionalOutput").mimeType("text/plain");
        JobOutputDescriptor expectedLiteralOutput = new JobOutputDescriptor().identifier("literalOutput").mimeType("text/json");
        this.processContext.setExpectedOutputs(Arrays.asList(expectedLiteralOutput, additionalExpectedOutput));
 
        WPSResultValidator instance = new WPSResultValidator(this.wpsProcessDescription);
        Optional<Throwable> result = instance.validateProcessOutput(this.wpsResult, this.processContext);

        assertTrue(result.isPresent());
        System.out.println("validation exception:");
        System.out.println(result.get());
    }

}
