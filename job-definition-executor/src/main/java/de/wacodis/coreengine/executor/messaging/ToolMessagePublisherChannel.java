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
