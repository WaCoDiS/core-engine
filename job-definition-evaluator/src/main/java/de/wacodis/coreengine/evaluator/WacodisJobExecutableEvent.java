/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator;

import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import org.springframework.context.ApplicationEvent;

/**
 * indicates an executable WacodisJob
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobExecutableEvent extends ApplicationEvent {
    
    private final WacodisJobWrapper job;
    private final EvaluationStatus status;

    /**
     * 
     * @param source
     * @param job the job that is executable
     * @param status
     */
    public WacodisJobExecutableEvent(Object source, WacodisJobWrapper job, EvaluationStatus status){
        super(source);
        this.job = job;
        this.status = status;
    }

    /**
     * returns the executable job
     * @return 
     */
    public WacodisJobWrapper getJob() {
        return job;
    }
    
    
    public EvaluationStatus getStatus() {
        return status;
    }
}
