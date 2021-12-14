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
package de.wacodis.coreengine.executor.exception;

import java.util.UUID;

/**
 * use to signal a failure during the execution of a process
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ExecutionException extends Exception {

    private final static String PROCESSIDUNASSIGNED_TEXT = "unassigned";

    private final UUID wacodisJobId;
    private String processId;

    public ExecutionException(UUID wacodisJobId) {
        this.wacodisJobId = wacodisJobId;
    }

    public ExecutionException(UUID wacodisJobId, String message) {
        super(message);
        this.wacodisJobId = wacodisJobId;
    }

    public ExecutionException(UUID wacodisJobId, String message, Throwable cause) {
        super(message, cause);
        this.wacodisJobId = wacodisJobId;
    }

    public ExecutionException(UUID wacodisJobId, Throwable cause) {
        super(cause);
        this.wacodisJobId = wacodisJobId;
    }

    public ExecutionException(UUID wacodisJobId, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.wacodisJobId = wacodisJobId;
    }

    /**
     * @return internal process id (e.g wps process id) or unassigned text
     */
    public String getProcessId() {
        if (processId != null) {
            return processId;
        } else {
            return getPROCESSIDUNASSIGNED_TEXT();
        }
    }

    /**
     * @param processId internal process id (e.g wps process id)
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * @return default value if this.processId is unassigned
     */
    public static String getPROCESSIDUNASSIGNED_TEXT() {
        return PROCESSIDUNASSIGNED_TEXT;
    }

    public UUID getWacodisJobId() {
        return wacodisJobId;
    }

}
