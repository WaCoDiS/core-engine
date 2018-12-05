/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.http;

import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 * @param <R> response type
 */
public interface HTTPRequest<R> {

    ResponseEntity<R> execute();

    static URL stringToURL(String urlStr) throws MalformedURLException {
        return new URL(urlStr);
    }
}
