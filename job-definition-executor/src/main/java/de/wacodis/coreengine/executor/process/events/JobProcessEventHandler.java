/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.events;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.events.WacodisJobExecutionFailedEvent;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.process.JobProcess;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

/**
 *
 * @author Arne
 */
public class JobProcessEventHandler implements JobProcessExecutedEventHandler, JobProcessFailedEventHandler, JobProcessStartedEventHandler {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JobProcessEventHandler.class);

    private final ApplicationEventPublisher jobProcessFailedPublisher;
    private final WacodisJobWrapper job;

    public JobProcessEventHandler(WacodisJobWrapper job, ApplicationEventPublisher jobProcessFailedPublisher) {
        this.jobProcessFailedPublisher = jobProcessFailedPublisher;
        this.job = job;
    }

    
    @Override
    public void onJobProcessFinished(JobProcessExecutedEvent e) {
        JobProcess subProcess = e.getJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();

        LOGGER.info("sub process {} of wacodis job {} finished sucessfully", subProcess.getJobProcessIdentifier(), jobDefinition.getId());
    }

    @Override
    public void onJobProcessFailed(JobProcessFailedEvent e) {
        JobProcess subProcess = e.getJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();
        
        LOGGER.error("execution of  sub process " + subProcess.getJobProcessIdentifier() + " of wacodis job " + jobDefinition.getId() + " failed", e.getException());
        //trigger job execution failed event
        if (this.job.getExecutionContext().getRetryCount() < this.job.getJobDefinition().getRetrySettings().getMaxRetries()) { //check if retry
            //fire event to schedule retry attempt
            int retries = this.job.incrementRetryCount();
            LOGGER.info("publish jobExecutionFailedEvent to schedule retry attempt {} of {}, WacodisJobID {}, toolID: {}", retries, job.getJobDefinition().getRetrySettings().getMaxRetries(), jobDefinition.getId(), subProcess.getExecutionContext().getWacodisProcessID());
            WacodisJobExecutionFailedEvent failEvent = new WacodisJobExecutionFailedEvent(this, job, new ExecutionException(e.getException()));
            this.jobProcessFailedPublisher.publishEvent(failEvent);
        } else {
            //retries exceeded, job failed ultimately
            LOGGER.error("execution of wacodis job failed ultimately after " + job.getExecutionContext().getRetryCount() + " of " + job.getJobDefinition().getRetrySettings().getMaxRetries() + " retries, WacodisJobID: " + jobDefinition.getId().toString() + ",toolID: " + subProcess.getExecutionContext().getWacodisProcessID());
        }
    }

    @Override
    public void onJobProcessStarted(JobProcessStartedEvent e) {
        JobProcess subProcess = e.getJobProcess();
        WacodisJobDefinition jobDefinition = subProcess.getJobDefinition();

        LOGGER.info("execution of sub process {} of wacodis job {} started", subProcess.getJobProcessIdentifier(), jobDefinition.getId());
    }

}
