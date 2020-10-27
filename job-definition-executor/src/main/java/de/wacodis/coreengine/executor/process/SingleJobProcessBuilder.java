/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.engine.utils.factories.DefaultOutputHelper;
import de.wacodis.core.engine.utils.factories.JobOutputHelper;
import de.wacodis.core.models.JobOutputDescriptor;
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

    private final JobOutputHelper outputHelper = new DefaultOutputHelper();
    private final ProcessContextBuilder contextBuilder;
    private final ProcessContextToJobProcessConverter contextConverter;
    private Map<String, Object> additionalProcessParameters;

    public SingleJobProcessBuilder(ProcessContextBuilder contextBuilder, ProcessContextToJobProcessConverter contextConverter) {
        this(contextBuilder, contextConverter, new HashMap<>());
    }

    public SingleJobProcessBuilder(ProcessContextBuilder contextBuilder) {
        this(contextBuilder, new DefaultProcessContextToJobProcessConverter());
    }

    public SingleJobProcessBuilder(ProcessContextBuilder contextBuilder, ProcessContextToJobProcessConverter contextConverter, Map<String, Object> additionalProcessParameters) {
        this.contextBuilder = contextBuilder;
        this.contextConverter = contextConverter;
        this.additionalProcessParameters = additionalProcessParameters;
    }

    public SingleJobProcessBuilder(ProcessContextBuilder contextBuilder, Map<String, Object> additionalProcessParameters) {
        this(contextBuilder, new DefaultProcessContextToJobProcessConverter(), additionalProcessParameters);
    }

    public Map<String, Object> getAdditionalProcessParameters() {
        return additionalProcessParameters;
    }

    public void setAdditionalProcessParameters(Map<String, Object> additionalProcessParameters) {
        this.additionalProcessParameters = additionalProcessParameters;
    }

    @Override
    public List<JobProcess> getJobProcessesForWacodisJob(WacodisJobWrapper job, Process tool) throws JobProcessCreationException {
        List<JobOutputDescriptor> expectedOutputs = this.outputHelper.getExepectedOutputsForJob(job.getJobDefinition());
        ProcessContext completeContext = this.contextBuilder.buildProcessContext(job, this.additionalProcessParameters, expectedOutputs.toArray(new JobOutputDescriptor[expectedOutputs.size()]));

        LOGGER.debug("retain complete context of wacodis job {}, create single job process", job.getJobDefinition().getId());

        return contextConverter.createJobProcesses(Arrays.asList(completeContext), job.getJobDefinition(), tool);
    }

}
