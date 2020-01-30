/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.events;

import de.wacodis.core.models.WacodisJobDefinition;
import org.springframework.context.ApplicationEvent;

/**
 * event to indicate that the execution of a wacodis job failed
 * @author Arne
 */
public class WacodisJobExecutionFailedEvent extends ApplicationEvent {
    
    private final WacodisJobDefinition wacodisJob;

    public WacodisJobExecutionFailedEvent(Object source, WacodisJobDefinition wacodisJob) {
        super(source);
        this.wacodisJob = wacodisJob;
    }

    public WacodisJobDefinition getWacodisJob() {
        return wacodisJob;
    } 
}
