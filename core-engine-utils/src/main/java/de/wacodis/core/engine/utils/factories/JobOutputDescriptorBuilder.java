/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
