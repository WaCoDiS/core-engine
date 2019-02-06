/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("spring.scheduler.jobrepository")
public class JobRepositoryConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRepositoryConfiguration.class);

    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
        LOGGER.info("JobRepository URI set to: {}", this.uri);
    }

}
