/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.manage;

import de.wacodis.core.models.WacodisJobDefinition;
import java.util.Date;
import java.util.Map;

/**
 * schedule single wacodis job excution
 *
 * @author Arne
 */
public interface SingleExecutionSchedulingManager {

    /**
     * schedule single wacodis job execution that starts as soon as possible
     *
     * @param jobDefinition
     * @return date of scheduled job execution event
     */
    Date scheduleSingleJobExecutionImmediately(WacodisJobDefinition jobDefinition);

    /**
     * schedule single wacodis job execution that starts delayed, delay is
     * calculated from jobDefiontion.retrySettings
     *
     * @param jobDefinition
     * @return date of scheduled job execution event
     */
    Date scheduleSingleJobExecutionDelayed(WacodisJobDefinition jobDefinition);

    /**
     * schedule single wacodis job execution that starts delayed, delay is
     * calculated from jobDefiontion.retrySettings
     *
     * @param jobDefinition
     * @param startAt
     * @return date of scheduled job execution event
     */
    Date scheduleSingleJobExecutionAt(WacodisJobDefinition jobDefinition, Date startAt);

    /**
     * schedule single wacodis job execution that starts as soon as possible
     *
     * @param jobDefinition
     * @param jobData additional parameters used for job execution
     * @return date of scheduled job execution event
     */
    Date scheduleSingleJobExecutionImmediately(WacodisJobDefinition jobDefinition, Map<String, String> jobData);

    /**
     * schedule single wacodis job execution that starts delayed, delay is
     * calculated from jobDefiontion.retrySettings
     *
     * @param jobDefinition
     * @param jobData additional parameters used for job execution
     * @return date of scheduled job execution event
     */
    Date scheduleSingleJobExecutionDelayed(WacodisJobDefinition jobDefinition, Map<String, String> jobData);

    /**
     * schedule single wacodis job execution that starts delayed, delay is
     * calculated from jobDefiontion.retrySettings
     *
     * @param jobDefinition
     * @param jobData additional parameters used for job execution
     * @param startAt
     * @return date of scheduled job execution event
     */
    Date scheduleSingleJobExecutionAt(WacodisJobDefinition jobDefinition, Map<String, String> jobData, Date startAt);

}
