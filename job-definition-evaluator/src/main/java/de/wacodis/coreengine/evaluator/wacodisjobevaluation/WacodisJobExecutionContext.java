/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import java.util.UUID;
import org.joda.time.DateTime;

/**
 *
 * @author Arne
 */
public class WacodisJobExecutionContext {

    private final UUID executionID;
    private final int retryCount;
    private final DateTime executionTime;

    public WacodisJobExecutionContext(UUID executionID, DateTime executionTime, int retryCount) {
        this.executionID = executionID;
        this.retryCount = retryCount;
        this.executionTime = executionTime;
    }

    public UUID getExecutionID() {
        return executionID;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public DateTime getExecutionTime() {
        return executionTime;
    }

    public WacodisJobExecutionContext createCopyWithIncrementedRetryCount() {
        int incrementedRetryCount = this.retryCount + 1;
        return new WacodisJobExecutionContext(this.executionID, this.executionTime, incrementedRetryCount);
    }
}
