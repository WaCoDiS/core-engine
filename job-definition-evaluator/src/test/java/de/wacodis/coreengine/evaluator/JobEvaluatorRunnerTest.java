/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.DataAccessResourceSearchBody;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecution;
import de.wacodis.core.models.WacodisJobDefinitionTemporalCoverage;
import de.wacodis.coreengine.evaluator.http.dataaccess.DataAccessConnector;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.BasicDataEnvelopeMatcher;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.DataEnvelopeMatcher;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobInputTracker;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class JobEvaluatorRunnerTest {

    public JobEvaluatorRunnerTest() {
    }

    /**
     * Test of setDataAccessConnector method, of class JobEvaluatorRunner.
     */
    @Test
    public void testSetDataAccessConnector() {
        JobEvaluatorRunner evaluator = new JobEvaluatorRunner();
        DataAccessConnector dataAccess = new EmptyResponseDataAccessConnector();
        evaluator.setDataAccessConnector(dataAccess);

        assertEquals(dataAccess, evaluator.getDataAccessConnector());
    }

    /**
     * Test of setInputTracker method, of class JobEvaluatorRunner.
     */
    @Test
    public void testSetInputTracker() {
        JobEvaluatorRunner evaluator = new JobEvaluatorRunner();
        WacodisJobInputTracker inputTracker = new WacodisJobInputTracker(new BasicDataEnvelopeMatcher());
        evaluator.setInputTracker(inputTracker);

        assertEquals(inputTracker, evaluator.getInputTracker());
    }

    /**
     *
     */
    @Test
    @DisplayName("WacodisJob executable")
    public void testEvaluateJob_WacodisJobWrapper_Executable() {
        DataEnvelopeMatcher matcher = new BasicDataEnvelopeMatcher();
        WacodisJobInputTracker inputTracker = new WacodisJobInputTracker(matcher);
        JobEvaluatorRunner evaluator = new JobEvaluatorRunner();
        evaluator.setInputTracker(inputTracker);
        evaluator.setDataAccessConnector(new EmptyResponseDataAccessConnector());
        WacodisJobDefinition jobDefinition = new WacodisJobDefinition();
        jobDefinition.setInputs(new ArrayList<>()); //no inputs
        WacodisJobDefinitionExecution execution = new WacodisJobDefinitionExecution();
        execution.setPattern("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        WacodisJobDefinitionTemporalCoverage tempCov = new WacodisJobDefinitionTemporalCoverage();
        tempCov.setPreviousExecution(Boolean.TRUE);
        jobDefinition.setTemporalCoverage(tempCov);
        jobDefinition.setExecution(execution);
        WacodisJobWrapper job = new WacodisJobWrapper(jobDefinition, new DateTime());

        //job has no inputs, empty response
        assertAll(
                () -> assertEquals(EvaluationStatus.EXECUTABLE, evaluator.evaluateJob(job)),
                () -> assertEquals(EvaluationStatus.EXECUTABLE, evaluator.evaluateJob(job, false))
        );

    }

    /**
     * Test of evaluateJob method, of class JobEvaluatorRunner.
     */
    @Test
    @DisplayName("WacodisJob not executable because of missing resources")
    public void testEvaluateJob_WacodisJobWrapper_NotExecutable() {
        DataEnvelopeMatcher matcher = new BasicDataEnvelopeMatcher();
        WacodisJobInputTracker inputTracker = new WacodisJobInputTracker(matcher);
        JobEvaluatorRunner evaluator = new JobEvaluatorRunner();
        evaluator.setInputTracker(inputTracker);
        evaluator.setDataAccessConnector(new EmptyResponseDataAccessConnector());
        WacodisJobDefinition jobDefinition = new WacodisJobDefinition();
        jobDefinition.setInputs(new ArrayList<>());
        jobDefinition.addInputsItem(new AbstractSubsetDefinition()); //add input

        WacodisJobDefinitionExecution execution = new WacodisJobDefinitionExecution();
        execution.setPattern("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        WacodisJobDefinitionTemporalCoverage tempCov = new WacodisJobDefinitionTemporalCoverage();
        tempCov.setPreviousExecution(Boolean.TRUE);
        jobDefinition.setTemporalCoverage(tempCov);
        jobDefinition.setExecution(execution);

        WacodisJobWrapper job = new WacodisJobWrapper(jobDefinition, new DateTime());
        EvaluationStatus status = evaluator.evaluateJob(job, true);

        //job has one input, empty response
        assertAll(
                () -> assertEquals(EvaluationStatus.NOTEXECUTABLE, evaluator.evaluateJob(job)),
                () -> assertEquals(EvaluationStatus.NOTEXECUTABLE, evaluator.evaluateJob(job, false))
        );
    }

    /**
     * Test of evaluateJob method, of class JobEvaluatorRunner.
     */
    @Test
    @DisplayName("WacodisJob unevaluated because data access request fails")
    public void testEvaluateJob_WacodisJobWrapper_Unevaluated() {
        DataEnvelopeMatcher matcher = new BasicDataEnvelopeMatcher();
        WacodisJobInputTracker inputTracker = new WacodisJobInputTracker(matcher);
        JobEvaluatorRunner evaluator = new JobEvaluatorRunner();
        evaluator.setInputTracker(inputTracker);
        evaluator.setDataAccessConnector(new IOExceptionDataAccessConnector()); //always throws exception
        WacodisJobDefinition jobDefinition = new WacodisJobDefinition();
        jobDefinition.setInputs(new ArrayList<>());
        jobDefinition.addInputsItem(new AbstractSubsetDefinition()); //add input

        WacodisJobDefinitionExecution execution = new WacodisJobDefinitionExecution();
        execution.setPattern("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        WacodisJobDefinitionTemporalCoverage tempCov = new WacodisJobDefinitionTemporalCoverage();
        tempCov.setPreviousExecution(Boolean.TRUE);
        jobDefinition.setTemporalCoverage(tempCov);
        jobDefinition.setExecution(execution);

        WacodisJobWrapper job = new WacodisJobWrapper(jobDefinition, new DateTime());
        EvaluationStatus status = evaluator.evaluateJob(job, true);

        //expect UNEVALUATED because data access request throws exception
        assertAll(
                () -> assertEquals(EvaluationStatus.UNEVALUATED, evaluator.evaluateJob(job)),
                () -> assertEquals(EvaluationStatus.UNEVALUATED, evaluator.evaluateJob(job, false))
        );
    }

    /**
     * Test of evaluateJob method, of class JobEvaluatorRunner.
     */
    @Test
    @DisplayName("check job is added to InputTracker")
    public void testEvaluateJob_boolean_InputTrackerContainsJob() {
        DataEnvelopeMatcher matcher = new BasicDataEnvelopeMatcher();
        WacodisJobInputTracker inputTracker = new WacodisJobInputTracker(matcher);
        JobEvaluatorRunner evaluator = new JobEvaluatorRunner();
        evaluator.setInputTracker(inputTracker);
        evaluator.setDataAccessConnector(new EmptyResponseDataAccessConnector());
        WacodisJobDefinition jobDefinition = new WacodisJobDefinition();
        jobDefinition.setInputs(new ArrayList<>()); //no inputs
        WacodisJobDefinitionExecution execution = new WacodisJobDefinitionExecution();
        execution.setPattern("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        WacodisJobDefinitionTemporalCoverage tempCov = new WacodisJobDefinitionTemporalCoverage();
        tempCov.setPreviousExecution(Boolean.TRUE);
        jobDefinition.setTemporalCoverage(tempCov);
        jobDefinition.setExecution(execution);
        WacodisJobWrapper job = new WacodisJobWrapper(jobDefinition, new DateTime());

        evaluator.evaluateJob(job, true);
        assertTrue(inputTracker.containsJob(job));
    }
    
        /**
     * Test of evaluateJob method, of class JobEvaluatorRunner.
     */
    @Test
    @DisplayName("check job is not added to InputTracker")
    public void testEvaluateJob_boolean_JobNotAdded() {
        DataEnvelopeMatcher matcher = new BasicDataEnvelopeMatcher();
        WacodisJobInputTracker inputTracker = new WacodisJobInputTracker(matcher);
        JobEvaluatorRunner evaluator = new JobEvaluatorRunner();
        evaluator.setInputTracker(inputTracker);
        evaluator.setDataAccessConnector(new EmptyResponseDataAccessConnector());
        WacodisJobDefinition jobDefinition = new WacodisJobDefinition();
        jobDefinition.setInputs(new ArrayList<>()); //no inputs
        WacodisJobDefinitionExecution execution = new WacodisJobDefinitionExecution();
        execution.setPattern("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        WacodisJobDefinitionTemporalCoverage tempCov = new WacodisJobDefinitionTemporalCoverage();
        tempCov.setPreviousExecution(Boolean.TRUE);
        jobDefinition.setTemporalCoverage(tempCov);
        jobDefinition.setExecution(execution);
        WacodisJobWrapper job = new WacodisJobWrapper(jobDefinition, new DateTime());

        evaluator.evaluateJob(job, false);
        assertFalse(inputTracker.containsJob(job));
    }

    /**
     * dummy data access with empty response
     */
    private class EmptyResponseDataAccessConnector extends DataAccessConnector {

        @Override
        public Map<String, List<AbstractResource>> searchResources(DataAccessResourceSearchBody searchBody) throws IOException {
            Map<String, List<AbstractResource>> emptyResponse = new HashMap<>();
            return emptyResponse;
        }

    }

    /**
     * dummy data access throws IOException
     */
    private class IOExceptionDataAccessConnector extends DataAccessConnector {

        @Override
        public Map<String, List<AbstractResource>> searchResources(DataAccessResourceSearchBody searchBody) throws IOException {
            throw new IOException("dummy exception");
        }

    }

}
