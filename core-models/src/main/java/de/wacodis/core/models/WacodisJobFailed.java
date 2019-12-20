package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.UUID;
import org.joda.time.DateTime;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * message to indicate a job execution failed 
 */
@ApiModel(description = "message to indicate a job execution failed ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-12-20T13:50:52.962+01:00[Europe/Berlin]")

public class WacodisJobFailed  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("wpsJobIdentifier")
  private String wpsJobIdentifier = null;

  @JsonProperty("reason")
  private String reason = null;

  @JsonProperty("created")
  private DateTime created = null;

  @JsonProperty("wacodisJobIdentifier")
  private UUID wacodisJobIdentifier = null;

  public WacodisJobFailed wpsJobIdentifier(String wpsJobIdentifier) {
    this.wpsJobIdentifier = wpsJobIdentifier;
    return this;
  }

  /**
   * wps job identifier 
   * @return wpsJobIdentifier
  **/
  @ApiModelProperty(value = "wps job identifier ")


  public String getWpsJobIdentifier() {
    return wpsJobIdentifier;
  }

  public void setWpsJobIdentifier(String wpsJobIdentifier) {
    this.wpsJobIdentifier = wpsJobIdentifier;
  }

  public WacodisJobFailed reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * status message describing the reason of failure 
   * @return reason
  **/
  @ApiModelProperty(required = true, value = "status message describing the reason of failure ")
  @NotNull


  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public WacodisJobFailed created(DateTime created) {
    this.created = created;
    return this;
  }

  /**
   * time on which the execution has been identified as failed 
   * @return created
  **/
  @ApiModelProperty(required = true, value = "time on which the execution has been identified as failed ")
  @NotNull

  @Valid

  public DateTime getCreated() {
    return created;
  }

  public void setCreated(DateTime created) {
    this.created = created;
  }

  public WacodisJobFailed wacodisJobIdentifier(UUID wacodisJobIdentifier) {
    this.wacodisJobIdentifier = wacodisJobIdentifier;
    return this;
  }

  /**
   * wacodis job identifer (from WacodisJobDefinition, not wps job identifier!) 
   * @return wacodisJobIdentifier
  **/
  @ApiModelProperty(required = true, value = "wacodis job identifer (from WacodisJobDefinition, not wps job identifier!) ")
  @NotNull

  @Valid

  public UUID getWacodisJobIdentifier() {
    return wacodisJobIdentifier;
  }

  public void setWacodisJobIdentifier(UUID wacodisJobIdentifier) {
    this.wacodisJobIdentifier = wacodisJobIdentifier;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WacodisJobFailed wacodisJobFailed = (WacodisJobFailed) o;
    return Objects.equals(this.wpsJobIdentifier, wacodisJobFailed.wpsJobIdentifier) &&
        Objects.equals(this.reason, wacodisJobFailed.reason) &&
        Objects.equals(this.created, wacodisJobFailed.created) &&
        Objects.equals(this.wacodisJobIdentifier, wacodisJobFailed.wacodisJobIdentifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wpsJobIdentifier, reason, created, wacodisJobIdentifier);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisJobFailed {\n");
    
    sb.append("    wpsJobIdentifier: ").append(toIndentedString(wpsJobIdentifier)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    wacodisJobIdentifier: ").append(toIndentedString(wacodisJobIdentifier)).append("\n");
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

