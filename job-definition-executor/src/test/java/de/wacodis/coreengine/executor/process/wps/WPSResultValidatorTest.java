/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.ProcessContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import javafx.scene.chart.PieChart;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.n52.geoprocessing.wps.client.model.Format;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralOutputDescription;
import org.n52.geoprocessing.wps.client.model.OutputDescription;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.Result;
import org.n52.geoprocessing.wps.client.model.execution.Data;

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
        ExpectedProcessOutput expectedLiteralOutput = new ExpectedProcessOutput("literalOutput", "text/json");
        this.processContext.setExpectedOutputs(Arrays.asList(new ExpectedProcessOutput[]{expectedLiteralOutput}));
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

        ExpectedProcessOutput additionalExpectedOutput = new ExpectedProcessOutput("additionalOutput", "text/plain");
        ExpectedProcessOutput expectedLiteralOutput = new ExpectedProcessOutput("literalOutput", "text/json");
        this.processContext.setExpectedOutputs(Arrays.asList(new ExpectedProcessOutput[]{expectedLiteralOutput, additionalExpectedOutput}));

        WPSResultValidator instance = new WPSResultValidator(this.wpsProcessDescription);
        Optional<Throwable> result = instance.validateProcessOutput(this.wpsResult, this.processContext);

        assertTrue(result.isPresent());
        System.out.println("validation exception:");
        System.out.println(result.get());
    }

}
