/*
 * Copyright 2018-2021 52°North Spatial Information Research GmbH
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
package de.wacodis.coreengine.executor.process;

import de.wacodis.coreengine.executor.process.events.JobProcessExecutedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessFailedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessStartedEventHandler;
import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEventHandler;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Arne
 */
public class SequentialWacodisJobExecutor implements WacodisJobExecutor {

    //single thread executor makes async process executor effectively sequential
    ExecutorService singleThreadService = Executors.newSingleThreadExecutor();;

    private final AsynchronousWacodisJobExecutor wacodisJobExecutor;

    public SequentialWacodisJobExecutor() {
        this.wacodisJobExecutor = new AsynchronousWacodisJobExecutor( this.singleThreadService);
    }
    
    @Override
    public void executeAllSubProcesses(List<JobProcess> subProcesses) {
        this.wacodisJobExecutor.executeAllSubProcesses(subProcesses);
    }

    @Override
    public void setProcessStartedHandler(JobProcessStartedEventHandler handler) {
        this.wacodisJobExecutor.setProcessStartedHandler(handler);
    }

    @Override
    public void setProcessExecutedHandler(JobProcessExecutedEventHandler handler) {
        this.wacodisJobExecutor.setProcessExecutedHandler(handler);
    }

    @Override
    public void setProcessFailedHandler(JobProcessFailedEventHandler handler) {
        this.wacodisJobExecutor.setProcessFailedHandler(handler);
    }

    @Override
    public void setFirstProcessStartedHandler(WacodisJobExecutionEventHandler handler) {
        this.wacodisJobExecutor.setFirstProcessStartedHandler(handler);
    }

    @Override
    public void setFinalProcessFinishedHandler(WacodisJobExecutionEventHandler handler) {
        this.wacodisJobExecutor.setFinalProcessFinishedHandler(handler);
    }



}
