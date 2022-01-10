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
package de.wacodis.coreengine.executor.process;

import de.wacodis.core.models.WacodisJobDefinition;
import java.util.Arrays;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Arne
 */
public class DefaultProcessContextToJobProcessConverter implements ProcessContextToJobProcessConverter {

    @Override
    public List<JobProcess> createJobProcesses(List<ProcessContext> contexts, WacodisJobDefinition jobDefinition, Process processingTool) {
        JobProcess[] processes = new JobProcess[contexts.size()];
        String uniqueSuffix = DateTime.now().toString();
        String wacodisJobId = jobDefinition.getId().toString();

        JobProcess process;
        String processId;
        for (int i = 0; i < processes.length; i++) {
            processId = createJobProcessID(wacodisJobId, (i + 1), processes.length, uniqueSuffix);
            process = new JobProcess(processId, jobDefinition, processingTool, contexts.get(i));
            processes[i] = process;
        }

        return Arrays.asList(processes);
    }

    private String createJobProcessID(String commonId, int part, int of, String uniqueProcessSuffix) {
        String jobProcessId = commonId + "_" + part + "_" + of + "_" + uniqueProcessSuffix;
        return jobProcessId;
    }
}
