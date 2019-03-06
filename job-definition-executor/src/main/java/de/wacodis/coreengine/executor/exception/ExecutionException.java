/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.exception;

/** 
 * use to signal a failure during the execution of a process (e.g. Wacodis Job)
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class ExecutionException extends Exception {

    /**
     * Creates a new instance of <code>ExecutionException</code> without detail
     * message.
     */
    public ExecutionException() {
    }

    /**
     * Constructs an instance of <code>ExecutionException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ExecutionException(String msg) {
        super(msg);
    }
    
    public ExecutionException(String msg, Throwable cause){
        super(msg, cause);
    }
 
    
        public ExecutionException(Throwable cause){
        super(cause);
    }
}
