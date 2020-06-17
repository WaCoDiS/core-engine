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

    private boolean preselectCandidates;

    public boolean isPreselectCandidates() {
        return preselectCandidates;
    }

    public void setPreselectCandidates(boolean preselectCandidates) {
        this.preselectCandidates = preselectCandidates;
        LOGGER.debug("set preselectCandidates to " + preselectCandidates);
    }

    
}
