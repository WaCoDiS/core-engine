/*
 * Copyright 2018-2021 52°North Spatial Information Research GmbH
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

import de.wacodis.coreengine.scheduling.job.JobContext;
import de.wacodis.coreengine.scheduling.job.JobContextFactory;
import de.wacodis.core.models.WacodisJobDefinition;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

/**
 * Manages the scheduling of jobs
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class QuartzSchedulingManager implements SchedulingManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzSchedulingManager.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private JobContextFactory jCFactory;

    @Override
    public Date scheduleNewJob(WacodisJobDefinition jobDefinition) {
        Date firstFiringTime = null;
        try {
            JobContext jobContext = jCFactory.createJobContext(jobDefinition);
            firstFiringTime = scheduleNewJob(jobContext.getJobDetails(), jobContext.getTrigger());
        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while parsing job definition", ex);
        }
        return firstFiringTime;
    }

    @Override
    public Date scheduleNewJob(WacodisJobDefinition jobDefinition, String timeZoneId) {
        Date firstFiringTime = null;
        try {
            JobContext jobContext = jCFactory.createJobContext(jobDefinition, timeZoneId);
            firstFiringTime = scheduleNewJob(jobContext.getJobDetails(), jobContext.getTrigger());
        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while parsing job definition", ex);
        }
        return firstFiringTime;
    }

    @Override
    public Date scheduleNewJob(WacodisJobDefinition jobDefinition, Date startAt) {
        Date firstFiringTime = null;
        try {
            JobContext jobContext = jCFactory.createJobContext(jobDefinition);
            firstFiringTime = scheduleNewJob(jobContext.getJobDetails(), jobContext.getTrigger());
        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while parsing job definition", ex);
        }
        return firstFiringTime;
    }

    @Override
    public Date scheduleNewJob(WacodisJobDefinition jobDefinition, Date startAt, String timeZoneId) {
        Date firstFiringTime = null;
        try {
            JobContext jobContext = jCFactory.createJobContext(jobDefinition, timeZoneId);
            firstFiringTime = scheduleNewJob(jobContext.getJobDetails(), jobContext.getTrigger());
        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while parsing job definition", ex);
        }
        return firstFiringTime;
    }

    private Date scheduleNewJob(JobDetail jobDetail, Trigger trigger) {
        Date firstFiringTime = null;
        try {
            firstFiringTime = schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);    //runs QuartzJob's execute()
            LOGGER.info("Scheduling for job {} was successful. First fire time: {}", jobDetail.getKey(), firstFiringTime);
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while trying to schedule job", ex);
        }
        return firstFiringTime;
    }

    @Override
    public void pauseJob(WacodisJobDefinition jobDefinition) {
        JobKey key = jCFactory.createJobKey(jobDefinition);
        try {
            schedulerFactoryBean.getScheduler().pauseJob(key);
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug(String.format("Error while trying to pause job with key %s.%s", key.getGroup(), key.getName()), ex);
        }
    }

    @Override
    public void pauseAll() {
        try {
            schedulerFactoryBean.getScheduler().pauseAll();
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while trying to pause all jobs", ex);
        }
    }

    @Override
    public boolean deleteJob(String identifier) {
        boolean deleted = false;
        JobKey key = jCFactory.createJobKey(identifier);
        try {
            deleted = schedulerFactoryBean.getScheduler().deleteJob(key);
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug(String.format("Error while trying to delete job with key %s.%s", key.getGroup(), key.getName()), ex);
        }
        return deleted;
    }

    @Override
    public boolean deleteJob(WacodisJobDefinition jobDefinition) {
        return deleteJob(jobDefinition.getId().toString());
    }

    @Override
    public void deleteAll() {
        try {
            schedulerFactoryBean.getScheduler().clear();
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while trying to delete all jobs", ex);
        }
    }

    @Override
    public void rescheduleJob(WacodisJobDefinition jobDefinition) {
        JobKey jobKey = jCFactory.createJobKey(jobDefinition);
        TriggerKey triggerKey = jCFactory.createTriggerKey(jobDefinition);
        Trigger trigger;
        try {
            trigger = jCFactory.createTrigger(jobDefinition);
            schedulerFactoryBean.getScheduler().rescheduleJob(triggerKey, trigger);
        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug(String.format("Error while parsing trigger expression for job%s.%s", jobKey.getGroup(), jobKey.getName()), ex);
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug(String.format("Error while trying to reschedule job with key %s.%s", jobKey.getGroup(), jobKey.getName()), ex);
        }
    }

    @Override
    public boolean existsJob(String identifier) {
        boolean exists = false;
        JobKey key = jCFactory.createJobKey(identifier);
        try {
            exists = schedulerFactoryBean.getScheduler().checkExists(key);
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug(String.format("Error while checking if job with key %s.%s exists", key.getGroup(), key.getName()), ex);
        }
        return exists;
    }

    @Override
    public boolean existsJob(WacodisJobDefinition jobDefinition) {
        return existsJob(jobDefinition.getId().toString());
    }

    @Override
    public JobContext getJob(WacodisJobDefinition jobDefinition) {
        JobContext context = null;
        if (existsJob(jobDefinition)) {
            JobKey jobKey = jCFactory.createJobKey(jobDefinition);
            TriggerKey triggerKey = jCFactory.createTriggerKey(jobDefinition);
            try {
                JobDetail detail = schedulerFactoryBean.getScheduler().getJobDetail(jobKey);
                Trigger trigger = schedulerFactoryBean.getScheduler().getTrigger(triggerKey);
                context = new JobContext();
                context.setJobDetails(detail);
                context.setTrigger(trigger);
            } catch (SchedulerException ex) {
                LOGGER.error(ex.getMessage());
                LOGGER.debug(String.format("Error while retrieving job with key %s.%s", jobKey.getGroup(), jobKey.getName()), ex);
            }
        }
        return context;
    }

    @Override
    public void resumeAll() {
        try {
            schedulerFactoryBean.getScheduler().resumeAll();
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while trying to resume all job triggers", ex);
        }
    }

    @Override
    public void resumeJob(WacodisJobDefinition jobDefinition) {
        JobKey jobKey = jCFactory.createJobKey(jobDefinition);
        try {
            schedulerFactoryBean.getScheduler().resumeJob(jobKey);
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug(String.format("Error while trying to resume job with key %s.%s", jobKey.getGroup(), jobKey.getName()), ex);
        }
    }

    @Override
    public void triggerJobNow(WacodisJobDefinition jobDefinition) {
        JobKey jobKey = jCFactory.createJobKey(jobDefinition);
        try {
            schedulerFactoryBean.getScheduler().triggerJob(jobKey);
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug(String.format("Error while trying to trigger job with key now %s.%s", jobKey.getGroup(), jobKey.getName()), ex);
        }
    }

}
