package de.wacodis.core.models;

import java.util.Objects;
import de.wacodis.core.models.AbstractResource;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * resource that can be fetched with a HTTP GET request
 */
@ApiModel(description = "resource that can be fetched with a HTTP GET request")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-03-25T18:17:14.646+01:00[Europe/Berlin]")

public class GetResource extends AbstractResource implements Serializable {
  private static final long serialVersionUID = 1L;


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetResource {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
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

