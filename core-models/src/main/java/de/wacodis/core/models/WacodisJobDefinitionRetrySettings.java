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
 * determine if job execution should be retried after failed execution 
 */
@ApiModel(description = "determine if job execution should be retried after failed execution ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-09T12:32:19.982+02:00[Europe/Berlin]")

public class WacodisJobDefinitionRetrySettings  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("maxRetries")
  private Integer maxRetries = null;

  @JsonProperty("retryDelay_Millies")
  private Long retryDelayMillies = null;

  public WacodisJobDefinitionRetrySettings maxRetries(Integer maxRetries) {
    this.maxRetries = maxRetries;
    return this;
  }

  /**
   * maximum number of retries before job execution fails ultimately 
   * @return maxRetries
  **/
  @ApiModelProperty(value = "maximum number of retries before job execution fails ultimately ")


  public Integer getMaxRetries() {
    return maxRetries;
  }

  public void setMaxRetries(Integer maxRetries) {
    this.maxRetries = maxRetries;
  }

  public WacodisJobDefinitionRetrySettings retryDelayMillies(Long retryDelayMillies) {
    this.retryDelayMillies = retryDelayMillies;
    return this;
  }

  /**
   * delay before retry is scheduled (in milliseconds) 
   * @return retryDelayMillies
  **/
  @ApiModelProperty(value = "delay before retry is scheduled (in milliseconds) ")


  public Long getRetryDelayMillies() {
    return retryDelayMillies;
  }

  public void setRetryDelayMillies(Long retryDelayMillies) {
    this.retryDelayMillies = retryDelayMillies;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WacodisJobDefinitionRetrySettings wacodisJobDefinitionRetrySettings = (WacodisJobDefinitionRetrySettings) o;
    return Objects.equals(this.maxRetries, wacodisJobDefinitionRetrySettings.maxRetries) &&
        Objects.equals(this.retryDelayMillies, wacodisJobDefinitionRetrySettings.retryDelayMillies);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxRetries, retryDelayMillies);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisJobDefinitionRetrySettings {\n");
    
    sb.append("    maxRetries: ").append(toIndentedString(maxRetries)).append("\n");
    sb.append("    retryDelayMillies: ").append(toIndentedString(retryDelayMillies)).append("\n");
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

