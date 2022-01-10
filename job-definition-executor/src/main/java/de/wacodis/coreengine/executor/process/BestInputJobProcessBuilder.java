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
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.engine.utils.factories.DefaultOutputHelper;
import de.wacodis.core.engine.utils.factories.JobOutputHelper;
import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.exception.JobProcessCreationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.LoggerFactory;

/**
 * creates only one JobProcess using the first resource for the first copernicus
 * input that is found, or (if this.splitInputIndentifier != null) for the input with identifier == splitInputIndentifier, the first resource is considered the best/highes
 * priority resource (see DataAccess)
 *
 * @author Arne
 */
public class BestInputJobProcessBuilder implements JobProcessBuilder {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BestInputJobProcessBuilder.class);

    private final ProcessContextBuilder contextBuilder;
    private final ProcessContextToJobProcessConverter contextConverter;
    private final JobOutputHelper outputHelper = new DefaultOutputHelper();
    private String splitInputIdentifier;
    private Map<String, Object> additionalProcessParameters;

    public BestInputJobProcessBuilder(ProcessContextBuilder contextBuilder,  ProcessContextToJobProcessConverter contextConverter) {
        this(contextBuilder, contextConverter, new HashMap<>());
    }

    public BestInputJobProcessBuilder(ProcessContextBuilder contextBuilder) {
        this(contextBuilder, new DefaultProcessContextToJobProcessConverter());
    }

    public BestInputJobProcessBuilder(ProcessContextBuilder contextBuilder, ProcessContextToJobProcessConverter contextConverter, Map<String, Object> additionalProcessParameters) {
        this.contextBuilder = contextBuilder;
        this.contextConverter = contextConverter;
        this.additionalProcessParameters = additionalProcessParameters;
    }

    public BestInputJobProcessBuilder(ProcessContextBuilder contextBuilder,  Map<String, Object> additionalProcessParameters) {
        this(contextBuilder, new DefaultProcessContextToJobProcessConverter(), additionalProcessParameters);
    }

    public void setAdditionalProcessParameters(Map<String, Object> additionalProcessParameters) {
        this.additionalProcessParameters = additionalProcessParameters;
    }

    public Map<String, Object> getAdditionalProcessParameters() {
        return additionalProcessParameters;
    }

    public String getSplitInputIdentifier() {
        return splitInputIdentifier;
    }

    public void setSplitInputIdentifier(String splitInputIdentifier) {
        this.splitInputIdentifier = splitInputIdentifier;
    }

     @Override
    public List<JobProcess> getJobProcessesForWacodisJob(WacodisJobWrapper job, Process tool) throws JobProcessCreationException {
        //get input by id or first copernicus input
        Optional<InputHelper> optInput = (this.splitInputIdentifier != null && !this.splitInputIdentifier.isEmpty()) ? findInputByIdentifier(job, this.splitInputIdentifier) : findFirstCopernicusInput(job);
        InputHelper input;

        if (optInput.isPresent()) {
            input = optInput.get();
        } else {
            String msg;
            if (this.splitInputIdentifier != null && !this.splitInputIdentifier.isEmpty()) {
                msg = "Unable to create job process for wacodis job " + job.getJobDefinition().getId().toString() + " because job definition does not contain a input with identifier " + this.splitInputIdentifier;
            } else {
                msg = "Unable to create job process for wacodis job " + job.getJobDefinition().getId().toString() + " because job definition does not contain a input of type CopernicusSubsetDefinition";
            }
            throw new JobProcessCreationException(msg);
        }

        LOGGER.info("build single job process for wacodis job {} with best resources for input {}", job.getJobDefinition().getId(), input.getSubsetDefinitionIdentifier());

        List<AbstractResource> allResources = input.getResource();
        if (!allResources.isEmpty()) { //only keep best resources
            List<AbstractResource> bestResource = new ArrayList<>();
            bestResource.add(allResources.get(0)); //first resource is supposed to be the best resource (provided by data access)

            input.setResource(bestResource);
        } else {
            throw new JobProcessCreationException("Unable to create job process for wacodis job " + job.getJobDefinition().getId().toString() + " because no resources for input " + input.getSubsetDefinitionIdentifier() + " are provided");
        }

        List<JobOutputDescriptor> expectedOutputs = this.outputHelper.getExepectedOutputsForJob(job.getJobDefinition());
        ProcessContext context = this.contextBuilder.buildProcessContext(job, this.additionalProcessParameters, expectedOutputs.toArray(new JobOutputDescriptor[expectedOutputs.size()]));
        List<JobProcess> singleJobProcess = contextConverter.createJobProcesses(Arrays.asList(context), job.getJobDefinition(), tool);

        return singleJobProcess;
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

}
