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
 * definition of temporal coverage for which input data (see SubsetDefinitions) is of relevancy. Only one of the properties shall be provided. 
 */
@ApiModel(description = "definition of temporal coverage for which input data (see SubsetDefinitions) is of relevancy. Only one of the properties shall be provided. ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-26T17:56:55.611+01:00[Europe/Berlin]")

public class WacodisJobDefinitionTemporalCoverage  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("duration")
  private String duration = null;

  @JsonProperty("previousExecution")
  private Boolean previousExecution = null;

  @JsonProperty("offset")
  private String offset = null;

  public WacodisJobDefinitionTemporalCoverage duration(String duration) {
    this.duration = duration;
    return this;
  }

  /**
   * the duration in ISO8601 duration format (https://en.wikipedia.org/wiki/ISO_8601#Durations) the processing component will treat the duration as backwards considering optional offset. A duration of \"P1M\" will cover the last month before execution (without offset). This temporal coverage might be overriden by input specifc temporal coverage (see AbstractSubsetDefinition) 
   * @return duration
  **/
  @ApiModelProperty(value = "the duration in ISO8601 duration format (https://en.wikipedia.org/wiki/ISO_8601#Durations) the processing component will treat the duration as backwards considering optional offset. A duration of \"P1M\" will cover the last month before execution (without offset). This temporal coverage might be overriden by input specifc temporal coverage (see AbstractSubsetDefinition) ")


  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public WacodisJobDefinitionTemporalCoverage previousExecution(Boolean previousExecution) {
    this.previousExecution = previousExecution;
    return this;
  }

  /**
   * input data coverage since the last execution of the job. This is only valid in combination with pattern execution 
   * @return previousExecution
  **/
  @ApiModelProperty(value = "input data coverage since the last execution of the job. This is only valid in combination with pattern execution ")


  public Boolean getPreviousExecution() {
    return previousExecution;
  }

  public void setPreviousExecution(Boolean previousExecution) {
    this.previousExecution = previousExecution;
  }

  public WacodisJobDefinitionTemporalCoverage offset(String offset) {
    this.offset = offset;
    return this;
  }

  /**
   * the duration in ISO8601 duration format (https://en.wikipedia.org/wiki/ISO_8601#Durations) the processing component will treat the duration as backwards. A duration of \"P1M\" will cover the last month before execution 
   * @return offset
  **/
  @ApiModelProperty(value = "the duration in ISO8601 duration format (https://en.wikipedia.org/wiki/ISO_8601#Durations) the processing component will treat the duration as backwards. A duration of \"P1M\" will cover the last month before execution ")


  public String getOffset() {
    return offset;
  }

  public void setOffset(String offset) {
    this.offset = offset;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WacodisJobDefinitionTemporalCoverage wacodisJobDefinitionTemporalCoverage = (WacodisJobDefinitionTemporalCoverage) o;
    return Objects.equals(this.duration, wacodisJobDefinitionTemporalCoverage.duration) &&
        Objects.equals(this.previousExecution, wacodisJobDefinitionTemporalCoverage.previousExecution) &&
        Objects.equals(this.offset, wacodisJobDefinitionTemporalCoverage.offset);
  }

  @Override
  public int hashCode() {
    return Objects.hash(duration, previousExecution, offset);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisJobDefinitionTemporalCoverage {\n");
    
    sb.append("    duration: ").append(toIndentedString(duration)).append("\n");
    sb.append("    previousExecution: ").append(toIndentedString(previousExecution)).append("\n");
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
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

