/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisherChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Arne
 */
public class SynchronousProcessExecutor implements WacodisJobExecutor {

    //single thread executor makes async process executor effectively sequential
    ExecutorService singleThreadService = Executors.newSingleThreadExecutor();;

    private final AsynchronousWacodisJobExecutor wacodisJobExecutor;

    public SynchronousProcessExecutor(Process toolProcess, ToolMessagePublisherChannel toolMessagePublisher, List<ProcessContext> subProcesses, WacodisJobDefinition jobDefinition) {
        this.wacodisJobExecutor = new AsynchronousWacodisJobExecutor(toolProcess, toolMessagePublisher, subProcesses, jobDefinition, singleThreadService);
    }

    public SynchronousProcessExecutor(Process toolProcess, ToolMessagePublisherChannel toolMessagePublisher, List<ProcessContext> subProcesses, WacodisJobDefinition jobDefinition, long messagePublishingTimeout_Millis) { 
        this.wacodisJobExecutor = new AsynchronousWacodisJobExecutor(toolProcess, toolMessagePublisher, subProcesses, jobDefinition, singleThreadService, messagePublishingTimeout_Millis);
    }

    @Override
    public void setProcessStartedHandler(ProcessExecutionHandler processStartedHandler) {
        this.wacodisJobExecutor.setProcessStartedHandler(processStartedHandler);
    }

    @Override
    public void setProcessExecutedHandler(ProcessExecutionHandler processExecutedHandler) {
        this.wacodisJobExecutor.setProcessExecutedHandler(processExecutedHandler);
    }

    @Override
    public void setProcessFailedHandler(ProcessExecutionHandler processFailedHandler) {
        this.wacodisJobExecutor.setProcessFailedHandler(processFailedHandler);
    }

    @Override
    public void setFirstProcessStartedHandler(ProcessExecutionHandler firstProcessStartedHandler) {
        this.wacodisJobExecutor.setFirstProcessStartedHandler(firstProcessStartedHandler);
    }

    @Override
    public void setLastProcessExecutionHandler(ProcessExecutionHandler lastProcessExecutionHandler) {
        this.wacodisJobExecutor.setLastProcessExecutionHandler(lastProcessExecutionHandler);
    }

    @Override
    public void executeAllSubProcesses() {
        this.wacodisJobExecutor.executeAllSubProcesses();
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.singleThreadService;
    }

}
