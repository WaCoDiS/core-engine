/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.job;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.JobEvaluatorRunner;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import static de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants.JOB_KEY_ID;
import de.wacodis.coreengine.scheduling.http.jobrepository.JobRepositoryProvider;
import de.wacodis.coreengine.scheduling.http.jobrepository.JobRepositoryRequestException;
import org.joda.time.DateTime;
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

    @Autowired
    private JobRepositoryProvider jobRepositoryProvider;
    
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("Executing job: {}", context.getJobDetail().getKey());
        String id = context.getJobDetail().getJobDataMap().getString(JOB_KEY_ID);

        try {
            WacodisJobDefinition job = jobRepositoryProvider.getJobDefinitionForId(id);
            WacodisJobWrapper jobWrapper = new WacodisJobWrapper(job, new DateTime(context.getFireTime()));
            evaluator.evaluateJob(jobWrapper);
        } catch (JobRepositoryRequestException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while requesting JobDefinition.", ex);
        }
    }

}
