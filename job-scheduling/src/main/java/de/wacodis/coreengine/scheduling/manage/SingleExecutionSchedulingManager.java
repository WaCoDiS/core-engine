/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * @param customIdentifier
     * @return date of scheduled job execution event
     */
    Date scheduleSingleJobExecutionImmediately(WacodisJobDefinition jobDefinition, String customIdentifier);

    /**
     * schedule single wacodis job execution that starts delayed, delay is
     * calculated from jobDefiontion.retrySettings
     *
     * @param jobDefinition
     * @param customIdentifier
     * @return date of scheduled job execution event
     */
    Date scheduleSingleJobExecutionDelayed(WacodisJobDefinition jobDefinition, String customIdentifier);

    /**
     * schedule single wacodis job execution that starts delayed, delay is
     * calculated from jobDefiontion.retrySettings
     *
     * @param jobDefinition
     * @param startAt
     * @param customIdentifier
     * @return date of scheduled job execution event
     */
    Date scheduleSingleJobExecutionAt(WacodisJobDefinition jobDefinition, Date startAt, String customIdentifier);

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
    
        /**
     * schedule single wacodis job execution that starts as soon as possible
     *
     * @param jobDefinition
     * @param jobData additional parameters used for job execution
     * @param customIdentifier
     * @return date of scheduled job execution event
     */
    Date scheduleSingleJobExecutionImmediately(WacodisJobDefinition jobDefinition, Map<String, String> jobData, String customIdentifier);

    /**
     * schedule single wacodis job execution that starts delayed, delay is
     * calculated from jobDefiontion.retrySettings
     *
     * @param jobDefinition
     * @param jobData additional parameters used for job execution
     * @param customIdentifier
     * @return date of scheduled job execution event
     */
    Date scheduleSingleJobExecutionDelayed(WacodisJobDefinition jobDefinition, Map<String, String> jobData, String customIdentifier);

    /**
     * schedule single wacodis job execution that starts delayed, delay is
     * calculated from jobDefiontion.retrySettings
     *
     * @param jobDefinition
     * @param jobData additional parameters used for job execution
     * @param startAt
     * @param customIdentifier
     * @return date of scheduled job execution event
     */
    Date scheduleSingleJobExecutionAt(WacodisJobDefinition jobDefinition, Map<String, String> jobData, Date startAt, String customIdentifier);

}
