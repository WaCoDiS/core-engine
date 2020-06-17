/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import java.util.UUID;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobInputTrackerTest {

    @Test
    public void testAddRemoveJob() {
        WacodisJobInputTracker tracker = new WacodisJobInputTracker((WacodisJobWrapper job, WacodisJobInputTracker inputTracker) -> {
            //dummy, do nothing
        }, (AbstractDataEnvelope dataEnvelope, WacodisJobWrapper jobWrapper, AbstractSubsetDefinition subsetDefinition) -> true);

        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        jobDef.setId(UUID.randomUUID());
        WacodisJobWrapper job = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), DateTime.now(), 0), jobDef);

        tracker.addJob(job);
        assertTrue(tracker.containsJob(job));
        assertTrue(tracker.getScheduledJobs().contains(job));

        tracker.removeJob(job);
        assertFalse(tracker.containsJob(job));

        tracker.addJob(job);
        tracker.clearJobs();
        assertTrue(tracker.getScheduledJobs().isEmpty());
    }

    @Test
    public void testReturnScheduledJobAsUnmodifiableList() {
        WacodisJobInputTracker tracker = new WacodisJobInputTracker((WacodisJobWrapper job, WacodisJobInputTracker inputTracker) -> {
            //dummy, do nothing
        }, (AbstractDataEnvelope dataEnvelope, WacodisJobWrapper jobWrapper, AbstractSubsetDefinition subsetDefinition) -> true);
        
        //unsupported operation for unmodifiable list
        assertThrows(UnsupportedOperationException.class, () -> tracker.getScheduledJobs().clear());
    }

}
