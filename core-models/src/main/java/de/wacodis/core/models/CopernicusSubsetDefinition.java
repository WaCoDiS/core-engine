package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.wacodis.core.models.AbstractSubsetDefinition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * contains information for defining a subset definition for Copernicus process inputs
 */
@ApiModel(description = "contains information for defining a subset definition for Copernicus process inputs")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-02-06T15:56:49.020+01:00[Europe/Berlin]")

public class CopernicusSubsetDefinition extends AbstractSubsetDefinition implements Serializable {
  private static final long serialVersionUID = 1L;

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

  @JsonProperty("maximumCloudCoverage")
  private Float maximumCloudCoverage = null;

  public CopernicusSubsetDefinition satellite(SatelliteEnum satellite) {
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

  public CopernicusSubsetDefinition maximumCloudCoverage(Float maximumCloudCoverage) {
    this.maximumCloudCoverage = maximumCloudCoverage;
    return this;
  }

  /**
   * Get maximumCloudCoverage
   * @return maximumCloudCoverage
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public Float getMaximumCloudCoverage() {
    return maximumCloudCoverage;
  }

  public void setMaximumCloudCoverage(Float maximumCloudCoverage) {
    this.maximumCloudCoverage = maximumCloudCoverage;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CopernicusSubsetDefinition copernicusSubsetDefinition = (CopernicusSubsetDefinition) o;
    return Objects.equals(this.satellite, copernicusSubsetDefinition.satellite) &&
        Objects.equals(this.maximumCloudCoverage, copernicusSubsetDefinition.maximumCloudCoverage) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(satellite, maximumCloudCoverage, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CopernicusSubsetDefinition {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    satellite: ").append(toIndentedString(satellite)).append("\n");
    sb.append("    maximumCloudCoverage: ").append(toIndentedString(maximumCloudCoverage)).append("\n");
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

