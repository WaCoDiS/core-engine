/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.manage;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.scheduling.job.JobContext;
import java.util.Date;

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
