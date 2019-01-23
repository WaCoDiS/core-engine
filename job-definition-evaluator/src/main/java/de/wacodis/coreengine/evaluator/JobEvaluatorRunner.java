/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator;

import de.wacodis.core.models.AbstractDataEnvelopeTimeFrame;
import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.DataAccessResourceSearchBody;
import de.wacodis.coreengine.evaluator.http.dataaccess.ResourceProvider;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.DataEnvelopeMatcher;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.JobIsExecutableChangeListener;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobInputTracker;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class JobEvaluatorRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobEvaluatorRunner.class);

    private ResourceProvider<Map<String, List<AbstractResource>>, DataAccessResourceSearchBody> dataAccessConnector;
    private WacodisJobInputTracker inputTracker;
    
    @Autowired
    private ApplicationEventPublisher jobExecutablePublisher;

    public JobEvaluatorRunner() {}

    public JobEvaluatorRunner(ResourceProvider<Map<String, List<AbstractResource>>, DataAccessResourceSearchBody> dataAccessConnector) {
        this.dataAccessConnector = dataAccessConnector;
    }

    public ResourceProvider<Map<String, List<AbstractResource>>, DataAccessResourceSearchBody> getDataAccessConnector() {
        return dataAccessConnector;
    }

    public void setDataAccessConnector(ResourceProvider<Map<String, List<AbstractResource>>, DataAccessResourceSearchBody> dataAccessConnector) {
        this.dataAccessConnector = dataAccessConnector;
    }

    public WacodisJobInputTracker getInputTracker() {
        return inputTracker;
    }

    public void setInputTracker(WacodisJobInputTracker inputTracker) {
        this.inputTracker = inputTracker;
    }
    
    /**
     *
     * @param job
     * @return
     */
    public EvaluationStatus evaluateJob(WacodisJobWrapper job) {
        try {
            DataAccessResourceSearchBody query = generateDataAccessQuery(job);
            Map<String, List<AbstractResource>> searchResult = this.dataAccessConnector.searchResources(query);
            boolean isExecutable = editJobWrapperInputs(searchResult, job);

            return (isExecutable) ? EvaluationStatus.EXECUTABLE : EvaluationStatus.NOTEXECUTABLE;
        } catch (IOException ex) {
            LOGGER.error("Wacodis-Job could not be evaluated, unable to retrieve resources from data-access, ", ex);
            return EvaluationStatus.UNEVALUATED;
        }
    }

    public EvaluationStatus evaluateJob(WacodisJobWrapper job, boolean addJobToInputTracker) {
        EvaluationStatus status = evaluateJob(job);

        if (addJobToInputTracker) {
            this.inputTracker.addJob(job);
        }
        
        return status;
    }

    private Map<String, List<AbstractResource>> retrieveAvailableResourcesFromDataAccess(WacodisJobWrapper job) throws IOException {
        DataAccessResourceSearchBody query = generateDataAccessQuery(job);
        Map<String, List<AbstractResource>> response = this.dataAccessConnector.searchResources(query);

        return response;
    }

    private DataAccessResourceSearchBody generateDataAccessQuery(WacodisJobWrapper job) {
        DataAccessResourceSearchBody query = new DataAccessResourceSearchBody();
        query.setAreaOfInterest(job.getJobDefinition().getAreaOfInterest());
        query.setInputs(job.getJobDefinition().getInputs());
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

    private boolean editJobWrapperInputs(Map<String, List<AbstractResource>> response, WacodisJobWrapper job) {
        for (InputHelper input : job.getInputs()) {
            String inputID = input.getSubsetDefinition().getIdentifier();
            List<AbstractResource> resourceList = response.get(inputID);

            updateInputResource(input, Optional.ofNullable(resourceList));
        }

        return job.isExecutable();
    }

    private void updateInputResource(InputHelper input, Optional<List<AbstractResource>> resourceList) {
        if (resourceList.isPresent() && resourceList.get().size() > 0) {
            input.setResource(resourceList);
            input.setResourceAvailable(true);
        } else {
            input.setResource(Optional.empty());
            input.setResourceAvailable(false);
        }
    }
    
    private class EvaluatorListener implements JobIsExecutableChangeListener{
        
        private final WacodisJobInputTracker inputTracker;
        private final ApplicationEventPublisher publisher;
        private final Object eventSource;

        public EvaluatorListener(WacodisJobInputTracker inputTracker, ApplicationEventPublisher publisher, Object eventSource) {
            this.inputTracker = inputTracker;
            this.publisher = publisher;
            this.eventSource = eventSource;
        }

        @Override
        public void onJobIsExecutableChanged(WacodisJobWrapper job, EvaluationStatus status) {
            if(status.equals(EvaluationStatus.EXECUTABLE)){
                //check again (items might have been removed from DataAccess during waiting time)
                EvaluationStatus validityCheck = evaluateJob(job);
                
                if(validityCheck.equals(EvaluationStatus.EXECUTABLE)){
                    WacodisJobExecutableEvent event = new WacodisJobExecutableEvent(this.eventSource, job, validityCheck);
                    this.publisher.publishEvent(event);
                    this.inputTracker.removeJob(job); 
                }
                //else wait for further DataEnvelopes               
            }
        }
        
    }
}
