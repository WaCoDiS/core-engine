/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.quartz;

import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
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
