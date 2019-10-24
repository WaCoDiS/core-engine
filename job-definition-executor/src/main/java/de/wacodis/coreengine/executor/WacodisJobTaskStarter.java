/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor;

import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.configuration.WebProcessingServiceConfiguration;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class WacodisJobTaskStarter {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(WacodisJobTaskStarter.class);

    private static final ExpectedProcessOutput PRODUCTOUTPUT = new ExpectedProcessOutput("PRODUCT", "image/geotiff");
    private static final ExpectedProcessOutput METADATAOUTPUT = new ExpectedProcessOutput("METADATA", "text/json");

    private static final ExpectedProcessOutput LITERALOUTPUT = new ExpectedProcessOutput("LiteralOutputData", "text/xml");

    private final WPSClientSession wpsClient;
    private final ExecutorService wacodisJobExecutionService;
    private final ProcessContextBuilder contextBuilder;
    private List<ExpectedProcessOutput> expectedProcessOutputs;

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

    public List<ExpectedProcessOutput> getExpectedProcessOutputs() {
        return expectedProcessOutputs;
    }

    public void setExpectedProcessOutputs(List<ExpectedProcessOutput> expectedProcessOutputs) {
        this.expectedProcessOutputs = expectedProcessOutputs;
    }

    public void executeWacodisJob(WacodisJobWrapper job) {
        String toolProcessID = job.getJobDefinition().getProcessingTool();
        String wacodisJobID = job.getJobDefinition().getId().toString();
        ProcessContext toolContext = this.contextBuilder.buildProcessContext(job, getExpectedProcessOutputsOrDefaults()); //expect default outputs (as long as jobdefinition provides no output information)

        Process toolProcess = new WPSProcess(this.wpsClient, this.wpsConfig.getUri(), this.wpsConfig.getVersion(), toolProcessID);

        LOGGER.info("execute Wacodis Job " + wacodisJobID + " using processing tool " + toolProcessID);
        LOGGER.debug("start thread for process " + wacodisJobID);
        
        //submit job async
        CompletableFuture.supplyAsync(() -> { 
            try {
                LOGGER.info("start new thread for Wacodis Job " + wacodisJobID + ", toolProcess: " + toolProcess);
                
                new WacodisJobExecutor(toolProcess, toolContext, job.getJobDefinition(), this.newProductPublisher).execute();
                return null; //satisfy return type
            } catch (Exception e) { //catch all exception from async job, rethrow as CompetionException and handle in exceptionally()
                throw new CompletionException(e);
            }
        }, wacodisJobExecutionService).exceptionally((Throwable t) -> { //handle exceptions that were raised by async job
            LOGGER.error("execution of wacodis job failed, WacodisJobID: " + wacodisJobID + " toolID: " + toolProcessID, t.getCause());
            return null; //satisfy return type
        });
    }

    /**
     * @return default values (PRODUCTOUTPUT, METADATAOUTPUT) if
     * this.expecteProcessOutputs not set or empty, otherwise
     * this.expectedProcessOutput (as array)
     */
    private ExpectedProcessOutput[] getExpectedProcessOutputsOrDefaults() {
        if (this.expectedProcessOutputs != null && !this.expectedProcessOutputs.isEmpty()) {
            return this.expectedProcessOutputs.toArray(new ExpectedProcessOutput[this.expectedProcessOutputs.size()]);
        } else {
            return new ExpectedProcessOutput[]{/*PRODUCTOUTPUT, METADATAOUTPUT*/LITERALOUTPUT};
        }
    }

    @PreDestroy
    private void shutdownJobExecutionService() {
        this.wacodisJobExecutionService.shutdown();
        LOGGER.info("shutdown wacodis job executor service");
    }
}
