/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
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
package de.wacodis.coreengine.evaluator;

import de.wacodis.core.models.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.core.models.AbstractDataEnvelopeTimeFrame;
import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.DataAccessResourceSearchBody;
import de.wacodis.core.models.StaticSubsetDefinition;
import de.wacodis.coreengine.evaluator.configuration.DataAccessConfiguration;
import de.wacodis.coreengine.evaluator.http.dataaccess.DataAccessConnector;
import de.wacodis.coreengine.evaluator.http.dataaccess.DataAccessResourceProvider;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobInputTracker;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class JobEvaluatorRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobEvaluatorRunner.class);

    private DataAccessResourceProvider<Map<String, List<AbstractResource>>, DataAccessResourceSearchBody> dataAccessConnector;
    private WacodisJobInputTracker inputTracker;

    @Autowired
    private WacodisJobInputTrackerProvider inputTrackerProvider;

    @Autowired
    private DataAccessConfiguration dataAccessConfiguration;

    @PostConstruct
    private void initialize() throws MalformedURLException {
        //init DataAccessConnector
        DataAccessConnector dataAccess = new DataAccessConnector();
        //ToDo runtime changes of dataaccess configuration are not reflected
        URL dataAccessURL = new URL(this.dataAccessConfiguration.getUri());
        dataAccess.setUrl(dataAccessURL);

        this.setDataAccessConnector(dataAccess);
        this.inputTracker = inputTrackerProvider.getInputTracker();
    }

    public JobEvaluatorRunner() {
    }

    public JobEvaluatorRunner(DataAccessResourceProvider<Map<String, List<AbstractResource>>, DataAccessResourceSearchBody> dataAccessConnector) {
        this.dataAccessConnector = dataAccessConnector;
    }

    public DataAccessResourceProvider<Map<String, List<AbstractResource>>, DataAccessResourceSearchBody> getDataAccessConnector() {
        return dataAccessConnector;
    }

    public void setDataAccessConnector(DataAccessResourceProvider<Map<String, List<AbstractResource>>, DataAccessResourceSearchBody> dataAccessConnector) {
        this.dataAccessConnector = dataAccessConnector;
    }

    public WacodisJobInputTracker getInputTracker() {
        return inputTracker;
    }

    /**
     * Evaluates a job but does not inform the input tracker on execution status
     * changes
     *
     * @param job the job
     * @return info on the executable state
     */
    public EvaluationStatus evaluateJob(WacodisJobWrapper job) {
        return this.evaluateJob(job, false);
    }

    /**
     * Evaluates a job executability
     *
     * @param job the job
     * @param addJobToInputTracker if the input tracker should be informed about
     * the execution status
     * @return info on the executable state
     */
    public EvaluationStatus evaluateJob(WacodisJobWrapper job, boolean addJobToInputTracker) {
        try {
            //generate multiple queries for inputs with and without input-specific temporal coverage
            List<DataAccessResourceSearchBody> dataAccessQueries = generateDataAccessQueries(job);
            //retrieve resources from data access for each query
            LOGGER.info("retrieve available resources for wacodis job {}, total number of requests is {}", job.getJobDefinition().getId(), dataAccessQueries.size());
            //call data access for each query
            for (int i = 0; i < dataAccessQueries.size(); i++) {
                LOGGER.debug("initiate data access query {} of {} for wacodis job", i, dataAccessQueries.size(), job.getJobDefinition().getId());
                //call data access
                Map<String, List<AbstractResource>> availableResources = retrieveAvailableResourcesFromDataAccess(dataAccessQueries.get(i));
                LOGGER.debug("successfully retrieved available resources for query {} of {} of wacodis job {} from Data Access, response contained {} resources", i, dataAccessQueries.size(), job.getJobDefinition().getId(), getResourceCount(availableResources));
                //register available resources
                editJobWrapperInputs(availableResources, job);
            }
            LOGGER.info("successfully retrieved all available resources from data access for wacodis job, executed {} data access requests", job.getJobDefinition().getId(), dataAccessQueries.size());

            boolean isExecutable = job.isExecutable();
            LOGGER.debug(getResourceSummary(job));

            if (addJobToInputTracker) {
                this.inputTracker.addJob(job);
            }

            return (isExecutable) ? EvaluationStatus.EXECUTABLE : EvaluationStatus.NOTEXECUTABLE;
        } catch (IOException ex) {
            LOGGER.error("Wacodis-Job " + job.getJobDefinition().getId() + "could not be evaluated, unable to retrieve resources from data-access, ", ex);

            if (addJobToInputTracker) {
                this.inputTracker.addJob(job);
                LOGGER.warn("Wacodis-Job {} could not be evaluated, add Wacodis-Job to InputTracker to wait for new, accessible DataEnvelopes", job.getJobDefinition().getId());
            }
            LOGGER.error("Wacodis-Job " + job.getJobDefinition().getId() + "could not be evaluated", ex);

            return EvaluationStatus.UNEVALUATED;
        }
    }

    private Map<String, List<AbstractResource>> retrieveAvailableResourcesFromDataAccess(DataAccessResourceSearchBody query) throws IOException {
        Map<String, List<AbstractResource>> response = this.dataAccessConnector.searchResources(query);

        return response;
    }

    private DataAccessResourceSearchBody generateDataAccessQuery(List<AbstractSubsetDefinition> inputs, AbstractDataEnvelopeAreaOfInterest areaOfInterest, AbstractDataEnvelopeTimeFrame timeFrame) {
        DataAccessResourceSearchBody query = new DataAccessResourceSearchBody();
        query.setAreaOfInterest(areaOfInterest);
        //exclude static inputs from data access query
        query.setInputs(inputs);
        query.setTimeFrame(timeFrame);

        return query;
    }

    ;

    private AbstractDataEnvelopeTimeFrame getIntervalAsAbstractDataEnvelopeTimeFrame(Interval interval) {
        AbstractDataEnvelopeTimeFrame timeFrame = new AbstractDataEnvelopeTimeFrame();
        timeFrame.setStartTime(interval.getStart());
        timeFrame.setEndTime(interval.getEnd());

        return timeFrame;
    }

    private List<DataAccessResourceSearchBody> generateDataAccessQueries(WacodisJobWrapper job) {
        Interval inputRelevancyTimeFrame;
        DataAccessResourceSearchBody query;
        List<DataAccessResourceSearchBody> queries = new ArrayList<>();
        //do not include static inputs in data access queries
        List<AbstractSubsetDefinition> nonStaticInputs = getNonStaticInputs(job);

        //single query for all inputs without separate temporal coverage
        List<AbstractSubsetDefinition> inputsNoneTempCov = this.getInputsWithoutTemporalCoverage(nonStaticInputs);
        if (!inputsNoneTempCov.isEmpty()) { //only necessary if at least one input without temp cov
            inputRelevancyTimeFrame = job.calculateInputRelevancyTimeFrame();
            query = generateDataAccessQuery(inputsNoneTempCov, job.getJobDefinition().getAreaOfInterest(), getIntervalAsAbstractDataEnvelopeTimeFrame(inputRelevancyTimeFrame));
            queries.add(query);
            LOGGER.debug("generated data access query for {} of {} (non-static) inputs with common temporal coverage of wacodis job {}", inputsNoneTempCov.size(), nonStaticInputs.size(), job.getJobDefinition().getId());
        }
        //generate separate query for each input with temporal coverage
        List<AbstractSubsetDefinition> inputsTempCov = this.getInputsWithTemporalCoverage(nonStaticInputs);
        for (AbstractSubsetDefinition input : inputsTempCov) {
            inputRelevancyTimeFrame = job.calculateInputRelevancyTimeFrameForInput(input);
            query = generateDataAccessQuery(Arrays.asList(input), job.getJobDefinition().getAreaOfInterest(), getIntervalAsAbstractDataEnvelopeTimeFrame(inputRelevancyTimeFrame));
            queries.add(query);
        }
        LOGGER.debug("generated data access query for {} of {} (non-static) inputs with input-specific temporal coverage of wacodis job {}", inputsTempCov.size(), nonStaticInputs.size(), job.getJobDefinition().getId());

        return queries;
    }

    private void editJobWrapperInputs(Map<String, List<AbstractResource>> availableResources, WacodisJobWrapper job) {
        for (InputHelper input : job.getInputs()) {
            String inputID = input.getSubsetDefinitionIdentifier();
            List<AbstractResource> resourceList = availableResources.get(inputID);
            //set resources, override current resources
            if (resourceList != null) {
                input.setResource(resourceList);
            }
        }
    }

    private int getResourceCount(Map<String, List<AbstractResource>> resources) {
        int count = 0;

        for (Map.Entry<String, List<AbstractResource>> entry : resources.entrySet()) {
            count += entry.getValue().size();
        }

        return count;
    }

    private String getResourceSummary(WacodisJobWrapper job) {
        StringBuilder sb = new StringBuilder();

        sb.append("Resource Summary of Wacodis Job ").append(job.getJobDefinition().getId()).append(" {");
        for (InputHelper input : job.getInputs()) {
            sb.append(System.lineSeparator()).append("\t"); //line break + indent
            sb.append(input.getSubsetDefinitionIdentifier()).append(": resources available = ").append(input.hasResource());

            if (input.hasResource()) {
                sb.append(", number of resources = ").append(input.getResource().size());
            }
        }

        sb.append(System.lineSeparator()).append("}");

        return sb.toString();
    }

    private List<AbstractSubsetDefinition> getInputsWithTemporalCoverage(List<AbstractSubsetDefinition> inputs) {
        //find all inputs with temporal coverage and duration not null
        List<AbstractSubsetDefinition> inputsTempCov = inputs.stream().filter(input -> input.getTemporalCoverage() != null && input.getTemporalCoverage().getDuration() != null).collect(Collectors.toList());
        return inputsTempCov;
    }

    private List<AbstractSubsetDefinition> getInputsWithoutTemporalCoverage(List<AbstractSubsetDefinition> inputs) {
        //find all inputs without specific temporal coverage
        List<AbstractSubsetDefinition> inputsNoneTempCov = inputs.stream().filter(input -> input.getTemporalCoverage() == null || input.getTemporalCoverage().getDuration() == null).collect(Collectors.toList());
        return inputsNoneTempCov;
    }

    private List<AbstractSubsetDefinition> getNonStaticInputs(WacodisJobWrapper job) {
        return job.getInputs().stream().map(input -> input.getSubsetDefinition()).filter(input -> !StaticSubsetDefinition.class.isAssignableFrom(input.getClass())).collect(Collectors.toList());
    }

}
