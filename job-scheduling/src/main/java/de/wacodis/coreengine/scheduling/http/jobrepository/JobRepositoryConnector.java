/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.http.jobrepository;

import de.wacodis.core.engine.utils.http.GenericGetRequest;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.scheduling.configuration.JobRepositoryConfiguration;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class JobRepositoryConnector implements JobRepositoryProvider, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRepositoryConnector.class);

    private static final String JOB_DEFINITIONS_ENDPOINT = "jobDefinitions";

    @Autowired
    private JobRepositoryConfiguration config;

    private HttpHeaders defaultHttpHeaders;

    @Override
    public WacodisJobDefinition getJobDefinitionForId(String id) throws JobRepositoryRequestException {
        try {
            URL url = new URL(String.join("/", config.getUri(), JOB_DEFINITIONS_ENDPOINT));
            GenericGetRequest<WacodisJobDefinition> request = new GenericGetRequest<WacodisJobDefinition>(url, WacodisJobDefinition.class);
            ResponseEntity<WacodisJobDefinition> response = request.execute();

            LOGGER.debug("GET request for JobDefinition {} was sent with response code: {}",
                    id, response.getStatusCode());

            if (!response.hasBody()) {
                throw new JobRepositoryRequestException("Requested JobDefinition resource "
                        + id + "is not available. Reponse status code: "
                        + response.getStatusCode());
            }

            return response.getBody();

        } catch (MalformedURLException ex) {
            throw new JobRepositoryRequestException("Could not request JobDefinitionAPI: Service URL is malformed", ex);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.defaultHttpHeaders = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<>(); //accept
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        defaultHttpHeaders.setAccept(acceptableMediaTypes); //content
        defaultHttpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

}
