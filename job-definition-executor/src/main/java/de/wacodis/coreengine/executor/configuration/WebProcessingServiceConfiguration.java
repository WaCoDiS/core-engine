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
package de.wacodis.coreengine.executor.configuration;

import de.wacodis.coreengine.executor.process.Schema;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("spring.executor.wps")
public class WebProcessingServiceConfiguration {

    private String uri;
    private String version;
    private String defaultResourceMimeType;
    private Schema defaultResourceSchema;
    private boolean processInputsSequentially;
    private int maxParallelWPSProcessPerJob;
    private boolean processInputsDelayed;
    private long initialDelay_Milliseconds;
    private long delay_Milliseconds;
    private boolean validateInputs;

    public WebProcessingServiceConfiguration() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDefaultResourceMimeType() {
        return defaultResourceMimeType;
    }

    public void setDefaultResourceMimeType(String defaultResourceMimeType) {
        this.defaultResourceMimeType = defaultResourceMimeType;
    }

    public Schema getDefaultResourceSchema() {
        return defaultResourceSchema;
    }

    public void setDefaultResourceSchema(Schema defaultResourceSchema) {
        this.defaultResourceSchema = defaultResourceSchema;
    }

    public boolean isProcessInputsSequentially() {
        return processInputsSequentially;
    }

    public void setProcessInputsSequentially(boolean processInputsSequentially) {
        this.processInputsSequentially = processInputsSequentially;
    }

    public int getMaxParallelWPSProcessPerJob() {
        return maxParallelWPSProcessPerJob;
    }

    public void setMaxParallelWPSProcessPerJob(int maxParallelWPSProcessPerJob) {
        this.maxParallelWPSProcessPerJob = maxParallelWPSProcessPerJob;
    }

    public boolean isProcessInputsDelayed() {
        return processInputsDelayed;
    }

    public void setProcessInputsDelayed(boolean processInputsDelayed) {
        this.processInputsDelayed = processInputsDelayed;
    }

    public long getInitialDelay_Milliseconds() {
        return initialDelay_Milliseconds;
    }

    public void setInitialDelay_Milliseconds(long initialDelay_Milliseconds) {
        this.initialDelay_Milliseconds = initialDelay_Milliseconds;
    }

    public long getDelay_Milliseconds() {
        return delay_Milliseconds;
    }

    public void setDelay_Milliseconds(long delay_Milliseconds) {
        this.delay_Milliseconds = delay_Milliseconds;
    }

    public boolean isValidateInputs() {
        return validateInputs;
    }

    public void setValidateInputs(boolean validateInputs) {
        this.validateInputs = validateInputs;
    }
}
