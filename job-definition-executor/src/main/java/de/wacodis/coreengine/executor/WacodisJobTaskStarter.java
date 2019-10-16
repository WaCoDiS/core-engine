/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor;

import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.configuration.WebProcessingServiceConfiguration;
import de.wacodis.coreengine.executor.process.wps.WPSProcess;
import de.wacodis.coreengine.executor.process.WacodisJobExecutionTask;
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

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class WacodisJobTaskStarter {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobTaskStarter.class);
    
    private static final ExpectedProcessOutput PRODUCTOUTPUT = new ExpectedProcessOutput("PRODUCT", "image/geotiff");
    private static final ExpectedProcessOutput METADATAOUTPUT =  new ExpectedProcessOutput("METADATA" , "text/json");
    
    private final WPSClientSession wpsClient;
    private final ExecutorService wacodisJobExecutionService;
    private final ProcessContextBuilder contextBuilder;

    @Autowired
    ToolMessagePublisherChannel newProductPublisher;
    
    @Autowired
    private WebProcessingServiceConfiguration wpsConfig;

    public WacodisJobTaskStarter() {
        this.wpsClient = WPSClientSession.getInstance();
        this.wacodisJobExecutionService = Executors.newCachedThreadPool();
        this.contextBuilder = new WPSProcessContextBuilder();
    }

    public void setWpsConfig(WebProcessingServiceConfiguration wpsConfig) {
        this.wpsConfig = wpsConfig;
    }

    public WebProcessingServiceConfiguration getWpsConfig() {
        return wpsConfig;
    }

    public void executeWacodisJob(WacodisJobWrapper job) {
        String toolProcessID = job.getJobDefinition().getProcessingTool();
        ProcessContext toolContext = this.contextBuilder.buildProcessContext(job, PRODUCTOUTPUT, METADATAOUTPUT); //expect default outputs (as long as jobdefinition provides no output information)

        Process toolProcess = new WPSProcess(this.wpsClient, this.wpsConfig.getUri(), this.wpsConfig.getVersion(), toolProcessID);

        LOGGER.info("execute Wacodis Job " + job.getJobDefinition().getId().toString() + " using processing tool " + toolProcessID);
        LOGGER.debug("start thread for process " + job.getJobDefinition().getId().toString());
        this.wacodisJobExecutionService.submit(new WacodisJobExecutionTask(toolProcess, toolContext, job.getJobDefinition(), this.newProductPublisher));
    }

    @PreDestroy
    private void shutdownJobExecutionService() {
        this.wacodisJobExecutionService.shutdown();
        LOGGER.info("shutdown wacodis job executor service");
    }

}
