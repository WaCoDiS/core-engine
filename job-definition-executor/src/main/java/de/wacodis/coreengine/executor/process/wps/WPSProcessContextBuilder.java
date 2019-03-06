/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ProcessContextBuilder;
import java.util.List;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.InputHelper;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class WPSProcessContextBuilder implements ProcessContextBuilder {

    @Override
    public ProcessContext buildProcessContext(WacodisJobWrapper job) {
        ProcessContext context = new ProcessContext();

        List<InputHelper> jobInputs = job.getInputs();
        //ToDo handle Maps
        Map<String, AbstractResource> inputResources = jobInputs.stream().filter(inputHelper -> inputHelper.hasResource()).collect(Collectors.toMap(InputHelper::getSubsetDefinitionIdentifier, inputHelper -> inputHelper.getResource().get().get(0)));
        context.setProcessResources(inputResources);

        return context;
    }
}
