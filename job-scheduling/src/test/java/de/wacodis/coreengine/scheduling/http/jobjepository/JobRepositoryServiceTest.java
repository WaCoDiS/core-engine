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
package de.wacodis.coreengine.scheduling.http.jobjepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.scheduling.configuration.JobDefinitionServiceConfiguration;
import de.wacodis.coreengine.scheduling.http.jobrepository.JobRepositoryRequestException;
import de.wacodis.coreengine.scheduling.http.jobrepository.JobDefinitionService;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@RestClientTest(JobDefinitionService.class)
@ContextConfiguration(classes = {
    JobDefinitionServiceConfiguration.class,
    JobDefinitionService.class})
public class JobRepositoryServiceTest {

    @Autowired
    private JobDefinitionService jobDefinitionService;

    @Autowired
    @Qualifier("jobDefinitionService")
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockRestServiceServer mockServer;

    private WacodisJobDefinition testJobDefinition;
    private List<WacodisJobDefinition> testJobDefinitionList;

    private static final UUID ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        this.testJobDefinition = createJobDefinition();
        this.testJobDefinitionList = Arrays.asList(this.testJobDefinition);
    }

    @Test
    @DisplayName("Test getting WacodisJobDefinition for a specified ID")
    public void testGetWacodisJobDefinitionForId() throws JobRepositoryRequestException, JsonProcessingException {
        mockServer.expect(ExpectedCount.once(),
                requestTo("/jobDefinitions/" + ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(this.testJobDefinition))
                );
        WacodisJobDefinition result = this.jobDefinitionService.getJobDefinitionForId(ID.toString());

        mockServer.verify();
        Assertions.assertEquals(this.testJobDefinition, result);
    }

    @Test
    @DisplayName("Test getting WacodisJobDefinition for a specified ID with error response")
    public void testGetWacodisJobDefinitionForIdWithErrorResponse() throws JobRepositoryRequestException, JsonProcessingException {
        de.wacodis.core.models.Error error = new de.wacodis.core.models.Error();
        error.code(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.message("Requested resource is not available");

        mockServer.expect(ExpectedCount.once(),
                requestTo("/jobDefinitions/" + ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(error))
                );

        Assertions.assertThrows(JobRepositoryRequestException.class, () -> {
            this.jobDefinitionService.getJobDefinitionForId(ID.toString());
        });
        mockServer.verify();
    }

    @Test
    @DisplayName("Test getting WacodisJobDefinition list")
    public void testGetWacodisJobDefinitionList() throws JobRepositoryRequestException, JsonProcessingException {
        mockServer.expect(ExpectedCount.once(),
                requestTo("/jobDefinitions"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(this.testJobDefinitionList))
                );
        List<WacodisJobDefinition> resultList = this.jobDefinitionService.getJobDefinitionList();

        mockServer.verify();
        Assertions.assertIterableEquals(this.testJobDefinitionList, resultList);
    }

    @Test
    @DisplayName("Test getting WacodisJobDefinition list with error response")
    public void testGetWacodisJobDefinitionListWithErrorResponse() throws JobRepositoryRequestException, JsonProcessingException {
        de.wacodis.core.models.Error error = new de.wacodis.core.models.Error();
        error.code(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.message("Requested resource is not available");

        mockServer.expect(ExpectedCount.once(),
                requestTo("/jobDefinitions"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(error))
                );

        Assertions.assertThrows(JobRepositoryRequestException.class, () -> {
            this.jobDefinitionService.getJobDefinitionList();
        });
        mockServer.verify();
    }

    private WacodisJobDefinition createJobDefinition() {
        WacodisJobDefinition jobDefinition = new WacodisJobDefinition();
        jobDefinition.setId(ID);
        jobDefinition.setDescription("WaCoDiS Test Job");
        jobDefinition.setName("WaCoDiS Test Job");
        return jobDefinition;
    }

}
