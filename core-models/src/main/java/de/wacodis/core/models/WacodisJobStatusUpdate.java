package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.wacodis.core.models.WacodisJobStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.UUID;
import org.joda.time.DateTime;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * WacodisJobStatusUpdate
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-03-04T15:22:22.969+01:00[Europe/Berlin]")

public class WacodisJobStatusUpdate  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("wacodisJobIdentifier")
  private UUID wacodisJobIdentifier = null;

  @JsonProperty("executionFinished")
  private DateTime executionFinished = null;

  @JsonProperty("newStatus")
  private WacodisJobStatus newStatus = null;

  public WacodisJobStatusUpdate wacodisJobIdentifier(UUID wacodisJobIdentifier) {
    this.wacodisJobIdentifier = wacodisJobIdentifier;
    return this;
  }

  /**
   * Get wacodisJobIdentifier
   * @return wacodisJobIdentifier
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public UUID getWacodisJobIdentifier() {
    return wacodisJobIdentifier;
  }

  public void setWacodisJobIdentifier(UUID wacodisJobIdentifier) {
    this.wacodisJobIdentifier = wacodisJobIdentifier;
  }

  public WacodisJobStatusUpdate executionFinished(DateTime executionFinished) {
    this.executionFinished = executionFinished;
    return this;
  }

  /**
   * point in time when job execution finished successfully, only needed for updates after succesful job execution 
   * @return executionFinished
  **/
  @ApiModelProperty(value = "point in time when job execution finished successfully, only needed for updates after succesful job execution ")

  @Valid

  public DateTime getExecutionFinished() {
    return executionFinished;
  }

  public void setExecutionFinished(DateTime executionFinished) {
    this.executionFinished = executionFinished;
  }

  public WacodisJobStatusUpdate newStatus(WacodisJobStatus newStatus) {
    this.newStatus = newStatus;
    return this;
  }

  /**
   * Get newStatus
   * @return newStatus
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public WacodisJobStatus getNewStatus() {
    return newStatus;
  }

  public void setNewStatus(WacodisJobStatus newStatus) {
    this.newStatus = newStatus;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WacodisJobStatusUpdate wacodisJobStatusUpdate = (WacodisJobStatusUpdate) o;
    return Objects.equals(this.wacodisJobIdentifier, wacodisJobStatusUpdate.wacodisJobIdentifier) &&
        Objects.equals(this.executionFinished, wacodisJobStatusUpdate.executionFinished) &&
        Objects.equals(this.newStatus, wacodisJobStatusUpdate.newStatus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wacodisJobIdentifier, executionFinished, newStatus);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisJobStatusUpdate {\n");
    
    sb.append("    wacodisJobIdentifier: ").append(toIndentedString(wacodisJobIdentifier)).append("\n");
    sb.append("    executionFinished: ").append(toIndentedString(executionFinished)).append("\n");
    sb.append("    newStatus: ").append(toIndentedString(newStatus)).append("\n");
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

