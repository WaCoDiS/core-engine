/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import java.util.concurrent.ExecutorService;

/**
 *
 * @author Arne
 */
public interface WacodisJobExecutor {

    void executeAllSubProcesses();

    void setProcessStartedHandler(ProcessExecutionHandler handler);

    void setProcessExecutedHandler(ProcessExecutionHandler handler);

    public void setProcessFailedHandler(ProcessExecutionHandler handler);

    public void setFirstProcessStartedHandler(ProcessExecutionHandler handler);

    public void setLastProcessExecutionHandler(ProcessExecutionHandler handler);
    
    public ExecutorService getExecutorService();
}
