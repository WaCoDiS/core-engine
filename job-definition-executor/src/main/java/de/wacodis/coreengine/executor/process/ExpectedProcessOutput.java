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
    
    private String identifier;
    private String mimeType;
    private boolean publishedOutput = true;  //true by default

    /**
     * no args constructor (needed for configuration)
     */
    public ExpectedProcessOutput() {
    }
    
    /**
     * isPublishOutput is true by default
     * @param identifier
     * @param mimeType 
     */
    public ExpectedProcessOutput(String identifier, String mimeType) {
        this.identifier = identifier;
        this.mimeType = (mimeType != null) ? mimeType : "";
    }

    /**
     * @param identifier
     * @param mimeType
     * @param publishedOutput 
     */
    public ExpectedProcessOutput(String identifier, String mimeType, boolean publishedOutput) {
        this(identifier, mimeType);
        this.publishedOutput = publishedOutput;
    }
    
    public boolean isPublishedOutput() {
        return publishedOutput;
    }

    public void setPublishedOutput(boolean publishedOutput) {
        this.publishedOutput = publishedOutput;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return "ExpectedProcessOutput{" + "identifier=" + identifier + ", mimeType=" + mimeType + ", publishedOutput=" + publishedOutput + '}';
    }
}
