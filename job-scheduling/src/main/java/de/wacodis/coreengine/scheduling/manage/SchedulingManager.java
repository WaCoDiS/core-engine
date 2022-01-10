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
import de.wacodis.coreengine.scheduling.job.JobContext;
import java.util.Date;
import org.joda.time.DateTime;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public interface SchedulingManager {

    /**
     * Schedules a new job in the default timezone Europe/Berlin
     *
     * @param jobDefinition defintion for the job to be scheduled
     * @return first firing time
     */
    public Date scheduleNewJob(WacodisJobDefinition jobDefinition);

    /**
     *
     * @param jobDefinition defintion for the job to be scheduled
     * @param timeZoneId id for the timezone in which the scheduling is based
     * @return first firing time
     */
    public Date scheduleNewJob(WacodisJobDefinition jobDefinition, String timeZoneId);

    /**
     * Schedules a new job in the default timezone Europe/Berlin
     *
     * @param jobDefinition defintion for the job to be scheduled
     * @param startAt
     * @return first firing time
     */
    public Date scheduleNewJob(WacodisJobDefinition jobDefinition, Date startAt);

    /**
     *
     * @param jobDefinition defintion for the job to be scheduled
     * @param startAt
     * @param timeZoneId id for the timezone in which the scheduling is based
     * @return first firing time
     */
    public Date scheduleNewJob(WacodisJobDefinition jobDefinition, Date startAt, String timeZoneId);

    /**
     * Pauses the scheduling of a specified job
     *
     * @param jobDefinition defintion for the job to be paused
     */
    public void pauseJob(WacodisJobDefinition jobDefinition);

    /**
     * Pauses the scheduling of all jobss
     */
    public void pauseAll();

    /**
     * Deletes an existing job
     *
     * @param identifier job identifier to be deleted
     * @return true if deletion was successful
     */
    public boolean deleteJob(String identifier);

    /**
     * Deletes an existing job
     *
     * @param jobDefinition definition of the job to be deleted
     * @return true if deletion was successful
     */
    public boolean deleteJob(WacodisJobDefinition jobDefinition);

    /**
     * Deletes all existing jobs
     */
    public void deleteAll();

    /**
     * Reschedules an exisiting job based on the cron expression from the job
     * definition
     *
     * @param jobDefinition job definition of the job to be scheduled
     */
    public void rescheduleJob(WacodisJobDefinition jobDefinition);

    /**
     * Checks if a job that corresponds to the identifiers exists
     *
     * @param identifier identifier of the job to be checked
     * @return true if a job that corresponds to the job identifier exists
     */
    public boolean existsJob(String identifier);

    /**
     * Checks if a job that corresponds to the identifiers from the job
     * definition exists
     *
     * @param jobDefinition job definition to be checked
     * @return true if a job that corresponds to the job definition exists
     */
    public boolean existsJob(WacodisJobDefinition jobDefinition);

    /**
     * Gets the job context that belongs to a schedlued job for a job definition
     * if it exists
     *
     * @param jobDefinition job definition to get the job context for
     * @return
     */
    public JobContext getJob(WacodisJobDefinition jobDefinition);

    /**
     * Continue the scheduling of all jobs
     */
    public void resumeAll();

    /**
     * Continue the scheduling of a specified job if it exists
     *
     * @param jobDefinition
     */
    public void resumeJob(WacodisJobDefinition jobDefinition);

    /**
     * Trigger an exisiting job now
     *
     * @param jobDefinition job definition for an exisiting job to be triggered
     */
    public void triggerJobNow(WacodisJobDefinition jobDefinition);

}
