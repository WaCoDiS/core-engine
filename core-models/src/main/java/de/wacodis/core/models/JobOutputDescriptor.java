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
 * specifies an expected job output 
 */
@ApiModel(description = "specifies an expected job output ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-26T17:56:55.611+01:00[Europe/Berlin]")

public class JobOutputDescriptor  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("identifier")
  private String identifier = null;

  @JsonProperty("mimeType")
  private String mimeType = null;

  @JsonProperty("publishedOutput")
  private Boolean publishedOutput = true;

  @JsonProperty("asReference")
  private Boolean asReference = true;

  public JobOutputDescriptor identifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  /**
   * name (id) of the expected output 
   * @return identifier
  **/
  @ApiModelProperty(required = true, value = "name (id) of the expected output ")
  @NotNull


  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public JobOutputDescriptor mimeType(String mimeType) {
    this.mimeType = mimeType;
    return this;
  }

  /**
   * mime type of the expected output 
   * @return mimeType
  **/
  @ApiModelProperty(required = true, value = "mime type of the expected output ")
  @NotNull


  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public JobOutputDescriptor publishedOutput(Boolean publishedOutput) {
    this.publishedOutput = publishedOutput;
    return this;
  }

  /**
   * if 'false' this output is expected but will not be added to productCollection (see WacodisJobDescription) afterwards 
   * @return publishedOutput
  **/
  @ApiModelProperty(value = "if 'false' this output is expected but will not be added to productCollection (see WacodisJobDescription) afterwards ")


  public Boolean getPublishedOutput() {
    return publishedOutput;
  }

  public void setPublishedOutput(Boolean publishedOutput) {
    this.publishedOutput = publishedOutput;
  }

  public JobOutputDescriptor asReference(Boolean asReference) {
    this.asReference = asReference;
    return this;
  }

  /**
   * specifies whether output can be obtained as reference (true, default) or 'inline' (false) 
   * @return asReference
  **/
  @ApiModelProperty(value = "specifies whether output can be obtained as reference (true, default) or 'inline' (false) ")


  public Boolean getAsReference() {
    return asReference;
  }

  public void setAsReference(Boolean asReference) {
    this.asReference = asReference;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JobOutputDescriptor jobOutputDescriptor = (JobOutputDescriptor) o;
    return Objects.equals(this.identifier, jobOutputDescriptor.identifier) &&
        Objects.equals(this.mimeType, jobOutputDescriptor.mimeType) &&
        Objects.equals(this.publishedOutput, jobOutputDescriptor.publishedOutput) &&
        Objects.equals(this.asReference, jobOutputDescriptor.asReference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier, mimeType, publishedOutput, asReference);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JobOutputDescriptor {\n");
    
    sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
    sb.append("    mimeType: ").append(toIndentedString(mimeType)).append("\n");
    sb.append("    publishedOutput: ").append(toIndentedString(publishedOutput)).append("\n");
    sb.append("    asReference: ").append(toIndentedString(asReference)).append("\n");
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

