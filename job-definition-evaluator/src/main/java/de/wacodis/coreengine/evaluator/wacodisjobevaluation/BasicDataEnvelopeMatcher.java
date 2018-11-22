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
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionTemporalCoverage;
import de.wacodis.coreengine.evaluator.wacodisjobevaluation.cron.CronExecutionTimeCalculator;
import org.joda.time.DateTime;
import org.joda.time.Period;
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
     * matches DataEnvelope and SubsetDefinition by comparing common attributes, timeframe and area of interest
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
        boolean isMatchTF = matchTimeFrame(dataEnvelope, jobWrapper.getExecutionTime(), jobWrapper.getJobDefinition());
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
     * Returns true if the timeframe of dataEnvelopes intersectes the time between a point in the past and executionTime.
     * The point in the past is calculated according to jobDefinition (duration or previousExecution (scheduled point in time according to cron expression)).
     * @param dataEnvelope
     * @param executionTime
     * @param tempCoverage
     * @return
     */
    private boolean matchTimeFrame(AbstractDataEnvelope dataEnvelope, DateTime executionTime, WacodisJobDefinition jobDefinition) {
        WacodisJobDefinitionTemporalCoverage tempCoverage = jobDefinition.getTemporalCoverage();
        Period period = Period.parse(tempCoverage.getDuration()); //terms duration and period are mixed up
        DateTime beginRelevancy; //point in time when a DataEnvelope becomes relevant for a WacodisJob 

        if (tempCoverage.getPreviousExecution() != null && tempCoverage.getPreviousExecution()) { //previousExecution (data since last job execution is relevant)
            try {
                String cronExpression = jobDefinition.getExecution().getPattern();
                CronExecutionTimeCalculator timeCalculator = new CronExecutionTimeCalculator(cronExpression);
                beginRelevancy = timeCalculator.previousExecution(executionTime); //scheduled time of previous job execution
            } catch (NullPointerException e) {
                LOGGER.error("temporalCoverage previousExecution is true but no execution pattern (cron) is set");
                throw new NullPointerException("No execution pattern provided but attribute previousExecution is set true for JobDefinition: " + jobDefinition.getId());
            }

        } else { //duration (data since a specified point in time is relevant)
            beginRelevancy = executionTime.minus(period);
        }

        return dataEnvelope.getTimeFrame().getEndTime().isAfter(beginRelevancy) //envelopes timeframe intersects time between beginRelevancy and executionTime
                && dataEnvelope.getTimeFrame().getStartTime().isBefore(executionTime);
    }

    /**
     * returns true if aoiB is within or equal to aoiA
     *
     * @param aoiA
     * @param aoiB
     * @return
     */
    private boolean matchAreaofInterest(AbstractDataEnvelopeAreaOfInterest aoiA, AbstractDataEnvelopeAreaOfInterest aoiB) {
        return aoiB.getExtent().get(1) >= aoiA.getExtent().get(1) //minLat
                && aoiB.getExtent().get(0) >= aoiA.getExtent().get(0) //minLon
                && aoiB.getExtent().get(3) <= aoiA.getExtent().get(3) //maxLat
                && aoiB.getExtent().get(2) <= aoiA.getExtent().get(2); //maxLon
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
}
