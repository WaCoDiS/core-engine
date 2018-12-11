/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.listener;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public interface JobDefinitionListenerChannel {

    String JOB_CREATION_INPUT = "jobCreation";

    @Input(JOB_CREATION_INPUT)
    SubscribableChannel jobCreation();

}
