package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * DataEnvelopeQueryQueryParams
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-08-26T18:14:26.553+02:00[Europe/Berlin]")

public class DataEnvelopeQueryQueryParams  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("value")
  private Object value = null;

  /**
   * comparators other than 'equals' only apply to numeric or date values, string values are always checked for equality 
   */
  public enum ComparatorEnum {
    EQUALS("equals"),
    
    NOT("not"),
    
    LESSER("lesser"),
    
    GREATER("greater"),
    
    LESSEROREQUALS("lesserOrEquals"),
    
    GREATEROREQUALS("greaterOrEquals");

    private String value;

    ComparatorEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ComparatorEnum fromValue(String text) {
      for (ComparatorEnum b : ComparatorEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("comparator")
  private ComparatorEnum comparator = null;

  public DataEnvelopeQueryQueryParams value(Object value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  **/
  @ApiModelProperty(value = "")


  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public DataEnvelopeQueryQueryParams comparator(ComparatorEnum comparator) {
    this.comparator = comparator;
    return this;
  }

  /**
   * comparators other than 'equals' only apply to numeric or date values, string values are always checked for equality 
   * @return comparator
  **/
  @ApiModelProperty(value = "comparators other than 'equals' only apply to numeric or date values, string values are always checked for equality ")


  public ComparatorEnum getComparator() {
    return comparator;
  }

  public void setComparator(ComparatorEnum comparator) {
    this.comparator = comparator;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataEnvelopeQueryQueryParams dataEnvelopeQueryQueryParams = (DataEnvelopeQueryQueryParams) o;
    return Objects.equals(this.value, dataEnvelopeQueryQueryParams.value) &&
        Objects.equals(this.comparator, dataEnvelopeQueryQueryParams.comparator);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, comparator);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataEnvelopeQueryQueryParams {\n");
    
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    comparator: ").append(toIndentedString(comparator)).append("\n");
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

