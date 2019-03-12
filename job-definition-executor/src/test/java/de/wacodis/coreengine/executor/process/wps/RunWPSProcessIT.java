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
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.ProcessOutput;
import de.wacodis.coreengine.executor.process.ResourceDescription;
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
        String wpsURL = "http://localhost:8080/wps/service";
        String wpsProcessID = "de.hsbo.wacodis.land_cover_classification";

        WPSClientSession wpsClient = WPSClientSession.getInstance();

        de.wacodis.coreengine.executor.process.Process wpsProcess = new WPSProcess(wpsClient, wpsURL, "2.0.0", wpsProcessID);
        ProcessContext inputContext = buildInputContext();
        ProcessOutput processOutput = wpsProcess.execute(inputContext);

        System.out.println("Outputs:" + System.lineSeparator() + processOutput.getOutputResources().toString());
    }

    private ProcessContext buildInputContext() {
        AbstractResource opticalImgType = new GetResource();
        opticalImgType.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        opticalImgType.setUrl("Aerial_Image");

        AbstractResource opticalImgSrc = new GetResource();
        opticalImgSrc.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        opticalImgSrc.setUrl("http://www.example.com");

        AbstractResource refDataType = new GetResource();
        refDataType.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        refDataType.setUrl("MANUAL");

        AbstractResource refData = new GetResource();
        refData.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        refData.setUrl("http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_roads&SRS=EPSG:4326&OUTPUTFORMAT=GML3");

        ProcessContext inputContext = new ProcessContext();
        inputContext.addInputResource("OPTICAL_IMAGES_TYPE", new ResourceDescription(opticalImgType, "text/xml"));
        inputContext.addInputResource("OPTICAL_IMAGES_SOURCES", new ResourceDescription(opticalImgSrc, "text/xml"));
        inputContext.addInputResource("REFERENCE_DATA_TYPE", new ResourceDescription(refDataType, "text/xml"));
        inputContext.addInputResource("REFERENCE_DATA", new ResourceDescription(refData, "text/xml"));

        inputContext.addExpectedOutput(new ExpectedProcessOutput("PRODUCT", "text/xml"));

        inputContext.setProcessID("dummyLandCoverClassificationProcess");

        return inputContext;
    }

}
