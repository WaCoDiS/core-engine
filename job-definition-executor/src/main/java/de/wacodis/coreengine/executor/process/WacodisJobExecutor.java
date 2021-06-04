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

import de.wacodis.coreengine.executor.process.events.JobProcessStartedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessExecutedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessFailedEventHandler;
import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEventHandler;
import java.util.List;

/**
 *
 * @author Arne
 */
public interface WacodisJobExecutor {

    void executeAllSubProcesses(List<JobProcess> subProcesses);

    void setProcessStartedHandler(JobProcessStartedEventHandler handler);

    void setProcessExecutedHandler(JobProcessExecutedEventHandler handler);

    public void setProcessFailedHandler(JobProcessFailedEventHandler handler);

    public void setFirstProcessStartedHandler(WacodisJobExecutionEventHandler handler);

    public void setFinalProcessFinishedHandler(WacodisJobExecutionEventHandler handler);
}
