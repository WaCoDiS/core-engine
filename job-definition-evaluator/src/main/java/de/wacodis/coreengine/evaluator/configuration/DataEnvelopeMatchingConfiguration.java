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
