/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.messaging.listener;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;


/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public interface DataEnvelopeListenerChannel {
        //reference to config f binding
    	String DATAENVELOPE_INPUT = "newDataEnvelope";

	@Input(DATAENVELOPE_INPUT)
	SubscribableChannel newDataEnvelope();
}
