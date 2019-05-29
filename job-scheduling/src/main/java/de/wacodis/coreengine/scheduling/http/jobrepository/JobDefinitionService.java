/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
            LOGGER.debug("GET request for JobDefinition {} returned status code: {}.",
                    id, ex.getStatusCode());
            throw new JobRepositoryRequestException("HTTP client error while requesting JobDefinition resource " + id, ex);
        } catch (RestClientException ex) {
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
            LOGGER.debug("GET request for JobDefinitions returned status code: {}.",
                    ex.getStatusCode());
            throw new JobRepositoryRequestException("HTTP client error while requesting JobDefinition resources.", ex);
        } catch (RestClientException ex) {
            throw new JobRepositoryRequestException("Unexpected client error while requesting JobDefinition resources.", ex);
        }
    }
}
