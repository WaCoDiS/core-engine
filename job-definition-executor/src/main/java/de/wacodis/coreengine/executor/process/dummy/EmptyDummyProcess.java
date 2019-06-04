/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.dummy;

import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessOutputDescription;

/**
 * dummy process without functionality
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class EmptyDummyProcess implements de.wacodis.coreengine.executor.process.Process {

    /**
     * @param context ignored, can be null
     * @return ProcessOutputDescription with ProcessIdentifier "dummyProcess_timestamp" and empty set OutputIdentifiers
     * @throws ExecutionException  never thrown
     */
    @Override
    public ProcessOutputDescription execute(ProcessContext context) throws ExecutionException {
        ProcessOutputDescription outputDescription = new ProcessOutputDescription();
        outputDescription.setProcessIdentifier("dummyProcess_" + System.currentTimeMillis());
        
        return outputDescription;
    }

    /**
     * @param context ignored, can be null
     * @return always returns true 
     */
    @Override
    public boolean validateContext(ProcessContext context) {
        return true;
    }
    
}
