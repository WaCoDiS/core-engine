/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionRetrySettings;
import de.wacodis.core.models.WacodisJobFailed;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.configuration.WebProcessingServiceConfiguration;
import de.wacodis.coreengine.executor.exception.JobProcessCompletionException;
import de.wacodis.coreengine.executor.exception.JobProcessCreationException;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisher;
import de.wacodis.coreengine.executor.process.wps.WPSProcess;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.wacodis.coreengine.executor.process.Process;
import de.wacodis.coreengine.executor.process.ProcessContextBuilder;
import de.wacodis.coreengine.executor.process.wps.WPSProcessContextBuilder;
import de.wacodis.coreengine.executor.messaging.ToolMessagePublisherChannel;
import de.wacodis.coreengine.executor.process.AsynchronousWacodisJobExecutor;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.IntervalAsynchronousWacodisJobExecutor;
import de.wacodis.coreengine.executor.process.JobProcess;
import de.wacodis.coreengine.executor.process.JobProcessBuilder;
import de.wacodis.coreengine.executor.process.SequentialWacodisJobExecutor;
import de.wacodis.coreengine.executor.process.SingleJobProcessBuilder;
import de.wacodis.coreengine.executor.process.SplitByCopernicusSubsetJobProcessBuilder;
import de.wacodis.coreengine.executor.process.WacodisJobExecutor;
import de.wacodis.coreengine.executor.process.events.JobExecutionEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessEventHandler;
import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEventHandler;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

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
    private ProcessContextBuilder wpsContextBuilder;
    private List<ExpectedProcessOutput> expectedProcessOutputs;

    @Autowired
    ToolMessagePublisherChannel processMessagePublisher;

    @Autowired
    private ApplicationEventPublisher jobExecutionFailedPublisher;

    @Autowired
    private WebProcessingServiceConfiguration wpsConfig;

    public WacodisJobExecutionStarter() {
        this.wpsClient = WPSClientSession.getInstance();
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

    @PostConstruct
    private void initContextBuilder() {
        this.wpsContextBuilder = new WPSProcessContextBuilder(this.wpsConfig);
    }

    public void executeWacodisJob(WacodisJobWrapper job) {
        String toolProcessID = job.getJobDefinition().getProcessingTool();
        String wacodisJobID = job.getJobDefinition().getId().toString();

        LOGGER.info("execute Wacodis Job " + wacodisJobID + " using processing tool " + toolProcessID);

        JobProcessBuilder subProcessCreator;

        if (this.wpsConfig.isCallWPSPerInput()) { //call wps proess for each copernicus input
            //create separate input for each copernicus input
            subProcessCreator = new SplitByCopernicusSubsetJobProcessBuilder(this.wpsContextBuilder, this.expectedProcessOutputs);
        } else { //call wps process only once with all inputs
            subProcessCreator = new SingleJobProcessBuilder(this.wpsContextBuilder, this.expectedProcessOutputs);
        }

        setDefaultRetrySettingsIfAbsent(job.getJobDefinition()); //use defaults if retry settings are not set in job definition

        //execute all sub processes
        executeWPSProcesses(job, subProcessCreator);
    }

    private void executeWPSProcesses(WacodisJobWrapper job, JobProcessBuilder subProcessCreator) {
        String toolProcessID = job.getJobDefinition().getProcessingTool();

        Process toolProcess = new WPSProcess(this.wpsClient, this.wpsConfig.getUri(), this.wpsConfig.getVersion(), toolProcessID, this.wpsConfig.isValidateInputs());
        List<JobProcess> subProcesses;
        try {
            subProcesses = subProcessCreator.getJobProcessesForWacodisJob(job, toolProcess);
        } catch (JobProcessCreationException ex) {
            LOGGER.error("could not create sub processes for wacodis job " + job.getJobDefinition().getId().toString(), ex);
            LOGGER.warn("try to process wacodis job {} as single sub process, processing tool is not called per input", job.getJobDefinition().getId().toString());
            subProcessCreator = new SingleJobProcessBuilder(this.wpsContextBuilder, this.expectedProcessOutputs);
            try {
                subProcesses = subProcessCreator.getJobProcessesForWacodisJob(job, toolProcess);
            } catch (JobProcessCreationException ex2) {
                //publish failure message if unable to build subprocesses and throw exception
                IllegalArgumentException iae = new IllegalArgumentException("unable to execute wacodis job " + job.getJobDefinition().getId().toString() + ", could not create sub process, fallback to single sub process also failed ", ex2);
                ToolMessagePublisher.publishMessageSync(this.processMessagePublisher.toolFailure(), buildUnableToProcessMessage(job.getJobDefinition(), iae.getMessage(), DateTime.now()));
            
                throw iae;
            }
        }
        WacodisJobExecutor jobExecutor = createWacodisJobExecutor(job.getJobDefinition());

        //declare handler for execution events
        WacodisJobExecutionEventHandler jobExecutionHandler = new JobExecutionEventHandler(job, this.processMessagePublisher, this.jobExecutionFailedPublisher);
        JobProcessEventHandler jobProcessHandler = new JobProcessEventHandler(this.processMessagePublisher, this.jobExecutionFailedPublisher);

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

    private void setDefaultRetrySettingsIfAbsent(WacodisJobDefinition jobDef) {
        if (jobDef.getRetrySettings() == null) {
            WacodisJobDefinitionRetrySettings defaultRetrySettings = new WacodisJobDefinitionRetrySettings();
            defaultRetrySettings.setMaxRetries(DEFAULT_MAXRETRIES);
            defaultRetrySettings.setRetryDelayMillies(DEFAULT_RETRIEDELAY_MILLIES);

            jobDef.setRetrySettings(defaultRetrySettings);
        }
    }

    private WacodisJobExecutor createWacodisJobExecutor(WacodisJobDefinition jobDefinition) {
        WacodisJobExecutor executor;

        if (this.wpsConfig.isProcessInputsSequentially()) {
            executor = new SequentialWacodisJobExecutor();
        } else if (this.wpsConfig.isProcessInputsDelayed()) {
            long delay = (this.wpsConfig.getDelay_Milliseconds() >= 0) ? this.wpsConfig.getDelay_Milliseconds() : 0;
            long initDelay = (this.wpsConfig.getInitialDelay_Milliseconds() >= 0) ? this.wpsConfig.getInitialDelay_Milliseconds() : 0;
            executor = new IntervalAsynchronousWacodisJobExecutor(initDelay, delay);
        } else {
            ExecutorService execService;

            if (this.wpsConfig.getMaxParallelWPSProcessPerJob() <= 0) {
                execService = Executors.newCachedThreadPool();
            } else {
                execService = Executors.newFixedThreadPool(this.wpsConfig.getMaxParallelWPSProcessPerJob());
            }

            executor = new AsynchronousWacodisJobExecutor(execService);
        }

        LOGGER.debug("created executor of type {} for wacodis job {}", executor.getClass().getSimpleName(), jobDefinition.getId());

        return executor;
    }
    
        private Message<WacodisJobFailed> buildUnableToProcessMessage(WacodisJobDefinition jobDef,  String reason, DateTime timestamp) {
        WacodisJobFailed msg = new WacodisJobFailed();
        msg.setCreated(timestamp);
        msg.setReason(reason);
        msg.setWacodisJobIdentifier(jobDef.getId());
        return MessageBuilder.withPayload(msg).build();
    }

}
