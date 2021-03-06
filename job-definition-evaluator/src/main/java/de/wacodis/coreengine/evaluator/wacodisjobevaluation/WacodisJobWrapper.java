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
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.AbstractSubsetDefinitionTemporalCoverage;
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
            if (!input.hasResource()) {
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
        DateTime start, end;

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

            //consider optional offset
            if (tempCov.getOffset() != null && !tempCov.getOffset().isEmpty()) {
                Period offsetPeriod = Period.parse(tempCov.getOffset());
                period = period.plus(offsetPeriod);
            }

            start = this.executionContext.getExecutionTime().minus(period);
        }

        //consider optional offset
        end = this.executionContext.getExecutionTime();
        if (tempCov.getOffset() != null && !tempCov.getOffset().isEmpty()) {
            Period offsetPeriod = Period.parse(tempCov.getOffset());
            end = end.minus(offsetPeriod);
        }

        return new Interval(start, end);
    }

    public Interval calculateInputRelevancyTimeFrameForInput(AbstractSubsetDefinition input) {
        DateTime start, end;
        Interval relevancyTimeFrameInput;
        AbstractSubsetDefinitionTemporalCoverage tempCov;
        Interval relevancyTimeFrameJob = this.calculateInputRelevancyTimeFrame();

        //return if input has no temporal coverage
        if (input.getTemporalCoverage() != null && input.getTemporalCoverage().getDuration() != null) {
            tempCov = input.getTemporalCoverage();
        } else {
            return relevancyTimeFrameJob;
        }

        //consider offset if provided
        end = relevancyTimeFrameJob.getEnd();
        if (tempCov.getOffset() != null && !tempCov.getOffset().isEmpty()) {
            Period offset = Period.parse(tempCov.getOffset());
            end = end.minus(offset);
        }

        start = end.minus(Period.parse(tempCov.getDuration()));

        relevancyTimeFrameInput = new Interval(start, end);

        return relevancyTimeFrameInput;
    }

    /**
     * @return incremented retry count
     */
    public int incrementRetryCount() {
        this.executionContext = this.executionContext.createCopyWithIncrementedRetryCount();
        return this.executionContext.getRetryCount();
    }

    private void initInputs() {
        for (AbstractSubsetDefinition inputDef : this.jobDefinition.getInputs()) {
            InputHelper input = new InputHelper(inputDef);

            if (StaticSubsetDefinition.class.isAssignableFrom(inputDef.getClass())) { //handle static inputs
                input.setResource(createStaticDummyResource(((StaticSubsetDefinition) inputDef)));
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
