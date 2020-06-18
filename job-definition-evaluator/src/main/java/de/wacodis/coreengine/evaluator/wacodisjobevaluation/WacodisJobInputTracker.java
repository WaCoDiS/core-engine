/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractDataEnvelope;
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
    protected final JobEvaluatorService jobEvaluator;
    protected boolean preselectCandidates;
    protected DataEnvelopeMatcher matcher;

    public DataEnvelopeMatcher getMatcher() {
        return matcher;
    }

    /**
     *
     * @param jobEvaluator
     * @param matcher
     * @param preselectCandidates if true only evaluate candidates identifierd
     * by matcher
     */
    public WacodisJobInputTracker(JobEvaluatorService jobEvaluator, DataEnvelopeMatcher matcher, boolean preselectCandidates) {
        this.matcher = matcher;
        this.scheduledWacodisJobs = new ArrayList<>();
        this.jobEvaluator = jobEvaluator;
        this.preselectCandidates = preselectCandidates;

        LOGGER.debug(WacodisJobInputTracker.class.getSimpleName() + " instance created with matcher " + this.matcher.getClass().getSimpleName());
    }

    /**
     * preselectCandidates == true
     *
     * @param jobEvaluator
     * @param matcher
     */
    public WacodisJobInputTracker(JobEvaluatorService jobEvaluator, DataEnvelopeMatcher matcher) {
        this(jobEvaluator, matcher, true);
    }

    public boolean isPreselectCandidates() {
        return preselectCandidates;
    }

    public void setPreselectCandidates(boolean preselectCandidates) {
        this.preselectCandidates = preselectCandidates;
    }

    /**
     *
     * @param job
     */
    public void addJob(WacodisJobWrapper job) {
        this.scheduledWacodisJobs.add(job);
        LOGGER.info("add wacodis job {} to input tracker", job.getJobDefinition().getId());
    }

    /**
     *
     * @param job
     * @return
     */
    public boolean removeJob(WacodisJobWrapper job) {
        LOGGER.info("remove wacodis job {} from input tracker", job.getJobDefinition().getId());
        return this.scheduledWacodisJobs.remove(job);
    }

    /**
     *
     */
    public void clearJobs() {
        LOGGER.info("clear all wacodis jobs from input tracker");
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

    private void handleDataEnvelope(AbstractDataEnvelope dataEnvelope, boolean dataAvailable) {
        LOGGER.info("received " + dataEnvelope.getClass().getSimpleName() + " " + dataEnvelope.getIdentifier() + " for job evaluation, dataAvailable: " + dataAvailable);
        List<WacodisJobWrapper> candidates;
        if (this.preselectCandidates) {
            candidates = findCandidates(dataEnvelope);
            LOGGER.info("found {} candidates (wacodis job) for job evaluation after receiving data envelope {}", candidates.size(), dataEnvelope.getIdentifier());
        } else {
            candidates = new ArrayList<>(this.scheduledWacodisJobs);
            LOGGER.info("select all scheduled wacodis jobs (count: {} ) for job evaluation after receiving data envelope {}", candidates.size(), dataEnvelope.getIdentifier());
        }

        for (WacodisJobWrapper candidate : candidates) {
            LOGGER.info("(re-)evaluate candidate wacodis job {}", candidate.getJobDefinition().getId());
            this.jobEvaluator.handleJobEvaluation(candidate, this);
        }

    }

    private List<WacodisJobWrapper> findCandidates(AbstractDataEnvelope dataEnvelope) {
        List<WacodisJobWrapper> candidates = new ArrayList<>();

        //iterate each input of each scheduled job
        for (WacodisJobWrapper job : this.scheduledWacodisJobs) {
            for (InputHelper input : job.getInputs()) {
                boolean isMatch = this.matcher.match(dataEnvelope, job, input.getSubsetDefinition());

                if (isMatch) {
                    candidates.add(job);
                    break; //add job only once
                }
            }
        }

        return candidates;
    }

    /**
     * returns unmodifiable List of all scheduled jobs
     *
     * @return
     */
    public List<WacodisJobWrapper> getScheduledJobs() {
        return Collections.unmodifiableList(this.scheduledWacodisJobs);
    }
}
