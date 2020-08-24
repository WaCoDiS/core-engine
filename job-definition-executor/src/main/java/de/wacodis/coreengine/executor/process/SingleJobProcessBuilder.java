/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.exception.JobProcessCreationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne
 */
public class SingleJobProcessBuilder implements JobProcessBuilder {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SingleJobProcessBuilder.class);

    private final ProcessContextBuilder contextBuilder;
    private final List<ExpectedProcessOutput> expectedOutputs;
    private final ProcessContextToJobProcessConverter contextConverter;
    private Map<String, Object> additionalProcessParameters;

    public SingleJobProcessBuilder(ProcessContextBuilder contextBuilder, List<ExpectedProcessOutput> expectedOutputs, ProcessContextToJobProcessConverter contextConverter) {
        this(contextBuilder, expectedOutputs, contextConverter, new HashMap<>());
    }

    public SingleJobProcessBuilder(ProcessContextBuilder contextBuilder, List<ExpectedProcessOutput> expectedOutputs) {
        this(contextBuilder, expectedOutputs, new DefaultProcessContextToJobProcessConverter());
    }

    public SingleJobProcessBuilder(ProcessContextBuilder contextBuilder, List<ExpectedProcessOutput> expectedOutputs, ProcessContextToJobProcessConverter contextConverter, Map<String, Object> additionalProcessParameters) {
        this.contextBuilder = contextBuilder;
        this.expectedOutputs = expectedOutputs;
        this.contextConverter = contextConverter;
        this.additionalProcessParameters = additionalProcessParameters;
    }

    public SingleJobProcessBuilder(ProcessContextBuilder contextBuilder, List<ExpectedProcessOutput> expectedOutputs, Map<String, Object> additionalProcessParameters) {
        this(contextBuilder, expectedOutputs, new DefaultProcessContextToJobProcessConverter(), additionalProcessParameters);
    }

    public Map<String, Object> getAdditionalProcessParameters() {
        return additionalProcessParameters;
    }

    public void setAdditionalProcessParameters(Map<String, Object> additionalProcessParameters) {
        this.additionalProcessParameters = additionalProcessParameters;
    }

    @Override
    public List<JobProcess> getJobProcessesForWacodisJob(WacodisJobWrapper job, Process tool) throws JobProcessCreationException {
        ExpectedProcessOutput[] expectedOutputArr = this.expectedOutputs.toArray(new ExpectedProcessOutput[this.expectedOutputs.size()]);
        ProcessContext completeContext = this.contextBuilder.buildProcessContext(job, this.additionalProcessParameters, expectedOutputArr);
        
        LOGGER.debug("retain complete context of wacodis job {}, create single job process", job.getJobDefinition().getId());
        
        return contextConverter.createJobProcesses(Arrays.asList(completeContext), job.getJobDefinition(), tool);
    }

}
