/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
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
