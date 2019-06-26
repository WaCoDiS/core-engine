package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.core.models.AbstractDataEnvelopeTimeFrame;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.joda.time.DateTime;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * WacodisProductDataEnvelope
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-06-26T11:36:14.192+02:00[Europe/Berlin]")

public class WacodisProductDataEnvelope extends AbstractDataEnvelope implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("productCollection")
  private String productCollection = null;

  @JsonProperty("productType")
  private String productType = null;

  @JsonProperty("serviceName")
  private String serviceName = null;

  public WacodisProductDataEnvelope productCollection(String productCollection) {
    this.productCollection = productCollection;
    return this;
  }

  /**
   * collection to which the new product is part of 
   * @return productCollection
  **/
  @ApiModelProperty(required = true, value = "collection to which the new product is part of ")
  @NotNull


  public String getProductCollection() {
    return productCollection;
  }

  public void setProductCollection(String productCollection) {
    this.productCollection = productCollection;
  }

  public WacodisProductDataEnvelope productType(String productType) {
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

  public WacodisProductDataEnvelope serviceName(String serviceName) {
    this.serviceName = serviceName;
    return this;
  }

  /**
   * the reference to the service (e.g. Image Server name) 
   * @return serviceName
  **/
  @ApiModelProperty(required = true, value = "the reference to the service (e.g. Image Server name) ")
  @NotNull


  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WacodisProductDataEnvelope wacodisProductDataEnvelope = (WacodisProductDataEnvelope) o;
    return Objects.equals(this.productCollection, wacodisProductDataEnvelope.productCollection) &&
        Objects.equals(this.productType, wacodisProductDataEnvelope.productType) &&
        Objects.equals(this.serviceName, wacodisProductDataEnvelope.serviceName) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productCollection, productType, serviceName, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisProductDataEnvelope {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    productCollection: ").append(toIndentedString(productCollection)).append("\n");
    sb.append("    productType: ").append(toIndentedString(productType)).append("\n");
    sb.append("    serviceName: ").append(toIndentedString(serviceName)).append("\n");
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

