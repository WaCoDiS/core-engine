/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.job;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecution;
import de.wacodis.core.models.WacodisJobDefinitionRetrySettings;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants;
import static de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants.JOB_KEY_ID;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.joda.time.DateTime;
import org.junit.Assert;
import static org.junit.Assert.*;
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
@SpringBootTest(classes = {JobContextFactory.class, JobDetailFactory.class, RetryJobContextFactory.class})
public class RetryJobContextFactoryTest {

    private static final String GROUP_NAME = "de.hsbo.wacodis";
    private static final UUID JOB_KEY = UUID.randomUUID();

    @Autowired
    private RetryJobContextFactory jobContextFactory;

    private WacodisJobDefinition jobDefinition;
    private WacodisJobExecutionContext execContext;
    private UUID executionID;
    private int retryCount;

    @BeforeEach
    public void setup() {
        jobDefinition = new WacodisJobDefinition();
        jobDefinition.setId(JOB_KEY);
        WacodisJobDefinitionRetrySettings retrySettings = new WacodisJobDefinitionRetrySettings();
        retrySettings.setMaxRetries(3);
        retrySettings.setRetryDelayMillies(600000l); // + 10 min
        jobDefinition.setRetrySettings(retrySettings);

        executionID = UUID.randomUUID();
        retryCount = 3;
        execContext = new WacodisJobExecutionContext(executionID, new DateTime(), retryCount);
    }

    @Test
    @DisplayName("test create job context for immediate retry")
    public void testCreateRetryJobContext() {
        JobContext jc = jobContextFactory.createRetryJobContext(jobDefinition, execContext);

        JobDetail jd = jc.getJobDetails();
        JobDataMap jdm = jd.getJobDataMap();
        Trigger t = jc.getTrigger();

        assertEquals(retryCount, jdm.getInt(WacodisSchedulingConstants.RETRY_COUNT_KEY));
        assertEquals(executionID, UUID.fromString(jdm.getString(WacodisSchedulingConstants.EXECUTION_ID_KEY)));
        assertTrue(t.getFinalFireTime().equals(t.getStartTime())); //only fire once at start time
        assertTrue(t.getFinalFireTime().equals(t.getFireTimeAfter(new Date(System.currentTimeMillis() - 10000))));
    }

    @Test
    @DisplayName("test create job context for delayed retry")
    public void testCreateRetryJobContext_RetryAt() {
        long now = System.currentTimeMillis();
        Date retryAt = new Date(now + jobDefinition.getRetrySettings().getRetryDelayMillies());
        JobContext jc = jobContextFactory.createRetryJobContext(jobDefinition, execContext, retryAt);

        JobDetail jd = jc.getJobDetails();
        JobDataMap jdm = jd.getJobDataMap();
        Trigger t = jc.getTrigger();
        
        assertEquals(retryCount, jdm.getInt(WacodisSchedulingConstants.RETRY_COUNT_KEY));
        assertEquals(executionID, UUID.fromString(jdm.getString(WacodisSchedulingConstants.EXECUTION_ID_KEY)));
        assertTrue(t.getFinalFireTime().equals(t.getStartTime())); //only fire once at start time
        assertTrue(t.getFinalFireTime().equals(t.getFireTimeAfter(new Date(System.currentTimeMillis() - 10000))));
        assertTrue(t.getFinalFireTime().equals(retryAt));
    }

}
