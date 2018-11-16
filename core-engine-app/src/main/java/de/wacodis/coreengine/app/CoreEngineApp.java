/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@SpringBootApplication
public class CoreEngineApp {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreEngineApp.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CoreEngineApp.class, args);
    }
}
