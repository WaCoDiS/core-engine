package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * abstract type for a remote resource that can be identified by an URL
 */
@ApiModel(description = "abstract type for a remote resource that can be identified by an URL")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-01-08T09:20:28.480+01:00[Europe/Berlin]")

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "method", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = PostResource.class, name = "PostResource"),
  @JsonSubTypes.Type(value = GetResource.class, name = "GetResource"),
})

public class AbstractResource  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("url")
  private String url = null;

  /**
   * Gets or Sets method
   */
  public enum MethodEnum {
    GETRESOURCE("GetResource"),
    
    POSTRESOURCE("PostResource");

    private String value;

    MethodEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static MethodEnum fromValue(String text) {
      for (MethodEnum b : MethodEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("method")
  private MethodEnum method = null;

  @JsonProperty("dataEnvelopeId")
  private String dataEnvelopeId = null;

  public AbstractResource url(String url) {
    this.url = url;
    return this;
  }

  /**
   * Get url
   * @return url
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public AbstractResource method(MethodEnum method) {
    this.method = method;
    return this;
  }

  /**
   * Get method
   * @return method
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public MethodEnum getMethod() {
    return method;
  }

  public void setMethod(MethodEnum method) {
    this.method = method;
  }

  public AbstractResource dataEnvelopeId(String dataEnvelopeId) {
    this.dataEnvelopeId = dataEnvelopeId;
    return this;
  }

  /**
   * the ID (assigned by data access) of the DataEnvelope from which this resource was derived 
   * @return dataEnvelopeId
  **/
  @ApiModelProperty(value = "the ID (assigned by data access) of the DataEnvelope from which this resource was derived ")


  public String getDataEnvelopeId() {
    return dataEnvelopeId;
  }

  public void setDataEnvelopeId(String dataEnvelopeId) {
    this.dataEnvelopeId = dataEnvelopeId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractResource abstractResource = (AbstractResource) o;
    return Objects.equals(this.url, abstractResource.url) &&
        Objects.equals(this.method, abstractResource.method) &&
        Objects.equals(this.dataEnvelopeId, abstractResource.dataEnvelopeId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, method, dataEnvelopeId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AbstractResource {\n");
    
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    method: ").append(toIndentedString(method)).append("\n");
    sb.append("    dataEnvelopeId: ").append(toIndentedString(dataEnvelopeId)).append("\n");
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

