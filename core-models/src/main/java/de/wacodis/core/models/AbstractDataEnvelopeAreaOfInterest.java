package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * AbstractDataEnvelopeAreaOfInterest
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-12-20T13:50:52.962+01:00[Europe/Berlin]")

public class AbstractDataEnvelopeAreaOfInterest  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("extent")
  @Valid
  private List<Float> extent = null;

  public AbstractDataEnvelopeAreaOfInterest extent(List<Float> extent) {
    this.extent = extent;
    return this;
  }

  public AbstractDataEnvelopeAreaOfInterest addExtentItem(Float extentItem) {
    if (this.extent == null) {
      this.extent = new ArrayList<Float>();
    }
    this.extent.add(extentItem);
    return this;
  }

  /**
   * the coordinates, using EPSG:4326, (in analogy to GeoJSON bbox) in the order \"southwesterly point followed by more northeasterly point\". Schema is [minLon, minLat, maxLon, maxLat] 
   * @return extent
  **/
  @ApiModelProperty(value = "the coordinates, using EPSG:4326, (in analogy to GeoJSON bbox) in the order \"southwesterly point followed by more northeasterly point\". Schema is [minLon, minLat, maxLon, maxLat] ")

@Size(min=4,max=4) 
  public List<Float> getExtent() {
    return extent;
  }

  public void setExtent(List<Float> extent) {
    this.extent = extent;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractDataEnvelopeAreaOfInterest abstractDataEnvelopeAreaOfInterest = (AbstractDataEnvelopeAreaOfInterest) o;
    return Objects.equals(this.extent, abstractDataEnvelopeAreaOfInterest.extent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(extent);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AbstractDataEnvelopeAreaOfInterest {\n");
    
    sb.append("    extent: ").append(toIndentedString(extent)).append("\n");
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

