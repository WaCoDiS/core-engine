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
package de.wacodis.coreengine.executor.process.wps;

import org.n52.geoprocessing.wps.client.model.execution.Data;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteOutput;

import java.util.List;
import java.util.stream.Collectors;
import org.n52.geoprocessing.wps.client.model.execution.ComplexData;

public class ToStringHelper {

    public static String executeToString(Execute exec) {
        return "Execute{"
                + "inputs=[" + inputsToString(exec.getInputs()) + ']'
                + ", outputs=[" + outputsToString(exec.getOutputs()) + ']'
                + ", id='" + exec.getId() + '\''
                + '}';
    }

    public static String inputsToString(List<Data> inputs) {
        return inputs.stream().map(d -> String.format("id=%s, value=%s", d.getId(), getInputValue(d))).collect(Collectors.joining(", "));
    }

    public static String outputsToString(List<ExecuteOutput> outputs) {
        return outputs.stream().map(d -> String.format("id=%s, mode=%s", d.getId(), d.getTransmissionMode())).collect(Collectors.joining(", "));
    }

    private static Object getInputValue(Data input) {
        if (input instanceof ComplexData) {
            ComplexData complexInput = (ComplexData) input;
            if (complexInput.isReference() && complexInput.getReference() != null) {
                return complexInput.getReference().getHref();
            } else if (complexInput.isReference() && complexInput.getReference() == null) {
                return null;
            } else {
                return complexInput.getValue();
            }
        } else {
            if (input != null) {
                return input.getValue();
            } else {
                return null;
            }
        }
    }
}
