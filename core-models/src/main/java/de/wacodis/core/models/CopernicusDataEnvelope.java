package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.core.models.AbstractDataEnvelopeTimeFrame;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * describes specific metadata information about a Copernicus dataset
 */
@ApiModel(description = "describes specific metadata information about a Copernicus dataset")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-26T17:56:55.611+01:00[Europe/Berlin]")

public class CopernicusDataEnvelope extends AbstractDataEnvelope implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("datasetId")
  private Object datasetId = null;

  @JsonProperty("footprint")
  private String footprint = null;

  /**
   * Gets or Sets satellite
   */
  public enum SatelliteEnum {
    _1("sentinel-1"),
    
    _2("sentinel-2"),
    
    _3("sentinel-3");

    private String value;

    SatelliteEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static SatelliteEnum fromValue(String text) {
      for (SatelliteEnum b : SatelliteEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("satellite")
  private SatelliteEnum satellite = null;

  @JsonProperty("instrument")
  private String instrument = null;

  @JsonProperty("sensorMode")
  private String sensorMode = null;

  @JsonProperty("productType")
  private String productType = null;

  @JsonProperty("productLevel")
  private String productLevel = null;

  @JsonProperty("cloudCoverage")
  private Float cloudCoverage = null;

  /**
   * Gets or Sets portal
   */
  public enum PortalEnum {
    CODE_DE("Code-DE"),
    
    SENTINEL_HUB("Sentinel-Hub");

    private String value;

    PortalEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static PortalEnum fromValue(String text) {
      for (PortalEnum b : PortalEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("portal")
  private PortalEnum portal = null;

  public CopernicusDataEnvelope datasetId(Object datasetId) {
    this.datasetId = datasetId;
    return this;
  }

  /**
   * the id of the
   * @return datasetId
  **/
  @ApiModelProperty(required = true, value = "the id of the")
  @NotNull


  public Object getDatasetId() {
    return datasetId;
  }

  public void setDatasetId(Object datasetId) {
    this.datasetId = datasetId;
  }

  public CopernicusDataEnvelope footprint(String footprint) {
    this.footprint = footprint;
    return this;
  }

  /**
   * the footprint representing the spatial coverage without NODATA values of the Copernicus dataset as GeoJSON string 
   * @return footprint
  **/
  @ApiModelProperty(value = "the footprint representing the spatial coverage without NODATA values of the Copernicus dataset as GeoJSON string ")


  public String getFootprint() {
    return footprint;
  }

  public void setFootprint(String footprint) {
    this.footprint = footprint;
  }

  public CopernicusDataEnvelope satellite(SatelliteEnum satellite) {
    this.satellite = satellite;
    return this;
  }

  /**
   * Get satellite
   * @return satellite
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public SatelliteEnum getSatellite() {
    return satellite;
  }

  public void setSatellite(SatelliteEnum satellite) {
    this.satellite = satellite;
  }

  public CopernicusDataEnvelope instrument(String instrument) {
    this.instrument = instrument;
    return this;
  }

  /**
   * abbreviation for the instrument that is carried by a Sentinel mission [SAR (Sentinel-1 Synthetic Aperture Radar), MSI ( Sentinel-2 MultiSpectral Instrument)] 
   * @return instrument
  **/
  @ApiModelProperty(value = "abbreviation for the instrument that is carried by a Sentinel mission [SAR (Sentinel-1 Synthetic Aperture Radar), MSI ( Sentinel-2 MultiSpectral Instrument)] ")


  public String getInstrument() {
    return instrument;
  }

  public void setInstrument(String instrument) {
    this.instrument = instrument;
  }

  public CopernicusDataEnvelope sensorMode(String sensorMode) {
    this.sensorMode = sensorMode;
    return this;
  }

  /**
   * abbreviation for sensor mode used by Sentinel-1 satellite instruments [EW (Extra Wide), IW (Interferometric Wide), SM (Stripmap), WV (Wave)] 
   * @return sensorMode
  **/
  @ApiModelProperty(value = "abbreviation for sensor mode used by Sentinel-1 satellite instruments [EW (Extra Wide), IW (Interferometric Wide), SM (Stripmap), WV (Wave)] ")


  public String getSensorMode() {
    return sensorMode;
  }

  public void setSensorMode(String sensorMode) {
    this.sensorMode = sensorMode;
  }

  public CopernicusDataEnvelope productType(String productType) {
    this.productType = productType;
    return this;
  }

  /**
   * abbreviation for the product type of the Copernicus subset [RAW (raw data), GRD (Ground Range Detected), SLC (Single Look Complex), OCN (Ocean), L1C (Sentinel-2 Level 1C), L2A (Sentinel-2 Level 2A)] 
   * @return productType
  **/
  @ApiModelProperty(value = "abbreviation for the product type of the Copernicus subset [RAW (raw data), GRD (Ground Range Detected), SLC (Single Look Complex), OCN (Ocean), L1C (Sentinel-2 Level 1C), L2A (Sentinel-2 Level 2A)] ")


  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public CopernicusDataEnvelope productLevel(String productLevel) {
    this.productLevel = productLevel;
    return this;
  }

  /**
   * abbreviation for the level of the Copernicus product (e.g) [LEVEL0, LEVEL1, LEVEL2, LEVEL1C, LEVEL2A] 
   * @return productLevel
  **/
  @ApiModelProperty(value = "abbreviation for the level of the Copernicus product (e.g) [LEVEL0, LEVEL1, LEVEL2, LEVEL1C, LEVEL2A] ")


  public String getProductLevel() {
    return productLevel;
  }

  public void setProductLevel(String productLevel) {
    this.productLevel = productLevel;
  }

  public CopernicusDataEnvelope cloudCoverage(Float cloudCoverage) {
    this.cloudCoverage = cloudCoverage;
    return this;
  }

  /**
   * Get cloudCoverage
   * @return cloudCoverage
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public Float getCloudCoverage() {
    return cloudCoverage;
  }

  public void setCloudCoverage(Float cloudCoverage) {
    this.cloudCoverage = cloudCoverage;
  }

  public CopernicusDataEnvelope portal(PortalEnum portal) {
    this.portal = portal;
    return this;
  }

  /**
   * Get portal
   * @return portal
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public PortalEnum getPortal() {
    return portal;
  }

  public void setPortal(PortalEnum portal) {
    this.portal = portal;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CopernicusDataEnvelope copernicusDataEnvelope = (CopernicusDataEnvelope) o;
    return Objects.equals(this.datasetId, copernicusDataEnvelope.datasetId) &&
        Objects.equals(this.footprint, copernicusDataEnvelope.footprint) &&
        Objects.equals(this.satellite, copernicusDataEnvelope.satellite) &&
        Objects.equals(this.instrument, copernicusDataEnvelope.instrument) &&
        Objects.equals(this.sensorMode, copernicusDataEnvelope.sensorMode) &&
        Objects.equals(this.productType, copernicusDataEnvelope.productType) &&
        Objects.equals(this.productLevel, copernicusDataEnvelope.productLevel) &&
        Objects.equals(this.cloudCoverage, copernicusDataEnvelope.cloudCoverage) &&
        Objects.equals(this.portal, copernicusDataEnvelope.portal) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetId, footprint, satellite, instrument, sensorMode, productType, productLevel, cloudCoverage, portal, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CopernicusDataEnvelope {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    datasetId: ").append(toIndentedString(datasetId)).append("\n");
    sb.append("    footprint: ").append(toIndentedString(footprint)).append("\n");
    sb.append("    satellite: ").append(toIndentedString(satellite)).append("\n");
    sb.append("    instrument: ").append(toIndentedString(instrument)).append("\n");
    sb.append("    sensorMode: ").append(toIndentedString(sensorMode)).append("\n");
    sb.append("    productType: ").append(toIndentedString(productType)).append("\n");
    sb.append("    productLevel: ").append(toIndentedString(productLevel)).append("\n");
    sb.append("    cloudCoverage: ").append(toIndentedString(cloudCoverage)).append("\n");
    sb.append("    portal: ").append(toIndentedString(portal)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

