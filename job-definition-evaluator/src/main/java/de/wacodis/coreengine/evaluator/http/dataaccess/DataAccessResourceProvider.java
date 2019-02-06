/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.http.dataaccess;

import java.util.List;
import java.util.Map;
import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.DataAccessResourceSearchBody;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 * @param <R> response
 * @param <Q> query
 */
public interface DataAccessResourceProvider<R, Q> {

    Map<String, List<AbstractResource>> searchResources(DataAccessResourceSearchBody query) throws java.io.IOException;

}
