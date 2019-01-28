/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.core.engine.utils.http;

import de.wacodis.core.engine.utils.http.HTTPRequest;
import de.wacodis.core.engine.utils.http.GenericPostRequest;
import de.wacodis.core.models.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.core.models.AbstractDataEnvelopeTimeFrame;
import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.CatalogueSubsetDefinition;
import de.wacodis.core.models.DataAccessResourceSearchBody;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class GenericPostRequestTest {

    private DataAccessResourceSearchBody postBody;

    public GenericPostRequestTest() {
    }

    /**
     * Test of setUrl method, of class GenericPostRequest.
     *
     * @throws java.net.MalformedURLException
     */
    @Test
    public void testSetUrl() throws MalformedURLException {
        GenericPostRequest<String, String> request = new GenericPostRequest<>(String.class);
        URL exampleURL = new URL("http://www.example.com");

        request.setUrl(exampleURL);
        assertEquals(exampleURL, request.getUrl());
    }

    /**
     * Test of setPayload method, of class GenericPostRequest.
     */
    @Test
    public void testSetPayload() {
        GenericPostRequest<String, String> request = new GenericPostRequest<>(String.class);
        String examplePayload = "testpayload";

        request.setPayload(examplePayload);
        assertEquals(examplePayload, request.getPayload());
    }

    /**
     * Test of setHeaders method, of class GenericPostRequest.
     */
    @Test
    public void testSetHeaders() {
        GenericPostRequest<String, String> request = new GenericPostRequest<>(String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");

        request.setHeaders(headers);
        assertEquals(headers, request.getHeaders());
        assertTrue(request.getHeaders().get("accept").contains("application/json"));
    }

    @Test
    @DisplayName("check responseTypeReference is null evokes exception")
    public void testExecute_Exception_TypeReference_Null() throws Exception {
        ParameterizedTypeReference<List<String>> typeReference = null;
        GenericPostRequest<String, List<String>> request = new GenericPostRequest<>(typeReference);
        request.setUrl(new URL("http://www.example.com"));

        assertThrows(IllegalArgumentException.class, () -> request.execute());
    }

    @Test
    @DisplayName("check responseType is null evokes exception")
    public void testExecute_Exception_Class_Null() throws Exception {
        Class<String> classStr = null;
        GenericPostRequest<String, String> request = new GenericPostRequest<>(classStr);
        request.setUrl(new URL("http://www.example.com"));

        assertThrows(IllegalArgumentException.class, () -> request.execute());
    }

    /**
     * Test of execute method, of class GenericPostRequest.
     * Integration Test, needs Wacodis DataAcces on localhost:8080
     * @throws java.lang.Exception
     */
    @Test
    @Disabled
    public void testExecute() throws Exception {
        ParameterizedTypeReference<Map<String, List<AbstractResource>>> typeReference = new ParameterizedTypeReference<Map<String, List<AbstractResource>>>() {
        };
        GenericPostRequest<DataAccessResourceSearchBody, Map<String, List<AbstractResource>>> request = new GenericPostRequest<>(typeReference);
        request.setUrl(HTTPRequest.stringToURL("http://localhost:8080/dataAccess/resources/search"));
        request.getHeaders().add("accept", " application/json");
        request.getHeaders().add("Content-Type", " application/json");
        request.setPayload(this.postBody);

        ResponseEntity<Map<String, List<AbstractResource>>> response = request.execute();
        System.out.println(response.getBody());
        assertTrue(response.hasBody());
    }

    @BeforeEach
    private void initPostBody() {
        this.postBody = new DataAccessResourceSearchBody();

        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        List<Float> extent = new ArrayList<>();
        extent.add(0.0f);
        extent.add(0.0f);
        extent.add(10.0f);
        extent.add(10.0f);
        aoi.setExtent(extent);
        CatalogueSubsetDefinition input = new CatalogueSubsetDefinition();
        input.setDatasetIdentifier("testDatasetID");
        input.setServiceUrl("www.example.com");
        input.setIdentifier("testID");
        input.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.CATALOGUESUBSETDEFINITION);
        List<AbstractSubsetDefinition> inputs = new ArrayList<>();
        inputs.add(input);
        AbstractDataEnvelopeTimeFrame frame = new AbstractDataEnvelopeTimeFrame();
        frame.setStartTime(DateTime.parse("2000-01-01T00:00:00Z"));
        frame.setEndTime(DateTime.parse("2000-02-01T00:00:00Z"));

        this.postBody.setAreaOfInterest(aoi);
        this.postBody.setInputs(inputs);
        this.postBody.setTimeFrame(frame);
    }
}
