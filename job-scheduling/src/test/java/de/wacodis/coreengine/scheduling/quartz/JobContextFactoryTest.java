/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.quartz;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecution;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.MatcherAssert.assertThat;
import org.joda.time.DateTime;
import static org.junit.Assert.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.quartz.CronExpression;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class JobContextFactoryTest {

    private static final UUID JOB_KEY = UUID.randomUUID();
    private static final String UNIX_CRON_EXPRESSION = "0 0 1 * *";
    private static final String QUARTZ_CRON_EXPRESSION = "0 0 0 1 * ? *";

    private JobContextFactory jobContextFactory;
    private WacodisJobDefinition jobDefinition;

    @BeforeEach
    public void setup() {
        jobContextFactory = new JobContextFactory();
        jobDefinition = new WacodisJobDefinition();
        jobDefinition.setId(JOB_KEY);
        jobDefinition.setExecution(new WacodisJobDefinitionExecution().pattern(UNIX_CRON_EXPRESSION));

    }

    @Test
    @DisplayName("Test creation of a cron schedule from a valid cron expression")
    public void testCronScheduleCreationForValidExpression() throws ParseException {
        String timeZoneId = "UTC";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));

        CronExpression expr = jobContextFactory.createCronSchedule(jobDefinition);

        assertThat(expr.getCronExpression(), is(equalTo(QUARTZ_CRON_EXPRESSION)));
        assertThat(sdf.format(expr.getNextValidTimeAfter(new DateTime("2019-01-01T00:00:00").toDate())),
                is(equalTo(sdf.format(new DateTime("2019-02-01T00:00:00").toDate()))));
    }

}
