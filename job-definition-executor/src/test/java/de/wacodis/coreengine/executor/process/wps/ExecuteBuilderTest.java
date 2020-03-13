package de.wacodis.coreengine.executor.process.wps;

import com.google.common.collect.Collections2;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.GetResource;
import de.wacodis.core.models.StaticSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobExecutionContext;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.WacodisJobWrapper;
import de.wacodis.coreengine.executor.configuration.WebProcessingServiceConfiguration;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.Schema;
import org.assertj.core.util.Lists;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.n52.geoprocessing.wps.client.model.ComplexInputDescription;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.Process;
import org.n52.geoprocessing.wps.client.model.execution.ComplexData;
import org.n52.geoprocessing.wps.client.model.execution.Data;
import org.n52.geoprocessing.wps.client.model.execution.LiteralData;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class ExecuteBuilderTest {

    @Test
    public void testStaticGetResource() {
        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        UUID processID = UUID.randomUUID();
        jobDef.setId(processID);

        StaticSubsetDefinition input = new StaticSubsetDefinition();
        input.setIdentifier("REFERENCE_DATA");
        input.setDataType(StaticSubsetDefinition.DataTypeEnum.TEXT);
        input.setValue("http://online-resource.url");
        jobDef.addInputsItem(input);

        StaticSubsetDefinition input2 = new StaticSubsetDefinition();
        input2.setIdentifier("AREA_OF_INTEREST");
        input2.setDataType(StaticSubsetDefinition.DataTypeEnum.TEXT);
        input2.setValue("[6.9315, 50.9854, 7.6071, 51.3190]");
        jobDef.addInputsItem(input2);

        CopernicusSubsetDefinition input3 = new CopernicusSubsetDefinition();
        input3.setIdentifier("OPTICAL_IMAGES_SOURCES");
        input3.setMaximumCloudCoverage(15.0f);
        input3.setSatellite(CopernicusSubsetDefinition.SatelliteEnum._2);
        jobDef.addInputsItem(input3);

        WacodisJobWrapper jobWrapper = new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), DateTime.now(), 0), jobDef);
        jobWrapper.getInputs().get(2).setResource(Lists.list(new GetResource().url("https://sentinel.hub/abcd")));

        WPSProcessContextBuilder contextBuilder = new WPSProcessContextBuilder(getWPSConfig());
        ProcessContext context = contextBuilder.buildProcessContext(jobWrapper);

        ExecuteBuilder builder = new ExecuteBuilder(getProcessDescription());

        WPSProcessInput result = builder.buildExecuteRequest(context);
        Assert.assertThat(result.getExecute().getInputs().size(), CoreMatchers.is(3));
        Assert.assertThat(result.getExecute().getInputs().get(0), CoreMatchers.instanceOf(ComplexData.class));
        Assert.assertThat(result.getExecute().getInputs().get(1), CoreMatchers.instanceOf(LiteralData.class));
        Assert.assertThat(result.getExecute().getInputs().get(2), CoreMatchers.instanceOf(LiteralData.class));

        ComplexData one = (ComplexData) result.getExecute().getInputs().get(0);
        Assert.assertThat(one.isReference(), CoreMatchers.is(true));
        Assert.assertThat(one.getReference().getHref().toString(), CoreMatchers.equalTo("http://online-resource.url"));

        LiteralData three = (LiteralData) result.getExecute().getInputs().get(2);
        Assert.assertThat(three.getValue().toString(), CoreMatchers.equalTo("https://sentinel.hub/abcd"));
    }

    private Process getProcessDescription() {
        Process p = new Process();
        p.setId("test.process");
        InputDescription id = new ComplexInputDescription();
        id.setId("REFERENCE_DATA");
        id.setMaxOccurs(1);
        InputDescription id2 = new LiteralInputDescription();
        id2.setId("AREA_OF_INTEREST");
        id2.setMaxOccurs(1);
        InputDescription id3 = new LiteralInputDescription();
        id3.setId("OPTICAL_IMAGES_SOURCES");
        id3.setMaxOccurs(1);
        p.setInputs(Lists.list(id, id2, id3));

        return p;
    }

    private WebProcessingServiceConfiguration getWPSConfig() {
        ExpectedProcessOutput productOutput = new ExpectedProcessOutput("PRODUCT", "image/geotiff");
        ExpectedProcessOutput metadataOutput = new ExpectedProcessOutput("METADATA", "text/json", false);
        WebProcessingServiceConfiguration config = new WebProcessingServiceConfiguration();

        config.setVersion("2.0.0");
        config.setUri("localhost:8080");
        config.setDefaultResourceMimeType("text/xml");
        config.setDefaultResourceSchema(Schema.GML3);
        config.setExpectedProcessOutputs(Arrays.asList(new ExpectedProcessOutput[]{metadataOutput, productOutput}));

        return config;
    }
}
