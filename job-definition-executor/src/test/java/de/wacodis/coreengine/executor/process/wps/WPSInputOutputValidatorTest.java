/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.core.models.extension.staticresource.StaticDummyResource;
import de.wacodis.coreengine.executor.process.ResourceDescription;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

/**
 *
 * @author Arne
 */
public class WPSInputOutputValidatorTest {

    private Process wpsProcessDescription;

    private static Format txtJson;
    private static Format txtXML;
    private static Format txtPlain;

    public WPSInputOutputValidatorTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        //init mime types
        txtJson = new Format();
        txtJson.setMimeType("text/json");
        txtXML = new Format();
        txtXML.setMimeType("text/xml");
        txtPlain = new Format();
        txtPlain.setMimeType("text/plain");
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
    }

    /**
     * Test of validateExpectedOutputs method, of class WPSInputOutputValidator.
     */
    @Test
    @DisplayName("validateExpectedOutputs valid")
    public void testValidateExpectedOutputs() {
        System.out.println("validateExpectedOutputs valid");
        JobOutputDescriptor additionalExpectedOutput = new JobOutputDescriptor().identifier("literalOutput").mimeType("text/json");
        List<JobOutputDescriptor> expectedOutputs = Arrays.asList(additionalExpectedOutput);
        WPSInputOutputValidator instance = new WPSInputOutputValidator(this.wpsProcessDescription);
        Optional<Throwable> result = instance.validateExpectedOutputs(expectedOutputs);

        assertEquals(Optional.empty(), result); //empty if valid
    }

    /**
     * Test of validateExpectedOutputs method, of class WPSInputOutputValidator.
     */
    @Test
    @DisplayName("validateExpectedOutputs invalid unsupported output")
    public void testValidateExpectedOutputs_UnsupportedOutput() {
        System.out.println("validateExpectedOutputs invalid unsupported output");
        JobOutputDescriptor expectedLiteralOutput = new JobOutputDescriptor().identifier("unsupportedOnput").mimeType("text/json");// unsupported output
        List<JobOutputDescriptor> expectedOutputs = Arrays.asList(expectedLiteralOutput);
        WPSInputOutputValidator instance = new WPSInputOutputValidator(this.wpsProcessDescription);
        Optional<Throwable> result = instance.validateExpectedOutputs(expectedOutputs);

        assertTrue(result.isPresent());
        System.out.println("validation exception:");
        System.out.println(result.get());
    }

    /**
     * Test of validateExpectedOutputs method, of class WPSInputOutputValidator.
     */
    @Test
    @DisplayName("validateExpectedOutputs invalid unsupported mime type")
    public void testValidateExpectedOutputs_UnsupportedMimeType() {
        System.out.println("validateExpectedOutputs invalid unsupported mime type");
        JobOutputDescriptor expectedLiteralOutput = new JobOutputDescriptor().identifier("literalOutput").mimeType("image/geotiff"); //unsupported mime type for literalOutput
        List<JobOutputDescriptor> expectedOutputs = Arrays.asList(expectedLiteralOutput);
        WPSInputOutputValidator instance = new WPSInputOutputValidator(this.wpsProcessDescription);
        Optional<Throwable> result = instance.validateExpectedOutputs(expectedOutputs);

        assertTrue(result.isPresent());
        System.out.println("validation exception:");
        System.out.println(result.get());
    }

    /**
     * Test of validateProvidedInputResources method, of class
     * WPSInputOutputValidator.
     */
    @Test
    @DisplayName("validateProvidedInputResources valid")
    public void testValidateProvidedInputResources() {
        System.out.println("validateProvidedInputResources valid");
        //set provided inputs
        Map<String, List<ResourceDescription>> providedInputResources = new HashMap<>();
        StaticDummyResource resource = new StaticDummyResource();
        resource.setValue("test value");
        ResourceDescription providedInput = new ResourceDescription(resource, "text/plain");
        providedInputResources.put("literalInput", Arrays.asList(new ResourceDescription[]{providedInput}));

        WPSInputOutputValidator instance = new WPSInputOutputValidator(this.wpsProcessDescription);
        Optional<Throwable> result = instance.validateProvidedInputResources(providedInputResources);

        assertEquals(Optional.empty(), result); //empty if valid
    }

    /**
     * Test of validateProvidedInputResources method, of class
     * WPSInputOutputValidator.
     */
    @Test
    @DisplayName("validateProvidedInputResources invalid missing mandatory input")
    public void testValidateProvidedInputResources_MissingMandatoryInput() {
        System.out.println("validateProvidedInputResources mandatory input");
        //add mandatory input
        InputDescription mandatoryInput = new InputDescription();
        mandatoryInput.setId("mandatoryTestInput");
        mandatoryInput.setMinOccurs(1);
        this.wpsProcessDescription.setInputs(Arrays.asList(new InputDescription[]{mandatoryInput}));

        WPSInputOutputValidator instance = new WPSInputOutputValidator(this.wpsProcessDescription);
        Optional<Throwable> result = instance.validateProvidedInputResources(new HashMap<>()); // provide no input resources

        assertTrue(result.isPresent());
        System.out.println("validation exception:");
        System.out.println(result.get());
    }

    /**
     * Test of validateProvidedInputResources method, of class
     * WPSInputOutputValidator.
     */
    @Test
    @DisplayName("validateProvidedInputResources invalid undercut minimum occurrence")
    public void testValidateProvidedInputResources_MultipleInput() {
        System.out.println("validateProvidedInputResources invalid undercut minimum occurrence");
        //add mandatory input
        InputDescription mulitpleInput = new InputDescription();
        mulitpleInput.setId("multipleTestInput");
        mulitpleInput.setMaxOccurs(999);
        mulitpleInput.setMinOccurs(5);
        mulitpleInput.setFormats(Arrays.asList(new Format[]{txtPlain}));
        this.wpsProcessDescription.setInputs(Arrays.asList(new InputDescription[]{mulitpleInput}));

        Map<String, List<ResourceDescription>> providedInputResources = new HashMap<>();
        StaticDummyResource resource = new StaticDummyResource();
        resource.setValue("test value");
        ResourceDescription providedInput = new ResourceDescription(resource, "text/plain");
        providedInputResources.put("multipleTestInput", Arrays.asList(new ResourceDescription[]{providedInput})); //provide only one input resource for multipleTestInput

        WPSInputOutputValidator instance = new WPSInputOutputValidator(this.wpsProcessDescription);
        Optional<Throwable> result = instance.validateProvidedInputResources(providedInputResources);

        assertTrue(result.isPresent());
        System.out.println("validation exception:");
        System.out.println(result.get());
    }

    /**
     * Test of validateProvidedInputResources method, of class
     * WPSInputOutputValidator.
     */
    @Test
    @DisplayName("validateProvidedInputResources invalid unsupported mime type")
    public void testValidateProvidedInputResources_UnsupportedMimeType() {
        System.out.println("validateProvidedInputResources invalid unsupported mime type");
        Map<String, List<ResourceDescription>> providedInputResources = new HashMap<>();
        StaticDummyResource resource = new StaticDummyResource();
        resource.setValue("test value");
        ResourceDescription providedInput = new ResourceDescription(resource, "image/geotiff"); //unsupported mime type
        providedInputResources.put("literalInput", Arrays.asList(new ResourceDescription[]{providedInput}));

        WPSInputOutputValidator instance = new WPSInputOutputValidator(this.wpsProcessDescription);
        Optional<Throwable> result = instance.validateProvidedInputResources(providedInputResources);

        assertTrue(result.isPresent());
        System.out.println("validation exception:");
        System.out.println(result.get());
    }

    /**
     * Test of validateProvidedInputResources method, of class
     * WPSInputOutputValidator.
     */
    @Test
    @DisplayName("validateProvidedInputResources invalid unsupported input")
    public void testValidateProvidedInputResources_UnsupportedInput() {
        System.out.println("validateProvidedInputResources invalid unsupported input");
        //set provided inputs
        Map<String, List<ResourceDescription>> providedInputResources = new HashMap<>();
        StaticDummyResource resource = new StaticDummyResource();
        resource.setValue("test value");
        ResourceDescription providedInput = new ResourceDescription(resource, "text/plain");
        providedInputResources.put("literalInput", Arrays.asList(new ResourceDescription[]{providedInput}));
        ResourceDescription unsupportedProvidedInput = new ResourceDescription(resource, "text/plain");
        providedInputResources.put("unsupportedInput", Arrays.asList(new ResourceDescription[]{unsupportedProvidedInput})); // unsupported input

        WPSInputOutputValidator instance = new WPSInputOutputValidator(this.wpsProcessDescription);
        Optional<Throwable> result = instance.validateProvidedInputResources(providedInputResources);

        assertTrue(result.isPresent());
        System.out.println("validation exception:");
        System.out.println(result.get());
    }

}
