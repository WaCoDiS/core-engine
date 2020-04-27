/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.executor.process.JobProcess;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne
 */
public class JobExecutionEventHandler implements WacodisJobExecutionEventHandler {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JobProcessEventHandler.class);

    @Override
    public void onFirstJobProcessStarted(WacodisJobExecutionEvent e) {
        JobProcess subProcess = e.getCurrentJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();

        LOGGER.info("execution of wacodis job {} started, first sub process {} started", jobDefinition.getId(), subProcess.getJobProcessIdentifier());
    }

    @Override
    public void onFinalJobProcessFinished(WacodisJobExecutionEvent e) {
        JobProcess subProcess = e.getCurrentJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();

        LOGGER.info("final subprocess ({}) of wacodis job {} finished, shutdown threadpool", subProcess.getJobProcessIdentifier(), jobDefinition.getId());
        if (!e.getExecutorService().isShutdown()) {
            e.getExecutorService().shutdown();
             LOGGER.debug("successfully shut down threadpool for execution of wacodis job {}", jobDefinition.getId());
        }else{
            LOGGER.debug("threadpool for execution of wacodis job {} is already shut down", jobDefinition.getId());
        }
    }

}
