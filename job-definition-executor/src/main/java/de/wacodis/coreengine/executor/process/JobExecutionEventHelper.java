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

import de.wacodis.coreengine.executor.process.events.JobProcessExecutedEvent;
import de.wacodis.coreengine.executor.process.events.JobProcessExecutedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessFailedEvent;
import de.wacodis.coreengine.executor.process.events.JobProcessFailedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessStartedEvent;
import de.wacodis.coreengine.executor.process.events.JobProcessStartedEventHandler;
import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEvent;
import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEventHandler;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne
 */
public class JobExecutionEventHelper {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AsynchronousWacodisJobExecutor.class);

    public static void fireProcessStartedEvent(JobProcessStartedEvent e, JobProcessStartedEventHandler processStartedHandler) {
        String subProcessId = e.getJobProcess().getJobProcessIdentifier();
        String wacodisJobId = e.getJobProcess().getJobDefinition().getId().toString();

        if (processStartedHandler != null) {
            LOGGER.debug("fire event of type {} for job process {} of wacodis job {}", e.getClass().getSimpleName(), subProcessId, wacodisJobId);
            processStartedHandler.onJobProcessStarted(e);
        } else {
            LOGGER.warn("cannot fire event of type {}, event handler is null", e.getClass().getSimpleName());
        }
    }

    public static void fireProcessExecutedEvent(JobProcessExecutedEvent e, JobProcessExecutedEventHandler processExecutedHandler, boolean isFinalJobProcess) {
        String subProcessId = e.getJobProcess().getJobProcessIdentifier();
        String wacodisJobId = e.getJobProcess().getJobDefinition().getId().toString();

        if (processExecutedHandler != null) {
            LOGGER.debug("fire event of type {} for job process {} of wacodis job {}", e.getClass().getSimpleName(), subProcessId, wacodisJobId);
            processExecutedHandler.onJobProcessFinished(e, isFinalJobProcess);
        } else {
            LOGGER.warn("cannot fire event of type {}, event handler is null", e.getClass().getSimpleName());
        }
    }

    public static void fireProcessFailedEvent(JobProcessFailedEvent e, JobProcessFailedEventHandler processFailedHandler, boolean isFinalJobProcess) {
        String subProcessId = e.getJobProcess().getJobProcessIdentifier();
        String wacodisJobId = e.getJobProcess().getJobDefinition().getId().toString();

        if (processFailedHandler != null) {
            LOGGER.debug("fire event of type {} for job process {} of wacodis job {}", e.getClass().getSimpleName(), subProcessId, wacodisJobId);
            processFailedHandler.onJobProcessFailed(e, isFinalJobProcess);
        } else {
            LOGGER.warn("cannot fire event of type {}, event handler is null", e.getClass().getSimpleName());
        }
    }

    public static void fireWacodisJobExecutionEvent(WacodisJobExecutionEvent e, WacodisJobExecutionEventHandler eventHandler) {
        String wacodisJobId = e.getCurrentJobProcess().getJobDefinition().getId().toString();

        if (eventHandler != null) {
            LOGGER.debug("fire event {} of type {} for of wacodis job {}", e.getClass().getSimpleName(), wacodisJobId);

            switch (e.getEventType()) {
                case FINALPROCESSFINISHED:
                    eventHandler.onFinalJobProcessFinished(e);
                    break;
                case FIRSTPROCESSSTARTED:
                    eventHandler.onFirstJobProcessStarted(e);
                    break;
                default:
                    LOGGER.error("cannot fire event {} of type {}, event type {} is unknown", e.getEventType(), e.getClass().getSimpleName(), e.getEventType());
                    break;
            }

        } else {
            LOGGER.warn("cannot fire event {} of type {}, event handler is null", e.getEventType(), e.getClass().getSimpleName());
        }
    }



}
