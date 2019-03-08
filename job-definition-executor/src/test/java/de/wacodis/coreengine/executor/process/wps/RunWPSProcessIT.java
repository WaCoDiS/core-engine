/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.coreengine.executor.process.ProcessContext;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.GetResource;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.process.ProcessOutput;
import org.junit.jupiter.api.Disabled;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class RunWPSProcessIT {

    public RunWPSProcessIT() {
    }

    @Test
    @Disabled
    public void runWPSProcess() throws ExecutionException {
        String wpsURL = "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService";
        String wpsProcessID = "org.n52.wps.server.algorithm.test.LongRunningDummyTestClass";

        WPSClientSession wpsClient = WPSClientSession.getInstance();

        de.wacodis.coreengine.executor.process.Process wpsProcess = new WPSProcess(wpsClient, wpsURL, "2.0.0", wpsProcessID);
        ProcessContext inputContext = buildInputContext();
        ProcessOutput processOutput = wpsProcess.execute(inputContext);
        
        System.out.println("Outputs:" + System.lineSeparator() + processOutput.getOutputResources().toString());
    }
    
    
    
    private ProcessContext buildInputContext(){
        AbstractResource literalInput = new GetResource();
        literalInput.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        literalInput.setUrl("http://www.example.com");
        
        ProcessContext inputContext = new ProcessContext();
        inputContext.addInputResource("LiteralInputData", literalInput);
        inputContext.addExpectedOutput("LiteralOutputData");

        inputContext.setProcessID("dummyProcess");
        
        return inputContext;
    }

}
