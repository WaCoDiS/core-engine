package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.wacodis.core.models.AbstractResource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * resource that can be fetched with a HTTP POST request
 */
@ApiModel(description = "resource that can be fetched with a HTTP POST request")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-12-03T09:55:06.704+01:00[Europe/Berlin]")

public class PostResource extends AbstractResource implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("body")
  private String body = null;

  @JsonProperty("contentType")
  private String contentType = null;

  public PostResource body(String body) {
    this.body = body;
    return this;
  }

  /**
   * Get body
   * @return body
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public PostResource contentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  /**
   * Get contentType
   * @return contentType
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PostResource postResource = (PostResource) o;
    return Objects.equals(this.body, postResource.body) &&
        Objects.equals(this.contentType, postResource.contentType) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(body, contentType, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PostResource {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    body: ").append(toIndentedString(body)).append("\n");
    sb.append("    contentType: ").append(toIndentedString(contentType)).append("\n");
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

