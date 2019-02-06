/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.app;

import de.wacodis.coreengine.evaluator.JobEvaluatorRunner;
import de.wacodis.coreengine.evaluator.WacodisJobInputTrackerProvider;
import de.wacodis.coreengine.evaluator.configuration.DataAccessConfiguration;
import de.wacodis.coreengine.evaluator.configuration.DataEnvelopeMatchingConfiguration;
import de.wacodis.coreengine.evaluator.http.dataaccess.DataAccessConnector;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.BasicDataEnvelopeMatcher;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobInputTracker;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.PostConstruct;
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
    
    @Autowired
    private DataAccessConfiguration dataAccessConfiguration;
    
    @Autowired
    private JobEvaluatorRunner jobEvaluator;
    
    @Autowired
    private WacodisJobInputTrackerProvider provider;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CoreEngineApp.class, args);
    }
    
    @PostConstruct
    private void initialize() throws MalformedURLException{
        //init DataEnvelopeMatcher
        BasicDataEnvelopeMatcher dataMatcher = new BasicDataEnvelopeMatcher();
        dataMatcher.setConfig(this.dataMatchingConfiguration);
 
        //init InputTracker
        WacodisJobInputTracker inputTracker = new WacodisJobInputTracker(dataMatcher);
        this.provider.setInputTracker(inputTracker);
        
        //init DataAccessConnector
        DataAccessConnector dataAccess = new DataAccessConnector();
        //ToDo runtime changes of dataaccess configuration are not reflected
        URL dataAccessURL = new URL(this.dataAccessConfiguration.getUri());
        dataAccess.setUrl(dataAccessURL);
        
        //init JobEvaluatorRunner
        this.jobEvaluator.setInputTracker(inputTracker);
        this.jobEvaluator.setDataAccessConnector(dataAccess);
    }     
} 
 