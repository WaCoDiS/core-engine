/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator;

import de.wacodis.coreengine.evaluator.configuration.DataEnvelopeMatchingConfiguration;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.SourceTypeDataEnvelopeMatcher;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.JobEvaluatorService;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobInputTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Arne
 */
@Service
public class WacodisJobInputTrackerProvider {

    private WacodisJobInputTracker inputTracker;

    @Autowired
    JobEvaluatorService jobEvaluator;

    @Autowired
    DataEnvelopeMatchingConfiguration matchingConfig;

    public WacodisJobInputTracker getInputTracker() {
        if (this.inputTracker == null) {
            this.inputTracker = new WacodisJobInputTracker(jobEvaluator, new SourceTypeDataEnvelopeMatcher());
        }

        return inputTracker;
    }

}
