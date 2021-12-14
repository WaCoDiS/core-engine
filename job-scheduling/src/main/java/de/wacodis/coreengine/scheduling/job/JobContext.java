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

import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * Defines the context (job details and trigger) for a job
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class JobContext {

    private JobDetail jobDetails;

    private Trigger trigger;

    public JobDetail getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(JobDetail jobDetails) {
        this.jobDetails = jobDetails;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

}
