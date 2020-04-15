/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.listener;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Defines the channels to listen on for job definition messages
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public interface JobDefinitionListenerChannel {

    String JOB_CREATION_INPUT = "job-creation";

    @Input(JOB_CREATION_INPUT)
    SubscribableChannel jobCreation();

    String JOB_STATUS_UPDATE_INPUT = "job-status-update";

    @Input(JOB_STATUS_UPDATE_INPUT)
    SubscribableChannel jobStatusUpdate();

}
