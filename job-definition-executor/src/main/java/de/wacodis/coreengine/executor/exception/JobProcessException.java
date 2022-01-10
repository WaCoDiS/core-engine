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
package de.wacodis.coreengine.executor.exception;

import de.wacodis.coreengine.executor.process.JobProcess;

/**
 *
 * @author Arne
 */
public class JobProcessException extends Exception{
    
    private final JobProcess jobProcess;

    public JobProcessException(JobProcess jobProcess) {
        this.jobProcess = jobProcess;
    }

    public JobProcessException(JobProcess jobProcess, String message) {
        super(message);
        this.jobProcess = jobProcess;
    }

    public JobProcessException(JobProcess jobProcess, String message, Throwable cause) {
        super(message, cause);
        this.jobProcess = jobProcess;
    }

    public JobProcessException(JobProcess jobProcess, Throwable cause) {
        super(cause);
        this.jobProcess = jobProcess;
    }

    public JobProcessException(JobProcess jobProcess, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.jobProcess = jobProcess;
    }

    public JobProcess getJobProcess() {
        return jobProcess;
    }
}
