/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.exception.JobProcessCreationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne
 */
public class SplitByCopernicusSubsetJobProcessBuilder implements JobProcessBuilder {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SplitByCopernicusSubsetJobProcessBuilder.class);

    private final ProcessContextBuilder contextBuilder;
    private final List<ExpectedProcessOutput> expectedOutputs;
    private final ProcessContextToJobProcessConverter contextConverter;
    private Map<String, Object> additionalProcessParameters;

    public SplitByCopernicusSubsetJobProcessBuilder(ProcessContextBuilder contextBuilder, List<ExpectedProcessOutput> expectedOutputs, ProcessContextToJobProcessConverter contextConverter) {
        this(contextBuilder, expectedOutputs, contextConverter, new HashMap<>());
    }

    public SplitByCopernicusSubsetJobProcessBuilder(ProcessContextBuilder contextBuilder, List<ExpectedProcessOutput> expectedOutputs) {
        this(contextBuilder, expectedOutputs, new DefaultProcessContextToJobProcessConverter());
    }

    public SplitByCopernicusSubsetJobProcessBuilder(ProcessContextBuilder contextBuilder, List<ExpectedProcessOutput> expectedOutputs, ProcessContextToJobProcessConverter contextConverter, Map<String, Object> additionalProcessParameters) {
        this.contextBuilder = contextBuilder;
        this.expectedOutputs = expectedOutputs;
        this.contextConverter = contextConverter;
        this.additionalProcessParameters = additionalProcessParameters;
    }

    public SplitByCopernicusSubsetJobProcessBuilder(ProcessContextBuilder contextBuilder, List<ExpectedProcessOutput> expectedOutputs, Map<String, Object> additionalProcessParameters) {
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
        String splitInputID = findSplitInputIdentifier(job);
        ExpectedProcessOutput[] expectedOutputArr = this.expectedOutputs.toArray(new ExpectedProcessOutput[this.expectedOutputs.size()]);
        ProcessContext completeContext = this.contextBuilder.buildProcessContext(job, this.additionalProcessParameters, expectedOutputArr);

        if (splitInputID == null) {
            throw new JobProcessCreationException("Unable to create job processes for wacodis job " + job.getJobDefinition().getId().toString() + " because job definition does not contain a input of type CopernicusSubsetDefinition");
        }
        
        List<ProcessContext> jobProcessContext = buildContextForEachInput(completeContext, splitInputID, job);
        List<JobProcess> jobProcesses = this.contextConverter.createJobProcesses(jobProcessContext, job.getJobDefinition(), tool);

         LOGGER.debug("split wacodis job {} by input {}, created {} job processes", job.getJobDefinition().getId(), splitInputID, jobProcesses.size());
        
        return jobProcesses;
    }



    /**
     * assume first found CopernicusSubsetDefinition as input to split context
     *
     * @param job
     * @return
     */
    private String findSplitInputIdentifier(WacodisJobWrapper job) {
        List<InputHelper> inputs = job.getInputs();
        String splitInputIdentifier = null;

        for (InputHelper input : inputs) {
            if (input.getSubsetDefinition() instanceof CopernicusSubsetDefinition) {
                splitInputIdentifier = input.getSubsetDefinitionIdentifier();
                break;
            }
        }

        LOGGER.debug("automatically select input {} of wacodis job {} to split job into multiple sub processes");

        return splitInputIdentifier;
    }

    private List<ProcessContext> buildContextForEachInput(ProcessContext completeContext, String splitInputID, WacodisJobWrapper job) throws JobProcessCreationException {
        List<ProcessContext> contexts = new ArrayList<>();

        List<ResourceDescription> rds = completeContext.getInputResource(splitInputID);

        if (rds == null) {
            throw new JobProcessCreationException("cannot split process context of wacodis job " + job.getJobDefinition().getId().toString() + ", no resources for input with identifier " + splitInputID);
        }

        ProcessContext splitContext;
        for (ResourceDescription rd : rds) {
            splitContext = new ProcessContext();
            //copy common attributes
            splitContext.setExpectedOutputs(completeContext.getExpectedOutputs());
            splitContext.setWacodisProcessID(completeContext.getWacodisProcessID());
            //copy input resources excluding split input
            Map<String, List<ResourceDescription>> commonInputs = completeContext.getInputResources();
            commonInputs.remove(splitInputID);
            splitContext.setInputResources(commonInputs);
            //add split input to resources
            splitContext.addInputResource(splitInputID, rd);

            contexts.add(splitContext);
        }

        return contexts;
    }
}
