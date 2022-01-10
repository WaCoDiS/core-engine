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

import de.wacodis.coreengine.executor.process.JobProcess;
import de.wacodis.coreengine.executor.process.WacodisJobExecutor;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.joda.time.DateTime;

/**
 *
 * @author Arne
 */
public class WacodisJobExecutionEvent {

    private final JobProcess currentJobProcess;
    private final List<JobProcess> allJobProcesses;
    private final ProcessExecutionEventType eventType;
    private final WacodisJobExecutor executor;
    private final ExecutorService executorService;
    private final DateTime timestamp;

    public WacodisJobExecutionEvent(JobProcess currentJobProcess, List<JobProcess> allJobProcesses, ProcessExecutionEventType eventType, ExecutorService executorService, WacodisJobExecutor executor) {
        this(currentJobProcess,allJobProcesses, eventType, executorService, executor, DateTime.now());
    }

    public WacodisJobExecutionEvent(JobProcess currentJobProcess, List<JobProcess> allJobProcesses, ProcessExecutionEventType eventType,  ExecutorService executorService, WacodisJobExecutor executor, DateTime timestamp) {
        this.currentJobProcess = currentJobProcess;
        this.allJobProcesses = allJobProcesses;
        this.eventType = eventType;
        this.executor = executor;
        this.executorService = executorService;
        this.timestamp = timestamp;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }


    public JobProcess getCurrentJobProcess() {
        return currentJobProcess;
    }

    public List<JobProcess> getAllJobProcesses() {
        return allJobProcesses;
    }

    public ProcessExecutionEventType getEventType() {
        return eventType;
    }

    public WacodisJobExecutor getExecutor() {
        return executor;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public enum ProcessExecutionEventType {
        FIRSTPROCESSSTARTED, FINALPROCESSFINISHED
    }
}
