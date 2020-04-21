/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.configuration;

import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.Schema;
import java.util.List;
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
    private List<ExpectedProcessOutput> expectedProcessOutputs;
    private String defaultResourceMimeType;
    private Schema defaultResourceSchema;
    private boolean callWPSPerInput;
    private boolean processInputsSequentially;
    private int maxParallelWPSProcessPerJob;


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

    public List<ExpectedProcessOutput> getExpectedProcessOutputs() {
        return expectedProcessOutputs;
    }

    public void setExpectedProcessOutputs(List<ExpectedProcessOutput> expectedProcessOutputs) {
        this.expectedProcessOutputs = expectedProcessOutputs;
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

    public boolean isCallWPSPerInput() {
        return callWPSPerInput;
    }

    public void setCallWPSPerInput(boolean callWPSPerInput) {
        this.callWPSPerInput = callWPSPerInput;
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
    
    
}
