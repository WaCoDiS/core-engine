/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.core.engine.utils.factories;

import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.core.models.WacodisJobDefinition;
import java.util.List;

/**
 * Helper to get expected outputs for a Wacodis Job, needed since expected outputs are optional in job definition
 * @author Arne
 */
public interface JobOutputHelper {
    
    /**
     * @param jobDef
     * @return list of expected outputs
     */
    List<JobOutputDescriptor> getExepectedOutputsForJob(WacodisJobDefinition jobDef);
    
}
