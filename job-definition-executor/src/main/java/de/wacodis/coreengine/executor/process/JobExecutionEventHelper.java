/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process;

import de.wacodis.coreengine.executor.process.events.JobProcessExecutedEvent;
import de.wacodis.coreengine.executor.process.events.JobProcessExecutedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessFailedEvent;
import de.wacodis.coreengine.executor.process.events.JobProcessFailedEventHandler;
import de.wacodis.coreengine.executor.process.events.JobProcessStartedEvent;
import de.wacodis.coreengine.executor.process.events.JobProcessStartedEventHandler;
import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEvent;
import de.wacodis.coreengine.executor.process.events.WacodisJobExecutionEventHandler;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne
 */
public class JobExecutionEventHelper {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AsynchronousWacodisJobExecutor.class);

    public static void fireProcessStartedEvent(JobProcessStartedEvent e, JobProcessStartedEventHandler processStartedHandler) {
        String subProcessId = e.getJobProcess().getJobProcessIdentifier();
        String wacodisJobId = e.getJobProcess().getJobDefinition().getId().toString();

        if (processStartedHandler != null) {
            LOGGER.debug("fire event of type {} for job process {} of wacodis job {}", e.getClass().getSimpleName(), subProcessId, wacodisJobId);
            processStartedHandler.onJobProcessStarted(e);
        } else {
            LOGGER.warn("cannot fire event of type {}, event handler is null", e.getClass().getSimpleName());
        }
    }

    public static void fireProcessExecutedEvent(JobProcessExecutedEvent e, JobProcessExecutedEventHandler processExecutedHandler, boolean isFinalJobProcess) {
        String subProcessId = e.getJobProcess().getJobProcessIdentifier();
        String wacodisJobId = e.getJobProcess().getJobDefinition().getId().toString();

        if (processExecutedHandler != null) {
            LOGGER.debug("fire event of type {} for job process {} of wacodis job {}", e.getClass().getSimpleName(), subProcessId, wacodisJobId);
            processExecutedHandler.onJobProcessFinished(e, isFinalJobProcess);
        } else {
            LOGGER.warn("cannot fire event of type {}, event handler is null", e.getClass().getSimpleName());
        }
    }

    public static void fireProcessFailedEvent(JobProcessFailedEvent e, JobProcessFailedEventHandler processFailedHandler, boolean isFinalJobProcess) {
        String subProcessId = e.getJobProcess().getJobProcessIdentifier();
        String wacodisJobId = e.getJobProcess().getJobDefinition().getId().toString();

        if (processFailedHandler != null) {
            LOGGER.debug("fire event of type {} for job process {} of wacodis job {}", e.getClass().getSimpleName(), subProcessId, wacodisJobId);
            processFailedHandler.onJobProcessFailed(e, isFinalJobProcess);
        } else {
            LOGGER.warn("cannot fire event of type {}, event handler is null", e.getClass().getSimpleName());
        }
    }

    public static void fireWacodisJobExecutionEvent(WacodisJobExecutionEvent e, WacodisJobExecutionEventHandler eventHandler) {
        String wacodisJobId = e.getCurrentJobProcess().getJobDefinition().getId().toString();

        if (eventHandler != null) {
            LOGGER.debug("fire event {} of type {} for of wacodis job {}", e.getClass().getSimpleName(), wacodisJobId);

            switch (e.getEventType()) {
                case FINALPROCESSFINISHED:
                    eventHandler.onFinalJobProcessFinished(e);
                    break;
                case FIRSTPROCESSSTARTED:
                    eventHandler.onFirstJobProcessStarted(e);
                    break;
                default:
                    LOGGER.error("cannot fire event {} of type {}, event type {} is unknown", e.getEventType(), e.getClass().getSimpleName(), e.getEventType());
                    break;
            }

        } else {
            LOGGER.warn("cannot fire event {} of type {}, event handler is null", e.getEventType(), e.getClass().getSimpleName());
        }
    }



}
