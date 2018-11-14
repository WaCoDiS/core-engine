/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.CatalogueSubsetDefinition;
import de.wacodis.core.models.CopernicusDataEnvelope;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.GdiDeDataEnvelope;
import de.wacodis.core.models.SensorWebDataEnvelope;
import de.wacodis.core.models.SensorWebSubsetDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class BasicDataEnvelopeMatcher implements DataEnvelopeMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicDataEnvelopeMatcher.class);

    public BasicDataEnvelopeMatcher() {
    }

    /**
     * matches DataEnvelope and SubsetDefinition by comparing common attributes
     * @param dataEnvelope
     * @param subsetDefinition
     * @return 
     */
    @Override
    public boolean match(AbstractDataEnvelope dataEnvelope, AbstractSubsetDefinition subsetDefinition) {
        boolean isMatch;

        //SourceTypes must match
        if (!matchSourceTypes(dataEnvelope, subsetDefinition)) {
            LOGGER.info("Matching incompatible SourceTypes " + dataEnvelope.getSourceType() + " with " + subsetDefinition.getSourceType() + "(ID: " + subsetDefinition.getIdentifier() +  "), Result: " + false);
            return false;
        }

        switch (dataEnvelope.getSourceType()) { //select matching method by SourceType
            case COPERNICUSDATAENVELOPE:
                CopernicusDataEnvelope copernicusDataEnvelope = (CopernicusDataEnvelope) dataEnvelope;
                CopernicusSubsetDefinition copernicusSubsetDefinition = (CopernicusSubsetDefinition) subsetDefinition;

                isMatch = matchCopernicusDataEnvelope(copernicusDataEnvelope, copernicusSubsetDefinition);

                break;
            case SENSORWEBDATAENVELOPE:
                SensorWebDataEnvelope sensorWebDataEnvelope = (SensorWebDataEnvelope) dataEnvelope;
                SensorWebSubsetDefinition sensorWebSubsetDefinition = (SensorWebSubsetDefinition) subsetDefinition;

                isMatch = matchSensorWebDataEnvelope(sensorWebDataEnvelope, sensorWebSubsetDefinition);

                break;
            case GDIDEDATAENVELOPE:
                GdiDeDataEnvelope gdiDataEnvelope = (GdiDeDataEnvelope) dataEnvelope;
                CatalogueSubsetDefinition catalogueSubsetDefinition = (CatalogueSubsetDefinition) subsetDefinition;

                isMatch = matchGdiDeDataEnevelope(gdiDataEnvelope, catalogueSubsetDefinition);

                break;
            default:
                //unknown source type or not set
                LOGGER.warn("SourceType for DataEnvelope unknown or not set, matching not possible: " + dataEnvelope.getSourceType().toString());
                isMatch = false;
        }

        LOGGER.info("Matching " + dataEnvelope.getSourceType() + " with " + subsetDefinition.getSourceType() + "(ID: " + subsetDefinition.getIdentifier() +  "), Result: " + isMatch);
        
        return isMatch;
    }

    private boolean matchGdiDeDataEnevelope(GdiDeDataEnvelope dataEnvelope, CatalogueSubsetDefinition subsetDefinition) {
        if (dataEnvelope.getCatalougeUrl().equals(subsetDefinition.getServiceUrl())
                && dataEnvelope.getRecordRefId().equals(subsetDefinition.getDatasetIdentifier())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean matchCopernicusDataEnvelope(CopernicusDataEnvelope dataEnvelope, CopernicusSubsetDefinition subsetDefinition) {
        if (dataEnvelope.getDatasetId().equals(subsetDefinition.getIdentifier())
                && matchSatellite(dataEnvelope.getSatellite(), subsetDefinition.getSatellite())
                && dataEnvelope.getCloudCoverage() <= subsetDefinition.getMaximumCloudCoverage()) { //cloud coverage must no exceed max. cloud coverage
            return true;
        } else {
            return false;
        }
    }

    private boolean matchSensorWebDataEnvelope(SensorWebDataEnvelope dataEnvelope, SensorWebSubsetDefinition subsetDefinition) {
        if (dataEnvelope.getServiceUrl().equals(subsetDefinition.getServiceUrl())
                && dataEnvelope.getOffering().equals(subsetDefinition.getOffering()) //offering, foi, property and procedure identify a dataset
                && dataEnvelope.getFeatureOfInterest().equals(subsetDefinition.getFeatureOfInterest())
                && dataEnvelope.getObservedProperty().equals(subsetDefinition.getObservedProperty())
                && dataEnvelope.getProcedure().equals(subsetDefinition.getProcedure())) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * check for compatible source types
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
        } else {
            return false;
        }
    }

    /**
     * match Sattelite for CopernicusDataEnvelope and CopernicusSubsetDefinition
     * @param satelliteEnv
     * @param satelliteSub
     * @return 
     */
    private boolean matchSatellite(CopernicusDataEnvelope.SatelliteEnum satelliteEnv, CopernicusSubsetDefinition.SatelliteEnum satelliteSub) {
        return satelliteEnv.toString().equals(satelliteSub.toString());
    }
}
