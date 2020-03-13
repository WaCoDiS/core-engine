package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.AbstractResource;
import de.wacodis.core.models.extension.staticresource.StaticDummyResource;
import de.wacodis.coreengine.executor.process.ExpectedProcessOutput;
import de.wacodis.coreengine.executor.process.ProcessContext;
import de.wacodis.coreengine.executor.process.ResourceDescription;
import org.apache.commons.lang.NotImplementedException;
import org.n52.geoprocessing.wps.client.ExecuteRequestBuilder;
import org.n52.geoprocessing.wps.client.model.BoundingBoxInputDescription;
import org.n52.geoprocessing.wps.client.model.ComplexInputDescription;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.LiteralInputDescription;
import org.n52.geoprocessing.wps.client.model.execution.Execute;
import org.n52.geoprocessing.wps.client.model.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExecuteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger( ExecuteBuilder.class );

    private Process wpsProcessDescription;

    public ExecuteBuilder(Process wpsProcessDescription) {
        this.wpsProcessDescription = wpsProcessDescription;
    }

    public WPSProcessInput buildExecuteRequest(ProcessContext context) {
        ExecuteRequestBuilder executeRequestBuilder = new ExecuteRequestBuilder(wpsProcessDescription);
        List<String> originDataEnvelopes = setWPSInputs(context.getInputResources(), executeRequestBuilder); //register inputs
        setExpectedWPSOutputs(context.getExpectedOutputs(), executeRequestBuilder); //register expected outputs
        executeRequestBuilder.setAsynchronousExecute(); //make execute request asynchronous
        Execute execute = executeRequestBuilder.getExecute();

        return new WPSProcessInput(execute, originDataEnvelopes);
    }


    /**
     * register each expected output
     *
     * @param executeRequestBuilder
     * @param expectedOutputs
     */
    private void setExpectedWPSOutputs(List<ExpectedProcessOutput> expectedOutputs, ExecuteRequestBuilder executeRequestBuilder) {
        for (ExpectedProcessOutput expectedOutput : expectedOutputs) {
            executeRequestBuilder.setResponseDocument(expectedOutput.getIdentifier(), "", "", expectedOutput.getMimeType() /*mime type*/);
            executeRequestBuilder.setAsReference(expectedOutput.getIdentifier(), expectedOutput.isByReference());
        }
    }

    /**
     * register inputs for wps process
     *
     * @param providedInputResources
     * @param executeRequestBuilder
     */
    private List<String> setWPSInputs(Map<String, List<ResourceDescription>> providedInputResources, ExecuteRequestBuilder executeRequestBuilder) {
        List<String> originDataEnvelopes = new ArrayList<>();
        List<InputDescription> processInputs = this.wpsProcessDescription.getInputs();
        List<ResourceDescription> providedResourcesForProcessInput;
        String inputID;

        for (InputDescription processInput : processInputs) {
            inputID = processInput.getId();

            if (!providedInputResources.containsKey(inputID)) { //skip if no resource provided for specific wps input
                continue;
            }

            providedResourcesForProcessInput = providedInputResources.get(inputID);

            if (processInput instanceof LiteralInputDescription) {
                List<String> literalInputOrigins = setLiteralInputData((LiteralInputDescription) processInput, providedResourcesForProcessInput, executeRequestBuilder);
                originDataEnvelopes.addAll(literalInputOrigins);
            } else if (processInput instanceof ComplexInputDescription) {
                List<String> complexInputOrigins = setComplexInputData((ComplexInputDescription) processInput, providedResourcesForProcessInput, executeRequestBuilder);
                originDataEnvelopes.addAll(complexInputOrigins);
            } else if (processInput instanceof BoundingBoxInputDescription) {
                //ToDo
                throw new NotImplementedException("cannot handle wps input " + inputID + ": BoundingBoxInput not supported");
                //originDataEnvelopes.addAll(bboxInputOrigins);
            } else {
                throw new IllegalArgumentException("cannot handle wps input " + inputID + ": input is of unknown type " + processInput.getClass().getSimpleName());
            }

        }


        return removeDuplicates(originDataEnvelopes);
    }


    /**
     * @param literalProcessInput
     * @param providedResources
     * @param executeRequestBuilder
     * @return list of data envelope id's that correspond with the used input resources
     */
    private List<String> setLiteralInputData(LiteralInputDescription literalProcessInput, List<ResourceDescription> providedResources, ExecuteRequestBuilder executeRequestBuilder) {
        String literalValue;
        ResourceDescription providedResource;
        List<String> originDataEnvelopes = new ArrayList<>();

        for (int i = 0; i < providedResources.size(); i++) {
            if (literalProcessInput.getMaxOccurs() < (i + 1)) {
                LOGGER.warn("Max occurs ({}) for input {} reached, provided {} resources.  Skipping surplus candidates.", literalProcessInput.getMaxOccurs(), literalProcessInput.getId(), providedResources.size());
                break;
            }

            providedResource = providedResources.get(i);
            literalValue = getValueFromResource(providedResource);
            executeRequestBuilder.addLiteralData(literalProcessInput.getId(), literalValue, "", "", providedResource.getMimeType()); //input id, value, schema, encoding, mime type

            originDataEnvelopes.add(providedResource.getResource().getDataEnvelopeId()); //add data envelope id to list of origins of process result
        }

        return originDataEnvelopes;
    }

    private List<String> setComplexInputData(ComplexInputDescription complexProcessInput, List<ResourceDescription> providedResources, ExecuteRequestBuilder executeRequestBuilder) {
        ResourceDescription providedResource;
        List<String> originDataEnvelopes = new ArrayList<>();

        for (int i = 0; i < providedResources.size(); i++) {
            if (complexProcessInput.getMaxOccurs() < (i + 1)) {
                LOGGER.warn("Max occurs ({}) for input {} reached, provided {} resources.  Skipping surplus candidates.", complexProcessInput.getMaxOccurs(), providedResources.size(), complexProcessInput.getId());
                break;
            }

            providedResource = providedResources.get(i);

            if (StaticDummyResource.class
                    .isAssignableFrom(providedResource.getResource().getClass())) {
                throw new IllegalArgumentException(
                        "illegal ressource type for complex wps input, resource for input " + complexProcessInput.getId() + " is a static resource, only non-static resources are allowed for complex inputs");
            }

            try {
                executeRequestBuilder.addComplexDataReference(complexProcessInput.getId(), providedResource.getResource().getUrl(), providedResource.getSchema().getSchemaLocation(), null, providedResource.getMimeType());
            } catch (MalformedURLException ex) {
                throw new IllegalArgumentException("cannot add complex input " + complexProcessInput.getId() + " as reference, invalid reference, malformed url" + providedResource.getResource().getUrl(), ex);
            }

            originDataEnvelopes.add(providedResource.getResource().getDataEnvelopeId());
        }

        return originDataEnvelopes;
    }


    /**
     * @return url for non-static resources, value for static resources
     */
    private String getValueFromResource(ResourceDescription input) {
        String literalValue;
        AbstractResource resource = input.getResource();

        if (StaticDummyResource.class
                .isAssignableFrom(resource.getClass())) { //StaticResource or subtype
            StaticDummyResource staticResource = (StaticDummyResource) resource;
            literalValue = staticResource.getValue();
        } else {
            literalValue = resource.getUrl();
        }

        return literalValue;
    }

    /**
     * @param <T>
     * @param list
     * @return list of unique items
     */
    private <T> List<T> removeDuplicates(List<T> list){
        return list.stream().distinct().collect(Collectors.toList());
    }


}
