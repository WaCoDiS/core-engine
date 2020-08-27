/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.listener;

import de.wacodis.core.models.*;
import de.wacodis.coreengine.scheduling.manage.QuartzSingleExecutionSchedulingManager;
import de.wacodis.coreengine.scheduling.manage.QuartzSchedulingManager;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handler implementation for job messages
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class JobMessageScheduleHandler implements JobMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JobMessageScheduleHandler.class);

    @Autowired
    private QuartzSchedulingManager regularExecutionschedulingManager;

    @Autowired
    private QuartzSingleExecutionSchedulingManager singleExecutionSchedulingManger;

    @Override
    public void handleNewJob(WacodisJobDefinition jobDefinition) {
        WacodisJobDefinitionExecution exec = jobDefinition.getExecution();

        if (exec.getPattern() != null && !exec.getPattern().isEmpty()) { //schedule job regularly if cron pattern is provided
            handleRegularExecution(jobDefinition);
        } else if (exec.getEvent() != null) {
            handleEventbasedExecution(jobDefinition);
        } else {
            throw new UnsupportedOperationException("unnable to schedule wacodis job " + jobDefinition.getId() + " because neither excution cron pattern nor execution event is provided");
        }
    }

    @Override
    public void handleJobDeletion(WacodisJobDefinition jobDefinition) {
        if (regularExecutionschedulingManager.existsJob(jobDefinition.getId().toString())) {
            if (regularExecutionschedulingManager.deleteJob(jobDefinition.getId().toString())) {
                LOG.info("Job '{}' deleted succesfully.", jobDefinition.getId().toString());
            } else {
                LOG.warn("Deletion of job '{}' failed.", jobDefinition.getId().toString());
            }
        } else {
            LOG.warn("Job '{}' does not exist.", jobDefinition.getId().toString());
        }
    }

    private void handleEventbasedExecution(WacodisJobDefinition jobDefinition) {
        AbstractWacodisJobExecutionEvent execEvent = jobDefinition.getExecution().getEvent();

        if (execEvent instanceof SingleJobExecutionEvent) {
            DateTime execAt = jobDefinition.getExecution().getStartAt();

            if (execAt != null) {
                singleExecutionSchedulingManger.scheduleSingleJobExecutionAt(jobDefinition, execAt.toDate()); //execute wacodis job once on specified date
            } else {
                singleExecutionSchedulingManger.scheduleSingleJobExecutionImmediately(jobDefinition); //execute immediately if no start date provided
            }

        }
    }

    private void handleRegularExecution(WacodisJobDefinition jobDefinition) {
        WacodisJobDefinitionExecution exec = jobDefinition.getExecution();

        if (exec.getStartAt() != null) {
            Date startAtDate = exec.getStartAt().toDate();
            regularExecutionschedulingManager.scheduleNewJob(jobDefinition, startAtDate);
        } else {
            regularExecutionschedulingManager.scheduleNewJob(jobDefinition);
        }
    }

}
