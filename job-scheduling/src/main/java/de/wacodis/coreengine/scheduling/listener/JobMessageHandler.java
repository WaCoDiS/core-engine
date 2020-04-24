/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.listener;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobStatusUpdate;
import org.springframework.stereotype.Component;

/**
 * Handler interface for job messages
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public interface JobMessageHandler {

    void handleNewJob(WacodisJobDefinition jobDefinition);

    void handleJobDeletion(WacodisJobDefinition jobDefinition);
}
