/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.exception.JobProcessCreationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

/**
 * creates only one JobProcess using the first resource for the first copernicus
 * input that is found, the first resource is considered the best/highes
 * priority resource (see DataAccess)
 *
 * @author Arne
 */
public class BestCopernicusInputJobProcessBuilder implements JobProcessBuilder {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BestCopernicusInputJobProcessBuilder.class);

    private final ProcessContextBuilder contextBuilder;
    private final List<ExpectedProcessOutput> expectedOutputs;
    private final ProcessContextToJobProcessConverter contextConverter;
    private Map<String, Object> additionalProcessParameters;

    public BestCopernicusInputJobProcessBuilder(ProcessContextBuilder contextBuilder, List<ExpectedProcessOutput> expectedOutputs, ProcessContextToJobProcessConverter contextConverter) {
        this(contextBuilder, expectedOutputs, contextConverter, new HashMap<>());
    }

    public BestCopernicusInputJobProcessBuilder(ProcessContextBuilder contextBuilder, List<ExpectedProcessOutput> expectedOutputs) {
        this(contextBuilder, expectedOutputs, new DefaultProcessContextToJobProcessConverter());
    }

    public BestCopernicusInputJobProcessBuilder(ProcessContextBuilder contextBuilder, List<ExpectedProcessOutput> expectedOutputs, ProcessContextToJobProcessConverter contextConverter, Map<String, Object> additionalProcessParameters) {
        this.contextBuilder = contextBuilder;
        this.expectedOutputs = expectedOutputs;
        this.contextConverter = contextConverter;
        this.additionalProcessParameters = additionalProcessParameters;
    }

    public BestCopernicusInputJobProcessBuilder(ProcessContextBuilder contextBuilder, List<ExpectedProcessOutput> expectedOutputs, Map<String, Object> additionalProcessParameters) {
        this(contextBuilder, expectedOutputs, new DefaultProcessContextToJobProcessConverter(), additionalProcessParameters);
    }

    public void setAdditionalProcessParameters(Map<String, Object> additionalProcessParameters) {
        this.additionalProcessParameters = additionalProcessParameters;
    }

    public Map<String, Object> getAdditionalProcessParameters() {
        return additionalProcessParameters;
    }

    @Override
    public List<JobProcess> getJobProcessesForWacodisJob(WacodisJobWrapper job, Process tool) throws JobProcessCreationException {
        //get first copernicus input
        InputHelper copernicusInput = findCopernicusInput(job);

        if (copernicusInput == null) {
            throw new JobProcessCreationException("Unable to create job process for wacodis job " + job.getJobDefinition().getId().toString() + " because job definition does not contain a input of type CopernicusSubsetDefinition");
        }

        LOGGER.info("build single job process for wacodis job {} with best resources for input {}", job.getJobDefinition().getId(), copernicusInput.getSubsetDefinitionIdentifier());

        List<AbstractResource> allResources = copernicusInput.getResource();
        if (!allResources.isEmpty()) { //only keep best resources
            List<AbstractResource> bestResource = new ArrayList<>();
            bestResource.add(allResources.get(0)); //first resource is supposed to be the best resource (provided by data access)

            copernicusInput.setResource(bestResource);
        } else {
            throw new JobProcessCreationException("Unable to create job process for wacodis job " + job.getJobDefinition().getId().toString() + " because no resources for input " + copernicusInput.getSubsetDefinitionIdentifier() + " are provided");
        }

        ExpectedProcessOutput[] expectedOutputArr = this.expectedOutputs.toArray(new ExpectedProcessOutput[this.expectedOutputs.size()]);
        ProcessContext context = this.contextBuilder.buildProcessContext(job, this.additionalProcessParameters, expectedOutputArr);
        List<JobProcess> singleJobProcess = contextConverter.createJobProcesses(Arrays.asList(context), job.getJobDefinition(), tool);
    
        return singleJobProcess;
    }

    private InputHelper findCopernicusInput(WacodisJobWrapper job) {
        List<InputHelper> inputs = job.getInputs();
        InputHelper copernicusInput = null;

        for (InputHelper input : inputs) {
            if (input.getSubsetDefinition() instanceof CopernicusSubsetDefinition) {
                copernicusInput = input;
                break;
            }
        }
        return copernicusInput;
    }

}
