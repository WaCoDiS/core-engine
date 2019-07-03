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
public interface ToolMessagePublisherChannel {

    String TOOL_FINISHED = "toolFinished";
    String TOOL_EXECUTION = "toolExecution";
    String TOOL_FAILURE = "toolFailure";

    @Output(TOOL_FINISHED)
    MessageChannel toolFinished();
    
    @Output(TOOL_EXECUTION)
    MessageChannel toolExecution();
    
    @Output(TOOL_FAILURE)
    MessageChannel toolFailure();
    
}
