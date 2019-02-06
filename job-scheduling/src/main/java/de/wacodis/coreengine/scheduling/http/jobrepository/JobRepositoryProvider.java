/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.http.jobrepository;

import de.wacodis.core.models.WacodisJobDefinition;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public interface JobRepositoryProvider {

    WacodisJobDefinition getJobDefinitionForId(String id) throws JobRepositoryRequestException;

}
