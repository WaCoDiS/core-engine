package de.wacodis.coreengine.evaluator.listener;

import de.wacodis.core.models.AbstractDataEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @StreamListener(DataEnvelopeListenerChannel.DATAENVELOPE_INPUT)
    public void evaluateDateEnvelope(AbstractDataEnvelope dataEnvelope) {
        //ToDo
        if (dataEnvelope.getSourceType().equals(AbstractDataEnvelope.SourceTypeEnum.GDIDEDATAENVELOPE)) {
            LOGGER.info("Received :" + dataEnvelope.toString());
        }
    }

}
