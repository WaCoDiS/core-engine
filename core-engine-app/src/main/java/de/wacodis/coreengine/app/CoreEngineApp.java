/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.app;

import de.wacodis.coreengine.evaluator.configuration.DataEnvelopeMatchingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@SpringBootApplication
@ComponentScan({
    "de.wacodis.coreengine.app",
    "de.wacodis.coreengine.evaluator",
    "de.wacodis.coreengine.scheduling"})
@RefreshScope
public class CoreEngineApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreEngineApp.class);

    @Autowired
    private DataEnvelopeMatchingConfiguration dataMatchingConfiguration;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CoreEngineApp.class, args);
    }
}
