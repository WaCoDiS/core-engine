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
import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.*;

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
    public void testIsMatchCopernicusDataEnvelopeCopernicusSubsetDefinition() {
        CopernicusDataEnvelope copernicusEnv = getCopernicusDataEnvelope();
        CopernicusSubsetDefinition copernicusSubset = getCopernicusSubsetDefinition();

        assertTrue(this.matcher.match(copernicusEnv, copernicusSubset));
    }

    @Test
    public void testIsMatchCopernicusDataEnvelopeCopernicusSubsetDefinitionCloudCoverage() {
        CopernicusDataEnvelope copernicusEnv = getCopernicusDataEnvelope();
        CopernicusSubsetDefinition copernicusSubset = getCopernicusSubsetDefinition();

        copernicusEnv.setCloudCoverage(50.0f);
        copernicusSubset.setMaximumCloudCoverage(5.0f);

        assertFalse(this.matcher.match(copernicusEnv, copernicusSubset));
    }

    @Test
    public void testIsMatchCopernicusDataEnvelopeCopernicusSubsetDefinitionIdentifier() {
        CopernicusDataEnvelope copernicusEnv = getCopernicusDataEnvelope();
        CopernicusSubsetDefinition copernicusSubset = getCopernicusSubsetDefinition();

        copernicusEnv.setDatasetId("identifierA");
        copernicusSubset.setIdentifier("identifierB");

        assertFalse(this.matcher.match(copernicusEnv, copernicusSubset));
    }

    @Test
    public void testIsMatchSensorWebDataEnvelopeSensorWebSubsetDefinition() {
        SensorWebDataEnvelope sensorWebEnv = getSensorWebDataEnvelope();
        SensorWebSubsetDefinition sensorWebSubset = getSensorWebSubsetDefinition();

        assertTrue(this.matcher.match(sensorWebEnv, sensorWebSubset));
    }

    @Test
    public void testIsMatchSensorWebDataEnvelopeSensorWebSubsetDefinitionOffering() {
        SensorWebDataEnvelope sensorWebEnv = getSensorWebDataEnvelope();
        SensorWebSubsetDefinition sensorWebSubset = getSensorWebSubsetDefinition();

        sensorWebEnv.setOffering("offeringA");
        sensorWebSubset.setOffering("offeringB");

        assertFalse(this.matcher.match(sensorWebEnv, sensorWebSubset));
    }

    @Test
    public void testIsMatchGdiDeEnvelopeCatalogueSubsetDefinition() {
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        assertTrue(this.matcher.match(gdiDeEnv, catalogueSubset));
    }

    @Test
    public void testIsMatchGdiDeEnvelopeCatalogueSubsetDefinitionIdentifier() {
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        gdiDeEnv.setRecordRefId("identifierA");
        catalogueSubset.setDatasetIdentifier("identifierB");

        assertFalse(this.matcher.match(gdiDeEnv, catalogueSubset));
    }

    @Test
    public void testIsMatchAbstractDeEnvelopeAbstractSubsetDefinition() {
        AbstractDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        AbstractSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        assertTrue(this.matcher.match(gdiDeEnv, catalogueSubset));
    }

    @Test
    public void testIsMatchIncompatibleTypes() {
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CopernicusSubsetDefinition copernicusSubset = getCopernicusSubsetDefinition();

        assertFalse(this.matcher.match(gdiDeEnv, copernicusSubset));
    }

    @Test
    public void testIsMatchSourceTypeNotSet() {
        GdiDeDataEnvelope gdiDeEnv = getGdiDeDataEnvelope();
        CatalogueSubsetDefinition catalogueSubset = getCatalogueSubsetDefinition();

        gdiDeEnv.setSourceType(null);
        catalogueSubset.setSourceType(null);

        assertFalse(this.matcher.match(gdiDeEnv, catalogueSubset));
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
}
