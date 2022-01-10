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
package de.wacodis.coreengine.scheduling.listener;

import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobStatus;
import de.wacodis.core.models.WacodisJobStatusUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

/**
 * Listener for job definition messages
 *
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 */
@EnableBinding(JobDefinitionListenerChannel.class)
public class JobDefinitionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobDefinitionListener.class);

    @Autowired
    private JobMessageHandler messageHandler;

    @StreamListener(JobDefinitionListenerChannel.JOB_CREATION_INPUT)
    public void jobDefinitionCreated(WacodisJobDefinition jobDefinition) {
        LOGGER.info("Received new job: {}", jobDefinition.toString());
        messageHandler.handleNewJob(jobDefinition);
    }

    @StreamListener(target = JobDefinitionListenerChannel.JOB_DELETION_INPUT)
    public void jobDefinitionDeleted(WacodisJobDefinition jobDefinition) {
        LOGGER.info("Received deletion for job: {}", jobDefinition.getId().toString());
        messageHandler.handleJobDeletion(jobDefinition);
    }

}
