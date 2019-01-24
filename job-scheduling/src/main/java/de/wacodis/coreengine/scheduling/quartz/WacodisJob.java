/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.quartz;

import static de.wacodis.coreengine.scheduling.quartz.WacodisSchedulingConstants.JOB_KEY_ID;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class WacodisJob implements Job {
    
    //TODO injectable Job
    private static final Logger LOGGER = LoggerFactory.getLogger(WacodisJob.class);
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        LOGGER.info("Executing JOB: {}", context.getJobDetail().getKey());
        // TODO fetch JobDefinition
        context.getJobDetail().getJobDataMap().get(JOB_KEY_ID);
    }
    
}
