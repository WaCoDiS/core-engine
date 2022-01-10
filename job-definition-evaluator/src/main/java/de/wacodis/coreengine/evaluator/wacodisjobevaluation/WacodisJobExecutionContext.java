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

import java.util.UUID;
import org.joda.time.DateTime;

/**
 *
 * @author Arne
 */
public class WacodisJobExecutionContext {

    private final UUID executionID;
    private final int retryCount;
    private final DateTime executionTime;

    public WacodisJobExecutionContext(UUID executionID, DateTime executionTime, int retryCount) {
        this.executionID = executionID;
        this.retryCount = retryCount;
        this.executionTime = executionTime;
    }

    public UUID getExecutionID() {
        return executionID;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public DateTime getExecutionTime() {
        return executionTime;
    }

    public WacodisJobExecutionContext createCopyWithIncrementedRetryCount() {
        int incrementedRetryCount = this.retryCount + 1;
        return new WacodisJobExecutionContext(this.executionID, this.executionTime, incrementedRetryCount);
    }
}
