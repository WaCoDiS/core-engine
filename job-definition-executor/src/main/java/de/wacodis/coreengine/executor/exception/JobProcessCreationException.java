/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.exception;

/**
 *
 * @author Arne
 */
public class JobProcessCreationException extends Exception{

    public JobProcessCreationException() {
    }

    public JobProcessCreationException(String message) {
        super(message);
    }

    public JobProcessCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobProcessCreationException(Throwable cause) {
        super(cause);
    }

    public JobProcessCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }   
}
