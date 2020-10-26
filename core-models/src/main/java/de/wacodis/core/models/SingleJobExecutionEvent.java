package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.wacodis.core.models.AbstractWacodisJobExecutionEvent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * event that describes a single execution of a wacods job without regular schedule 
 */
@ApiModel(description = "event that describes a single execution of a wacods job without regular schedule ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-26T17:30:08.688+01:00[Europe/Berlin]")

public class SingleJobExecutionEvent extends AbstractWacodisJobExecutionEvent implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("temporalCoverageEndDate")
  private DateTime temporalCoverageEndDate = null;

  public SingleJobExecutionEvent temporalCoverageEndDate(DateTime temporalCoverageEndDate) {
    this.temporalCoverageEndDate = temporalCoverageEndDate;
    return this;
  }

  /**
   * determines together with WacodisJobDefinition.temporalCoverage.duration which time period has to be considered for inputs. The attribute WacodisJobDefinition.temporalCoverage.duration has to be specified in WacodisJobDefinition. 
   * @return temporalCoverageEndDate
  **/
  @ApiModelProperty(required = true, value = "determines together with WacodisJobDefinition.temporalCoverage.duration which time period has to be considered for inputs. The attribute WacodisJobDefinition.temporalCoverage.duration has to be specified in WacodisJobDefinition. ")
  @NotNull

  @Valid

  public DateTime getTemporalCoverageEndDate() {
    return temporalCoverageEndDate;
  }

  public void setTemporalCoverageEndDate(DateTime temporalCoverageEndDate) {
    this.temporalCoverageEndDate = temporalCoverageEndDate;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SingleJobExecutionEvent singleJobExecutionEvent = (SingleJobExecutionEvent) o;
    return Objects.equals(this.temporalCoverageEndDate, singleJobExecutionEvent.temporalCoverageEndDate) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(temporalCoverageEndDate, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SingleJobExecutionEvent {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    temporalCoverageEndDate: ").append(toIndentedString(temporalCoverageEndDate)).append("\n");
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

