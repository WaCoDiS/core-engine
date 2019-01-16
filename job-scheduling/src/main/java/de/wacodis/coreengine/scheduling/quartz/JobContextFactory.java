/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.quartz;

import com.cronutils.mapper.CronMapper;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import de.wacodis.core.models.WacodisJobDefinition;
import static de.wacodis.coreengine.scheduling.quartz.WacodisSchedulingConstants.*;
import java.text.ParseException;
import java.util.TimeZone;
import org.quartz.CronExpression;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.springframework.stereotype.Component;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class JobContextFactory {

    public JobContext createJobContext(WacodisJobDefinition jobDefinition, String timeZoneId) throws ParseException {
        JobContext jobContext = new JobContext();
        jobContext.setJobDetails(createJobDetail(jobDefinition));
        jobContext.setTrigger(createTrigger(jobDefinition, timeZoneId));
        return jobContext;
    }

    public Trigger createTrigger(WacodisJobDefinition jobDefinition, String timeZoneId) throws ParseException {
        Trigger trigger = newTrigger()
                .withIdentity(jobDefinition.getId().toString(), GROUP_NAME)
                .withSchedule(cronSchedule(createCronSchedule(jobDefinition))
                        .inTimeZone(TimeZone.getTimeZone(timeZoneId)))
                .build();
        return trigger;
    }

    public JobDetail createJobDetail(WacodisJobDefinition jobDefinition) {
        JobDetail detail = newJob(WacodisJob.class)
                .withIdentity(createJobKey(jobDefinition))
                .setJobData(createJobDataMap(jobDefinition))
                .build();
        return detail;
    }

    public JobKey createJobKey(WacodisJobDefinition jobDefinition) {
        return new JobKey(jobDefinition.getId().toString(), GROUP_NAME);
    }

    public JobDataMap createJobDataMap(WacodisJobDefinition jobDefinition) {
        JobDataMap jobData = new JobDataMap();
        jobData.put(JOB_KEY_ID, jobDefinition.getId().toString());
        return jobData;
    }

    public CronExpression createCronSchedule(WacodisJobDefinition jobDefinition) throws ParseException {
        CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
        Cron cron = parser.parse(jobDefinition.getExecution().getPattern());

        CronMapper cronMapper = CronMapper.fromUnixToQuartz();
        Cron quartzCron = cronMapper.map(cron);

        return new CronExpression(quartzCron.asString());
    }
}
