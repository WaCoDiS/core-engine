/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.http.dataaccess;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.DataAccessResourceSearchBody;
import de.wacodis.core.models.PostResource;
import de.wacodis.core.engine.utils.http.GenericPostRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DataAccessConnectorTest {

    public DataAccessConnectorTest() {
    }

    /**
     * Test of getUrl method, of class DataAccessConnector.
     */
    @Test
    public void testGetUrl() throws MalformedURLException {
        URL url = new URL("http://www.example.com");
        DataAccessConnector conn = new DataAccessConnector(url);
        
        assertEquals(url, conn.getUrl());
    }

    /**
     * Test of setUrl method, of class DataAccessConnector.
     */
    @Test
    public void testSetUrl() throws MalformedURLException {
        URL url = new URL("http://www.example.com");
        DataAccessConnector conn = new DataAccessConnector();
        
        conn.setUrl(url);
        assertEquals(url, conn.getUrl());
    }

    /**
     * Test of searchResources method, of class DataAccessConnector.
     *
     * @throws java.lang.Exception
     */
    @Test
    @DisplayName("check throws exception if no response body is present")
    public void testSearchResources_noResponseBody() throws Exception {
        MockPostRequest req = new MockPostRequest();
        req.setExpectedResponse(null); //no response body
        DataAccessConnector conn = new DataAccessConnector(req);
        conn.setUrl(new URL("http://www.example.com")); //avoid nullpointer

        assertThrows(java.io.IOException.class, () -> conn.searchResources(new DataAccessResourceSearchBody())); //search body irrelevant with MockPostRequest
    }

    /**
     * Test of searchResources method, of class DataAccessConnector.
     *
     * @throws java.lang.Exception
     */
    @Test
    @DisplayName("check no exception thrown if response body is present")
    public void testSearchResources_ResponseBody() throws Exception {
        MockPostRequest req = new MockPostRequest();
        Map<String, List<AbstractResource>> expectedResponse = new HashMap<>();
        PostResource resource = new PostResource();
        resource.setMethod(AbstractResource.MethodEnum.POSTRESOURCE);
        resource.setBody("test");
        resource.setContentType("text/plain");
        resource.setUrl("http://www.example.com/test");
        List<AbstractResource> resourceList = new ArrayList<>();
        resourceList.add(resource);
        expectedResponse.put("testID", resourceList);
        
        req.setExpectedResponse(expectedResponse);
        DataAccessConnector conn = new DataAccessConnector(req);
        conn.setUrl(new URL("http://www.example.com")); //avoid nullpointer

        assertDoesNotThrow(()-> conn.searchResources(new DataAccessResourceSearchBody())); //search body irrelevant with MockPostRequest
    }

    /**
     * mock post requests to data-access-api, always returns expectedResponse
     */
    private class MockPostRequest extends GenericPostRequest<DataAccessResourceSearchBody, Map<String, List<AbstractResource>>> {

        private Map<String, List<AbstractResource>> expectedResponse;

        public MockPostRequest() {
            super(new ParameterizedTypeReference<Map<String, List<AbstractResource>>>() {
            });
        }

        public void setExpectedResponse(Map<String, List<AbstractResource>> expectedResponse) {
            this.expectedResponse = expectedResponse;
        }

        public Map<String, List<AbstractResource>> getExpectedResponse() {
            return expectedResponse;
        }

        /**
         * 
         * @return expectedResponse
         */
        @Override
        public ResponseEntity<Map<String, List<AbstractResource>>> execute() {
            ResponseEntity<Map<String, List<AbstractResource>>> resp = ResponseEntity.of(Optional.ofNullable(this.expectedResponse));
            return resp;
        }

    }

}
