package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * input specific definition of temporal coverage for which dat is of relevancy. Optinonal, if not provided temporalCoverage of WacodisJobDefinition must be considered 
 */
@ApiModel(description = "input specific definition of temporal coverage for which dat is of relevancy. Optinonal, if not provided temporalCoverage of WacodisJobDefinition must be considered ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-09T12:32:19.982+02:00[Europe/Berlin]")

public class AbstractSubsetDefinitionTemporalCoverage  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("offset")
  private String offset = null;

  @JsonProperty("duration")
  private String duration = null;

  public AbstractSubsetDefinitionTemporalCoverage offset(String offset) {
    this.offset = offset;
    return this;
  }

  /**
   * the duration in ISO8601 duration format (https://en.wikipedia.org/wiki/ISO_8601#Durations) the processing component will treat the duration as backwards from the scheduled time of execution. 
   * @return offset
  **/
  @ApiModelProperty(value = "the duration in ISO8601 duration format (https://en.wikipedia.org/wiki/ISO_8601#Durations) the processing component will treat the duration as backwards from the scheduled time of execution. ")


  public String getOffset() {
    return offset;
  }

  public void setOffset(String offset) {
    this.offset = offset;
  }

  public AbstractSubsetDefinitionTemporalCoverage duration(String duration) {
    this.duration = duration;
    return this;
  }

  /**
   * the duration in ISO8601 duration format (https://en.wikipedia.org/wiki/ISO_8601#Durations) the processing component will treat the duration as backwards from the scheduled time of execution considering optional offset 
   * @return duration
  **/
  @ApiModelProperty(value = "the duration in ISO8601 duration format (https://en.wikipedia.org/wiki/ISO_8601#Durations) the processing component will treat the duration as backwards from the scheduled time of execution considering optional offset ")


  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractSubsetDefinitionTemporalCoverage abstractSubsetDefinitionTemporalCoverage = (AbstractSubsetDefinitionTemporalCoverage) o;
    return Objects.equals(this.offset, abstractSubsetDefinitionTemporalCoverage.offset) &&
        Objects.equals(this.duration, abstractSubsetDefinitionTemporalCoverage.duration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(offset, duration);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AbstractSubsetDefinitionTemporalCoverage {\n");
    
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
    sb.append("    duration: ").append(toIndentedString(duration)).append("\n");
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

