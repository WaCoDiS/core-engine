/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.dummy;

import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessOutputDescription;
import org.slf4j.LoggerFactory;

/**
 * dummy process without functionality
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class EmptyDummyProcess implements de.wacodis.coreengine.executor.process.Process {
    
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EmptyDummyProcess.class);

    /**
     * @param context ignored, can be null
     * @throws ExecutionException  never thrown
     */
    @Override
    public ProcessOutputDescription execute(ProcessContext context) throws ExecutionException {
        LOGGER.info("executing dummy process");
        
        return new ProcessOutputDescription("emptyDummyProcess_" + System.currentTimeMillis());
    }
   
}
