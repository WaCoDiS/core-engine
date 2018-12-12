/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.quartz;

import de.wacodis.core.models.WacodisJobDefinition;
import org.springframework.stereotype.Component;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class JobContextFactory {

    public JobContext createJobContext(WacodisJobDefinition jobDefintion) {
        JobContext jobContext = new JobContext();
        //TODO creation of job context
        return jobContext;
    }
}
