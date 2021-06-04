/*
 * Copyright 2018-2021 52°North Spatial Information Research GmbH
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
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.coreengine.executor.process.ProcessContext;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.n52.geoprocessing.wps.client.model.Result;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.execution.Data;

/**
 * provide methods to validate received result of a wps process
 * @author Arne
 */
public class WPSResultValidator {
    
    
    private Process wpsProcessDescription;

    public WPSResultValidator(Process wpsProcessDescription) {
        this.wpsProcessDescription = wpsProcessDescription;
    }

    public Process getWpsProcessDescription() {
        return wpsProcessDescription;
    }

    public void setWpsProcessDescription(Process wpsProcessDescription) {
        this.wpsProcessDescription = wpsProcessDescription;
    }
    
        /**
     * check if wps output actually contains all expected output parameters
     *
     * @param wpsProcessResult
     * @param context
     * @return Optional.empty() if valid, Optiona.of(Throwable) if invalid
     */
    public Optional<Throwable> validateProcessOutput(Result wpsProcessResult, ProcessContext context) {
        Set<String> missingProcessOutputs = getMissingProcessOutputs(wpsProcessResult, context.getExpectedOutputs());

        if (missingProcessOutputs.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new IllegalArgumentException("result for wps process " + this.wpsProcessDescription.getId() + " (wps job id: " + wpsProcessResult.getJobId() + ") misses expected outputs: " + missingProcessOutputs.toString()));
        }
    }
    
        /**
     * get all identifiers of outputs included in a wps process result
     *
     * @param wpsProcessResult
     * @return
     */
    private Set<String> getMissingProcessOutputs(Result wpsProcessResult, List<JobOutputDescriptor> expectedOutputs) {
        List<Data> availableOutputs = wpsProcessResult.getOutputs();
        Set<String> availableOutputIdentifiers = availableOutputs.stream().map(availableOutput -> availableOutput.getId()).collect(Collectors.toSet());
        Set<String> expectedOutputIdentifiers = expectedOutputs.stream().map(expectedOutput -> expectedOutput.getIdentifier()).collect(Collectors.toSet());

        expectedOutputIdentifiers.removeAll(availableOutputIdentifiers); //missing entries remain in the set

        return expectedOutputIdentifiers;
    }
    
    
}
