/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wacodis.coreengine.evaluator.wacodisjobevaluation;

import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.core.models.AbstractDataEnvelopeTimeFrame;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.CatalogueSubsetDefinition;
import de.wacodis.core.models.CopernicusDataEnvelope;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.GdiDeDataEnvelope;
import de.wacodis.core.models.SensorWebDataEnvelope;
import de.wacodis.core.models.SensorWebSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecution;
import de.wacodis.core.models.WacodisJobDefinitionTemporalCoverage;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author <a href="mailto:arne.vogt@hs-bochum.de">Arne Vogt</a>
 */
public class BasicDataEnvelopeMatcherTest {

    private final DataEnvelopeMatcher matcher;

    public BasicDataEnvelopeMatcherTest() {
        this.matcher = new BasicDataEnvelopeMatcher();
    }

    @Test
    public void testSetMinimumOverlapPercentage_IllegalArgument() {
        BasicDataEnvelopeMatcher basicMatcher = new BasicDataEnvelopeMatcher();

        assertAll(
                () -> assertThrows(java.lang.IllegalArgumentException.class, () -> basicMatcher.setMinimumOverlapPercentage(110.5f)),
                () -> assertThrows(java.lang.IllegalArgumentException.class, () -> basicMatcher.setMinimumOverlapPercentage(-1.0f))
        );
    }

    @Test
    public void testSetMinimumOverlapPercentage_IllegalArgument_Constructor() {
        assertAll(
                () -> assertThrows(java.lang.IllegalArgumentException.class, () -> new BasicDataEnvelopeMatcher(110.5f)),
                () -> assertThrows(java.lang.IllegalArgumentException.class, () -> new BasicDataEnvelopeMatcher(-1.0f))
        );
    }

    @Test
    public void testSetMinimumOverlapPercentage() {
        BasicDataEnvelopeMatcher basicMatcher = new BasicDataEnvelopeMatcher();

        basicMatcher.setMinimumOverlapPercentage(0.0f);
        assertEquals(0.0f, basicMatcher.getMinimumOverlapPercentage());
    }

    @Test
    public void testGetMinimumOverlapPercentage() {
        BasicDataEnvelopeMatcher basicMatcher = new BasicDataEnvelopeMatcher(100.0f);

        assertEquals(100.0f, basicMatcher.getMinimumOverlapPercentage());
    }

    @Test
    public void testIsMatchCopernicusDataEnvelopeCopernicusSubsetDefinition() {
        CopernicusDataEnvelope copernicusEnv = getCopernicusDataEnvelope();
        CopernicusSubsetDefinition copernicusSubset = getCopernicusSubsetDefinition();

        assertTrue(this.matcher.match(copernicusEnv, getJobWrapper(), copernicusSubset));
    }

    @Test
    public void testIsMatchCopernicusDataEnvelopeCopernicusSubsetDefinitionCloudCoverage() {
        CopernicusDataEnvelope copernicusEnv = getCopernicusDataEnvelope();
        CopernicusSubsetDefinition copernicusSubset = getCopernicusSubsetDefinition();

        copernicusEnv.setCloudCoverage(50.0f);
        copernicusSubset.setMaximumCloudCoverage(5.0f);

        assertFalse(this.matcher.match(copernicusEnv, getJobWrapper(), copernicusSubset));
    }

    @Test
    public void testIsMatchCopernicusDataEnvelopeCopernicusSubsetDefinitionIdentifier() {
        CopernicusDataEnvelope copernicusEnv = getCopernicusDataEnvelope();
        CopernicusSubsetDefinition copernicusSubset = getCopernicusSubsetDefinition();

        copernicusEnv.setDatasetId("identifierA");
        copernicusSubset.setIdentifier("identifierB");

        assertFalse(this.matcher.match(copernicusEnv, getJobWrapper(), copernicusSubset));
    }

    @Test
    public void testIsMatchSensorWebDataEnvelopeSensorWebSubsetDefinition() {
        SensorWebDataEnvelope sensorWebEnv = getSensorWebDataEnvelope();
        SensorWebSubsetDefinition sensorWebSubset = getSensorWebSubsetDefinition();

        assertTrue(this.matcher.match(sensorWebEnv, getJobWrapper(), sensorWebSubset));
    }

    @Test
    public void testIsMatchSensorWebDataEnvelopeSensorWebSubsetDefinitionOffering() {
        SensorWebDataEnvelope sensorWebEnv = getSensorWebDataEnvelope();
        SensorWebSubsetDefinition sensorWebSubset = getSensorWebSubsetDefinition();

        sensorWebEnv.setOffering("offeringA");
        sensorWebSubset.setOffering("offeringB");

        assertFalse(this.matcher.match(sensorWebEnv, getJobWrapper(), sensorWebSubset));
    }

    @Test
    public void testIsMatchGdiDeEnvelopeCatalogueSubsetDefinition() {
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        assertTrue(this.matcher.match(gdiDeEnv, getJobWrapper(), catalogueSubset));
    }

    @Test
    public void testIsMatchGdiDeEnvelopeCatalogueSubsetDefinitionIdentifier() {
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        gdiDeEnv.setRecordRefId("identifierA");
        catalogueSubset.setDatasetIdentifier("identifierB");

        assertFalse(this.matcher.match(gdiDeEnv, getJobWrapper(), catalogueSubset));
    }

    @Test
    public void testIsMatchAbstractDeEnvelopeAbstractSubsetDefinition() {
        AbstractDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        AbstractSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        assertTrue(this.matcher.match(gdiDeEnv, getJobWrapper(), catalogueSubset));
    }

    @Test
    public void testIsMatchIncompatibleTypes() {
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CopernicusSubsetDefinition copernicusSubset = getCopernicusSubsetDefinition();

        assertFalse(this.matcher.match(gdiDeEnv, getJobWrapper(), copernicusSubset));
    }

    @Test
    public void testIsMatchSourceTypeNotSet() {
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        gdiDeEnv.setSourceType(null);
        catalogueSubset.setSourceType(null);

        assertFalse(this.matcher.match(gdiDeEnv, getJobWrapper(), catalogueSubset));
    }

    @Test
    public void testMatchTimeFrameDuration() {
        WacodisJobWrapper wrapper = getJobWrapper();
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        wrapper.getJobDefinition().getTemporalCoverage().setDuration("P1D");

        assertFalse(this.matcher.match(gdiDeEnv, wrapper, catalogueSubset));
    }

    @Test
    public void testMatchTimeFrameDataEnvelopeStartBeforeRelevancy() {
        WacodisJobWrapper wrapper = getJobWrapper();
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        gdiDeEnv.getTimeFrame().setEndTime(DateTime.parse("2000-01-01T00:00:00Z"));

        assertFalse(this.matcher.match(gdiDeEnv, wrapper, catalogueSubset));
    }

    @Test
    public void testMatchTimeFrameDataEnvelopeStartAfterRelevancy() {
        WacodisJobWrapper wrapper = getJobWrapper();
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        gdiDeEnv.getTimeFrame().setStartTime(DateTime.parse("2019-01-01T00:00:00Z"));

        assertFalse(this.matcher.match(gdiDeEnv, wrapper, catalogueSubset));
    }

    @Test
    public void testMatchTimeFrameDataEnvelopeStartCoversDuration() {
        WacodisJobWrapper wrapper = getJobWrapper();
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        gdiDeEnv.getTimeFrame().setStartTime(DateTime.parse("2000-01-01T00:00:00Z"));
        gdiDeEnv.getTimeFrame().setEndTime(DateTime.parse("2020-01-01T00:00:00Z"));

        assertTrue(this.matcher.match(gdiDeEnv, wrapper, catalogueSubset));
    }

    @Test
    public void testMatchTimeFrameDataEnvelopeStartPreviousExectution_BeforeRelevancy() {
        WacodisJobWrapper wrapper = getJobWrapper();
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        wrapper.getJobDefinition().getTemporalCoverage().setPreviousExecution(Boolean.TRUE);
        gdiDeEnv.getTimeFrame().setEndTime(DateTime.parse("2017-12-31T00:00:00Z"));

        assertFalse(this.matcher.match(gdiDeEnv, wrapper, catalogueSubset));
    }

    @Test
    public void testMatchTimeFrameDataEnvelopeStartPreviousExectution_AfterExecution() {
        WacodisJobWrapper wrapper = getJobWrapper();
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        wrapper.getJobDefinition().getTemporalCoverage().setPreviousExecution(Boolean.TRUE);
        gdiDeEnv.getTimeFrame().setStartTime(DateTime.parse("2018-03-01T00:00:00Z"));

        assertFalse(this.matcher.match(gdiDeEnv, wrapper, catalogueSubset));
    }

    @Test
    @DisplayName("check fail when TemporalCoverage set to PreviousExecution and no execution pattern provided")
    public void testMatchTimeFrameDataEnvelopeStartPreviousExectution_NoPattern() {
        WacodisJobWrapper wrapper = getJobWrapper();
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        wrapper.getJobDefinition().getTemporalCoverage().setPreviousExecution(Boolean.TRUE);
        wrapper.getJobDefinition().getExecution().setPattern(null);

        assertThrows(java.lang.IllegalArgumentException.class, () -> this.matcher.match(gdiDeEnv, wrapper, catalogueSubset));
    }

    @Test
    @DisplayName("check fail when TemporalCoverage set to PreviousExecution and no execution information provided")
    public void testMatchTimeFrameDataEnvelopeStartPreviousExectution_NoExecution() {
        WacodisJobWrapper wrapper = getJobWrapper();
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        wrapper.getJobDefinition().getTemporalCoverage().setPreviousExecution(Boolean.TRUE);
        wrapper.getJobDefinition().setExecution(null);

        assertThrows(java.lang.IllegalArgumentException.class, () -> this.matcher.match(gdiDeEnv, wrapper, catalogueSubset));
    }

    @Test
    public void testMatchAreaOfInterest_CompleteOverlap() {
        WacodisJobWrapper wrapper = getJobWrapper();
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();
        //default minimum overlap percentage = 100%
        assertTrue(this.matcher.match(gdiDeEnv, wrapper, catalogueSubset));
    }

    @Test
    public void testMatchAreaOfInterest_Disjoint() {
        BasicDataEnvelopeMatcher basicMatcher = new BasicDataEnvelopeMatcher();
        basicMatcher.setMinimumOverlapPercentage(0.1f);
        WacodisJobWrapper wrapper = getJobWrapper();
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.addExtentItem(15.0f);
        aoi.addExtentItem(15.0f);
        aoi.addExtentItem(20.0f);
        aoi.addExtentItem(20.0f);
        gdiDeEnv.setAreaOfInterest(aoi);

        assertFalse(basicMatcher.match(gdiDeEnv, wrapper, catalogueSubset));
    }

    @Test
    public void testMatchAreaOfInterest_PartialOverlap() {
        BasicDataEnvelopeMatcher basicMatcher = new BasicDataEnvelopeMatcher();
        basicMatcher.setMinimumOverlapPercentage(50.0f);
        WacodisJobWrapper wrapper = getJobWrapper();
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.addExtentItem(5.0f);
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(15.0f);
        aoi.addExtentItem(10.0f);
        gdiDeEnv.setAreaOfInterest(aoi); //create 50% overlap 

        assertTrue(basicMatcher.match(gdiDeEnv, wrapper, catalogueSubset));
    }

    @Test
    public void testMatchAreaOfInterest_PartialOverlap_higherThreshold() {
        BasicDataEnvelopeMatcher basicMatcher = new BasicDataEnvelopeMatcher();
        basicMatcher.setMinimumOverlapPercentage(70.0f);
        WacodisJobWrapper wrapper = getJobWrapper();
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.addExtentItem(5.0f);
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(15.0f);
        aoi.addExtentItem(10.0f);
        gdiDeEnv.setAreaOfInterest(aoi); //create 50% overlap 

        assertFalse(basicMatcher.match(gdiDeEnv, wrapper, catalogueSubset));
    }

    private CopernicusDataEnvelope getCopernicusDataEnvelope() {
        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(10.0f);
        aoi.addExtentItem(10.0f);

        AbstractDataEnvelopeTimeFrame tf = new AbstractDataEnvelopeTimeFrame();
        tf.setStartTime(new DateTime(DateTime.parse("2018-01-01T07:30:15Z")));
        tf.setEndTime(new DateTime(DateTime.parse("2018-01-02T07:30:15Z")));

        CopernicusDataEnvelope copernicusEnv = new CopernicusDataEnvelope();
        copernicusEnv.setSourceType(AbstractDataEnvelope.SourceTypeEnum.COPERNICUSDATAENVELOPE);
        copernicusEnv.setCloudCoverage(20.5f);
        copernicusEnv.setSatellite(CopernicusDataEnvelope.SatelliteEnum._1);
        copernicusEnv.setDatasetId("testID");
        copernicusEnv.setPortal(CopernicusDataEnvelope.PortalEnum.CODE_DE);
        copernicusEnv.setAreaOfInterest(aoi);
        copernicusEnv.setTimeFrame(tf);
        copernicusEnv.setCreated(new DateTime(DateTime.parse("2012-01-01T07:00:35Z")));
        copernicusEnv.setModified(new DateTime(DateTime.parse("2012-01-01T07:30:15Z")));

        return copernicusEnv;
    }

    private GdiDeDataEnvelope getGdiDeDataEnvelope() {
        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(10.0f);
        aoi.addExtentItem(10.0f);

        AbstractDataEnvelopeTimeFrame tf = new AbstractDataEnvelopeTimeFrame();
        tf.setStartTime(new DateTime(DateTime.parse("2018-01-01T07:30:15Z")));
        tf.setEndTime(new DateTime(DateTime.parse("2018-01-02T07:30:15Z")));

        GdiDeDataEnvelope gdiDeEnv = new GdiDeDataEnvelope();
        gdiDeEnv.setSourceType(AbstractDataEnvelope.SourceTypeEnum.GDIDEDATAENVELOPE);
        gdiDeEnv.setCatalougeUrl("www.geoportal.de");
        gdiDeEnv.setRecordRefId("testID");
        gdiDeEnv.setAreaOfInterest(aoi);
        gdiDeEnv.setTimeFrame(tf);
        gdiDeEnv.setCreated(new DateTime(DateTime.parse("2012-01-01T07:00:35Z")));
        gdiDeEnv.setModified(new DateTime(DateTime.parse("2012-01-01T07:30:15Z")));

        return gdiDeEnv;
    }

    private SensorWebDataEnvelope getSensorWebDataEnvelope() {
        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(10.0f);
        aoi.addExtentItem(10.0f);

        AbstractDataEnvelopeTimeFrame tf = new AbstractDataEnvelopeTimeFrame();
        tf.setStartTime(new DateTime(DateTime.parse("2018-01-01T07:30:15Z")));
        tf.setEndTime(new DateTime(DateTime.parse("2018-01-02T07:30:15Z")));

        SensorWebDataEnvelope sensorWebEnv = new SensorWebDataEnvelope();
        sensorWebEnv.setSourceType(AbstractDataEnvelope.SourceTypeEnum.SENSORWEBDATAENVELOPE);
        sensorWebEnv.setFeatureOfInterest("testFeature");
        sensorWebEnv.setObservedProperty("testProperty");
        sensorWebEnv.setServiceUrl("www.example.com");
        sensorWebEnv.setOffering("testOffering");
        sensorWebEnv.setProcedure("testProcedure");
        sensorWebEnv.setAreaOfInterest(aoi);
        sensorWebEnv.setTimeFrame(tf);
        sensorWebEnv.setCreated(new DateTime(DateTime.parse("2012-01-01T07:00:35Z")));
        sensorWebEnv.setModified(new DateTime(DateTime.parse("2012-01-01T07:30:15Z")));

        return sensorWebEnv;
    }

    private CatalogueSubsetDefinition getCatalogueSubsetDefinition() {
        CatalogueSubsetDefinition catalogueSubset = new CatalogueSubsetDefinition();
        catalogueSubset.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.CATALOGUESUBSETDEFINITION);
        catalogueSubset.setIdentifier("testID");
        catalogueSubset.setDatasetIdentifier("testID");
        catalogueSubset.setServiceUrl("www.geoportal.de");

        return catalogueSubset;
    }

    private SensorWebSubsetDefinition getSensorWebSubsetDefinition() {
        SensorWebSubsetDefinition sensorWebSubset = new SensorWebSubsetDefinition();
        sensorWebSubset.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.SENSORWEBSUBSETDEFINITION);
        sensorWebSubset.setObservedProperty("testProperty");
        sensorWebSubset.setOffering("testOffering");
        sensorWebSubset.setProcedure("testProcedure");
        sensorWebSubset.setServiceUrl("www.example.com");
        sensorWebSubset.setFeatureOfInterest("testFeature");
        sensorWebSubset.setIdentifier("testID");

        return sensorWebSubset;
    }

    private CopernicusSubsetDefinition getCopernicusSubsetDefinition() {
        CopernicusSubsetDefinition copernicusSubset = new CopernicusSubsetDefinition();
        copernicusSubset.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.COPERNICUSSUBSETDEFINITION);
        copernicusSubset.setMaximumCloudCoverage(50.0f);
        copernicusSubset.setSatellite(CopernicusSubsetDefinition.SatelliteEnum._1);
        copernicusSubset.setIdentifier("testID");

        return copernicusSubset;
    }

    private WacodisJobWrapper getJobWrapper() {
        WacodisJobDefinitionTemporalCoverage tempCov = new WacodisJobDefinitionTemporalCoverage();
        tempCov.setDuration("P1M"); //covers the month before execution

        List<AbstractSubsetDefinition> inputs = new ArrayList<>();
        inputs.add(getCatalogueSubsetDefinition());
        inputs.add(getCopernicusSubsetDefinition());
        inputs.add(getSensorWebSubsetDefinition());

        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(10.0f);
        aoi.addExtentItem(10.0f);

        WacodisJobDefinitionExecution execution = new WacodisJobDefinitionExecution();
        execution.setPattern("0 0 1 * *"); //executes on the 1st day of each month (00:00:00)

        WacodisJobDefinition jobDef = new WacodisJobDefinition();
        jobDef.setInputs(inputs);
        jobDef.setTemporalCoverage(tempCov);
        jobDef.setAreaOfInterest(aoi);
        jobDef.setLastFinishedExecution(DateTime.parse("2017-02-01T00:00:00Z"));
        jobDef.setExecution(execution);

        DateTime executionTime = new DateTime(DateTime.parse("2018-02-01T00:00:00Z"));
        WacodisJobWrapper jobWrapper = new WacodisJobWrapper(jobDef, executionTime);

        return jobWrapper;
    }
}
