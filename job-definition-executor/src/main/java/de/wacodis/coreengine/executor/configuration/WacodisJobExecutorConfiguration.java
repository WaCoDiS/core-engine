/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("spring.executor")
public class WacodisJobExecutorConfiguration {
    
    private String cleanUpTool;

    public String getCleanUpTool() {
        return cleanUpTool;
    }

    public void setCleanUpTool(String cleanUpTool) {
        this.cleanUpTool = cleanUpTool;
    }
    
}
