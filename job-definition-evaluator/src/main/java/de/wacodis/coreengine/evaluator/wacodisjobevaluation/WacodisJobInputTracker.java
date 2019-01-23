/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.coreengine.evaluator.EvaluationStatus;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobInputTracker {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WacodisJobInputTracker.class);
    
    protected final List<WacodisJobWrapper> scheduledWacodisJobs;
    protected final List<WacodisJobWrapper> executableWacodisJobs;
    protected final List<JobIsExecutableChangeListener> listeners;
    
    protected DataEnvelopeMatcher matcher;
    
    public DataEnvelopeMatcher getMatcher() {
        return matcher;
    }

    /**
     *
     * @param matcher
     */
    public WacodisJobInputTracker(DataEnvelopeMatcher matcher) {
        this.matcher = matcher;
        this.scheduledWacodisJobs = new ArrayList<>();
        this.executableWacodisJobs = new ArrayList<>();
        this.listeners = new ArrayList<>();
        
        LOGGER.debug(WacodisJobInputTracker.class.getSimpleName() + " instance created with matcher " + this.matcher.getClass().getSimpleName());
    }

    /**
     *
     * @param job
     */
    public void addJob(WacodisJobWrapper job) {
        this.scheduledWacodisJobs.add(job);
        
        if (job.isExecutable() && !this.executableWacodisJobs.contains(job)) {
            this.executableWacodisJobs.add(job);
            this.notifyListeners(job, true);
        }
    }

    /**
     *
     * @param job
     * @return
     */
    public boolean removeJob(WacodisJobWrapper job) {
        if (this.executableWacodisJobs.contains(job)) {
            this.executableWacodisJobs.remove(job);
        }
        
        return this.scheduledWacodisJobs.remove(job);
    }

    /**
     *
     */
    public void clearJobs() {
        this.executableWacodisJobs.clear();
        this.scheduledWacodisJobs.clear();
    }

    /**
     *
     * @param job
     * @return
     */
    public boolean containsJob(WacodisJobWrapper job) {
        return this.scheduledWacodisJobs.contains(job);
    }
    
    public void publishDataEnvelope(AbstractDataEnvelope dataEnvelope) {
        handleDataEnvelope(dataEnvelope, true);
    }
    
    public void removeDataEnvelope(AbstractDataEnvelope dataEnvelope) {
        handleDataEnvelope(dataEnvelope, false);
    }
    
    public void addJobIsExecutableChangeListener(JobIsExecutableChangeListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
    
    public boolean removeJobIsExecutableChangeListener(JobIsExecutableChangeListener listener) {
        return this.listeners.remove(listener);
    }
    
    public void clearJobIsExecutableChangeListeners() {
        this.listeners.clear();
    }
    
    private void handleDataEnvelope(AbstractDataEnvelope dataEnvelope, boolean dataAvailable) {
        LOGGER.info("received DataEnvelope for job evaluation, dataAvailable: " + dataAvailable);
        
        for (WacodisJobWrapper job : this.scheduledWacodisJobs) {
            for (InputHelper input : job.getInputs()) {
                boolean isMatch = this.matcher.match(dataEnvelope, job, input.getSubsetDefinition());
                
                if (isMatch) {
                    updateInput(input, dataAvailable);
                    updateExecutableWacodisJobs(job);
                }
            }
        }
    }

    /**
     * returns unmodifiable List of all jobs ready for execution
     *
     * @return
     */
    public List<WacodisJobWrapper> getExecutableJobs() {
        return Collections.unmodifiableList(this.executableWacodisJobs);
    }

    /**
     * returns unmodifiable List of all scheduled jobs
     *
     * @return
     */
    public List<WacodisJobWrapper> getScheduledJobs() {
        return Collections.unmodifiableList(this.scheduledWacodisJobs);
    }
    
    private void updateInput(InputHelper pair, boolean isResourceAvailable) {
        pair.setResourceAvailable(isResourceAvailable);
    }
    
    private void updateExecutableWacodisJobs(WacodisJobWrapper job) {
        if (job.isExecutable() && !this.executableWacodisJobs.contains(job)) {
            this.executableWacodisJobs.add(job);
            notifyListeners(job, true);
        } else if (!job.isExecutable() && this.executableWacodisJobs.contains(job)) {
            this.executableWacodisJobs.remove(job);
            notifyListeners(job, false);
        }
    }
    
    private void notifyListeners(WacodisJobWrapper job, boolean isExecutable) {
        LOGGER.debug("Notify Listeners for Job " + job.getJobDefinition().getName() + ", isExecutable: " + isExecutable);
        EvaluationStatus status = (isExecutable) ? EvaluationStatus.EXECUTABLE : EvaluationStatus.NOTEXECUTABLE;
        
        for (JobIsExecutableChangeListener listener : this.listeners) {
            listener.onJobIsExecutableChanged(job, status);
        }
    }
}
