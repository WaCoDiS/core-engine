/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps.exception;

/**
 *
 * @author Arne
 */
public class WPSException extends Exception {

    public WPSException() {
    }

    public WPSException(String message) {
        super(message);
    }

    public WPSException(String message, Throwable cause) {
        super(message, cause);
    }

    public WPSException(Throwable cause) {
        super(cause);
    }

    public WPSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
