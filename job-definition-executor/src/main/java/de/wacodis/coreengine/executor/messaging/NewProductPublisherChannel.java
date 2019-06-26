/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public interface NewProductPublisherChannel {

    String NEW_PRODUCT = "newProduct";
    String TOOL_EXECUTION = "toolExecution";

    @Output(NEW_PRODUCT)
    MessageChannel newProduct();
    
    @Output(TOOL_EXECUTION)
    MessageChannel toolExecution();
    
}
