/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.CatalogueSubsetDefinition;
import de.wacodis.core.models.DataAccessResourceSearchBody;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecution;
import de.wacodis.core.models.WacodisJobDefinitionTemporalCoverage;
import de.wacodis.coreengine.evaluator.http.dataaccess.DataAccessConnector;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.BasicDataEnvelopeMatcher;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobInputTracker;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {JobEvaluatorRunner.class, TestJobExecutableListener.class})
public class JobEvaluatorRunnerTest {

    @Autowired
    private JobEvaluatorRunner evaluator;

    @Autowired
    private TestJobExecutableListener jobExecutableListener;

    public JobEvaluatorRunnerTest() {
    }
    
    @PostConstruct
    private void initListener(){
        this.jobExecutableListener.setEvalutor(this.evaluator);
    }

    @BeforeEach
    public void initJobEvaluatorRunner() {
        WacodisJobInputTracker inputTracker = new WacodisJobInputTracker(new BasicDataEnvelopeMatcher());
        this.evaluator.setInputTracker(inputTracker);
        this.evaluator.setDataAccessConnector(new EmptyResponseDataAccessConnector());

    }

    /**
     * Test of setDataAccessConnector method, of class JobEvaluatorRunner.
     */
    @Test
    public void testSetDataAccessConnector() {
        DataAccessConnector dataAccess = new EmptyResponseDataAccessConnector();
        this.evaluator.setDataAccessConnector(dataAccess);

        assertEquals(dataAccess, this.evaluator.getDataAccessConnector());
    }

    /**
     * Test of setInputTracker method, of class JobEvaluatorRunner.
     */
    @Test
    public void testSetInputTracker() {
        WacodisJobInputTracker inputTracker = new WacodisJobInputTracker(new BasicDataEnvelopeMatcher());
        this.evaluator.setInputTracker(inputTracker);

        assertEquals(inputTracker, this.evaluator.getInputTracker());
    }

    /**
     * Test of setInputTracker method, of class JobEvaluatorRunner.
     */
    @Test
    @DisplayName("register and deregister listener with inputtracker")
    public void testSetInputTracker_InputTracker_Listener_Registered() {
        WacodisJobInputTracker inputTracker = new WacodisJobInputTracker(new BasicDataEnvelopeMatcher());
        this.evaluator.setInputTracker(inputTracker);
        //listener must be add to inputTracker
        assertTrue(inputTracker.containsJobIsExecutableChangeListener(this.evaluator.getExecutableJobListener()));

        WacodisJobInputTracker inputTracker2 = new WacodisJobInputTracker(new BasicDataEnvelopeMatcher());
        this.evaluator.setInputTracker(inputTracker2);

        //listener must be removed from inputTracker and added to inputTracker2
        assertFalse(inputTracker.containsJobIsExecutableChangeListener(this.evaluator.getExecutableJobListener()));
        assertTrue(inputTracker2.containsJobIsExecutableChangeListener(this.evaluator.getExecutableJobListener()));
    }

    /**
     * Test of evaluateJob method, of class JobEvaluatorRunner.
     */
    @Test
    @DisplayName("WacodisJob executable")
    public void testEvaluateJob_WacodisJobWrapper_Executable() {
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
                () -> assertEquals(EvaluationStatus.EXECUTABLE, this.evaluator.evaluateJob(job)),
                () -> assertEquals(EvaluationStatus.EXECUTABLE, this.evaluator.evaluateJob(job, false))
        );

    }

    /**
     * Test of evaluateJob method, of class JobEvaluatorRunner.
     */
    @Test
    @DisplayName("WacodisJob not executable because of missing resources")
    public void testEvaluateJob_WacodisJobWrapper_NotExecutable() {
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

        //job has one input, empty response
        assertAll(
                () -> assertEquals(EvaluationStatus.NOTEXECUTABLE, this.evaluator.evaluateJob(job)),
                () -> assertEquals(EvaluationStatus.NOTEXECUTABLE, this.evaluator.evaluateJob(job, false))
        );
    }

    /**
     * Test of evaluateJob method, of class JobEvaluatorRunner.
     */
    @Test
    @DisplayName("WacodisJob unevaluated because data access request fails")
    public void testEvaluateJob_WacodisJobWrapper_Unevaluated() {
        this.evaluator.setDataAccessConnector(new IOExceptionDataAccessConnector()); //always throws exception

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

        //expect UNEVALUATED because data access request throws exception
        assertAll(
                () -> assertEquals(EvaluationStatus.UNEVALUATED, this.evaluator.evaluateJob(job)),
                () -> assertEquals(EvaluationStatus.UNEVALUATED, this.evaluator.evaluateJob(job, false))
        );
    }

    /**
     * Test of evaluateJob method, of class JobEvaluatorRunner.
     */
    @Test
    @DisplayName("check job is added to InputTracker")
    public void testEvaluateJob_boolean_InputTrackerContainsJob() {
        WacodisJobDefinition jobDefinition = new WacodisJobDefinition();
        jobDefinition.setInputs(new ArrayList<>());
        jobDefinition.getInputs().add(new CatalogueSubsetDefinition()); //add input
        WacodisJobDefinitionExecution execution = new WacodisJobDefinitionExecution();
        execution.setPattern("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        WacodisJobDefinitionTemporalCoverage tempCov = new WacodisJobDefinitionTemporalCoverage();
        tempCov.setPreviousExecution(Boolean.TRUE);
        jobDefinition.setTemporalCoverage(tempCov);
        jobDefinition.setExecution(execution);
        WacodisJobWrapper job = new WacodisJobWrapper(jobDefinition, new DateTime());

        //job has one input, empty response, waiting for further data
        EvaluationStatus status = this.evaluator.evaluateJob(job, true);
        assertEquals(EvaluationStatus.NOTEXECUTABLE, status);
        assertTrue(this.evaluator.getInputTracker().containsJob(job));
    }

    /**
     * Test of evaluateJob method, of class JobEvaluatorRunner.
     */
    @Test
    @DisplayName("check job is not added to InputTracker")
    public void testEvaluateJob_boolean_JobNotAdded() {
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
        assertFalse(this.evaluator.getInputTracker().containsJob(job));
    }

    @Test
    @DisplayName("ceck if executable job is removed from InputTracker")
    public void testEvaluateJob_boolean_ExecutableJobRemovedFromInputTracker() {
        WacodisJobDefinition jobDefinition = new WacodisJobDefinition();
        jobDefinition.setInputs(new ArrayList<>()); //no inputs
        WacodisJobDefinitionExecution execution = new WacodisJobDefinitionExecution();
        execution.setPattern("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)
        WacodisJobDefinitionTemporalCoverage tempCov = new WacodisJobDefinitionTemporalCoverage();
        tempCov.setPreviousExecution(Boolean.TRUE);
        jobDefinition.setTemporalCoverage(tempCov);
        jobDefinition.setExecution(execution);
        WacodisJobWrapper job = new WacodisJobWrapper(jobDefinition, new DateTime());

        EvaluationStatus status = this.evaluator.evaluateJob(job, true); //add to input tracker
        
        //job has no inputs, empty response -> executable
        assertEquals(EvaluationStatus.EXECUTABLE, status);
        assertFalse(this.evaluator.getInputTracker().containsJob(job)); //job must be removed from InputTracker if executable
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
