package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.wacodis.core.models.AbstractSubsetDefinition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * contains information for defining a subset definition for WaCoDiS product process inputs
 */
@ApiModel(description = "contains information for defining a subset definition for WaCoDiS product process inputs")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-11-29T15:03:40.466+01:00[Europe/Berlin]")

public class WacodisProductSubsetDefinition extends AbstractSubsetDefinition implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("serviceUrl")
  private String serviceUrl = null;

  @JsonProperty("productCollection")
  private String productCollection = null;

  @JsonProperty("productType")
  private String productType = null;

  public WacodisProductSubsetDefinition serviceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
    return this;
  }

  /**
   * the base URL of the service from which the data should be requested 
   * @return serviceUrl
  **/
  @ApiModelProperty(required = true, value = "the base URL of the service from which the data should be requested ")
  @NotNull


  public String getServiceUrl() {
    return serviceUrl;
  }

  public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }

  public WacodisProductSubsetDefinition productCollection(String productCollection) {
    this.productCollection = productCollection;
    return this;
  }

  /**
   * collection in which the data is stored 
   * @return productCollection
  **/
  @ApiModelProperty(required = true, value = "collection in which the data is stored ")
  @NotNull


  public String getProductCollection() {
    return productCollection;
  }

  public void setProductCollection(String productCollection) {
    this.productCollection = productCollection;
  }

  public WacodisProductSubsetDefinition productType(String productType) {
    this.productType = productType;
    return this;
  }

  /**
   * data type 
   * @return productType
  **/
  @ApiModelProperty(value = "data type ")


  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
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
    return Objects.equals(this.serviceUrl, wacodisProductSubsetDefinition.serviceUrl) &&
        Objects.equals(this.productCollection, wacodisProductSubsetDefinition.productCollection) &&
        Objects.equals(this.productType, wacodisProductSubsetDefinition.productType) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serviceUrl, productCollection, productType, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisProductSubsetDefinition {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    serviceUrl: ").append(toIndentedString(serviceUrl)).append("\n");
    sb.append("    productCollection: ").append(toIndentedString(productCollection)).append("\n");
    sb.append("    productType: ").append(toIndentedString(productType)).append("\n");
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

