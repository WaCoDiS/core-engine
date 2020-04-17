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
import de.wacodis.coreengine.executor.events.WacodisJobExecutionFailedEvent;
import de.wacodis.coreengine.executor.process.wps.WPSProcess;
import de.wacodis.coreengine.executor.process.WacodisJobExecutor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.wacodis.coreengine.executor.process.Process;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessContextBuilder;
import de.wacodis.coreengine.executor.process.wps.WPSProcessContextBuilder;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisherChannel;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.ProcessOutputDescription;
import de.wacodis.coreengine.executor.process.ResourceDescription;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.PostConstruct;
import org.springframework.context.ApplicationEventPublisher;

/**
 * start execution of wacodis jobs asynchronously in separate threads
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class WacodisJobTaskStarter {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobTaskStarter.class);

    //default expected outputs
    private static final ExpectedProcessOutput PRODUCTOUTPUT = new ExpectedProcessOutput("PRODUCT", "image/geotiff");
    private static final ExpectedProcessOutput METADATAOUTPUT = new ExpectedProcessOutput("METADATA", "text/json", false, false); //no published output
    //default retry settings
    private static final int DEFAULT_MAXRETRIES = 0;
    private static final long DEFAULT_RETRIEDELAY_MILLIES = 0l;

    private final WPSClientSession wpsClient;
    private final ExecutorService wacodisJobExecutionService;
    private final ProcessContextBuilder contextBuilder;
    private List<ExpectedProcessOutput> expectedProcessOutputs;

    @Autowired
    ToolMessagePublisherChannel newProductPublisher;

    @Autowired
    private ApplicationEventPublisher jobExecutionFailedPublisher;

    @Autowired
    private WebProcessingServiceConfiguration wpsConfig;

    public WacodisJobTaskStarter() {
        this.wpsClient = WPSClientSession.getInstance();
        this.wacodisJobExecutionService = Executors.newCachedThreadPool();
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
            LOGGER.debug("split context of wacodis job {} by input {}, created {} sub contexts", wacodisJobID, splitInputID, wpsCallContexts.size());
        }else{ //call wps process only once with all inputs
            wpsCallContexts = new ArrayList<>();
            wpsCallContexts.add(totalToolContext);
            LOGGER.debug("retain complete context of wacodis job {}", wacodisJobID);
        }
        
        int contextCount = wpsCallContexts.size();
        int executeCounter = 0;
        while(!wpsCallContexts.isEmpty()){
            //submit job async
            ++executeCounter;
            LOGGER.info("execute part " + executeCounter + " of " + contextCount + " of Wacodis Job " + wacodisJobID + " using processing tool " + toolProcessID);
            ProcessContext context = wpsCallContexts.remove(0); //pop
            executeWPSProcess(job, context);
        }

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

    @PreDestroy
    private void shutdownJobExecutionService() {
        this.wacodisJobExecutionService.shutdown();
        LOGGER.info("shutdown wacodis job executor service");
    }

    private void executeWPSProcess(WacodisJobWrapper job, ProcessContext toolContext) {
        String toolProcessID = job.getJobDefinition().getProcessingTool();
        String wacodisJobID = job.getJobDefinition().getId().toString();

        Process toolProcess = new WPSProcess(this.wpsClient, this.wpsConfig.getUri(), this.wpsConfig.getVersion(), toolProcessID);
        
        CompletableFuture<ProcessOutputDescription> processFuture = CompletableFuture.supplyAsync(() -> {
            try {
                LOGGER.info("start new thread for Wacodis Job " + wacodisJobID + ", toolProcess: " + toolProcess);

                ProcessOutputDescription processOutput = new WacodisJobExecutor(toolProcess, toolContext, job.getJobDefinition(), this.newProductPublisher).execute();
                return processOutput;
            } catch (Exception e) { //catch all exception from async job, rethrow as CompetionException and handle in exceptionally()
                throw new CompletionException(e);
            }
        }, wacodisJobExecutionService);

        //handle process output if async job was successfully executed        
        processFuture.thenAccept((ProcessOutputDescription processOutput)
                -> {
            LOGGER.info("execution of wacodis job returned successfully, WacodisJobID: {}, toolID: {}, processOutput: ", wacodisJobID, toolProcessID, processOutput.toString());
        }).exceptionally((Throwable t) -> { //handle exceptions that were raised by async job
            setDefaultRetrySettingsIfAbsent(job.getJobDefinition()); //use defaults if retry settings are not set in job definition
            LOGGER.error("execution of wacodis job failed, WacodisJobID: " + wacodisJobID + ",toolID: " + toolProcessID + ", retry attempt: " + job.getExecutionContext().getRetryCount() + " of " + job.getJobDefinition().getRetrySettings().getMaxRetries(), t.getCause());
            //trigger job execution failed event

            if (job.getExecutionContext().getRetryCount() < job.getJobDefinition().getRetrySettings().getMaxRetries()) { //check if retry
                //fire event to schedule retry attempt
                int retries = job.incrementRetryCount();
                LOGGER.info("publish jobExecutionFailedEvent to schedule retry attempt {} of {}, WacodisJobID {}, toolID: {}", retries, job.getJobDefinition().getRetrySettings().getMaxRetries(), wacodisJobID, toolProcessID);
                WacodisJobExecutionFailedEvent failEvent = new WacodisJobExecutionFailedEvent(this, job, t);
                this.jobExecutionFailedPublisher.publishEvent(failEvent);
            } else {
                //retries exceeded, job failed ultimately
                LOGGER.error("execution of wacodis job failed ultimately after " + job.getExecutionContext().getRetryCount() + " of " + job.getJobDefinition().getRetrySettings().getMaxRetries() + " retries, WacodisJobID: " + wacodisJobID + ",toolID: " + toolProcessID);
            }
            return null; //satisfy return type
        });
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

        return splitInputIdentifier;
    }
}
