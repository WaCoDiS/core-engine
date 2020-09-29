package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.AbstractSubsetDefinitionTemporalCoverage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * contains information for defining a subset definition for static process inputs
 */
@ApiModel(description = "contains information for defining a subset definition for static process inputs")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-29T15:53:35.626+02:00[Europe/Berlin]")

public class StaticSubsetDefinition extends AbstractSubsetDefinition implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("value")
  private String value = null;

  /**
   * determines how the value should be interpreted 
   */
  public enum DataTypeEnum {
    TEXT("text"),
    
    NUMERIC("numeric");

    private String value;

    DataTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static DataTypeEnum fromValue(String text) {
      for (DataTypeEnum b : DataTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("dataType")
  private DataTypeEnum dataType = null;

  public StaticSubsetDefinition value(String value) {
    this.value = value;
    return this;
  }

  /**
   * string encoded value 
   * @return value
  **/
  @ApiModelProperty(required = true, value = "string encoded value ")
  @NotNull


  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public StaticSubsetDefinition dataType(DataTypeEnum dataType) {
    this.dataType = dataType;
    return this;
  }

  /**
   * determines how the value should be interpreted 
   * @return dataType
  **/
  @ApiModelProperty(required = true, value = "determines how the value should be interpreted ")
  @NotNull


  public DataTypeEnum getDataType() {
    return dataType;
  }

  public void setDataType(DataTypeEnum dataType) {
    this.dataType = dataType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StaticSubsetDefinition staticSubsetDefinition = (StaticSubsetDefinition) o;
    return Objects.equals(this.value, staticSubsetDefinition.value) &&
        Objects.equals(this.dataType, staticSubsetDefinition.dataType) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, dataType, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StaticSubsetDefinition {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    dataType: ").append(toIndentedString(dataType)).append("\n");
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

