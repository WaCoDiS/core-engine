/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.events;


import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import org.springframework.context.ApplicationEvent;

/**
 * event to indicate that the execution of a wacodis job failed
 * @author Arne
 */
public class WacodisJobExecutionFailedEvent extends ApplicationEvent {
    
    private final WacodisJobWrapper wacodisJob;
    private final Throwable failure;

    public WacodisJobExecutionFailedEvent(Object source, WacodisJobWrapper wacodisJob, Throwable failure) {
        super(source);
        this.wacodisJob = wacodisJob;
        this.failure = failure;
    }

    public WacodisJobWrapper getWacodisJob() {
        return wacodisJob;
    } 

    public Throwable getFailure() {
        return failure;
    }
}
