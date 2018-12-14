/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.CatalogueSubsetDefinition;
import de.wacodis.core.models.CopernicusDataEnvelope;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.GdiDeDataEnvelope;
import de.wacodis.core.models.SensorWebDataEnvelope;
import de.wacodis.core.models.SensorWebSubsetDefinition;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class BasicDataEnvelopeMatcher implements DataEnvelopeMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicDataEnvelopeMatcher.class);

    private float minimumOverlapPercentage;

    public BasicDataEnvelopeMatcher(float overlapPercentageThreshold) {
        if (overlapPercentageThreshold > 100.0f || overlapPercentageThreshold < 0.0f) {
            throw new IllegalArgumentException("overlapPercentageThreshold is " + overlapPercentageThreshold + " but must be between 0.0 and 100.0");
        }

        this.minimumOverlapPercentage = overlapPercentageThreshold;
    }

    public BasicDataEnvelopeMatcher() {
        this(100.0f);
    }

    public float getMinimumOverlapPercentage() {
        return minimumOverlapPercentage;
    }

    public void setMinimumOverlapPercentage(float minimumOverlapPercentage) {
        if (minimumOverlapPercentage > 100.0f || minimumOverlapPercentage < 0.0f) {
            throw new IllegalArgumentException("overlapPercentageThreshold is " + minimumOverlapPercentage + " but must be between 0.0 and 100.0");
        }

        this.minimumOverlapPercentage = minimumOverlapPercentage;

    }

    /**
     * matches DataEnvelope and SubsetDefinition by comparing common attributes,
     * timeframe and area of interest
     *
     * @param dataEnvelope
     * @param jobWrapper
     * @param subsetDefinition
     * @return
     */
    @Override
    public boolean match(AbstractDataEnvelope dataEnvelope, WacodisJobWrapper jobWrapper, AbstractSubsetDefinition subsetDefinition) {
        //SourceTypes must match
        if (!matchSourceTypes(dataEnvelope, subsetDefinition)) {
            LOGGER.info("Matching " + dataEnvelope.getSourceType() + " with " + subsetDefinition.getSourceType() + "(ID: " + subsetDefinition.getIdentifier() + "), Result: " + false + " (incompatible SourceType)");
            return false;
        }

        //match by timeframe, area of interest and attributes (parial results)
        boolean isMatchTF = matchTimeFrame(dataEnvelope, jobWrapper);
        boolean isMatchAOI = matchAreaofInterest(dataEnvelope.getAreaOfInterest(), jobWrapper.getJobDefinition().getAreaOfInterest());
        boolean isMatchAttr = matchAttributes(dataEnvelope, subsetDefinition);
        //combine partial results
        boolean isMatch = (isMatchTF && isMatchAOI && isMatchAttr);

        LOGGER.info("Matching " + dataEnvelope.getSourceType() + " with " + subsetDefinition.getSourceType() + "(ID: " + subsetDefinition.getIdentifier() + "), Result: " + isMatch
                + " (TimeFrame: " + isMatchTF + ", AreaOfInterest: " + isMatchAOI + ", Attributes: " + isMatchAttr + ")");

        return isMatch;
    }

    private boolean matchAttributes(AbstractDataEnvelope dataEnvelope, AbstractSubsetDefinition subsetDefinition) {
        boolean isMatch;

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

        return isMatch;
    }

    /**
     * Returns true if the timeframe of dataEnvelopes intersectes the time
     * between a point in the past and executionTime. The point in the past is
     * calculated according to jobDefinition (duration or previousExecution
     * (scheduled point in time according to cron expression)).
     *
     * @param dataEnvelope
     * @param executionTime
     * @param tempCoverage
     * @return
     */
    private boolean matchTimeFrame(AbstractDataEnvelope dataEnvelope, WacodisJobWrapper jobWrapper) {
        Interval inputRelevancyTimeFrame = jobWrapper.calculateInputRelevancyTimeFrame();

        return dataEnvelope.getTimeFrame().getEndTime().isAfter(inputRelevancyTimeFrame.getStart()) //envelopes timeframe intersects time between beginRelevancy (start) and executionTime (end)
                && dataEnvelope.getTimeFrame().getStartTime().isBefore(inputRelevancyTimeFrame.getEnd());
    }

    /**
     * returns true if aoiB is within or equal to aoiA
     *
     * @param aoiA
     * @param aoiB
     * @return
     */
    /*
    private boolean matchAreaofInterest(AbstractDataEnvelopeAreaOfInterest aoiA, AbstractDataEnvelopeAreaOfInterest aoiB) {
        return aoiB.getExtent().get(1) >= aoiA.getExtent().get(1) //minLat
                && aoiB.getExtent().get(0) >= aoiA.getExtent().get(0) //minLon
                && aoiB.getExtent().get(3) <= aoiA.getExtent().get(3) //maxLat
                && aoiB.getExtent().get(2) <= aoiA.getExtent().get(2); //maxLon
    }*/
    /**
     * 
     * @param extentAreaOfInterest
     * @param extentDataEnvelope
     * @return 
     */
    private boolean matchAreaofInterest(AbstractDataEnvelopeAreaOfInterest extentAreaOfInterest, AbstractDataEnvelopeAreaOfInterest extentDataEnvelope) {
        return (calculateOverlapPercentage(extentAreaOfInterest, extentDataEnvelope) >= this.minimumOverlapPercentage);
    }

    private boolean matchGdiDeDataEnevelope(GdiDeDataEnvelope dataEnvelope, CatalogueSubsetDefinition subsetDefinition) {
        return dataEnvelope.getCatalougeUrl().equals(subsetDefinition.getServiceUrl())
                && dataEnvelope.getRecordRefId().equals(subsetDefinition.getDatasetIdentifier());
    }

    private boolean matchCopernicusDataEnvelope(CopernicusDataEnvelope dataEnvelope, CopernicusSubsetDefinition subsetDefinition) {
        return dataEnvelope.getDatasetId().equals(subsetDefinition.getIdentifier())
                && matchSatellite(dataEnvelope.getSatellite(), subsetDefinition.getSatellite())
                && dataEnvelope.getCloudCoverage() <= subsetDefinition.getMaximumCloudCoverage(); //cloud coverage must no exceed max. cloud coverage
    }

    private boolean matchSensorWebDataEnvelope(SensorWebDataEnvelope dataEnvelope, SensorWebSubsetDefinition subsetDefinition) {
        return dataEnvelope.getServiceUrl().equals(subsetDefinition.getServiceUrl()) //offering, foi, property and procedure identify a dataset
                && dataEnvelope.getOffering().equals(subsetDefinition.getOffering())
                && dataEnvelope.getFeatureOfInterest().equals(subsetDefinition.getFeatureOfInterest())
                && dataEnvelope.getObservedProperty().equals(subsetDefinition.getObservedProperty())
                && dataEnvelope.getProcedure().equals(subsetDefinition.getProcedure());
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
        } else {
            return false;
        }
    }

    /**
     * match Sattelite for CopernicusDataEnvelope and CopernicusSubsetDefinition
     *
     * @param satelliteEnv
     * @param satelliteSub
     * @return
     */
    private boolean matchSatellite(CopernicusDataEnvelope.SatelliteEnum satelliteEnv, CopernicusSubsetDefinition.SatelliteEnum satelliteSub) {
        return satelliteEnv.toString().equals(satelliteSub.toString());
    }

    private double calculateOverlapPercentage(AbstractDataEnvelopeAreaOfInterest areaOfInterestExtent, AbstractDataEnvelopeAreaOfInterest dataEnvelopeExtent) {
        float[] intersection = new float[4];
        Float[] aoiExtent = areaOfInterestExtent.getExtent().toArray(new Float[0]);
        Float[] envExtent = dataEnvelopeExtent.getExtent().toArray(new Float[0]);

        if (intersectsExtent(aoiExtent, envExtent)) {
            if (aoiExtent[0] > envExtent[0]) {
                intersection[0] = aoiExtent[0];
            } else {
                intersection[0] = envExtent[0];
            }
            if (aoiExtent[1] > envExtent[1]) {
                intersection[1] = aoiExtent[1];
            } else {
                intersection[1] = envExtent[1];
            }
            if (aoiExtent[2] < envExtent[2]) {
                intersection[2] = aoiExtent[2];
            } else {
                intersection[2] = envExtent[2];
            }
            if (aoiExtent[3] < envExtent[3]) {
                intersection[3] = aoiExtent[3];
            } else {
                intersection[3] = envExtent[3];
            }

            float aIntersection = Math.abs(intersection[0] - intersection[2]);
            float bIntersection = Math.abs(intersection[1] - intersection[3]);
            float areaIntersection = aIntersection * bIntersection;

            float aAoI = Math.abs(aoiExtent[0] - aoiExtent[2]);
            float bAoI = Math.abs(aoiExtent[1] - aoiExtent[3]);
            float areaAoI = aAoI * bAoI;

            float overlapPercentage = ((areaAoI / 100) * areaIntersection);
            
            assert(overlapPercentage <= 100.0f);

            return overlapPercentage;
        } else {
            return 0.0f; //no overlap
        }
    }

    private boolean intersectsExtent(Float[] extent1, Float[] extent2) {
        return extent1[0] <= extent2[2]
                && extent1[2] >= extent2[0]
                && extent1[1] <= extent2[3]
                && extent1[3] >= extent2[1];
    }
}
