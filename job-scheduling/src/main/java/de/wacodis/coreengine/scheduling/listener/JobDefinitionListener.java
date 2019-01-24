/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.listener;

import de.wacodis.core.models.WacodisJobDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@EnableBinding(JobDefinitionListenerChannel.class)
public class JobDefinitionListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JobDefinitionListener.class);
    
    @Autowired
    private JobMessageHandler messageHandler;
    
    @StreamListener(JobDefinitionListenerChannel.JOB_CREATION_INPUT)
    public void evaluateJobDefinition(WacodisJobDefinition jobDefinition) {
        LOGGER.info("Received new job: {}", jobDefinition.toString());
        messageHandler.handleNewJob(jobDefinition);
    }
    
}
