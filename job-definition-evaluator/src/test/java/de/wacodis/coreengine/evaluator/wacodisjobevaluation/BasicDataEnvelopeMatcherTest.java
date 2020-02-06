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
import de.wacodis.core.models.ArcGISImageServerBackend;
import de.wacodis.core.models.CatalogueSubsetDefinition;
import de.wacodis.core.models.CopernicusDataEnvelope;
import de.wacodis.core.models.CopernicusSubsetDefinition;
import de.wacodis.core.models.DwdDataEnvelope;
import de.wacodis.core.models.DwdSubsetDefinition;
import de.wacodis.core.models.GdiDeDataEnvelope;
import de.wacodis.core.models.ProductBackend;
import de.wacodis.core.models.SensorWebDataEnvelope;
import de.wacodis.core.models.SensorWebSubsetDefinition;
import de.wacodis.core.models.WacodisJobDefinition;
import de.wacodis.core.models.WacodisJobDefinitionExecution;
import de.wacodis.core.models.WacodisJobDefinitionTemporalCoverage;
import de.wacodis.core.models.WacodisProductDataEnvelope;
import de.wacodis.core.models.WacodisProductSubsetDefinition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
    public void testSetMinimumOverlapPercentage() {
        BasicDataEnvelopeMatcher basicMatcher = new BasicDataEnvelopeMatcher();

        basicMatcher.setMinimumOverlapPercentage(0.0f);
        assertEquals(0.0f, basicMatcher.getMinimumOverlapPercentage());
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
    public void testMatchDwdDataEnvelope() {
        DwdDataEnvelope dwdEnvelope = getDwdDataEnvelope();
        DwdSubsetDefinition dwdSubset = getDwdSubsetDefinition();
        WacodisJobWrapper wrapper = getJobWrapper();

        assertTrue(this.matcher.match(dwdEnvelope, wrapper, dwdSubset));
    }

    @Test
    public void testMatchDwdDataEnvelope_False() {
        DwdDataEnvelope dwdEnvelope = getDwdDataEnvelope();
        DwdSubsetDefinition dwdSubset = getDwdSubsetDefinition();
        WacodisJobWrapper wrapper = getJobWrapper();

        dwdEnvelope.setLayerName("layer2");
        assertFalse(this.matcher.match(dwdEnvelope, wrapper, dwdSubset));
    }

    @Test
    public void testMatchWacodisProductDataEnvelope_DifferentProductType() {
        WacodisProductDataEnvelope productEnvelope = getProductDataEnvelope();
        WacodisProductSubsetDefinition productSubset = getProductSubsetDefinition();
        WacodisJobWrapper wrapper = getJobWrapper();

        productEnvelope.setProductType("land cover classification");
        productSubset.setProductType("unknown product type");

        assertFalse(this.matcher.match(productEnvelope, wrapper, productSubset));
    }

    @Test
    public void testMatchWacodisProductDataEnvelope_EqualProductType() {
        WacodisProductDataEnvelope productEnvelope = getProductDataEnvelope();
        WacodisProductSubsetDefinition productSubset = getProductSubsetDefinition();
        WacodisJobWrapper wrapper = getJobWrapper();

        productEnvelope.setProductType("land cover classification");
        productSubset.setProductType("land cover classification");

        assertTrue(this.matcher.match(productEnvelope, wrapper, productSubset));
    }

    @Test
    public void testMatchWacodisProductDataEnvelope_DifferentBackendType() {
        WacodisProductDataEnvelope productEnvelope = getProductDataEnvelope();
        WacodisProductSubsetDefinition productSubset = getProductSubsetDefinition();
        WacodisJobWrapper wrapper = getJobWrapper();

        productEnvelope.getServiceDefinition().setBackendType(ProductBackend.ARCGISIMAGESERVERBACKEND);
        productSubset.setBackendType(ProductBackend.GEOSERVERBACKEND);

        assertFalse(this.matcher.match(productEnvelope, wrapper, productSubset));
    }

    @Test
    public void testMatchWacodisProductDataEnvelope_EqualBackendType() {
        WacodisProductDataEnvelope productEnvelope = getProductDataEnvelope();
        WacodisProductSubsetDefinition productSubset = getProductSubsetDefinition();
        WacodisJobWrapper wrapper = getJobWrapper();

        productEnvelope.getServiceDefinition().setBackendType(ProductBackend.ARCGISIMAGESERVERBACKEND);
        productSubset.setBackendType(ProductBackend.ARCGISIMAGESERVERBACKEND);
        
        assertTrue(this.matcher.match(productEnvelope, wrapper, productSubset));
    }

    @Test
    public void testMatchWacodisProductDataEnvelope_WithProductType_False() {
        WacodisProductDataEnvelope productEnvelope = getProductDataEnvelope();
        WacodisProductSubsetDefinition productSubset = getProductSubsetDefinition();
        WacodisJobWrapper wrapper = getJobWrapper();

        productSubset.setProductType("someType");
        assertFalse(this.matcher.match(productEnvelope, wrapper, productSubset));
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

    private DwdSubsetDefinition getDwdSubsetDefinition() {
        DwdSubsetDefinition dwdSubset = new DwdSubsetDefinition();
        dwdSubset.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.DWDSUBSETDEFINITION);
        dwdSubset.setLayerName("layer1");
        dwdSubset.setServiceUrl("http://www.example.com");
        dwdSubset.setIdentifier("dwdsubset");

        return dwdSubset;
    }

    private DwdDataEnvelope getDwdDataEnvelope() {
        DwdDataEnvelope dwdEnvelope = new DwdDataEnvelope();
        dwdEnvelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.DWDDATAENVELOPE);
        dwdEnvelope.setLayerName("layer1");
        dwdEnvelope.setServiceUrl("http://www.example.com");
        dwdEnvelope.setParameter("param1");
        dwdEnvelope.setModified(new DateTime(DateTime.parse("2012-01-01T07:30:15Z")));
        dwdEnvelope.setCreated(new DateTime(DateTime.parse("2012-01-01T07:00:35Z")));

        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(10.0f);
        aoi.addExtentItem(10.0f);
        dwdEnvelope.setAreaOfInterest(aoi); //create 50% overlap 

        AbstractDataEnvelopeTimeFrame tf = new AbstractDataEnvelopeTimeFrame();
        tf.setStartTime(new DateTime(DateTime.parse("2018-01-01T07:30:15Z")));
        tf.setEndTime(new DateTime(DateTime.parse("2018-01-02T07:30:15Z")));
        dwdEnvelope.setTimeFrame(tf);

        return dwdEnvelope;
    }

    private WacodisProductSubsetDefinition getProductSubsetDefinition() {
        WacodisProductSubsetDefinition productSubset = new WacodisProductSubsetDefinition();
        productSubset.setSourceType(AbstractSubsetDefinition.SourceTypeEnum.WACODISPRODUCTSUBSETDEFINITION);
        productSubset.setBackendType(ProductBackend.ARCGISIMAGESERVERBACKEND);
        productSubset.setProductType("land cover classification");
        productSubset.setIdentifier("testID");

        return productSubset;
    }

    private WacodisProductDataEnvelope getProductDataEnvelope() {
        WacodisProductDataEnvelope productEnvelope = new WacodisProductDataEnvelope();
        productEnvelope.setSourceType(AbstractDataEnvelope.SourceTypeEnum.WACODISPRODUCTDATAENVELOPE);
        productEnvelope.setProductType("land cover classification");

        AbstractDataEnvelopeAreaOfInterest aoi = new AbstractDataEnvelopeAreaOfInterest();
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(0.0f);
        aoi.addExtentItem(10.0f);
        aoi.addExtentItem(10.0f);
        productEnvelope.setAreaOfInterest(aoi); //create 50% overlap 

        AbstractDataEnvelopeTimeFrame tf = new AbstractDataEnvelopeTimeFrame();
        tf.setStartTime(new DateTime(DateTime.parse("2018-01-01T07:30:15Z")));
        tf.setEndTime(new DateTime(DateTime.parse("2018-01-02T07:30:15Z")));
        productEnvelope.setTimeFrame(tf);

        ArcGISImageServerBackend serviceDef = new ArcGISImageServerBackend();
        serviceDef.setBackendType(ProductBackend.ARCGISIMAGESERVERBACKEND);
        serviceDef.setBaseUrl("www.example.com");
        serviceDef.setProductCollection("landcover");
        serviceDef.setServiceTypes(Arrays.asList("WmsServer, ImageServer"));
        productEnvelope.setServiceDefinition(serviceDef);

        return productEnvelope;
    }

    private WacodisJobWrapper getJobWrapper() {
        WacodisJobDefinitionTemporalCoverage tempCov = new WacodisJobDefinitionTemporalCoverage();
        tempCov.setDuration("P1M"); //covers the month before execution

        List<AbstractSubsetDefinition> inputs = new ArrayList<>();
        inputs.add(getCatalogueSubsetDefinition());
        inputs.add(getCopernicusSubsetDefinition());
        inputs.add(getSensorWebSubsetDefinition());
        inputs.add(getDwdSubsetDefinition());

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
        WacodisJobWrapper jobWrapper =new WacodisJobWrapper(new WacodisJobExecutionContext(UUID.randomUUID(), executionTime, 0), jobDef);

        return jobWrapper;
    }
}
