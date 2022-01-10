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
