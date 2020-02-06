/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.StaticSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionTemporalCoverage;
import de.wacodis.core.models.extension.staticresource.StaticDummyResource;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.cron.CronExecutionTimeCalculator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for a JobDefinition to keep track of available/missing inputs
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WacodisJobWrapper.class);

    private final WacodisJobDefinition jobDefinition;
    private final List<InputHelper> inputs;
    private WacodisJobExecutionContext executionContext;

    public WacodisJobWrapper(WacodisJobExecutionContext executionContext, WacodisJobDefinition job) {
        this.jobDefinition = job;
        this.inputs = new ArrayList<>();
        this.executionContext = executionContext;

        initInputs();

        LOGGER.debug(WacodisJobWrapper.class.getSimpleName() + " instance created for WacodisJobDefinition " + this.jobDefinition.getId());
    }

    /**
     * returns a unmodifiable list of all input pairs
     *
     * @return
     */
    public List<InputHelper> getInputs() {
        return Collections.unmodifiableList(inputs);
    }

    public WacodisJobDefinition getJobDefinition() {
        return jobDefinition;
    }

    public WacodisJobExecutionContext getExecutionContext() {
        return executionContext;
    }

    /**
     * check if all inputs are marked as executable
     *
     * @return
     */
    public boolean isExecutable() {
        for (InputHelper input : this.inputs) {
            if (!input.isResourceAvailable()) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @return
     */
    public Interval calculateInputRelevancyTimeFrame() {
        WacodisJobDefinitionTemporalCoverage tempCov = this.jobDefinition.getTemporalCoverage();
        DateTime start;

        if (tempCov.getPreviousExecution() != null && tempCov.getPreviousExecution()) { //previousExecution (data since last job execution is relevant)
            try {
                String cronExpression = this.jobDefinition.getExecution().getPattern();
                CronExecutionTimeCalculator timeCalculator = new CronExecutionTimeCalculator(cronExpression);
                start = timeCalculator.previousExecution(this.executionContext.getExecutionTime()); //scheduled time of previous job execution
            } catch (NullPointerException e) {
                LOGGER.error("temporalCoverage previousExecution is true but no execution pattern (cron) is set");
                throw new java.lang.IllegalArgumentException("No execution pattern provided but attribute previousExecution is set true for JobDefinition: " + jobDefinition.getId(), e);
            }

        } else { //duration (data since a specified point in time is relevant)
            Period period = Period.parse(tempCov.getDuration()); //terms duration and period are mixed up
            start = this.executionContext.getExecutionTime().minus(period);
        }

        return new Interval(start, this.executionContext.getExecutionTime());
    }
    
    public int incrementRetryCount(){
        this.executionContext = this.executionContext.createCopyWithIncrementedRetryCount();
        return this.executionContext.getRetryCount();
    }

    private void initInputs() {
        for (AbstractSubsetDefinition subset : this.jobDefinition.getInputs()) {
            InputHelper input = new InputHelper(subset);

            if (StaticSubsetDefinition.class.isAssignableFrom(subset.getClass())) { //handle static inputs
                input.setResourceAvailable(true); //static input always available 
                input.setResource(createStaticDummyResource(((StaticSubsetDefinition) subset)));
            }

            this.inputs.add(input);
        }
    }

    private List<AbstractResource> createStaticDummyResource(StaticSubsetDefinition subset) {
        List<AbstractResource> resourceList = new ArrayList<>();

        StaticDummyResource resource = new StaticDummyResource();
        resource.setDataType(subset.getDataType());
        resource.setValue(subset.getValue());

        resourceList.add(resource);
        return resourceList;
    }
}
