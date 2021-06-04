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

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating bean-style Quartz {@Link JobDetail} instances
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@Component
public class JobDetailFactory {

    @Autowired
    private ApplicationContext context;

    /**
     * Creates a Quartz {@Link JobDetail} that is configured by
     * {@Link JobDetailFactoryBean}
     *
     * @param jobClass class that implements the job execution
     * @param isDurable durability after job completion
     * @param isRecoverable recover flag for the Job
     * @param jobName job name
     * @param jobGroup job group
     * @param jobDataMap additional job data
     * @return a Quartz {@Link JobDetail} instance
     */
    public JobDetail createJob(Class<? extends QuartzJobBean> jobClass, boolean isDurable, boolean isRecoverable,
            String jobName, String jobGroup, JobDataMap jobDataMap) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        factoryBean.setDurability(isDurable);
        factoryBean.setRequestsRecovery(isRecoverable);
        factoryBean.setApplicationContext(context);
        factoryBean.setName(jobName);
        factoryBean.setGroup(jobGroup);
        factoryBean.setJobDataMap(jobDataMap);

        factoryBean.afterPropertiesSet();

        return factoryBean.getObject();
    }

}
