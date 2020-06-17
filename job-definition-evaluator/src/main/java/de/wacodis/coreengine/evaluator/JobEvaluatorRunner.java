/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator;

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
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.JobEvaluatorService;
import java.net.MalformedURLException;
import java.net.URL;

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
    WacodisJobInputTrackerProvider inputTrackerProvider;

    @Autowired
    private DataAccessConfiguration dataAccessConfiguration;

    @PostConstruct
    private void initialize() throws MalformedURLException {
        //init DataAccessConnector
        DataAccessConnector dataAccess = new DataAccessConnector();
        //ToDo runtime changes of dataaccess configuration are not reflected
        URL dataAccessURL = new URL(this.dataAccessConfiguration.getUri());
        dataAccess.setUrl(dataAccessURL);

        this.setDataAccessConnector(dataAccessConnector);
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
            Map<String, List<AbstractResource>> searchResult = this.retrieveAvailableResourcesFromDataAccess(job);
            LOGGER.info("retrieved available resources for wacodis job {} from Data Access, response contained {} resources", job.getJobDefinition().getId(), getResourceCount(searchResult));
            editJobWrapperInputs(searchResult, job);
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
                LOGGER.warn("Wacodis-Job {} could not be evaluated and might not be executed, add Wacodis-Job to InputTracker to wait for new, accessible DataEnvelopes");
            } else {
                LOGGER.error("Wacodis-Job " + job.getJobDefinition().getId() + "could not be evaluated and will not be executed!");
            }

            return EvaluationStatus.UNEVALUATED;
        }
    }

    private Map<String, List<AbstractResource>> retrieveAvailableResourcesFromDataAccess(WacodisJobWrapper job) throws IOException {
        DataAccessResourceSearchBody query = generateDataAccessQuery(job);
        Map<String, List<AbstractResource>> response = this.dataAccessConnector.searchResources(query);

        return response;
    }

    private DataAccessResourceSearchBody generateDataAccessQuery(WacodisJobWrapper job) {
        DataAccessResourceSearchBody query = new DataAccessResourceSearchBody();
        query.setAreaOfInterest(job.getJobDefinition().getAreaOfInterest());
        //exclude static inputs from data access query
        List<AbstractSubsetDefinition> nonStaticInputs = job.getJobDefinition().getInputs().stream().filter(input -> !StaticSubsetDefinition.class.isAssignableFrom(input.getClass())).collect(Collectors.toList());
        query.setInputs(nonStaticInputs);
        query.setTimeFrame(calculateTimeFrame(job));

        return query;
    }

    private AbstractDataEnvelopeTimeFrame calculateTimeFrame(WacodisJobWrapper job) {
        Interval relevancyTimeFrame = job.calculateInputRelevancyTimeFrame();

        AbstractDataEnvelopeTimeFrame relevancyTimeFrameConverted = new AbstractDataEnvelopeTimeFrame();
        relevancyTimeFrameConverted.setStartTime(relevancyTimeFrame.getStart());
        relevancyTimeFrameConverted.setEndTime(relevancyTimeFrame.getEnd());

        return relevancyTimeFrameConverted;
    }

    private void editJobWrapperInputs(Map<String, List<AbstractResource>> response, WacodisJobWrapper job) {
        for (InputHelper input : job.getInputs()) {
            String inputID = input.getSubsetDefinitionIdentifier();
            List<AbstractResource> resourceList = response.get(inputID);
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

}
