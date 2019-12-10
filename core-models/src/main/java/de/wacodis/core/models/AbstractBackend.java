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
 * abstract type for a backend that provides WaCoDiS products 
 */
@ApiModel(description = "abstract type for a backend that provides WaCoDiS products ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-12-10T10:50:17.158+01:00[Europe/Berlin]")

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "backendType", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = ArcGISImageServerBackend.class, name = "ArcGISImageServerBackend"),
})

public class AbstractBackend  implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * shall be used to determine the responsible product backend type 
   */
  public enum BackendTypeEnum {
    ARCGISIMAGESERVERBACKEND("ArcGISImageServerBackend"),
    
    GEOSERVERBACKEND("GeoServerBackend");

    private String value;

    BackendTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static BackendTypeEnum fromValue(String text) {
      for (BackendTypeEnum b : BackendTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("backendType")
  private BackendTypeEnum backendType = null;

  public AbstractBackend backendType(BackendTypeEnum backendType) {
    this.backendType = backendType;
    return this;
  }

  /**
   * shall be used to determine the responsible product backend type 
   * @return backendType
  **/
  @ApiModelProperty(required = true, value = "shall be used to determine the responsible product backend type ")
  @NotNull


  public BackendTypeEnum getBackendType() {
    return backendType;
  }

  public void setBackendType(BackendTypeEnum backendType) {
    this.backendType = backendType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractBackend abstractBackend = (AbstractBackend) o;
    return Objects.equals(this.backendType, abstractBackend.backendType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(backendType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AbstractBackend {\n");
    
    sb.append("    backendType: ").append(toIndentedString(backendType)).append("\n");
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

