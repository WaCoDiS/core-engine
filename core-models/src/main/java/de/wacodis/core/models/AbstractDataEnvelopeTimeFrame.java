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
 * time frame the dataset covers/must intersect
 */
@ApiModel(description = "time frame the dataset covers/must intersect")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-12-20T13:50:52.962+01:00[Europe/Berlin]")

public class AbstractDataEnvelopeTimeFrame  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("startTime")
  private DateTime startTime = null;

  @JsonProperty("endTime")
  private DateTime endTime = null;

  public AbstractDataEnvelopeTimeFrame startTime(DateTime startTime) {
    this.startTime = startTime;
    return this;
  }

  /**
   * the beginning of the time frame 
   * @return startTime
  **/
  @ApiModelProperty(value = "the beginning of the time frame ")

  @Valid

  public DateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(DateTime startTime) {
    this.startTime = startTime;
  }

  public AbstractDataEnvelopeTimeFrame endTime(DateTime endTime) {
    this.endTime = endTime;
    return this;
  }

  /**
   * the ending of the time frame 
   * @return endTime
  **/
  @ApiModelProperty(value = "the ending of the time frame ")

  @Valid

  public DateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(DateTime endTime) {
    this.endTime = endTime;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractDataEnvelopeTimeFrame abstractDataEnvelopeTimeFrame = (AbstractDataEnvelopeTimeFrame) o;
    return Objects.equals(this.startTime, abstractDataEnvelopeTimeFrame.startTime) &&
        Objects.equals(this.endTime, abstractDataEnvelopeTimeFrame.endTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startTime, endTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AbstractDataEnvelopeTimeFrame {\n");
    
    sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
    sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
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

