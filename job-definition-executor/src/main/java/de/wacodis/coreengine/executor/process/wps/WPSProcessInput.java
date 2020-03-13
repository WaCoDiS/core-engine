package de.wacodis.coreengine.executor.process.wps;

import org.n52.geoprocessing.wps.client.model.execution.Execute;

import java.util.List;

/**
 * helper class to wrap execute request and origin data envelopes
 */
public class WPSProcessInput {

    private final Execute execute;
    private final List<String> originDataEnvelopes;

    public WPSProcessInput(Execute execute, List<String> originDataEnvelopes) {
        this.execute = execute;
        this.originDataEnvelopes = originDataEnvelopes;
    }

    public Execute getExecute() {
        return execute;
    }

    public List<String> getOriginDataEnvelopes() {
        return originDataEnvelopes;
    }

}
