/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor;

import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.configuration.WacodisJobExecutorConfiguration;
import de.wacodis.coreengine.executor.configuration.WebProcessingServiceConfiguration;
import de.wacodis.coreengine.executor.messaging.NewProductPublisherChannel;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.wps.WPSProcess;
import de.wacodis.coreengine.executor.process.WacodisJobExecutionOutput;
import de.wacodis.coreengine.executor.process.WacodisJobExecutionTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.annotation.PreDestroy;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import de.wacodis.coreengine.executor.process.Process;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessContextBuilder;
import de.wacodis.coreengine.executor.process.wps.WPSProcessContextBuilder;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class WacodisJobTaskStarter {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobTaskStarter.class);
    
    private static final String DEFAULT_OUTPUT = "PRODUCT"
    private static final String DEFAULT_MIMETYPE = "text/xml";

    private final WPSClientSession wpsClient;
    private final ExecutorService wacodisJobExecutionService;
    private final ProcessContextBuilder contextBuilder;

    @Autowired
    NewProductPublisherChannel newProductPublisher;
    
    @Autowired
    private WacodisJobExecutorConfiguration executorConfig;

    @Autowired
    private WebProcessingServiceConfiguration wpsConfig;

    public WacodisJobTaskStarter() {
        this.wpsClient = WPSClientSession.getInstance();
        this.wacodisJobExecutionService = Executors.newCachedThreadPool();
        this.contextBuilder = new WPSProcessContextBuilder();
    }

    public void setExecutorConfig(WacodisJobExecutorConfiguration executorConfig) {
        this.executorConfig = executorConfig;
    }

    public void setWpsConfig(WebProcessingServiceConfiguration wpsConfig) {
        this.wpsConfig = wpsConfig;
    }

    public WacodisJobExecutorConfiguration getExecutorConfig() {
        return executorConfig;
    }

    public WebProcessingServiceConfiguration getWpsConfig() {
        return wpsConfig;
    }

    public void executeWacodisJob(WacodisJobWrapper job) {
        String toolProcessID = job.getJobDefinition().getProcessingTool();
        String cleanUpProcessID = this.executorConfig.getCleanUpTool();
        ExpectedProcessOutput expectedOutput = getDefaultExpectedOutput();
        ProcessContext toolContext = this.contextBuilder.buildProcessContext(job, expectedOutput);

        Process toolProcess = new WPSProcess(this.wpsClient, this.wpsConfig.getUri(), this.wpsConfig.getVersion(), toolProcessID);
        Process cleanUpProcess = new WPSProcess(this.wpsClient, this.wpsConfig.getUri(), this.wpsConfig.getVersion(), cleanUpProcessID);

        LOGGER.info("execute Wacodis Job " + job.getJobDefinition().getId().toString() + " using processing tool " + toolProcessID + " and cleanUp tool " + cleanUpProcessID);
        LOGGER.debug("start thread for process " + job.getJobDefinition().getId().toString());
        Future<WacodisJobExecutionOutput> jobExecutionResult = this.wacodisJobExecutionService.submit(new WacodisJobExecutionTask(toolProcess, toolContext, cleanUpProcess, job.getJobDefinition(), this.newProductPublisher));
    }

    @PreDestroy
    private void shutdownJobExecutionService() {
        this.wacodisJobExecutionService.shutdown();
        LOGGER.debug("shutdown executor service");
    }

    private ExpectedProcessOutput getDefaultExpectedOutput() {
        return new ExpectedProcessOutput(DEFAULT_OUTPUT, DEFAULT_MIMETYPE);
    }

}
