/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.executor.process.wps;

import de.wacodis.core.models.JobOutputDescriptor;
import de.wacodis.coreengine.executor.process.ResourceDescription;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.n52.geoprocessing.wps.client.model.InputDescription;
import org.n52.geoprocessing.wps.client.model.OutputDescription;
import org.n52.geoprocessing.wps.client.model.WPSDescriptionParameter;
import org.n52.geoprocessing.wps.client.model.Process;

/**
 * provides methods to validate provided inputs and expected outputs of a specific wps proess
 * @author Arne
 */
public class WPSInputOutputValidator {

    private Process wpsProcessDescription;

    public WPSInputOutputValidator(Process wpsProcessDescription) {
        this.wpsProcessDescription = wpsProcessDescription;
    }

    public Process getWpsProcessDescription() {
        return wpsProcessDescription;
    }

    public void setWpsProcessDescription(Process wpsProcessDescription) {
        this.wpsProcessDescription = wpsProcessDescription;
    }
 
    /**
     * validates if expected outputs are actually supported by specific wps process
     *
     * 1) expected output matches a wps output 2) MimeType for expected output
     * is supported for corresponding wps output
     *
     * @param expectedOutputs
     * @return returns Optional.of(Throwable t) if a expected output or a
     * MimeType of a expected output is not supported, Optinal.empty() if valid
     */
    public Optional<Throwable> validateExpectedOutputs(List<JobOutputDescriptor> expectedOutputs) {

        for (JobOutputDescriptor expectedOutput : expectedOutputs) {
            Optional<OutputDescription> supportedOutput = getOutputDescriptionByID(expectedOutput.getIdentifier());

            if (supportedOutput.isPresent()) { //output supported
                boolean isMimeTypeSupported = isMimeTypeSupported(supportedOutput.get(), expectedOutput.getMimeType());

                if (!isMimeTypeSupported) { //MimeType not supported for supported output
                    return Optional.of(new IllegalArgumentException("MimeType  " + expectedOutput.getMimeType() + " is not supported for expected output " + expectedOutput.getIdentifier() + " of wps process " + this.wpsProcessDescription.getTitle() + " (" + this.wpsProcessDescription.getId() + ")"));
                }

            } else { //output not supported
                return Optional.of(new IllegalArgumentException("expected output " + expectedOutput.getIdentifier() + " is not supported by wps process " + this.wpsProcessDescription.getTitle() + " (" + this.wpsProcessDescription.getId() + ")"));
            }
        }

        return Optional.empty(); //return empty if valid
    }

    /**
     * Checks whether the provided input resources are suitable for the wps
     * process.
     *
     * 1) resources for mandatory wps inputs are provided 2) resource key
     * matches a wps input 3) resource mime type is supported for corresponding
     * wps input 4) minimum occurrence of wps inputs is respected
     *
     * @param providedInputResources
     * @return
     */
    public Optional<Throwable> validateProvidedInputResources(Map<String, List<ResourceDescription>> providedInputResources) {
        Set<String> missingMandatoryInputs = getMissingMandatoryInputs(providedInputResources);

        if (!missingMandatoryInputs.isEmpty()) { //mandatory input not provided
            return Optional.of(new IllegalArgumentException("missing mandatory input(s) " + missingMandatoryInputs.toString() + " for wps process " + this.wpsProcessDescription.getTitle() + " (" + this.wpsProcessDescription.getId() + ")"));
        }

        for (String providedInputID : providedInputResources.keySet()) {
            Optional<InputDescription> supportedInput = getInputDescriptionByID(providedInputID);

            if (supportedInput.isPresent()) { //input supported
                boolean isMimeTypeSupported = checkResourcesSupportedMimeType(supportedInput.get(), providedInputResources.get(providedInputID));

                if (!isMimeTypeSupported) { //MimeType not supported for supported input
                    return Optional.of(new IllegalArgumentException("provided MimeType is not supported for input " + supportedInput.get().getId() + " of wps process " + this.wpsProcessDescription.getTitle() + " (" + this.wpsProcessDescription.getId() + ")"));
                }

                if (!validateMinumumInputQuantity(supportedInput.get(), providedInputResources.get(providedInputID))) { //input quantity
                    return Optional.of(new IllegalArgumentException("unsupported quantity " + providedInputResources.get(providedInputID).size() + " of input " + supportedInput.get().getId() + " provided, provided " + providedInputResources.size() + " resources ,minimum occurrence is " + supportedInput.get().getMinOccurs() + " for wps process " + this.wpsProcessDescription.getTitle() + " (" + this.wpsProcessDescription.getId() + ")"));
                }

            } else { //input not supported
                return Optional.of(new IllegalArgumentException("provided input " + providedInputID + " is not supported by wps process " + this.wpsProcessDescription.getTitle() + " (" + this.wpsProcessDescription.getId() + ")"));
            }
        }

        return Optional.empty(); //return empty if valid
    }

    private Optional<OutputDescription> getOutputDescriptionByID(String outputIdentifier) {
        return this.wpsProcessDescription.getOutputs().stream().filter(output -> output.getId().equals(outputIdentifier)).findAny();
    }

    private Optional<InputDescription> getInputDescriptionByID(String inputIdentifier) {
        return this.wpsProcessDescription.getInputs().stream().filter(input -> input.getId().equals(inputIdentifier)).findAny();
    }

    private boolean validateMinumumInputQuantity(InputDescription supportedInput, List<ResourceDescription> providedInput) {
        return (providedInput.size() >= supportedInput.getMinOccurs());
    }

    private Set<String> getMissingMandatoryInputs(Map<String, List<ResourceDescription>> providedInputResources) {
        Set<String> mandatoryInputs = this.wpsProcessDescription.getInputs().stream().filter(supportedInput -> !isInputOptional(supportedInput)).map(supportedInput -> supportedInput.getId()).collect(Collectors.toSet()); //get IDs of all mandatory inputs
        Set<String> providedInputs = providedInputResources.keySet().stream().filter(inputID -> isResourceAvailable(providedInputResources, inputID)).collect(Collectors.toSet()); //get all provided inputs (checks if != null && size is > 0)      
        mandatoryInputs.removeAll(providedInputs); //keep all entries that are not contained in provideInputs

        return mandatoryInputs;
    }

    private boolean isMimeTypeSupported(WPSDescriptionParameter wpsInputOutputParameter, String mimeType) {
        return wpsInputOutputParameter.getFormats().stream().anyMatch(format -> format.getMimeType().equalsIgnoreCase(mimeType));
    }

    private boolean checkResourcesSupportedMimeType(InputDescription supportedInput, List<ResourceDescription> providedResources) {
        for (ResourceDescription providedResource : providedResources) {
            if (!isMimeTypeSupported(supportedInput, providedResource.getMimeType())) {
                return false;
            }
        }

        return true;
    }

    private boolean isInputOptional(InputDescription input) {
        return (input.getMinOccurs() == 0);
    }

    private boolean isResourceAvailable(Map<String, List<ResourceDescription>> providedInputResources, String key) {
        return (providedInputResources.get(key) != null && providedInputResources.get(key).size() > 0);
    }
}
