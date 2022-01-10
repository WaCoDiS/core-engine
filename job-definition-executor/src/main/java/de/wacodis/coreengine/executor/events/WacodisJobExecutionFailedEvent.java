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
package de.wacodis.coreengine.executor.events;


import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import org.springframework.context.ApplicationEvent;

/**
 * event to indicate that the execution of a wacodis job failed
 * @author Arne
 */
public class WacodisJobExecutionFailedEvent extends ApplicationEvent {
    
    private final WacodisJobWrapper wacodisJob;
    private final Throwable failure;

    public WacodisJobExecutionFailedEvent(Object source, WacodisJobWrapper wacodisJob, Throwable failure) {
        super(source);
        this.wacodisJob = wacodisJob;
        this.failure = failure;
    }

    public WacodisJobWrapper getWacodisJob() {
        return wacodisJob;
    } 

    public Throwable getFailure() {
        return failure;
    }
}
