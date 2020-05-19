package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.wacodis.core.models.AbstractBackend;
import de.wacodis.core.models.ProductBackend;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * contains specific information about a GeoServer product backend 
 */
@ApiModel(description = "contains specific information about a GeoServer product backend ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-05-14T13:10:16.857+02:00[Europe/Berlin]")

public class GeoServerBackend extends AbstractBackend implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("baseUrl")
  private String baseUrl = null;

  @JsonProperty("productCollection")
  private String productCollection = null;

  @JsonProperty("serviceTypes")
  @Valid
  private List<String> serviceTypes = new ArrayList<String>();

  public GeoServerBackend baseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  /**
   * base URL of the GeoServer backend
   * @return baseUrl
  **/
  @ApiModelProperty(required = true, value = "base URL of the GeoServer backend")
  @NotNull


  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public GeoServerBackend productCollection(String productCollection) {
    this.productCollection = productCollection;
    return this;
  }

  /**
   * name of the product collection (e.g. for WMS the layer name, for WFS the FeatureType) 
   * @return productCollection
  **/
  @ApiModelProperty(required = true, value = "name of the product collection (e.g. for WMS the layer name, for WFS the FeatureType) ")
  @NotNull


  public String getProductCollection() {
    return productCollection;
  }

  public void setProductCollection(String productCollection) {
    this.productCollection = productCollection;
  }

  public GeoServerBackend serviceTypes(List<String> serviceTypes) {
    this.serviceTypes = serviceTypes;
    return this;
  }

  public GeoServerBackend addServiceTypesItem(String serviceTypesItem) {
    this.serviceTypes.add(serviceTypesItem);
    return this;
  }

  /**
   * the supported GeoServer services (e.g. WMS, WFS) 
   * @return serviceTypes
  **/
  @ApiModelProperty(required = true, value = "the supported GeoServer services (e.g. WMS, WFS) ")
  @NotNull


  public List<String> getServiceTypes() {
    return serviceTypes;
  }

  public void setServiceTypes(List<String> serviceTypes) {
    this.serviceTypes = serviceTypes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GeoServerBackend geoServerBackend = (GeoServerBackend) o;
    return Objects.equals(this.baseUrl, geoServerBackend.baseUrl) &&
        Objects.equals(this.productCollection, geoServerBackend.productCollection) &&
        Objects.equals(this.serviceTypes, geoServerBackend.serviceTypes) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(baseUrl, productCollection, serviceTypes, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeoServerBackend {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    baseUrl: ").append(toIndentedString(baseUrl)).append("\n");
    sb.append("    productCollection: ").append(toIndentedString(productCollection)).append("\n");
    sb.append("    serviceTypes: ").append(toIndentedString(serviceTypes)).append("\n");
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

