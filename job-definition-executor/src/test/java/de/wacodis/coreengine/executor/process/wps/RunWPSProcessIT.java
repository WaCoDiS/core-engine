/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.coreengine.executor.process.ProcessContext;
import org.n52.geoprocessing.wps.client.WPSClientSession;
import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.GetResource;
import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.core.models.extension.staticresource.StaticDummyResource;
import de.wacodis.coreengine.executor.exception.ExecutionException;
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
        refData.setUrl("https://wacodis.demo.52north.org/geoserver/traindata/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=traindata:wacodis_traindata&maxFeatures=50&outputFormat=gml3");

        ProcessContext inputContext = new ProcessContext();
        inputContext.addInputResource("OPTICAL_IMAGES_TYPE", new ResourceDescription(opticalImgType, "text/xml"));
        inputContext.addInputResource("OPTICAL_IMAGES_SOURCES", new ResourceDescription(opticalImgSrc, "text/xml"));
        //inputContext.addInputResource("OPTICAL_IMAGES_SOURCES", new ResourceDescription(opticalImgSrc2, "text/xml"));
        inputContext.addInputResource("REFERENCE_DATA_TYPE", new ResourceDescription(refDataType, "text/xml"));
        inputContext.addInputResource("REFERENCE_DATA", new ResourceDescription(refData, "text/xml"));

        
        inputContext.addExpectedOutput(new JobOutputDescriptor().identifier("PRODUCT").mimeType("image/geotiff"));
        inputContext.addExpectedOutput(new JobOutputDescriptor().identifier("METADATA").mimeType("text/json"));
        
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
