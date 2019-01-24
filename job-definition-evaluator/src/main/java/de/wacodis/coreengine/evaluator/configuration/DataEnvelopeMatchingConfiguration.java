/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("spring.evaluator.matching")
public class DataEnvelopeMatchingConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataEnvelopeMatchingConfiguration.class);

    private float minimumOverlapPercentage;

    public float getMinimumOverlapPercentage() {
        return minimumOverlapPercentage;
    }

    public void setMinimumOverlapPercentage(float minimumOverlapPercentage) {
        if (minimumOverlapPercentage > 100.0f || minimumOverlapPercentage < 0.0f) {
            throw new IllegalArgumentException("minimumOverlapPercentage is " + minimumOverlapPercentage + " but must be between 0.0 and 100.0");
        }

        this.minimumOverlapPercentage = minimumOverlapPercentage;
        LOGGER.info("minimumOverlapPercentage set to: " + this.minimumOverlapPercentage);
    }
}
