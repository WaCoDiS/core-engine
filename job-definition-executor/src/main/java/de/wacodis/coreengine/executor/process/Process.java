/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.coreengine.executor.exception.ExecutionException;



/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public interface Process {
    
    /**
     * execute Process
     * @param context
     * @return 
     */
    ProcessContext execute(ProcessContext context) throws ExecutionException;
    
    /**
     * check if context object is applicable for the specific implementation of Process
     * @param context
     * @return 
     */
    boolean validateContext(ProcessContext context);
    
}
