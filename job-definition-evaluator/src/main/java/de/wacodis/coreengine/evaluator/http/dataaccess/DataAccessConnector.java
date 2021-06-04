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
package de.wacodis.coreengine.evaluator.http.dataaccess;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.DataAccessResourceSearchBody;
import de.wacodis.core.engine.utils.http.GenericPostRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

/**
 * search data-access-api for available resources
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class DataAccessConnector implements DataAccessResourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessConnector.class);

    private final GenericPostRequest<DataAccessResourceSearchBody, Map<String, List<AbstractResource>>> dataAccessRequest;

    public DataAccessConnector() {
        this.dataAccessRequest = new DataAccessResourceSearchRequest();
    }

    public DataAccessConnector(GenericPostRequest<DataAccessResourceSearchBody, Map<String, List<AbstractResource>>> dataAccessRequest) {
        this.dataAccessRequest = dataAccessRequest;
    }

    public DataAccessConnector(URL url) {
        this();
        this.dataAccessRequest.setUrl(url);
    }

    public DataAccessConnector(URL url, GenericPostRequest<DataAccessResourceSearchBody, Map<String, List<AbstractResource>>> dataAccessRequest) {
        this(dataAccessRequest);
        this.dataAccessRequest.setUrl(url);
    }

    public URL getUrl() {
        return this.dataAccessRequest.getUrl();
    }

    public void setUrl(URL url) {
        this.dataAccessRequest.setUrl(url);
    }

    /**
     * execute HTTP-Post to data-access-api, instance variable Url must be set
     *
     * @param searchBody
     * @return response body
     * @throws java.io.IOException if request is not successful or has no
     * response body
     */
    @Override
    public Map<String, List<AbstractResource>> searchResources(DataAccessResourceSearchBody searchBody) throws IOException {
        this.dataAccessRequest.setPayload(searchBody);

        try {
            ResponseEntity<Map<String, List<AbstractResource>>> response = this.dataAccessRequest.execute();
            if (response.hasBody()) {
                LOGGER.info("Retrieving data from data-acces-api succeeded, Url: " + this.dataAccessRequest.getUrl().toString()
                        + ", StatusCode: " + response.getStatusCodeValue() + ", RequestBody: " + searchBody.toString()
                        + ", ResponseBody: " + response.getBody().toString());

                return response.getBody();
            } else {
                LOGGER.error("Retrieving data from data-acces-api ( " + this.dataAccessRequest.getUrl().toString() + ")failed because response has no body"
                        + ", StatusCode: " + response.getStatusCodeValue() + ", RequestBody: " + searchBody.toString()
                        + ", hasResponseBody: " + response.hasBody());
                throw new java.io.IOException("Could not retrieve resources from " + this.dataAccessRequest.getUrl().toString() + ", StatusCode: " + response.getStatusCodeValue() + ", hasBody: " + response.hasBody());
            }
        } catch (HttpStatusCodeException ex) {
            throw new java.io.IOException("Unable to retrieve resources from DataAccess (" + this.dataAccessRequest.getUrl().toString() + "), status code: " + ex.getStatusCode().toString() + System.lineSeparator() + "search body: " + searchBody.toString(), ex);
        } catch (RestClientException ex) {
            throw new java.io.IOException("Could not retrieve resources from DataAccess (" + this.dataAccessRequest.getUrl().toString() + ") due to unexpected RestClientException" + System.lineSeparator() + "search body: " + searchBody.toString(), ex);
        }
    }

}
