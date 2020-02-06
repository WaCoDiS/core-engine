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
 *
 * @author Arne
 */
public interface RetrySchedulingManager {
    
    void scheduleRetryImmediately(WacodisJobDefinition jobDefinition, WacodisJobExecutionContext context);
    
    void scheduleRetryAt(WacodisJobDefinition jobDefinition, WacodisJobExecutionContext context, Date at);
    
}
