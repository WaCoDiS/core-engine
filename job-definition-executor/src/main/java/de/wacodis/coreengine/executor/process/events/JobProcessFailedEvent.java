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
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.coreengine.executor.exception.JobProcessCompletionException;
import de.wacodis.coreengine.executor.process.JobProcess;
import de.wacodis.coreengine.executor.process.WacodisJobExecutor;
import org.joda.time.DateTime;

/**
 *
 * @author Arne
 */
public class JobProcessFailedEvent {
    
    private final JobProcess jobProcess;
    private final JobProcessCompletionException exception;
    private final WacodisJobExecutor executor;
    private final DateTime timestamp;

    public JobProcessFailedEvent(JobProcess jobProcess, JobProcessCompletionException exception, WacodisJobExecutor executor) {
        this(jobProcess, exception, executor, DateTime.now());
    }

    public JobProcessFailedEvent(JobProcess jobProcess, JobProcessCompletionException exception, WacodisJobExecutor executor, DateTime timestamp) {
        this.jobProcess = jobProcess;
        this.exception = exception;
        this.executor = executor;
        this.timestamp = timestamp;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public JobProcess getJobProcess() {
        return jobProcess;
    }

    public JobProcessCompletionException getException() {
        return exception;
    }

    public WacodisJobExecutor getExecutor() {
        return executor;
    }
}
