/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.manage;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import java.util.Date;

/**
 * schedule single wacodis job excution
 * @author Arne
 */
public interface SingleExecutionSchedulingManager {
    
    /**
     * schedule single wacodis job execution that starts as soon as possible
     * @param jobDefinition
     * @param context
     * @return date of scheduled job execution event
     */
    Date scheduleSingleExecutionImmediately(WacodisJobDefinition jobDefinition, WacodisJobExecutionContext context);
    
    /**
     * schedule single wacodis job execution that starts delayed, delay is calculated from jobDefiontion.retrySettings
     * @param jobDefinition
     * @param context
     * @return date of scheduled job execution event
     */
    Date scheduleSingleExecutionDelayed(WacodisJobDefinition jobDefinition, WacodisJobExecutionContext context);
    
}
