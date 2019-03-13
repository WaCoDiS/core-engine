/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import java.util.concurrent.Callable;
import org.slf4j.LoggerFactory;

/**
 * execute wacodis job, 1) execute processing tool, 2) execute cleanUp tool
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobExecutionTask implements Callable<WacodisJobExecutionOutput> {

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
        LOGGER.debug("new thread started for process " + toolContext.getProcessID() + ", toolProcess: " + toolProcess + " cleaUpProcess: " + cleanUpProcess);

        //execute processing tool
        ProcessOutput toolOutput = this.toolProcess.execute(this.toolContext);
        LOGGER.debug("Process: " + toolContext.getProcessID() + ",executed toolProcess " + toolProcess);

        //execute cleanup tool
        ProcessContext cleanUpContext = buildCleanUpToolContext(toolOutput);
        ProcessOutput cleanUpOutput = this.cleanUpProcess.execute(cleanUpContext);
        LOGGER.debug("Process: " + toolContext.getProcessID() + ",executed cleanUpProcess " + cleanUpProcess);

        WacodisJobExecutionOutput jobOutput = buildJobExecutionOutput(toolOutput, cleanUpOutput);

        LOGGER.info("Process: " + toolContext.getProcessID() + ",finished execution");

        return jobOutput;
    }

    private ProcessContext buildCleanUpToolContext(ProcessOutput toolOutput) {
        ProcessContext cleanUpContext = new ProcessContext();

        for (String inputID : toolOutput.getOutputResources().keySet()) { //create input for each output
            ResourceDescription input = toolOutput.getOutputResources().get(inputID);
            cleanUpContext.setInputResource(inputID, input);
        }

        //ToDo: expected outputs cleanUp process
        return cleanUpContext;
    }

    private WacodisJobExecutionOutput buildJobExecutionOutput(ProcessOutput toolOutput, ProcessOutput cleanUpOutput) {
        return new WacodisJobExecutionOutput();
    }

}
