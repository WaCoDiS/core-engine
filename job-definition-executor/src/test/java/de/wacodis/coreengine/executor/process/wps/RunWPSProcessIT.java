/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.coreengine.executor.process.ProcessContext;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.GetResource;
import de.wacodis.core.models.extension.staticresource.StaticDummyResource;
import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.ProcessOutputDescription;
import de.wacodis.coreengine.executor.process.ResourceDescription;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class RunWPSProcessIT {

    public RunWPSProcessIT() {
    }

    public void runWPSProcess() throws ExecutionException {
        String wpsURL = "http://localhost:8080/wacodis-wps/service";
        String wpsProcessID = "de.hsbo.wacodis.land_cover_classification";

        WPSClientSession wpsClient = WPSClientSession.getInstance();
        
        System.out.println("validate process context");
        de.wacodis.coreengine.executor.process.Process wpsProcess = new WPSProcess(wpsClient, wpsURL, "2.0.0", wpsProcessID);
        ProcessContext inputContext = buildInputContext();
        
        System.out.println("execute wps process " +  wpsProcessID);
        ProcessOutputDescription outputDescription = wpsProcess.execute(inputContext);
        
        System.out.println("Outputs:");
        for(String key: outputDescription.getAllOutputParameterKeys()){
            System.out.println("key: " + key + ",, value: " +outputDescription.getOutputParameter(key));
        }
    }

    private ProcessContext buildInputContext() {
        StaticDummyResource opticalImgType = new StaticDummyResource();
        opticalImgType.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        opticalImgType.setValue("Sentinel-2");

        AbstractResource opticalImgSrc = new GetResource();
        opticalImgSrc.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        opticalImgSrc.setUrl("https://scihub.copernicus.eu/dhus/odata/v1/Products('21eb3657-9a68-46cf-979a-9b731dfedf24')/$value");

        /*
        AbstractResource opticalImgSrc2 = new GetResource();
        opticalImgSrc2.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        opticalImgSrc2.setUrl("https://scihub.copernicus.eu/dhus/odata/v1/Products('72bdd44c-bc96-406f-95ee-e6e903a59452')/$value");
        */

        AbstractResource refDataType = new GetResource();
        refDataType.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        refDataType.setUrl("ATKIS");

        AbstractResource refData = new GetResource();
        refData.setMethod(AbstractResource.MethodEnum.GETRESOURCE);
        refData.setUrl("http://geoprocessing.demo.52north.org:8080/geoserver/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=topp:tasmania_roads&SRS=EPSG:32632&OUTPUTFORMAT=GML3");

        ProcessContext inputContext = new ProcessContext();
        inputContext.addInputResource("OPTICAL_IMAGES_TYPE", new ResourceDescription(opticalImgType, "text/xml"));
        inputContext.addInputResource("OPTICAL_IMAGES_SOURCES", new ResourceDescription(opticalImgSrc, "text/xml"));
        //inputContext.addInputResource("OPTICAL_IMAGES_SOURCES", new ResourceDescription(opticalImgSrc2, "text/xml"));
        inputContext.addInputResource("REFERENCE_DATA_TYPE", new ResourceDescription(refDataType, "text/xml"));
        inputContext.addInputResource("REFERENCE_DATA", new ResourceDescription(refData, "text/xml"));

        inputContext.addExpectedOutput(new ExpectedProcessOutput("PRODUCT", "image/geotiff"));
        inputContext.addExpectedOutput(new ExpectedProcessOutput("METADATA", "text/json"));

        inputContext.setWacodisProcessID("dummyLandCoverClassificationProcess");

        return inputContext;
    }

    public static void main(String[] args) {   
        RunWPSProcessIT wpsProcess = new RunWPSProcessIT();
        try {
            wpsProcess.runWPSProcess();
        } catch (ExecutionException ex) {
            System.err.println(ex);
        }
    }

}
