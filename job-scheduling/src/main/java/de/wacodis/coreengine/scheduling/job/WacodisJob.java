/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.job;

import de.wacodis.coreengine.evaluator.JobEvaluatorRunner;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class WacodisJob extends QuartzJobBean {

    //TODO injectable Job
    private static final Logger LOGGER = LoggerFactory.getLogger(WacodisJob.class);

    @Autowired
    private JobEvaluatorRunner evaluator;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("Executing job: {}", context.getJobDetail().getKey());
        // TODO fetch JobDefinition
        // TODO execute evaluator
    }

}
