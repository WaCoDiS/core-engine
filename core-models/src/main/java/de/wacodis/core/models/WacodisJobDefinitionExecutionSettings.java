package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * WacodisJobDefinitionExecutionSettings
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-29T15:53:35.626+02:00[Europe/Berlin]")

public class WacodisJobDefinitionExecutionSettings  implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   *  if multiple input data sets are available: all: all inputs will be included in one processing request, split: job will be splitted in multiple processing requests (split by pivotal input), best: only one processing request for input data set which is considered the best (by Data Access) (best input is selected for pivotalInput)
   */
  public enum ExecutionModeEnum {
    ALL("all"),
    
    SPLIT("split"),
    
    BEST("best");

    private String value;

    ExecutionModeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ExecutionModeEnum fromValue(String text) {
      for (ExecutionModeEnum b : ExecutionModeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("executionMode")
  private ExecutionModeEnum executionMode = ExecutionModeEnum.BEST;

  @JsonProperty("pivotalInput")
  private String pivotalInput = null;

  @JsonProperty("timeout_millies")
  private Long timeoutMillies = null;

  public WacodisJobDefinitionExecutionSettings executionMode(ExecutionModeEnum executionMode) {
    this.executionMode = executionMode;
    return this;
  }

  /**
   *  if multiple input data sets are available: all: all inputs will be included in one processing request, split: job will be splitted in multiple processing requests (split by pivotal input), best: only one processing request for input data set which is considered the best (by Data Access) (best input is selected for pivotalInput)
   * @return executionMode
  **/
  @ApiModelProperty(value = " if multiple input data sets are available: all: all inputs will be included in one processing request, split: job will be splitted in multiple processing requests (split by pivotal input), best: only one processing request for input data set which is considered the best (by Data Access) (best input is selected for pivotalInput)")


  public ExecutionModeEnum getExecutionMode() {
    return executionMode;
  }

  public void setExecutionMode(ExecutionModeEnum executionMode) {
    this.executionMode = executionMode;
  }

  public WacodisJobDefinitionExecutionSettings pivotalInput(String pivotalInput) {
    this.pivotalInput = pivotalInput;
    return this;
  }

  /**
   * only applicable if executionMode is 'split' or 'best',  must match a input identifier (see AbstractSubsetDefinition.identifier)  if not provided first input of type CopernicusSubsetDefinition is assumed 
   * @return pivotalInput
  **/
  @ApiModelProperty(value = "only applicable if executionMode is 'split' or 'best',  must match a input identifier (see AbstractSubsetDefinition.identifier)  if not provided first input of type CopernicusSubsetDefinition is assumed ")


  public String getPivotalInput() {
    return pivotalInput;
  }

  public void setPivotalInput(String pivotalInput) {
    this.pivotalInput = pivotalInput;
  }

  public WacodisJobDefinitionExecutionSettings timeoutMillies(Long timeoutMillies) {
    this.timeoutMillies = timeoutMillies;
    return this;
  }

  /**
   * optional timeout (in milliseconds) for process execution, no timeout if not provided or <= 0 
   * @return timeoutMillies
  **/
  @ApiModelProperty(value = "optional timeout (in milliseconds) for process execution, no timeout if not provided or <= 0 ")


  public Long getTimeoutMillies() {
    return timeoutMillies;
  }

  public void setTimeoutMillies(Long timeoutMillies) {
    this.timeoutMillies = timeoutMillies;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WacodisJobDefinitionExecutionSettings wacodisJobDefinitionExecutionSettings = (WacodisJobDefinitionExecutionSettings) o;
    return Objects.equals(this.executionMode, wacodisJobDefinitionExecutionSettings.executionMode) &&
        Objects.equals(this.pivotalInput, wacodisJobDefinitionExecutionSettings.pivotalInput) &&
        Objects.equals(this.timeoutMillies, wacodisJobDefinitionExecutionSettings.timeoutMillies);
  }

  @Override
  public int hashCode() {
    return Objects.hash(executionMode, pivotalInput, timeoutMillies);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisJobDefinitionExecutionSettings {\n");
    
    sb.append("    executionMode: ").append(toIndentedString(executionMode)).append("\n");
    sb.append("    pivotalInput: ").append(toIndentedString(pivotalInput)).append("\n");
    sb.append("    timeoutMillies: ").append(toIndentedString(timeoutMillies)).append("\n");
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

