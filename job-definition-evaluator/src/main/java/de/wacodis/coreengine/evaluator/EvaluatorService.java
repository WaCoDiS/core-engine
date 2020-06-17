/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator;

import de.wacodis.coreengine.evaluator.wacodisjobevaluation.JobEvaluatorService;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobInputTracker;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 *
 * @author Arne
 */
@Service
public class EvaluatorService implements JobEvaluatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluatorService.class);

    @Autowired
    private ApplicationEventPublisher publisher;
    
    @Autowired
    private JobEvaluatorRunner jobEvaluator;

    public EvaluatorService(ApplicationEventPublisher publisher, JobEvaluatorRunner jobEvaluator) {
        this.publisher = publisher;
        this.jobEvaluator = jobEvaluator;
    }

    public EvaluatorService() {
    }

    public ApplicationEventPublisher getPublisher() {
        return publisher;
    }

    public void setPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public JobEvaluatorRunner getJobEvaluator() {
        return jobEvaluator;
    }

    public void setJobEvaluator(JobEvaluatorRunner jobEvaluator) {
        this.jobEvaluator = jobEvaluator;
    }

    @Override
    public void handleJobEvaluation(WacodisJobWrapper job, WacodisJobInputTracker inputTracker) {
        LOGGER.info("evaluate WacodisJob " + job.getJobDefinition().getName() + " (Id: " + job.getJobDefinition().getId() + ")");
        //check again (items might have been removed from DataAccess during waiting time)
        EvaluationStatus evaluationStatus = this.jobEvaluator.evaluateJob(job);

        if (evaluationStatus.equals(EvaluationStatus.EXECUTABLE)) {
            LOGGER.info("evaluation of WacodisJob " + job.getJobDefinition().getName() + " (Id: " + job.getJobDefinition().getId() + ")  is executable, publishing WacodisJobExecutableEvent");
            //publish execution event
            WacodisJobExecutableEvent event = new WacodisJobExecutableEvent(this, job, evaluationStatus);
            this.publisher.publishEvent(event);
            //remove from InputTracker
            inputTracker.removeJob(job);

        } else {
            //wait for further DataEnvelopes     
            LOGGER.info("WacodisJob " + job.getJobDefinition().getName() + " (Id: " + job.getJobDefinition().getId() + ") is not yet executable, wait for further accessible DataEnvelopes");
        }
    }

}

