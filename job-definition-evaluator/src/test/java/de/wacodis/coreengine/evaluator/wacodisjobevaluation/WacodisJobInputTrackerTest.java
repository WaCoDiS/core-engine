/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobInputTrackerTest {

    private WacodisJobInputTracker inputTracker;

    @BeforeEach
    private void initInputTracker() {
        //mock matcher for testing
        DataEnvelopeMatcher matcher = new DataEnvelopeMatcher() {
            @Override
            public boolean match(AbstractDataEnvelope dataEnvelope, WacodisJobWrapper jobWrapper, AbstractSubsetDefinition subsetDefinition) {
                return true;
            }

        };
        this.inputTracker = new WacodisJobInputTracker(matcher);
    }

    /**
     * Test of addMatcher method, of class WacodisJobInputTracker.
     */
    @Test
    public void testGetMatcher() {
        BasicDataEnvelopeMatcher matcher = new BasicDataEnvelopeMatcher();
        WacodisJobInputTracker tracker = new WacodisJobInputTracker(matcher);

        assertEquals(matcher, tracker.getMatcher());
    }

    /**
     * Test of addJob method, of class WacodisJobInputTracker.
     */
    @Test
    public void testAddJob() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, DateTime.parse("2019-01-01T00:00:00Z"));

        this.inputTracker.addJob(job);
        assertTrue(this.inputTracker.getScheduledJobs().contains(job));
    }

    /**
     * Test of removeJob method, of class WacodisJobInputTracker.
     */
    @Test
    public void testRemoveJob() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, DateTime.parse("2019-01-01T00:00:00Z"));

        this.inputTracker.addJob(job);
        this.inputTracker.removeJob(job);
        assertFalse(this.inputTracker.getScheduledJobs().contains(job));
    }

    /**
     * Test of clearJobs method, of class WacodisJobInputTracker.
     */
    @Test
    public void testClear_scheduledJobs() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, DateTime.parse("2019-01-01T00:00:00Z"));

        this.inputTracker.addJob(job);
        this.inputTracker.clearJobs();
        assertEquals(0, this.inputTracker.getScheduledJobs().size());
    }

    /**
     * Test of clearJobs method, of class WacodisJobInputTracker.
     */
    @Test
    @DisplayName("check no executable jobs if scheduledJobs are cleared")
    public void testClear_executableJobs() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, DateTime.parse("2019-01-01T00:00:00Z"));

        this.inputTracker.addJob(job);
        this.inputTracker.clearJobs();
        assertEquals(0, this.inputTracker.getExecutableJobs().size());
    }

    /**
     * Test of containsJob method, of class WacodisJobInputTracker.
     */
    @Test
    public void testContains() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, DateTime.parse("2019-01-01T00:00:00Z"));

        this.inputTracker.addJob(job);
        assertTrue(this.inputTracker.containsJob(job));
    }

    /**
     * Test of publishDataEnvelope method, of class WacodisJobInputTracker.
     */
    @Test
    @DisplayName("check event is raised when job becomes executable after publishing a DataEnvelope")
    public void testPublishDataEnvelope() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        AbstractSubsetDefinition input = new AbstractSubsetDefinition();
        input.setIdentifier("testID");
        jobDef.getInputs().add(input);
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, DateTime.parse("2019-01-01T00:00:00Z"));
        JobIsExecutableChangeTestListener listener = new JobIsExecutableChangeTestListener();
        this.inputTracker.addJob(job);
        this.inputTracker.addJobIsExecutableChangeListener(listener);
        
        this.inputTracker.publishDataEnvelope(new AbstractDataEnvelope());
        assertTrue(listener.isEventOccured() && listener.isExecutable_LastEvent());       
    }

    /**
     * Test of removeDataEnvelope method, of class WacodisJobInputTracker.
     */
    @Test
    @DisplayName("check event is raised when job is not executable anymore after removing a DataEnvelope")
    public void testRemoveDataEnvelope() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        AbstractSubsetDefinition input = new AbstractSubsetDefinition();
        input.setIdentifier("testID");
        jobDef.getInputs().add(input);
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, DateTime.parse("2019-01-01T00:00:00Z"));
        JobIsExecutableChangeTestListener listener = new JobIsExecutableChangeTestListener();
        this.inputTracker.addJob(job);
        this.inputTracker.addJobIsExecutableChangeListener(listener);
        AbstractDataEnvelope env = new AbstractDataEnvelope();
        
        this.inputTracker.publishDataEnvelope(env);
        assertTrue(listener.isEventOccured() && listener.isExecutable_LastEvent());
        this.inputTracker.removeDataEnvelope(env);
        assertTrue(listener.isEventOccured() && !listener.isExecutable_LastEvent());
    }

    /**
     * Test of addJobIsExecutableChangeListener method, of class
     * WacodisJobInputTracker.
     */
    @Test
    public void testAddJobIsExecutableChangeListener() {
        JobIsExecutableChangeListener listener = (WacodisJobWrapper Job, boolean isExecutbable) -> {
        };
        WacodisJobInputTrackerTestSubclass tracker = new WacodisJobInputTrackerTestSubclass(this.inputTracker.getMatcher());

        tracker.addJobIsExecutableChangeListener(listener);
        assertEquals(1, tracker.getListeners().size());
    }

    /**
     * Test of removeJobIsExecutableChangeListener method, of class
     * WacodisJobInputTracker.
     */
    @Test
    public void testRemoveJobIsExecutableChangeListener() {
        JobIsExecutableChangeListener listener = (WacodisJobWrapper Job, boolean isExecutbable) -> {
        };
        WacodisJobInputTrackerTestSubclass tracker = new WacodisJobInputTrackerTestSubclass(this.inputTracker.getMatcher());

        tracker.addJobIsExecutableChangeListener(listener);
        tracker.removeJobIsExecutableChangeListener(listener);
        assertEquals(0, tracker.getListeners().size());
    }

    /**
     * Test of clearJobIsExecutableChangeListeners method, of class
     * WacodisJobInputTracker.
     */
    @Test
    public void testClearJobIsExecutableChangeListeners() {
        JobIsExecutableChangeListener listener = (WacodisJobWrapper Job, boolean isExecutbable) -> {
        };
        WacodisJobInputTrackerTestSubclass tracker = new WacodisJobInputTrackerTestSubclass(this.inputTracker.getMatcher());

        tracker.addJobIsExecutableChangeListener(listener);
        tracker.clearJobIsExecutableChangeListeners();
        assertTrue(tracker.getListeners().isEmpty());
    }

    /**
     * Test of getExecutableJobs method, of class WacodisJobInputTracker.
     */
    @Test
    public void testGetExecutableJobs() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        AbstractSubsetDefinition input = new AbstractSubsetDefinition();
        input.setIdentifier("testID");
        jobDef.getInputs().add(input);
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, DateTime.parse("2019-01-01T00:00:00Z"));

        //order matters
        job.getInputs().get(0).setResourceAvailable(true);
        this.inputTracker.addJob(job);

        assertTrue(this.inputTracker.getExecutableJobs().contains(job));
    }

    /**
     * Test of getExecutableJobs method, of class WacodisJobInputTracker.
     */
    @Test
    @DisplayName("check getExecutableJobs returns unmodifiable collection")
    public void testGetExecutableJobs_Unmodifiable() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, DateTime.parse("2019-01-01T00:00:00Z"));

        assertThrows(UnsupportedOperationException.class, () -> this.inputTracker.getExecutableJobs().add(job));
    }
    
        /**
     * Test of getExecutableJobs method, of class WacodisJobInputTracker.
     */
    @Test
    @DisplayName("check getScheduledJobs returns unmodifiable collection")
    public void testGetScheduledJobs_Unmodifiable() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, DateTime.parse("2019-01-01T00:00:00Z"));

        assertThrows(UnsupportedOperationException.class, () -> this.inputTracker.getScheduledJobs().add(job));
    }
    

    @Test
    @DisplayName("check event is raised when job is already executable when added to the Job Tracker")
    public void testCheckNotification_AddedExecutables() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        AbstractSubsetDefinition input = new AbstractSubsetDefinition();
        input.setIdentifier("testID");
        jobDef.getInputs().add(input);
        WacodisJobWrapper job = new WacodisJobWrapper(jobDef, DateTime.parse("2019-01-01T00:00:00Z"));
        JobIsExecutableChangeTestListener listener = new JobIsExecutableChangeTestListener();
        this.inputTracker.addJobIsExecutableChangeListener(listener);

        //order matters
        job.getInputs().get(0).setResourceAvailable(true);
        this.inputTracker.addJob(job);

        assertTrue(listener.isEventOccured() && listener.isExecutable_LastEvent());
    }

    //mock implementations
    private class WacodisJobInputTrackerTestSubclass extends WacodisJobInputTracker {

        private WacodisJobInputTrackerTestSubclass(DataEnvelopeMatcher matcher) {
            super(matcher);
        }

        private List<JobIsExecutableChangeListener> getListeners() {
            return super.listeners;
        }
    }

    private class JobIsExecutableChangeTestListener implements JobIsExecutableChangeListener {

        private boolean eventOccured = false;
        private boolean executable_LastEvent = false;

        public boolean isEventOccured() {
            return eventOccured;
        }

        public boolean isExecutable_LastEvent() {
            return executable_LastEvent;
        }

        @Override
        public void onJobIsExecutableChanged(WacodisJobWrapper job, boolean isExecutable) {
            this.eventOccured = true;
            this.executable_LastEvent = isExecutable;
        }
    }
}
