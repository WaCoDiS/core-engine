package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.wacodis.core.models.AbstractSubsetDefinition;
import de.wacodis.core.models.ProductBackend;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * contains information for defining a subset definition for WaCoDiS product process inputs
 */
@ApiModel(description = "contains information for defining a subset definition for WaCoDiS product process inputs")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-08-26T18:14:26.553+02:00[Europe/Berlin]")

public class WacodisProductSubsetDefinition extends AbstractSubsetDefinition implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("productType")
  private String productType = null;

  @JsonProperty("backendType")
  private ProductBackend backendType = null;

  public WacodisProductSubsetDefinition productType(String productType) {
    this.productType = productType;
    return this;
  }

  /**
   * the type of the product (collection). e.g. \"land cover classification\" 
   * @return productType
  **/
  @ApiModelProperty(required = true, value = "the type of the product (collection). e.g. \"land cover classification\" ")
  @NotNull


  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public WacodisProductSubsetDefinition backendType(ProductBackend backendType) {
    this.backendType = backendType;
    return this;
  }

  /**
   * Get backendType
   * @return backendType
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public ProductBackend getBackendType() {
    return backendType;
  }

  public void setBackendType(ProductBackend backendType) {
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
    WacodisProductSubsetDefinition wacodisProductSubsetDefinition = (WacodisProductSubsetDefinition) o;
    return Objects.equals(this.productType, wacodisProductSubsetDefinition.productType) &&
        Objects.equals(this.backendType, wacodisProductSubsetDefinition.backendType) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productType, backendType, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisProductSubsetDefinition {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    productType: ").append(toIndentedString(productType)).append("\n");
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

