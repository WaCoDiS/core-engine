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
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.engine.utils.factories.DefaultOutputHelper;
import de.wacodis.core.engine.utils.factories.JobOutputHelper;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.exception.JobProcessCreationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.LoggerFactory;

/**
 * split Wacodis Job into multiple JobProcesses using the first resource for the
 * first copernicus input that is found, or (if this.splitInputIndentifier !=
 * null) for the input with identifier == splitInputIndentifier, the first
 * resource is considered the best/highes priority resource (see DataAccess)
 *
 * @author Arne
 */
public class SplitByInputJobProcessBuilder implements JobProcessBuilder {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SplitByInputJobProcessBuilder.class);

    private final ProcessContextBuilder contextBuilder;
    private final ProcessContextToJobProcessConverter contextConverter;
    private final JobOutputHelper outputHelper = new DefaultOutputHelper();
    private Map<String, Object> additionalProcessParameters;
    private String splitInputIdentifier;

    public SplitByInputJobProcessBuilder(ProcessContextBuilder contextBuilder, ProcessContextToJobProcessConverter contextConverter) {
        this(contextBuilder, contextConverter, new HashMap<>());
    }

    public SplitByInputJobProcessBuilder(ProcessContextBuilder contextBuilder, List<JobOutputDescriptor> expectedOutputs) {
        this(contextBuilder, new DefaultProcessContextToJobProcessConverter());
    }

    public SplitByInputJobProcessBuilder(ProcessContextBuilder contextBuilder, ProcessContextToJobProcessConverter contextConverter, Map<String, Object> additionalProcessParameters) {
        this.contextBuilder = contextBuilder;
        this.contextConverter = contextConverter;
        this.additionalProcessParameters = additionalProcessParameters;
    }

    public SplitByInputJobProcessBuilder(ProcessContextBuilder contextBuilder, Map<String, Object> additionalProcessParameters) {
        this(contextBuilder, new DefaultProcessContextToJobProcessConverter(), additionalProcessParameters);
    }

    public Map<String, Object> getAdditionalProcessParameters() {
        return additionalProcessParameters;
    }

    public void setAdditionalProcessParameters(Map<String, Object> additionalProcessParameters) {
        this.additionalProcessParameters = additionalProcessParameters;
    }

    public String getSplitInputIdentifier() {
        return splitInputIdentifier;
    }

    public void setSplitInputIdentifier(String splitInputIdentifier) {
        this.splitInputIdentifier = splitInputIdentifier;
    }

    @Override
    public List<JobProcess> getJobProcessesForWacodisJob(WacodisJobWrapper job, Process tool) throws JobProcessCreationException {
        Optional<InputHelper> optSplitInput = (this.splitInputIdentifier != null && !this.splitInputIdentifier.isEmpty()) ? findInputByIdentifier(job, this.splitInputIdentifier) : findFirstCopernicusInput(job);
        String splitInputID;
        List<JobOutputDescriptor> expectedOutputs = this.outputHelper.getExepectedOutputsForJob(job.getJobDefinition());
        ProcessContext completeContext = this.contextBuilder.buildProcessContext(job, this.additionalProcessParameters, expectedOutputs.toArray(new JobOutputDescriptor[expectedOutputs.size()]));

        if (optSplitInput.isPresent()) {
            splitInputID = optSplitInput.get().getSubsetDefinitionIdentifier();
        } else {
            String msg;
            if (this.splitInputIdentifier != null && !this.splitInputIdentifier.isEmpty()) {
                msg = "Unable to create job process for wacodis job " + job.getJobDefinition().getId().toString() + " because job definition does not contain a input with identifier " + this.splitInputIdentifier;
            } else {
                msg = "Unable to create job process for wacodis job " + job.getJobDefinition().getId().toString() + " because job definition does not contain a input of type CopernicusSubsetDefinition";
            }
            throw new JobProcessCreationException(msg);
        }

        List<ProcessContext> jobProcessContext = buildContextForEachInput(completeContext, splitInputID, job);
        List<JobProcess> jobProcesses = this.contextConverter.createJobProcesses(jobProcessContext, job.getJobDefinition(), tool);

        LOGGER.debug("split wacodis job {} by input {}, created {} job processes", job.getJobDefinition().getId(), splitInputID, jobProcesses.size());

        return jobProcesses;
    }

    private Optional<InputHelper> findFirstCopernicusInput(WacodisJobWrapper job) {
        LOGGER.debug("create job process for wacodis job {} by first input of type CopernicusSubsetDefinition", job.getJobDefinition().getId());

        List<InputHelper> inputs = job.getInputs();
        Optional<InputHelper> copernicusInput = inputs.stream().filter(i -> i.getSubsetDefinition() instanceof CopernicusSubsetDefinition).findFirst();

        return copernicusInput;
    }

    private Optional<InputHelper> findInputByIdentifier(WacodisJobWrapper job, String inputIdentifier) {
        LOGGER.debug("create job process for wacodis job {} by input with id {}", job.getJobDefinition().getId(), inputIdentifier);

        List<InputHelper> inputs = job.getInputs();
        Optional<InputHelper> input = inputs.stream().filter(i -> i.getSubsetDefinitionIdentifier().equals(inputIdentifier)).findFirst();

        return input;
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
