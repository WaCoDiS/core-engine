/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.events;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.EvaluationStatus;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.JobIsExecutableChangeListener;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobInputTracker;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobExecutableStateChangedHandler implements JobIsExecutableChangeListener{

    @Override
    public void onJobIsExecutableChanged(WacodisJobInputTracker eventSource, WacodisJobWrapper job, EvaluationStatus status) {
        if(status.equals(EvaluationStatus.EXECUTABLE)){
            //if job executable, call WPS Process
            WacodisJobDefinition jobDef = job.getJobDefinition();
        }
    }
    
}
