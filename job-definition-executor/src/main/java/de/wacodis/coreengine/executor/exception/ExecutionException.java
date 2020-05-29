/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
