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
 * message to indicate a job execution is being started 
 */
@ApiModel(description = "message to indicate a job execution is being started ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-12-10T15:35:27.058+01:00[Europe/Berlin]")

public class WacodisJobExecution  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("jobIdentifier")
  private UUID jobIdentifier = null;

  @JsonProperty("processingTool")
  private String processingTool = null;

  @JsonProperty("productCollection")
  private String productCollection = null;

  @JsonProperty("created")
  private DateTime created = null;

  public WacodisJobExecution jobIdentifier(UUID jobIdentifier) {
    this.jobIdentifier = jobIdentifier;
    return this;
  }

  /**
   * wacodis job identifer (from WacodisJobDefinition, not wps job identifier!) 
   * @return jobIdentifier
  **/
  @ApiModelProperty(required = true, value = "wacodis job identifer (from WacodisJobDefinition, not wps job identifier!) ")
  @NotNull

  @Valid

  public UUID getJobIdentifier() {
    return jobIdentifier;
  }

  public void setJobIdentifier(UUID jobIdentifier) {
    this.jobIdentifier = jobIdentifier;
  }

  public WacodisJobExecution processingTool(String processingTool) {
    this.processingTool = processingTool;
    return this;
  }

  /**
   * the processingTool ID as provided by the WPS tool wrapper 
   * @return processingTool
  **/
  @ApiModelProperty(required = true, value = "the processingTool ID as provided by the WPS tool wrapper ")
  @NotNull


  public String getProcessingTool() {
    return processingTool;
  }

  public void setProcessingTool(String processingTool) {
    this.processingTool = processingTool;
  }

  public WacodisJobExecution productCollection(String productCollection) {
    this.productCollection = productCollection;
    return this;
  }

  /**
   * collection to which the output data will be added when it becomes available 
   * @return productCollection
  **/
  @ApiModelProperty(required = true, value = "collection to which the output data will be added when it becomes available ")
  @NotNull


  public String getProductCollection() {
    return productCollection;
  }

  public void setProductCollection(String productCollection) {
    this.productCollection = productCollection;
  }

  public WacodisJobExecution created(DateTime created) {
    this.created = created;
    return this;
  }

  /**
   * time on which the execution was invoked 
   * @return created
  **/
  @ApiModelProperty(value = "time on which the execution was invoked ")

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
    WacodisJobExecution wacodisJobExecution = (WacodisJobExecution) o;
    return Objects.equals(this.jobIdentifier, wacodisJobExecution.jobIdentifier) &&
        Objects.equals(this.processingTool, wacodisJobExecution.processingTool) &&
        Objects.equals(this.productCollection, wacodisJobExecution.productCollection) &&
        Objects.equals(this.created, wacodisJobExecution.created);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jobIdentifier, processingTool, productCollection, created);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisJobExecution {\n");
    
    sb.append("    jobIdentifier: ").append(toIndentedString(jobIdentifier)).append("\n");
    sb.append("    processingTool: ").append(toIndentedString(processingTool)).append("\n");
    sb.append("    productCollection: ").append(toIndentedString(productCollection)).append("\n");
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

