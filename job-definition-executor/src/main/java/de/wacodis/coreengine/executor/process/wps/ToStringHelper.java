package de.wacodis.coreengine.executor.process.wps;

import org.n52.geoprocessing.wps.client.model.execution.ComplexData;
import org.n52.geoprocessing.wps.client.model.execution.Data;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.n52.geoprocessing.wps.client.model.execution.ExecuteOutput;

import java.util.List;
import java.util.stream.Collectors;

public class ToStringHelper {

    public static String executeToString(Execute exec) {
        return "Execute{" +
                "inputs=[" + inputsToString(exec.getInputs()) + ']' +
                ", outputs=[" + outputsToString(exec.getOutputs()) + ']' +
                ", id='" + exec.getId() + '\'' +
                '}';
    }

    public static String inputsToString(List<Data> inputs) {
        return inputs.stream().map(d -> {
            String value = "(not serializable)";
            if (d instanceof ComplexData) {
                if (((ComplexData) d).isReference() && ((ComplexData) d).getReference().getHref() != null) {
                    value = ((ComplexData) d).getReference().getHref().toString();
                } else if (d.getValue() != null) {
                    value = d.getValue().toString();
                }
            } else if (d.getValue() != null){
                value = d.getValue().toString();
            }
            return String.format("{id=%s, value=%s}", d.getId(), d.getValue());
        }).collect(Collectors.joining(", "));
    }

    public static String outputsToString(List<ExecuteOutput> outputs) {
        return outputs.stream().map(d -> String.format("{id=%s, mode=%s}", d.getId(), d.getTransmissionMode())).collect(Collectors.joining(", "));
    }
}
