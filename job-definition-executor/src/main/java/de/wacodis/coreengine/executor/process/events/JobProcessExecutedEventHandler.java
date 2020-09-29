/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.events;

/**
 *
 * @author Arne
 */
public interface JobProcessExecutedEventHandler {
    void onJobProcessFinished(JobProcessExecutedEvent e, boolean isFinalJobProcess);
}
