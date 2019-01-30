/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.factory;

import de.wacodis.coreengine.scheduling.factory.JobContextFactory;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecution;
import de.wacodis.coreengine.scheduling.quartz.JobContext;
import static de.wacodis.coreengine.scheduling.quartz.WacodisSchedulingConstants.JOB_KEY_ID;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.TimeZone;
import java.util.UUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.joda.time.DateTime;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.quartz.CronExpression;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {JobContextFactory.class, JobDetailFactory.class})
public class JobContextFactoryTest {

    private static final String GROUP_NAME = "de.hsbo.wacodis";
    private static final UUID JOB_KEY = UUID.randomUUID();
    private static final String UNIX_CRON_EXPRESSION = "0 0 1 * *";
    private static final String QUARTZ_CRON_EXPRESSION = "0 0 0 1 * ? *";
    private static final String DEFAULT_TIMEZONE = "Europe/Berlin";

    @Autowired
    private JobContextFactory jobContextFactory;

    private WacodisJobDefinition jobDefinition;

    @BeforeEach
    public void setup() {
        jobDefinition = new WacodisJobDefinition();
        jobDefinition.setId(JOB_KEY);
        jobDefinition.setExecution(new WacodisJobDefinitionExecution().pattern(UNIX_CRON_EXPRESSION));

    }

    @Test
    @DisplayName("Test timezone check for a timezone ID")
    public void testTimeZoneCheckForValidId() {
        ZoneId timeZone = jobContextFactory.checkTimeZone("Europe/Berlin");

        assertThat(timeZone.getId(), is(equalTo(DEFAULT_TIMEZONE)));
    }

    @Test
    @DisplayName("Test timezone check throws expcetion for an unknown timezone ID")
    public void testTimeZoneCheckThrowsExpceptionForUnknownId() {
        assertThrows(ZoneRulesException.class, () -> jobContextFactory.checkTimeZone("Europe/Bochum"));
    }

    @Test
    @DisplayName("Test timezone check throws expcetion for an invalid timezone ID format")
    public void testTimeZoneCheckThrowsExpceptionForInvalidIdFormat() {
        assertThrows(DateTimeException.class, () -> jobContextFactory.checkTimeZone("Bochum"));
    }

    @Test
    @DisplayName("Test creation of a cron schedule from a valid cron expression")
    public void testCronScheduleCreationForValidExpression() throws ParseException {
        String timeZoneId = "UTC";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));

        CronExpression expr = jobContextFactory.createCronSchedule(jobDefinition.getExecution().getPattern());

        assertThat(expr.getCronExpression(), is(equalTo(QUARTZ_CRON_EXPRESSION)));
        assertThat(sdf.format(expr.getNextValidTimeAfter(new DateTime("2019-01-01T00:00:00").toDate())),
                is(equalTo(sdf.format(new DateTime("2019-02-01T00:00:00").toDate()))));
    }

    @Test
    @DisplayName("Test creation of a job data map contains job key from a job definition")
    public void testCreatedJobDataMapContainsJobId() throws ParseException {
        JobDataMap jobData = jobContextFactory.createJobDataMap(jobDefinition);

        assertThat(jobData.getString(JOB_KEY_ID), is(equalTo(JOB_KEY.toString())));
    }

    @Test
    @DisplayName("Test creation of a job key from a job definition")
    public void testCreateJobKey() throws ParseException {
        JobKey jobKey = jobContextFactory.createJobKey(jobDefinition);

        assertThat(jobKey.getName(), is(equalTo(JOB_KEY.toString())));
        assertThat(jobKey.getGroup(), is(equalTo(GROUP_NAME)));
    }

    @Test
    @DisplayName("Test creation of job details from a job definition")
    public void testCreateJobDetail() throws ParseException {
        JobDetail jobDetail = jobContextFactory.createJobDetail(jobDefinition);

        assertThat(jobDetail.getKey().getName(), is(equalTo(JOB_KEY.toString())));
        assertThat(jobDetail.getKey().getGroup(), is(equalTo(GROUP_NAME)));
        assertThat(jobDetail.getJobDataMap().getString(JOB_KEY_ID), is(equalTo(JOB_KEY.toString())));
    }

    @Test
    @DisplayName("Test creation of a trigger based in the default timezon from a job definition")
    public void testCreateTriggerbasedInDefaultTimezone() throws ParseException {
        Trigger trigger = jobContextFactory.createTrigger(jobDefinition);
        String timeZoneId = "UTC";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));

        assertThat(sdf.format(trigger.getFireTimeAfter(new DateTime("2019-01-01T00:00:00").toDate())),
                is(equalTo(sdf.format(new DateTime("2019-02-01T00:00:00").toDate()))));
        assertThat(trigger.getKey().getName(), is(equalTo(JOB_KEY.toString())));
        assertThat(trigger.getKey().getGroup(), is(equalTo(GROUP_NAME)));
    }

    @Test
    @DisplayName("Test creation of a job context from a job definition")
    public void testCreateJobContext() throws ParseException {
        JobContext jobContext = jobContextFactory.createJobContext(jobDefinition);
        String timeZoneId = "UTC";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));

        assertThat(sdf.format(jobContext.getTrigger().getFireTimeAfter(new DateTime("2019-01-01T00:00:00").toDate())),
                is(equalTo(sdf.format(new DateTime("2019-02-01T00:00:00").toDate()))));
        assertThat(jobContext.getTrigger().getKey().getName(), is(equalTo(JOB_KEY.toString())));
        assertThat(jobContext.getTrigger().getKey().getGroup(), is(equalTo(GROUP_NAME)));
        assertThat(jobContext.getJobDetails().getKey().getName(), is(equalTo(JOB_KEY.toString())));
        assertThat(jobContext.getJobDetails().getKey().getGroup(), is(equalTo(GROUP_NAME)));
        assertThat(jobContext.getJobDetails().getJobDataMap().getString(JOB_KEY_ID), is(equalTo(JOB_KEY.toString())));
    }

}
