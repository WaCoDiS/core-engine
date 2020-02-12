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
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-02-12T16:14:17.964+01:00[Europe/Berlin]")

public class SingleJobExecutionEvent extends AbstractWacodisJobExecutionEvent implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("startAt")
  private DateTime startAt = null;

  public SingleJobExecutionEvent startAt(DateTime startAt) {
    this.startAt = startAt;
    return this;
  }

  /**
   * date on which the wacodis job should be executed, null if wacodis job should be executed immediately 
   * @return startAt
  **/
  @ApiModelProperty(required = true, value = "date on which the wacodis job should be executed, null if wacodis job should be executed immediately ")
  @NotNull

  @Valid

  public DateTime getStartAt() {
    return startAt;
  }

  public void setStartAt(DateTime startAt) {
    this.startAt = startAt;
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
    return Objects.equals(this.startAt, singleJobExecutionEvent.startAt) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startAt, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SingleJobExecutionEvent {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    startAt: ").append(toIndentedString(startAt)).append("\n");
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

