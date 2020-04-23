/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.executor.process.JobProcess;
import de.wacodis.coreengine.executor.process.WacodisJobExecutor;
import java.util.List;
import java.util.concurrent.ExecutorService;

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

    public WacodisJobExecutionEvent(JobProcess currentJobProcess, List<JobProcess> allJobProcesses, ProcessExecutionEventType eventType, ExecutorService executorService, WacodisJobExecutor executor) {
        this.currentJobProcess = currentJobProcess;
        this.allJobProcesses = allJobProcesses;
        this.eventType = eventType;
        this.executor = executor;
        this.executorService = executorService;
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
