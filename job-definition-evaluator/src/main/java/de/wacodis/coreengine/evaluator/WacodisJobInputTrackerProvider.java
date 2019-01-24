/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator;

import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobInputTracker;
import org.springframework.stereotype.Component;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@Component
public class WacodisJobInputTrackerProvider {

    private WacodisJobInputTracker inputTracker;
    
    public WacodisJobInputTrackerProvider() {}

    public WacodisJobInputTrackerProvider(WacodisJobInputTracker inputTracker) {
        this.inputTracker = inputTracker;
    }

    public WacodisJobInputTracker getInputTracker() {
        return inputTracker;
    }

    public void setInputTracker(WacodisJobInputTracker inputTracker) {
        this.inputTracker = inputTracker;
    }
}
