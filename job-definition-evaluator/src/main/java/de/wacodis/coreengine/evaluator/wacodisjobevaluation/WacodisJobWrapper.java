/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for a JobDefinition to keep track of available/missing inputs
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WacodisJobWrapper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WacodisJobWrapper.class);

    private final WacodisJobDefinition jobDefinition;
    private final DateTime executionTime;
    private final List<Pair<AbstractSubsetDefinition, Boolean>> inputs;

    public WacodisJobWrapper(WacodisJobDefinition job, DateTime executionTime) {
        this.jobDefinition = job;
        this.executionTime = executionTime;
        this.inputs = new ArrayList<>();

        initSubsets();
        
        LOGGER.debug(WacodisJobWrapper.class.getSimpleName() + " instance created for WacodisJobDefinition " + this.jobDefinition.getId());
    }

    /**
     * returns a unmodifiable list of all input pairs
     *
     * @return
     */
    public List<Pair<AbstractSubsetDefinition, Boolean>> getInputs() {
        return Collections.unmodifiableList(inputs);
    }

    public WacodisJobDefinition getJobDefinition() {
        return jobDefinition;
    }

    public DateTime getExecutionTime() {
        return executionTime;
    }

    /**
     * check if all inputs are marked as executable
     *
     * @return
     */
    public boolean isExecutable() {
        for (Pair subset : this.inputs) {
            if (subset.getValue().equals(Boolean.FALSE)) {
                return false;
            }
        }

        return true;
    }

    private void initSubsets() {
        for (AbstractSubsetDefinition subset : this.jobDefinition.getInputs()) {
            Pair<AbstractSubsetDefinition, Boolean> inputPair = new MutablePair<>(subset, Boolean.FALSE);
            this.inputs.add(inputPair);
        }
    }
}
