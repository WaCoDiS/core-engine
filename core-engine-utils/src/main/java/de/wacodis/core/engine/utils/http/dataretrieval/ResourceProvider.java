/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.core.engine.utils.http.dataretrieval;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 * @param <R> response
 * @param <Q> query
 */
public interface ResourceProvider<R,Q> {
    
    R searchResources(Q query) throws java.io.IOException;
    
}
