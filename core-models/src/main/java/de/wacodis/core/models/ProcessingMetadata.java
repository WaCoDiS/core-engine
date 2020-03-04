package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.wacodis.core.models.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.core.models.AbstractDataEnvelopeTimeFrame;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * extensible datatype for metadata that describes the processing of a product 
 */
@ApiModel(description = "extensible datatype for metadata that describes the processing of a product ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-03-04T15:22:22.969+01:00[Europe/Berlin]")

public class ProcessingMetadata  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("areaOfInterest")
  private AbstractDataEnvelopeAreaOfInterest areaOfInterest = null;

  @JsonProperty("timeFrame")
  private AbstractDataEnvelopeTimeFrame timeFrame = null;

  @JsonProperty("created")
  private DateTime created = null;

  public ProcessingMetadata areaOfInterest(AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
    this.areaOfInterest = areaOfInterest;
    return this;
  }

  /**
   * Get areaOfInterest
   * @return areaOfInterest
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public AbstractDataEnvelopeAreaOfInterest getAreaOfInterest() {
    return areaOfInterest;
  }

  public void setAreaOfInterest(AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
    this.areaOfInterest = areaOfInterest;
  }

  public ProcessingMetadata timeFrame(AbstractDataEnvelopeTimeFrame timeFrame) {
    this.timeFrame = timeFrame;
    return this;
  }

  /**
   * Get timeFrame
   * @return timeFrame
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public AbstractDataEnvelopeTimeFrame getTimeFrame() {
    return timeFrame;
  }

  public void setTimeFrame(AbstractDataEnvelopeTimeFrame timeFrame) {
    this.timeFrame = timeFrame;
  }

  public ProcessingMetadata created(DateTime created) {
    this.created = created;
    return this;
  }

  /**
   * time on which the dataset was created 
   * @return created
  **/
  @ApiModelProperty(required = true, value = "time on which the dataset was created ")
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
    ProcessingMetadata processingMetadata = (ProcessingMetadata) o;
    return Objects.equals(this.areaOfInterest, processingMetadata.areaOfInterest) &&
        Objects.equals(this.timeFrame, processingMetadata.timeFrame) &&
        Objects.equals(this.created, processingMetadata.created);
  }

  @Override
  public int hashCode() {
    return Objects.hash(areaOfInterest, timeFrame, created);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProcessingMetadata {\n");
    
    sb.append("    areaOfInterest: ").append(toIndentedString(areaOfInterest)).append("\n");
    sb.append("    timeFrame: ").append(toIndentedString(timeFrame)).append("\n");
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

