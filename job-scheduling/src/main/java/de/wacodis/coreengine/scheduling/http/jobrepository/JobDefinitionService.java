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
package de.wacodis.coreengine.scheduling.http.jobrepository;

import de.wacodis.core.models.WacodisJobDefinition;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Service
public class JobDefinitionService implements JobRepositoryProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobDefinitionService.class);

    private static final String JOB_DEFINITIONS_ENDPOINT = "/jobDefinitions";
    private static final String SINGLE_JOB_DEFINITION_ENDPOINT = "/jobDefinitions/";

    private RestTemplate jobRepositoryService;

    @Autowired
    @Qualifier("jobDefinitionService")
    public void setBuilder(RestTemplate restTemplate) {
        this.jobRepositoryService = restTemplate;
    }

    @Override
    public WacodisJobDefinition getJobDefinitionForId(String id) throws JobRepositoryRequestException {
        try {
            ResponseEntity<WacodisJobDefinition> response = jobRepositoryService
                    .getForEntity(SINGLE_JOB_DEFINITION_ENDPOINT + id, WacodisJobDefinition.class);
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            LOGGER.error("GET request for JobDefinition {} returned status code: {}.",
                    id, ex.getStatusCode());
            throw new JobRepositoryRequestException("HTTP client error while requesting JobDefinition resource " + id, ex);
        } catch (RestClientException ex) {
            LOGGER.error(ex.getMessage());
            throw new JobRepositoryRequestException("Unexpected client error while requesting JobDefinition resource " + id, ex);
        }
    }

    @Override
    public List<WacodisJobDefinition> getJobDefinitionList() throws JobRepositoryRequestException {
        try {
            ResponseEntity<List<WacodisJobDefinition>> response = jobRepositoryService
                    .exchange(
                            JOB_DEFINITIONS_ENDPOINT,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<WacodisJobDefinition>>() {
                    });
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            LOGGER.error("GET request for JobDefinitions returned status code: {}.",
                    ex.getStatusCode());
            throw new JobRepositoryRequestException("HTTP client error while requesting JobDefinition resources.", ex);
        } catch (RestClientException ex) {
            LOGGER.error(ex.getMessage());
            throw new JobRepositoryRequestException("Unexpected client error while requesting JobDefinition resources.", ex);
        }
    }
}
