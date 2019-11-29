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
 * if present, this describe the execution pattern of a WacodisJobDefinition. if not present, the WacodisJobDefinition is treated as a one-time execution. Only one of the properties shall be provided. 
 */
@ApiModel(description = "if present, this describe the execution pattern of a WacodisJobDefinition. if not present, the WacodisJobDefinition is treated as a one-time execution. Only one of the properties shall be provided. ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-11-29T15:03:40.466+01:00[Europe/Berlin]")

public class WacodisJobDefinitionExecution  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("pattern")
  private String pattern = null;

  @JsonProperty("event")
  private Object event = null;

  public WacodisJobDefinitionExecution pattern(String pattern) {
    this.pattern = pattern;
    return this;
  }

  /**
   * The format follows the cron syntax with the first five fields (see: http://pubs.opengroup.org/onlinepubs/9699919799/utilities/crontab.html#tag_20_25_07) 
   * @return pattern
  **/
  @ApiModelProperty(value = "The format follows the cron syntax with the first five fields (see: http://pubs.opengroup.org/onlinepubs/9699919799/utilities/crontab.html#tag_20_25_07) ")


  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public WacodisJobDefinitionExecution event(Object event) {
    this.event = event;
    return this;
  }

  /**
   * the execution is scheduled by the occurence of an event (e.g. new data available); WIP - format to be defined. 
   * @return event
  **/
  @ApiModelProperty(value = "the execution is scheduled by the occurence of an event (e.g. new data available); WIP - format to be defined. ")


  public Object getEvent() {
    return event;
  }

  public void setEvent(Object event) {
    this.event = event;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WacodisJobDefinitionExecution wacodisJobDefinitionExecution = (WacodisJobDefinitionExecution) o;
    return Objects.equals(this.pattern, wacodisJobDefinitionExecution.pattern) &&
        Objects.equals(this.event, wacodisJobDefinitionExecution.event);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pattern, event);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisJobDefinitionExecution {\n");
    
    sb.append("    pattern: ").append(toIndentedString(pattern)).append("\n");
    sb.append("    event: ").append(toIndentedString(event)).append("\n");
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

