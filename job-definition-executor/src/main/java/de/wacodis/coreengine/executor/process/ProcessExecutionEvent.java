/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.WacodisJobDefinition;

/**
 *
 * @author Arne
 */
public class ProcessExecutionEvent {

    private final WacodisJobDefinition jobDefintion;
    private final String subProcessId;
    private final String message;
    private final ProcessExecutionEventType eventType;

    public ProcessExecutionEvent(WacodisJobDefinition jobDefintion, String subProcessId, String message, ProcessExecutionEventType eventType) {
        this.jobDefintion = jobDefintion;
        this.subProcessId = subProcessId;
        this.message = message;
        this.eventType = eventType;
    }

    public WacodisJobDefinition getJobDefintion() {
        return jobDefintion;
    }

    public String getSubProcessId() {
        return subProcessId;
    }

    public String getMessage() {
        return message;
    }

    public ProcessExecutionEventType getEventType() {
        return eventType;
    }


    public enum ProcessExecutionEventType {
        PROCESSSTARTED, PROCESSEXECUTED, PROCESSFAILED, FIRSTPROCESSSTARTED, FINALPROCESSFINISHED
    }
}
