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
 * describes specific metadata information about a dataset from a catalogue service that is part of a SDI
 */
@ApiModel(description = "describes specific metadata information about a dataset from a catalogue service that is part of a SDI")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-02-12T16:14:17.964+01:00[Europe/Berlin]")

public class GdiDeDataEnvelope extends AbstractDataEnvelope implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("catalougeUrl")
  private String catalougeUrl = null;

  @JsonProperty("recordRefId")
  private String recordRefId = null;

  public GdiDeDataEnvelope catalougeUrl(String catalougeUrl) {
    this.catalougeUrl = catalougeUrl;
    return this;
  }

  /**
   * URL of the GDI-DE catalogue 
   * @return catalougeUrl
  **/
  @ApiModelProperty(value = "URL of the GDI-DE catalogue ")


  public String getCatalougeUrl() {
    return catalougeUrl;
  }

  public void setCatalougeUrl(String catalougeUrl) {
    this.catalougeUrl = catalougeUrl;
  }

  public GdiDeDataEnvelope recordRefId(String recordRefId) {
    this.recordRefId = recordRefId;
    return this;
  }

  /**
   * the id of the dataset within the GDI-DE catalogue 
   * @return recordRefId
  **/
  @ApiModelProperty(required = true, value = "the id of the dataset within the GDI-DE catalogue ")
  @NotNull


  public String getRecordRefId() {
    return recordRefId;
  }

  public void setRecordRefId(String recordRefId) {
    this.recordRefId = recordRefId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GdiDeDataEnvelope gdiDeDataEnvelope = (GdiDeDataEnvelope) o;
    return Objects.equals(this.catalougeUrl, gdiDeDataEnvelope.catalougeUrl) &&
        Objects.equals(this.recordRefId, gdiDeDataEnvelope.recordRefId) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(catalougeUrl, recordRefId, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GdiDeDataEnvelope {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    catalougeUrl: ").append(toIndentedString(catalougeUrl)).append("\n");
    sb.append("    recordRefId: ").append(toIndentedString(recordRefId)).append("\n");
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

