/*
 * Copyright 2018-2021 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.wacodis.coreengine.scheduling.job;

import de.wacodis.core.models.AbstractWacodisJobExecutionEvent;
import de.wacodis.core.models.SingleJobExecutionEvent;
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
        //jobData contains retryCount and orig. execution time if retry
        int retryCount = (jobData.containsKey(RETRY_COUNT_KEY)) ? jobData.getIntFromString(RETRY_COUNT_KEY) : 0; //zero retries happened if not present (regular execution)

        try {
            UUID executionID;

            if (jobData.getString(EXECUTION_ID_KEY) == null || jobData.getString(EXECUTION_ID_KEY).isEmpty()) { //check if regular excution or retry
                executionID = UUID.randomUUID(); //is regular execution if ID does not exist -> generate ID
            } else {
                executionID = UUID.fromString(jobData.getString(EXECUTION_ID_KEY)); //is retry if ID already exits
            }

            WacodisJobDefinition job = jobRepositoryProvider.getJobDefinitionForId(id);
            DateTime executionTime = getTemporalCoverageEndDate(job, jobData, context); //(original) fire time or temporalCoverageEndDate (in case of SingleJobExecuionEvent)
            WacodisJobExecutionContext execContext = new WacodisJobExecutionContext(executionID, executionTime, retryCount);
            WacodisJobWrapper jobWrapper = new WacodisJobWrapper(execContext, job);
            evaluator.evaluateJob(jobWrapper, true);
        } catch (JobRepositoryRequestException ex) {
            LOGGER.error(ex.getMessage());
            LOGGER.debug("Error while requesting JobDefinition.", ex);
        }
    }

    private DateTime getTemporalCoverageEndDate(WacodisJobDefinition jobDef, JobDataMap quartzJobData, JobExecutionContext quartzContext) {
        AbstractWacodisJobExecutionEvent event = jobDef.getExecution().getEvent();
        DateTime tempCovEndDate;

        //use specified end date in case job is single execution job
        if (event != null && event instanceof SingleJobExecutionEvent) {
            SingleJobExecutionEvent singleExecEvent = (SingleJobExecutionEvent) event;
            
            if(singleExecEvent.getTemporalCoverageEndDate() != null){
                tempCovEndDate = singleExecEvent.getTemporalCoverageEndDate();
            }else{
                if(jobDef.getExecution() != null && jobDef.getExecution().getStartAt() != null){
                    //use startAt parameter if temporalCoverageEndDate is missing
                    tempCovEndDate = jobDef.getExecution().getStartAt();
                }else if(jobDef.getCreated() != null){
                    //use job creation date if temporalCoverageEndDate is missing and startAt is not 
                    tempCovEndDate = jobDef.getCreated();
                }else{
                    // use current DateTime as fallback
                    tempCovEndDate = DateTime.now();
                }
            }
        } else { //else use trigger date as end date
            tempCovEndDate = (quartzJobData.containsKey(RETRY_FIRST_EXECUTION_TIME_KEY)) ? DateTime.parse(quartzJobData.getString(RETRY_FIRST_EXECUTION_TIME_KEY)) : new DateTime(quartzContext.getFireTime()); //keep execution time of first (regular) execution if retry
        }

        return tempCovEndDate;
    }
}
