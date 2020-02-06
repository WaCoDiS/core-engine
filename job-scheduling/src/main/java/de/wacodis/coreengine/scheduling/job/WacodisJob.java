/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.job;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.JobEvaluatorRunner;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import static de.wacodis.coreengine.scheduling.configuration.WacodisSchedulingConstants.*;
import de.wacodis.coreengine.scheduling.http.jobrepository.JobRepositoryProvider;
import de.wacodis.coreengine.scheduling.http.jobrepository.JobRepositoryRequestException;
import java.util.UUID;
import org.joda.time.DateTime;
import org.quartz.JobDataMap;
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
        JobDataMap jobData = context.getMergedJobDataMap();
        String id = jobData.getString(JOB_KEY_ID);
        
        try {
            UUID executionID;
            
            if(jobData.getString(EXECUTION_ID_KEY) == null || jobData.getString(EXECUTION_ID_KEY).isEmpty()){ //check if regular excution or retry
                executionID = UUID.randomUUID(); //is regular execution if ID does not exist -> generate ID
            }else{
                executionID = UUID.fromString(jobData.getString(EXECUTION_ID_KEY)); //is retry if ID already exits
            }

            WacodisJobDefinition job = jobRepositoryProvider.getJobDefinitionForId(id);
            WacodisJobExecutionContext execContext = new WacodisJobExecutionContext(executionID, new DateTime(context.getFireTime()), jobData.getIntValue(RETRY_COUNT_KEY));
            WacodisJobWrapper jobWrapper = new WacodisJobWrapper(execContext, job);
            evaluator.evaluateJob(jobWrapper, true);
        } catch (JobRepositoryRequestException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while requesting JobDefinition.", ex);
        }
    }

}
