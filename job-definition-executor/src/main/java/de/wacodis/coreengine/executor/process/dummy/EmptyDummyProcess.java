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
package de.wacodis.coreengine.executor.process.dummy;

import de.wacodis.coreengine.executor.exception.ExecutionException;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessOutputDescription;
import org.slf4j.LoggerFactory;

/**
 * dummy process without functionality
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class EmptyDummyProcess implements de.wacodis.coreengine.executor.process.Process {
    
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EmptyDummyProcess.class);

    /**
     * @param context ignored, can be null
     * @throws ExecutionException  never thrown
     */
    @Override
    public ProcessOutputDescription execute(ProcessContext context) throws ExecutionException {
        LOGGER.info("executing dummy process");
        
        return new ProcessOutputDescription("emptyDummyProcess_" + System.currentTimeMillis());
    }
   
}
