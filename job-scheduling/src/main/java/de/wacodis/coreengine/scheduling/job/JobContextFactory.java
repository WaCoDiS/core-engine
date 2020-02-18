/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.job;

import com.cronutils.mapper.CronMapper;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import de.wacodis.core.models.WacodisJobDefinition;
import static de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants.*;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.TimeZone;
import org.quartz.CronExpression;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating job context information
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class JobContextFactory {

    @Autowired
    private JobDetailFactory jobDetailFactory;

    /**
     * Creates a job context with default timezone trigger from a job definition
     *
     * @param jobDefinition contains defintions for the job
     * @return a job context with specified timezone trigger
     * @throws ParseException
     */
    public JobContext createJobContext(WacodisJobDefinition jobDefinition) throws ParseException {
        JobContext jobContext = new JobContext();
        jobContext.setJobDetails(createJobDetail(jobDefinition));
        jobContext.setTrigger(createTrigger(jobDefinition));
        return jobContext;
    }

    /**
     * Creates a job context with a trigger based in the specified timezone from
     * a job definition
     *
     * @param jobDefinition contains defintions for the job
     * @param timeZoneId
     * @return
     * @throws ParseException
     */
    public JobContext createJobContext(WacodisJobDefinition jobDefinition, String timeZoneId) throws ParseException {
        JobContext jobContext = new JobContext();
        jobContext.setJobDetails(createJobDetail(jobDefinition));
        jobContext.setTrigger(createTrigger(jobDefinition, timeZoneId));
        return jobContext;
    }

    /**
     * Creates a trigger that will be per default scheduled based in timezone
     * Europe/Berlin
     *
     * @param jobDefinition contains defintions for the job
     * @return a job trigger that is based in the default timezone
     * @throws ParseException
     */
    public Trigger createTrigger(WacodisJobDefinition jobDefinition) throws ParseException {
        Trigger trigger = newTrigger()
                .withIdentity(createTriggerKey(jobDefinition))
                .withSchedule(cronSchedule(createCronSchedule(jobDefinition.getExecution().getPattern()))
                        .inTimeZone(TimeZone.getTimeZone(checkTimeZone(DEFAULT_TIMEZONE))))
                .build();
        return trigger;
    }

    /**
     * * Creates a trigger that will be scheduled based in the specified
     * timezone
     *
     * @param jobDefinition contains defintions for the job
     * @param timeZoneId timezone the trigger is based in
     * @return a job trigger that is based in the specified timezone
     * @throws ParseException
     */
    public Trigger createTrigger(WacodisJobDefinition jobDefinition, String timeZoneId) throws ParseException {
        Trigger trigger = newTrigger()
                .withIdentity(createTriggerKey(jobDefinition))
                .withSchedule(cronSchedule(createCronSchedule(jobDefinition.getExecution().getPattern()))
                        .inTimeZone(TimeZone.getTimeZone(checkTimeZone(timeZoneId))))
                .build();
        return trigger;
    }

    /**
     * Create job details from a job definition
     *
     * @param jobDefinition contains defintions for the job
     * @return job details
     */
    public JobDetail createJobDetail(WacodisJobDefinition jobDefinition) {
        return jobDetailFactory.createJob(WacodisJob.class, false, false,
                jobDefinition.getId().toString(), GROUP_NAME, createJobDataMap(jobDefinition));
    }

    /**
     * Creates a job key from a job definition
     *
     * @param jobDefinition contains defintions for the job
     * @return the job key
     */
    public JobKey createJobKey(WacodisJobDefinition jobDefinition) {
        return new JobKey(jobDefinition.getId().toString(), GROUP_NAME);
    }

    /**
     * Creates a trigger key from a job definition
     *
     * @param jobDefinition contains defintions for the job
     * @return the trigger key
     */
    public TriggerKey createTriggerKey(WacodisJobDefinition jobDefinition) {
        return new TriggerKey(jobDefinition.getId().toString(), GROUP_NAME);
    }

    /**
     * Creates a trigger key with the specified identfier
     *
     * @param identifer
     * @return the trigger key
     */
    public TriggerKey createTriggerKey(String identifer) {
        return new TriggerKey(identifer, GROUP_NAME);
    }

    /**
     * Creates job data from a job definition
     *
     * @param jobDefinition contains defintions for the job
     * @return job data
     */
    public JobDataMap createJobDataMap(WacodisJobDefinition jobDefinition) {
        JobDataMap jobData = new JobDataMap();
        jobData.put(JOB_KEY_ID, jobDefinition.getId().toString());
        return jobData;
    }

    /**
     * Creates a Quartz cron expression from a unix UNIX cron expression pattern
     *
     * @param executionPattern the UNIX cron expression
     * @return cron expression for Quartz that matches the UNIX cron expression
     * @throws ParseException
     */
    public CronExpression createCronSchedule(String executionPattern) throws ParseException {
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
        Cron cron = parser.parse(executionPattern);

        CronMapper cronMapper = CronMapper.fromUnixToQuartz();
        Cron quartzCron = cronMapper.map(cron);

        return new CronExpression(quartzCron.asString());
    }

    /**
     * Check the timezone for a timezone ID
     *
     * @param timeZoneId the timezone ID from the IANA time zone databse
     * @return
     */
    public ZoneId checkTimeZone(String timeZoneId) {
        return ZoneId.of(timeZoneId);
    }
}
