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
import de.wacodis.core.models.DwdDataEnvelope;
import de.wacodis.core.models.DwdSubsetDefinition;
import de.wacodis.core.models.GdiDeDataEnvelope;
import de.wacodis.core.models.SensorWebDataEnvelope;
import de.wacodis.core.models.SensorWebSubsetDefinition;
import de.wacodis.coreengine.evaluator.configuration.DataEnvelopeMatchingConfiguration;
import java.util.Arrays;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class BasicDataEnvelopeMatcher implements DataEnvelopeMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicDataEnvelopeMatcher.class);

    private DataEnvelopeMatchingConfiguration config;

    public BasicDataEnvelopeMatcher(DataEnvelopeMatchingConfiguration config) {
        this.config = config;
    }

    public BasicDataEnvelopeMatcher() {
        this.config = new DataEnvelopeMatchingConfiguration();
        this.config.setMinimumOverlapPercentage(100.0f);
    }

    public DataEnvelopeMatchingConfiguration getConfig() {
        return config;
    }

    public void setConfig(DataEnvelopeMatchingConfiguration config) {
        this.config = config;
    }

    public float getMinimumOverlapPercentage() {
        return this.config.getMinimumOverlapPercentage();
    }

    public void setMinimumOverlapPercentage(float minimumOverlapPercentage) {
        this.config.setMinimumOverlapPercentage(minimumOverlapPercentage);
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
            case DWDDATAENVELOPE:
                DwdDataEnvelope dwdDataEnvelope = (DwdDataEnvelope) dataEnvelope;
                DwdSubsetDefinition dwdSubsetDefinition = (DwdSubsetDefinition) subsetDefinition;

                isMatch = matchDWDDataEnvelope(dwdDataEnvelope, dwdSubsetDefinition);

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
     *
     * @param extentAreaOfInterest
     * @param extentDataEnvelope
     * @return
     */
    private boolean matchAreaofInterest(AbstractDataEnvelopeAreaOfInterest extentAreaOfInterest, AbstractDataEnvelopeAreaOfInterest extentDataEnvelope) {
        Float[] aoiExtent = extentAreaOfInterest.getExtent().toArray(new Float[0]);
        Float[] envExtent = extentDataEnvelope.getExtent().toArray(new Float[0]);

        return (calculateOverlapPercentage(aoiExtent, envExtent) >= this.config.getMinimumOverlapPercentage());
    }

    private boolean matchGdiDeDataEnevelope(GdiDeDataEnvelope dataEnvelope, CatalogueSubsetDefinition subsetDefinition) {
        return dataEnvelope.getCatalougeUrl().equals(subsetDefinition.getServiceUrl())
                && dataEnvelope.getRecordRefId().equals(subsetDefinition.getDatasetIdentifier());
    }

    private boolean matchCopernicusDataEnvelope(CopernicusDataEnvelope dataEnvelope, CopernicusSubsetDefinition subsetDefinition) {
        return dataEnvelope.getDatasetId().equals(subsetDefinition.getIdentifier())
                && matchSatellite(dataEnvelope.getSatellite(), subsetDefinition.getSatellite())
                && dataEnvelope.getCloudCoverage() <= subsetDefinition.getMaximumCloudCoverage(); //cloud coverage must not exceed max. cloud coverage
    }

    private boolean matchSensorWebDataEnvelope(SensorWebDataEnvelope dataEnvelope, SensorWebSubsetDefinition subsetDefinition) {
        return dataEnvelope.getServiceUrl().equals(subsetDefinition.getServiceUrl()) //offering, foi, property and procedure identify a dataset
                && dataEnvelope.getOffering().equals(subsetDefinition.getOffering())
                && dataEnvelope.getFeatureOfInterest().equals(subsetDefinition.getFeatureOfInterest())
                && dataEnvelope.getObservedProperty().equals(subsetDefinition.getObservedProperty())
                && dataEnvelope.getProcedure().equals(subsetDefinition.getProcedure());
    }

    private boolean matchDWDDataEnvelope(DwdDataEnvelope dataEnvelope, DwdSubsetDefinition subsetDefinition) {
        return dataEnvelope.getServiceUrl().equals(subsetDefinition.getServiceUrl())
                && dataEnvelope.getLayerName().equals(subsetDefinition.getLayerName());
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

    /**
     * calculates the area of intersection relative to the area of
     * areaOfInterestExtent
     *
     * @param areaOfInterestExtent
     * @param dataEnvelopeExtent
     * @return value between 0.0 and 100.0
     */
    private float calculateOverlapPercentage(Float[] areaOfInterestExtent, Float[] dataEnvelopeExtent) {
        float[] intersection = calculateIntersectionExtent(areaOfInterestExtent, dataEnvelopeExtent);
        float areaIntersection = calculateAreaExtent(intersection);
        float areaAreaOfInterest = calculateAreaExtent(areaOfInterestExtent);
        float overlapPercentage = ((areaAreaOfInterest / 100) * areaIntersection);

        assert (overlapPercentage <= 100.0f && overlapPercentage >= 0.0f);

        return overlapPercentage;
    }

    /**
     *
     * @param extent1
     * @param extent2
     * @return true if extent1 intersects extent2
     */
    private boolean intersectsExtent(Float[] extent1, Float[] extent2) {
        return extent1[0] <= extent2[2]
                && extent1[2] >= extent2[0]
                && extent1[1] <= extent2[3]
                && extent1[3] >= extent2[1];
    }

    /**
     *
     * @param extent1
     * @param extent2
     * @return intersection of extent1 and extent2
     */
    private float[] calculateIntersectionExtent(Float[] extent1, Float[] extent2) {
        float[] intersection = new float[4];
        Arrays.fill(intersection, 0.0f);

        if (intersectsExtent(extent1, extent2)) {
            if (extent1[0] > extent2[0]) {
                intersection[0] = extent1[0];
            } else {
                intersection[0] = extent2[0];
            }
            if (extent1[1] > extent2[1]) {
                intersection[1] = extent1[1];
            } else {
                intersection[1] = extent2[1];
            }
            if (extent1[2] < extent2[2]) {
                intersection[2] = extent1[2];
            } else {
                intersection[2] = extent2[2];
            }
            if (extent1[3] < extent2[3]) {
                intersection[3] = extent1[3];
            } else {
                intersection[3] = extent2[3];
            }
        }

        return intersection;
    }

    /**
     * calculates the area of an extent by a*b, does not respect curvature of
     * earth
     *
     * @param extent
     * @return
     */
    private float calculateAreaExtent(float[] extent) {
        float a = Math.abs(extent[0] - extent[2]);
        float b = Math.abs(extent[1] - extent[3]);
        return a * b;
    }

    /**
     * calculates the area of an extent by a*b, does not respect curvature of
     * earth
     *
     * @param extent
     * @return
     */
    private float calculateAreaExtent(Float[] extent) {
        float[] primitiveExtent = getArrayAsPrimitive(extent);
        return calculateAreaExtent(primitiveExtent);
    }

    /**
     * copies values of a Float[] to a float[]
     *
     * @param extent
     * @return
     */
    private float[] getArrayAsPrimitive(Float[] extent) {
        float[] primitiveExtent = new float[extent.length];

        for (int i = 0; i < extent.length; i++) {
            primitiveExtent[i] = extent[i];
        }

        return primitiveExtent;
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
}
