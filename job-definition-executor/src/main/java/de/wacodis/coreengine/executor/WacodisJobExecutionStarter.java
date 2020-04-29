/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor;

import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionRetrySettings;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.configuration.WebProcessingServiceConfiguration;
import de.wacodis.coreengine.executor.process.wps.WPSProcess;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.wacodis.coreengine.executor.process.Process;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessContextBuilder;
import de.wacodis.coreengine.executor.process.wps.WPSProcessContextBuilder;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisherChannel;
import de.wacodis.coreengine.executor.process.AsynchronousWacodisJobExecutor;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.JobProcess;
import de.wacodis.coreengine.executor.process.ResourceDescription;
import de.wacodis.coreengine.executor.process.SequentialWacodisJobExecutor;
import de.wacodis.coreengine.executor.process.WacodisJobExecutor;
import de.wacodis.coreengine.executor.process.events.JobExecutionEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessEventHandler;
import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEventHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationEventPublisher;

/**
 * start execution of wacodis jobs asynchronously in separate threads
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class WacodisJobExecutionStarter {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobExecutionStarter.class);

    //default expected outputs
    private static final ExpectedProcessOutput PRODUCTOUTPUT = new ExpectedProcessOutput("PRODUCT", "image/geotiff");
    private static final ExpectedProcessOutput METADATAOUTPUT = new ExpectedProcessOutput("METADATA", "text/json", false, false); //no published output
    //default retry settings
    private static final int DEFAULT_MAXRETRIES = 0;
    private static final long DEFAULT_RETRIEDELAY_MILLIES = 0l;

    private final WPSClientSession wpsClient;
    private final ProcessContextBuilder contextBuilder;
    private List<ExpectedProcessOutput> expectedProcessOutputs;

    @Autowired
    ToolMessagePublisherChannel processMessagePublisher;

    @Autowired
    private ApplicationEventPublisher jobExecutionFailedPublisher;

    @Autowired
    private WebProcessingServiceConfiguration wpsConfig;

    public WacodisJobExecutionStarter() {
        this.wpsClient = WPSClientSession.getInstance();
        this.contextBuilder = new WPSProcessContextBuilder(wpsConfig);
    }

    public void setWpsConfig(WebProcessingServiceConfiguration wpsConfig) {
        this.wpsConfig = wpsConfig;
    }

    public WebProcessingServiceConfiguration getWpsConfig() {
        return wpsConfig;
    }

    public List<ExpectedProcessOutput> getExpectedProcessOutputs() {
        return expectedProcessOutputs;
    }

    public void setExpectedProcessOutputs(List<ExpectedProcessOutput> expectedProcessOutputs) {
        this.expectedProcessOutputs = expectedProcessOutputs;
    }

    public void executeWacodisJob(WacodisJobWrapper job) {
        String toolProcessID = job.getJobDefinition().getProcessingTool();
        String wacodisJobID = job.getJobDefinition().getId().toString();
        ProcessContext totalToolContext = this.contextBuilder.buildProcessContext(job, this.expectedProcessOutputs.toArray(new ExpectedProcessOutput[this.expectedProcessOutputs.size()]));

        LOGGER.info("execute Wacodis Job " + wacodisJobID + " using processing tool " + toolProcessID);

        List<ProcessContext> wpsCallContexts;

        if (this.wpsConfig.isCallWPSPerInput()) { //call wps proess for each copernicus input
            //create seperate input for each copernicus input
            String splitInputID = getSplitInputIdentifier(job);
            wpsCallContexts = buildContextForEachInput(totalToolContext, splitInputID);
            LOGGER.debug("split context of wacodis job {} by input {}, created {} sub processes", wacodisJobID, splitInputID, wpsCallContexts.size());
        } else { //call wps process only once with all inputs
            wpsCallContexts = new ArrayList<>();
            wpsCallContexts.add(totalToolContext);
            LOGGER.debug("retain complete context of wacodis job {}, execute job as single process", wacodisJobID);
        }

        setDefaultRetrySettingsIfAbsent(job.getJobDefinition()); //use defaults if retry settings are not set in job definition

        //execute all sub processes
        executeWPSProcesses(job, wpsCallContexts);
    }

    @PostConstruct
    private void initExpectedProcessOutputs() {
        List<ExpectedProcessOutput> selectedOutputs;

        if (this.expectedProcessOutputs != null) {
            selectedOutputs = this.expectedProcessOutputs;
        } else if (this.wpsConfig.getExpectedProcessOutputs() != null) {
            selectedOutputs = this.wpsConfig.getExpectedProcessOutputs();
        } else {
            selectedOutputs = Arrays.asList(new ExpectedProcessOutput[]{PRODUCTOUTPUT, METADATAOUTPUT});
        }

        this.expectedProcessOutputs = selectedOutputs;
        LOGGER.debug("set expected process outputs to: " + selectedOutputs.toString());
    }

    private void executeWPSProcesses(WacodisJobWrapper job, List<ProcessContext> subProcessContexts) {
        String toolProcessID = job.getJobDefinition().getProcessingTool();

        Process toolProcess = new WPSProcess(this.wpsClient, this.wpsConfig.getUri(), this.wpsConfig.getVersion(), toolProcessID);
        List<JobProcess> subProcesses = createJobProcesses(subProcessContexts, job.getJobDefinition(), toolProcess);
        WacodisJobExecutor jobExecutor = createWacodisJobExecutor(job.getJobDefinition());

        //declare handler for execution events
        WacodisJobExecutionEventHandler jobExecutionHandler = new JobExecutionEventHandler(this.processMessagePublisher);
        JobProcessEventHandler jobProcessHandler = new JobProcessEventHandler(job, this.processMessagePublisher, this.jobExecutionFailedPublisher);

        //register handler for execution and job process events
        jobExecutor.setProcessExecutedHandler(jobProcessHandler); //job process events
        jobExecutor.setProcessFailedHandler(jobProcessHandler);
        jobExecutor.setProcessStartedHandler(jobProcessHandler);
        jobExecutor.setFinalProcessFinishedHandler(jobExecutionHandler);// job execution events
        jobExecutor.setFirstProcessStartedHandler(jobExecutionHandler);

        //execute  all sub process of wacodis job, call wps per sub process
        LOGGER.info("initiate execution of all sub processes of wacodis job {}", job.getJobDefinition().getId());
        jobExecutor.executeAllSubProcesses(subProcesses);
    }

    private List<ProcessContext> buildContextForEachInput(ProcessContext totalContext, String splitInputID) {
        List<ProcessContext> contexts = new ArrayList<>();

        List<ResourceDescription> rds = totalContext.getInputResource(splitInputID);

        if (rds == null) {
            throw new IllegalArgumentException("cannot split process context, process context does not contain input with identifier " + splitInputID);
        }

        ProcessContext splitContext;
        for (ResourceDescription rd : rds) {
            splitContext = new ProcessContext();
            //copy common attributes
            splitContext.setExpectedOutputs(totalContext.getExpectedOutputs());
            splitContext.setWacodisProcessID(totalContext.getWacodisProcessID());
            //copy input resources excluding split input
            Map<String, List<ResourceDescription>> commonInputs = totalContext.getInputResources();
            commonInputs.remove(splitInputID);
            splitContext.setInputResources(commonInputs);
            //add split input to resources
            splitContext.addInputResource(splitInputID, rd);

            contexts.add(splitContext);
        }

        return contexts;
    }

    private void setDefaultRetrySettingsIfAbsent(WacodisJobDefinition jobDef) {
        if (jobDef.getRetrySettings() == null) {
            WacodisJobDefinitionRetrySettings defaultRetrySettings = new WacodisJobDefinitionRetrySettings();
            defaultRetrySettings.setMaxRetries(DEFAULT_MAXRETRIES);
            defaultRetrySettings.setRetryDelayMillies(DEFAULT_RETRIEDELAY_MILLIES);

            jobDef.setRetrySettings(defaultRetrySettings);
        }
    }

    /**
     * assume first found CopernicusSubsetDefinition as input to split context
     *
     * @param job
     * @return
     */
    private String getSplitInputIdentifier(WacodisJobWrapper job) {
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

    private List<JobProcess> createJobProcesses(List<ProcessContext> contexts, WacodisJobDefinition jobDefinition, Process processingTool) {
        JobProcess[] processes = new JobProcess[contexts.size()];
        String uniqueSuffix = DateTime.now().toString();
        String wacodisJobId = jobDefinition.getId().toString();

        JobProcess process;
        String processId;
        for (int i = 0; i < processes.length; i++) {
            processId = createJobProcessID(wacodisJobId, (i + 1), processes.length, uniqueSuffix);
            process = new JobProcess(processId, jobDefinition, processingTool, contexts.get(i));
            processes[i] = process;
        }

        return Arrays.asList(processes);
    }

    private String createJobProcessID(String commonId, int part, int of, String uniqueProcessSuffix) {
        String jobProcessId = commonId + "_" + part + "_" + of + "_" + uniqueProcessSuffix;
        return jobProcessId;
    }

    private WacodisJobExecutor createWacodisJobExecutor(WacodisJobDefinition jobDefinition) {
        WacodisJobExecutor executor;

        if (this.wpsConfig.isProcessInputsSequentially()) {
            executor = new SequentialWacodisJobExecutor();
        } else {
            ExecutorService execService;

            if (this.wpsConfig.getMaxParallelWPSProcessPerJob() > 0) {
                execService = Executors.newCachedThreadPool();
            } else {
                execService = Executors.newFixedThreadPool(this.wpsConfig.getMaxParallelWPSProcessPerJob());
            }

            executor = new AsynchronousWacodisJobExecutor(execService);
        }

        LOGGER.debug("created executor of type {} for wacodis job {}", executor.getClass().getSimpleName(), jobDefinition.getId());

        return executor;
    }

}
