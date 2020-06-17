/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractSubsetDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class SourceTypeDataEnvelopeMatcher implements DataEnvelopeMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceTypeDataEnvelopeMatcher.class);

    

    /**
     * matches DataEnvelope and SubsetDefinition by source types
     *
     * @param dataEnvelope
     * @param jobWrapper
     * @param subsetDefinition
     * @return
     */
    @Override
    public boolean match(AbstractDataEnvelope dataEnvelope, WacodisJobWrapper jobWrapper, AbstractSubsetDefinition subsetDefinition) {
        //SourceTypes must match 
        boolean isMatch = matchSourceTypes(dataEnvelope, subsetDefinition);

        LOGGER.debug("Matching " + dataEnvelope.getSourceType() + "(ID: " + dataEnvelope.getIdentifier() + ") with " + subsetDefinition.getSourceType() + "(ID: " + subsetDefinition.getIdentifier() + ", Wacodis Job: " + jobWrapper.getJobDefinition().getId() + "), Result: " + isMatch);

        return isMatch;
    }

   
    /**
     * check for compatible source types
     *
     * @param dataEnvelope
     * @param subsetDefinition
     * @return
     */
    private boolean matchSourceTypes(AbstractDataEnvelope dataEnvelope, AbstractSubsetDefinition subsetDefinition) {
        if (dataEnvelope.getSourceType() == AbstractDataEnvelope.SourceTypeEnum.COPERNICUSDATAENVELOPE && subsetDefinition.getSourceType() == AbstractSubsetDefinition.SourceTypeEnum.COPERNICUSSUBSETDEFINITION) {
            return true;
        } else if (dataEnvelope.getSourceType() == AbstractDataEnvelope.SourceTypeEnum.SENSORWEBDATAENVELOPE && subsetDefinition.getSourceType() == AbstractSubsetDefinition.SourceTypeEnum.SENSORWEBSUBSETDEFINITION) {
            return true;
        } else if (dataEnvelope.getSourceType() == AbstractDataEnvelope.SourceTypeEnum.GDIDEDATAENVELOPE && subsetDefinition.getSourceType() == AbstractSubsetDefinition.SourceTypeEnum.CATALOGUESUBSETDEFINITION) {
            return true;
        } else if (dataEnvelope.getSourceType() == AbstractDataEnvelope.SourceTypeEnum.DWDDATAENVELOPE && subsetDefinition.getSourceType() == AbstractSubsetDefinition.SourceTypeEnum.DWDSUBSETDEFINITION) {
            return true;
        } else if (dataEnvelope.getSourceType() == AbstractDataEnvelope.SourceTypeEnum.WACODISPRODUCTDATAENVELOPE && subsetDefinition.getSourceType() == AbstractSubsetDefinition.SourceTypeEnum.WACODISPRODUCTSUBSETDEFINITION) {
            return true;
        } else {
            return false;
        }
    }
}
