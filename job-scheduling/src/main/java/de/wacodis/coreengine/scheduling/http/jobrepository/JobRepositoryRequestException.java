/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.scheduling.http.jobrepository;

import java.io.IOException;

/**
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
public class JobRepositoryRequestException extends IOException {

    public JobRepositoryRequestException() {
    }

    public JobRepositoryRequestException(String message) {
        super(message);
    }

    public JobRepositoryRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobRepositoryRequestException(Throwable cause) {
        super(cause);
    }

}
