package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.wacodis.core.models.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.core.models.AbstractDataEnvelopeTimeFrame;
import de.wacodis.core.models.DataEnvelopeQueryQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * query Data Access API for DataEnvelopes 
 */
@ApiModel(description = "query Data Access API for DataEnvelopes ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-26T17:30:08.688+01:00[Europe/Berlin]")

public class DataEnvelopeQuery  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("areaOfInterest")
  private AbstractDataEnvelopeAreaOfInterest areaOfInterest = null;

  @JsonProperty("timeFrame")
  private AbstractDataEnvelopeTimeFrame timeFrame = null;

  @JsonProperty("queryParams")
  @Valid
  private Map<String, DataEnvelopeQueryQueryParams> queryParams = null;

  public DataEnvelopeQuery areaOfInterest(AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
    this.areaOfInterest = areaOfInterest;
    return this;
  }

  /**
   * Get areaOfInterest
   * @return areaOfInterest
  **/
  @ApiModelProperty(value = "")

  @Valid

  public AbstractDataEnvelopeAreaOfInterest getAreaOfInterest() {
    return areaOfInterest;
  }

  public void setAreaOfInterest(AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
    this.areaOfInterest = areaOfInterest;
  }

  public DataEnvelopeQuery timeFrame(AbstractDataEnvelopeTimeFrame timeFrame) {
    this.timeFrame = timeFrame;
    return this;
  }

  /**
   * Get timeFrame
   * @return timeFrame
  **/
  @ApiModelProperty(value = "")

  @Valid

  public AbstractDataEnvelopeTimeFrame getTimeFrame() {
    return timeFrame;
  }

  public void setTimeFrame(AbstractDataEnvelopeTimeFrame timeFrame) {
    this.timeFrame = timeFrame;
  }

  public DataEnvelopeQuery queryParams(Map<String, DataEnvelopeQueryQueryParams> queryParams) {
    this.queryParams = queryParams;
    return this;
  }

  public DataEnvelopeQuery putQueryParamsItem(String key, DataEnvelopeQueryQueryParams queryParamsItem) {
    if (this.queryParams == null) {
      this.queryParams = new HashMap<String, DataEnvelopeQueryQueryParams>();
    }
    this.queryParams.put(key, queryParamsItem);
    return this;
  }

  /**
   * map for any query parameter that should be matched except areaOfInterest and timeFrame  
   * @return queryParams
  **/
  @ApiModelProperty(value = "map for any query parameter that should be matched except areaOfInterest and timeFrame  ")

  @Valid

  public Map<String, DataEnvelopeQueryQueryParams> getQueryParams() {
    return queryParams;
  }

  public void setQueryParams(Map<String, DataEnvelopeQueryQueryParams> queryParams) {
    this.queryParams = queryParams;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataEnvelopeQuery dataEnvelopeQuery = (DataEnvelopeQuery) o;
    return Objects.equals(this.areaOfInterest, dataEnvelopeQuery.areaOfInterest) &&
        Objects.equals(this.timeFrame, dataEnvelopeQuery.timeFrame) &&
        Objects.equals(this.queryParams, dataEnvelopeQuery.queryParams);
  }

  @Override
  public int hashCode() {
    return Objects.hash(areaOfInterest, timeFrame, queryParams);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataEnvelopeQuery {\n");
    
    sb.append("    areaOfInterest: ").append(toIndentedString(areaOfInterest)).append("\n");
    sb.append("    timeFrame: ").append(toIndentedString(timeFrame)).append("\n");
    sb.append("    queryParams: ").append(toIndentedString(queryParams)).append("\n");
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

