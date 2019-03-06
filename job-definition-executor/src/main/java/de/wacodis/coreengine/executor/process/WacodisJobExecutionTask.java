/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import java.util.concurrent.Callable;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobExecutionTask implements Callable<WacodisJobExecutionOutput>{
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobExecutionTask.class);
    
    private final de.wacodis.coreengine.executor.process.Process toolProcess;
    private final de.wacodis.coreengine.executor.process.Process cleanUpProcess;
    private final ProcessContext toolContext;

    public WacodisJobExecutionTask(Process toolProcess, ProcessContext toolContext, Process cleanUpProcess) {
        this.toolProcess = toolProcess;
        this.cleanUpProcess = cleanUpProcess;
        this.toolContext = toolContext;
    }

    @Override
    public WacodisJobExecutionOutput call() throws Exception {
        //ToDo
        // 1) call WPS-Tool-Process
        // 2) get result async
        // 3) call WPS-Storage-Process
        
        ProcessContext toolOutput = this.toolProcess.execute(this.toolContext);
        ProcessContext cleanUpContext = buildCleanUpToolContextFromToolOutput(toolOutput);
        ProcessContext cleanUpOutput = this.cleanUpProcess.execute(cleanUpContext);
        
        WacodisJobExecutionOutput jobOutput = buildJobExecutionOutput(toolOutput, cleanUpOutput);
        
        
        return new WacodisJobExecutionOutput();
    }
    
    
    
    private ProcessContext buildCleanUpToolContextFromToolOutput(ProcessContext toolOutput){
        return new ProcessContext();
    }
    
    private WacodisJobExecutionOutput buildJobExecutionOutput(ProcessContext toolOutput, ProcessContext cleanUpOutput){
        return new WacodisJobExecutionOutput();
    }

}
