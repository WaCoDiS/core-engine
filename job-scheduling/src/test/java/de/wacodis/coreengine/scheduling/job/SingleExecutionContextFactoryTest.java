/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.job;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionRetrySettings;
import de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {JobContextFactory.class, JobDetailFactory.class, SingleExecutionJobContextFactory.class})
public class SingleExecutionContextFactoryTest {

    private static final UUID JOB_KEY = UUID.randomUUID();

    @Autowired
    private SingleExecutionJobContextFactory jobContextFactory;

    private WacodisJobDefinition jobDefinition;
    private Map<String, Object> execParams;
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
        execParams = new HashMap<>();
        execParams.put(WacodisSchedulingConstants.RETRY_COUNT_KEY, retryCount);
        execParams.put(WacodisSchedulingConstants.EXECUTION_ID_KEY, executionID.toString());
    }

    @Test
    @DisplayName("test create job context for immediate retry")
    public void testCreateRetryJobContext() {
        JobContext jc = jobContextFactory.createSingleExecutionJobContextStartNow(jobDefinition, execParams);

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
    public void testCreateRetryJobContextDelayed() {
        long earliestTriggerTime = System.currentTimeMillis() + jobDefinition.getRetrySettings().getRetryDelayMillies();
        JobContext jc = jobContextFactory.createSingleExecutionContextStartDelayed(jobDefinition, execParams);

        JobDetail jd = jc.getJobDetails();
        JobDataMap jdm = jd.getJobDataMap();
        Trigger t = jc.getTrigger();

        assertEquals(retryCount, jdm.getInt(WacodisSchedulingConstants.RETRY_COUNT_KEY));
        assertEquals(executionID, UUID.fromString(jdm.getString(WacodisSchedulingConstants.EXECUTION_ID_KEY)));
        assertTrue(t.getFinalFireTime().equals(t.getStartTime())); //only fire once at start time
        assertTrue(t.getFinalFireTime().equals(t.getFireTimeAfter(new Date(System.currentTimeMillis() - 10000))));
        assertTrue(t.getFinalFireTime().getTime() >= earliestTriggerTime);
    }

    @Test
    @DisplayName("test delayed trigger is after immediate trigger")
    public void testCompareRetryDelayedRetryNow() {
        Date immediateTrigger = jobContextFactory.createSingleExecutionJobContextStartNow(jobDefinition, execParams).getTrigger().getFinalFireTime();
        Date delayedTrigger = jobContextFactory.createSingleExecutionContextStartDelayed(jobDefinition, execParams).getTrigger().getFinalFireTime();
        
        assertTrue(delayedTrigger.after(immediateTrigger));
    }
}
