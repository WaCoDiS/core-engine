package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * message to indicate a job execution is being started 
 */
@ApiModel(description = "message to indicate a job execution is being started ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-07-16T11:30:07.111+02:00[Europe/Berlin]")

public class WacodisJobFailed  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("jobIdentifier")
  private String jobIdentifier = null;

  @JsonProperty("reason")
  private String reason = null;

  @JsonProperty("created")
  private DateTime created = null;

  public WacodisJobFailed jobIdentifier(String jobIdentifier) {
    this.jobIdentifier = jobIdentifier;
    return this;
  }

  /**
   * wps job identifier 
   * @return jobIdentifier
  **/
  @ApiModelProperty(required = true, value = "wps job identifier ")
  @NotNull


  public String getJobIdentifier() {
    return jobIdentifier;
  }

  public void setJobIdentifier(String jobIdentifier) {
    this.jobIdentifier = jobIdentifier;
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WacodisJobFailed wacodisJobFailed = (WacodisJobFailed) o;
    return Objects.equals(this.jobIdentifier, wacodisJobFailed.jobIdentifier) &&
        Objects.equals(this.reason, wacodisJobFailed.reason) &&
        Objects.equals(this.created, wacodisJobFailed.created);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jobIdentifier, reason, created);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisJobFailed {\n");
    
    sb.append("    jobIdentifier: ").append(toIndentedString(jobIdentifier)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
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

