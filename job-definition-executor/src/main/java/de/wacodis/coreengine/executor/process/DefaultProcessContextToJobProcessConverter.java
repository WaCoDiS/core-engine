/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
