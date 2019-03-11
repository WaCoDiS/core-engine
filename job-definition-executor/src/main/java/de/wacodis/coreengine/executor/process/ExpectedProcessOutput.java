/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

/**
 * describes an output expected to be produced by a process
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ExpectedProcessOutput {
    
    private final String identifier;
    private final String mimeType;

    public ExpectedProcessOutput(String identifier, String mimeType) {
        this.identifier = identifier;
        this.mimeType = mimeType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getMimeType() {
        return mimeType;
    }
}
