/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.AbstractResource;

/**
 * single process resource
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ResourceDescription {
    
    private final AbstractResource resource;
    private final String mimeType;

    public ResourceDescription(AbstractResource resource, String mimeType) {
        this.resource = resource;
        this.mimeType = (mimeType != null) ? mimeType : "";
    }

    public AbstractResource getResource() {
        return resource;
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String toString() {
        return "ResourceDescription{" + "resource=" + resource + ", mimeType=" + mimeType + '}';
    }
    
    
    
}
