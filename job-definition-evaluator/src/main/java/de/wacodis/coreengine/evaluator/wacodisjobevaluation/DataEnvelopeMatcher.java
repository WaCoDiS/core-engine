/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractSubsetDefinition;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public interface DataEnvelopeMatcher {
    /**
     * matches DataEnvelope and SubsetDefinition regarding content,
     * return true if inputs are considered to be consistent
     * @param dataEnvelope
     * @param subsetDefinition
     * @return 
     */
    boolean match(AbstractDataEnvelope dataEnvelope, AbstractSubsetDefinition subsetDefinition);
}
