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
