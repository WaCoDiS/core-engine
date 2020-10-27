/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import java.util.Map;

/**
 * build ProcessContext for a Wacodis Job
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public interface ProcessContextBuilder {
    
    ProcessContext buildProcessContext(WacodisJobWrapper job, Map<String, Object> additionalParameters, JobOutputDescriptor... expectedProcessOutputIdentifiers);
    
}
