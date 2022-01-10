/*
 * Copyright 2018-2022 52°North Spatial Information Research GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.wacodis.coreengine.evaluator.messaging.listener;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.coreengine.evaluator.WacodisJobInputTrackerProvider;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobInputTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

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
        LOGGER.info("Received DataEnvelope " + dataEnvelope.getIdentifier());

        WacodisJobInputTracker inputTracker = this.inputTrackerProvider.getInputTracker();
        inputTracker.publishDataEnvelope(dataEnvelope);
    }

    public WacodisJobInputTrackerProvider getInputTrackerProvider() {
        return inputTrackerProvider;
    }
}
