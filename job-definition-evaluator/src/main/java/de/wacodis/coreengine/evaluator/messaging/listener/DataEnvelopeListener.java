package de.wacodis.coreengine.evaluator.messaging.listener;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.coreengine.evaluator.WacodisJobInputTrackerProvider;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobInputTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@EnableBinding(DataEnvelopeListenerChannel.class)
public class DataEnvelopeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataEnvelopeListener.class);

    @Autowired
    private WacodisJobInputTrackerProvider inputTrackerProvider;
    

    @StreamListener(DataEnvelopeListenerChannel.DATAENVELOPE_INPUT)
    public void evaluateDateEnvelope(AbstractDataEnvelope dataEnvelope) {
        LOGGER.info("Received :" + System.lineSeparator() + dataEnvelope.toString());

        WacodisJobInputTracker inputTracker = this.inputTrackerProvider.getInputTracker();
        inputTracker.publishDataEnvelope(dataEnvelope);
    }

    public WacodisJobInputTrackerProvider getInputTrackerProvider() {
        return inputTrackerProvider;
    }
}
