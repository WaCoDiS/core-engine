/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.messaging;

import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
@EnableBinding(ToolMessagePublisherChannel.class)
public class ToolMessagePublisher {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ToolMessagePublisher.class);

    /**
     * publish message with timeout
     *
     * @param publishChannel
     * @param msg
     * @param timeout_millis negative value for no timeout
     * @return true if message was published succesfully otherwise false
     */
    public static boolean publishMessageSync(MessageChannel publishChannel, Message msg, long timeout_millis) {
        try {
            boolean isSent = (timeout_millis >= 0) ? publishChannel.send(msg, timeout_millis) : publishChannel.send(msg);
            if (isSent) {
                LOGGER.info("published message on channel {}, message: {}", publishChannel.toString(), msg.getPayload().toString());
            } else {
                LOGGER.error("could not publish message on channel {}, exceeded timeout of {}, message: {}", publishChannel.toString(), timeout_millis, msg.getPayload().toString());
            }

            return isSent;
        } catch (Exception e) {
            LOGGER.error("could not publish message on channel " + publishChannel.toString() + ", exception of type " + e.getClass().getSimpleName() + " occurred, message: " + msg.getPayload().toString(), e);
            return false;
        }
    }

    /**
     * publish message without timeout
     *
     * @param publishChannel
     * @param msg
     * @return true if message was published succesfully otherwise false
     */
    public static boolean publishMessageSync(MessageChannel publishChannel, Message msg) {
        return publishMessageSync(publishChannel, msg, -1); //negative value = no timeout
    }

}
