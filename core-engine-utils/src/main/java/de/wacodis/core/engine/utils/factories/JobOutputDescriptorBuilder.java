/*
 * Copyright 2018-2021 52°North Spatial Information Research GmbH
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
package de.wacodis.core.engine.utils.factories;

import de.wacodis.core.models.JobOutputDescriptor;

/**
 *
 * @author Arne
 */
public class JobOutputDescriptorBuilder {

    private final static JobOutputDescriptor PRODUCTOUTPUT = buildDefaultProductOutput();
    private final static JobOutputDescriptor METADATAOUTPUT = buildDefaultMetadataOutput();

    public static JobOutputDescriptor getDefaultProductOutput() {
        return PRODUCTOUTPUT;
    }

    public static JobOutputDescriptor getDefaultMetadataOutput() {
        return METADATAOUTPUT;
    }
    
    /**
     * contains default metadata output and default product output
     * @return 
     */
    public static JobOutputDescriptor[] getDefaultOutputs(){
        return new JobOutputDescriptor[]{PRODUCTOUTPUT, METADATAOUTPUT};
    }
    
    private static JobOutputDescriptor buildDefaultProductOutput() {
        String productId = "PRODUCT";
        String productMimeType = "image/geotiff";
        boolean productIsPublishedOutput = true;
        boolean productAsReference = true;

        return new JobOutputDescriptor().identifier(productId).mimeType(productMimeType).publishedOutput(productIsPublishedOutput).asReference(productAsReference);
    }

    private static JobOutputDescriptor buildDefaultMetadataOutput() {
        String productId = "METADATA";
        String productMimeType = "text/json";
        boolean productIsPublishedOutput = false;
        boolean productAsReference = false;

        return new JobOutputDescriptor().identifier(productId).mimeType(productMimeType).publishedOutput(productIsPublishedOutput).asReference(productAsReference);
    }
}
