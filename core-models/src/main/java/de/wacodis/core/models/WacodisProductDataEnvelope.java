package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.wacodis.core.models.AbstractBackend;
import de.wacodis.core.models.AbstractDataEnvelope;
import de.wacodis.core.models.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.core.models.AbstractDataEnvelopeTimeFrame;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * describes specific metadata information about a product dataset created from the WaCoDiS System
 */
@ApiModel(description = "describes specific metadata information about a product dataset created from the WaCoDiS System")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-02-14T16:07:01.784+01:00[Europe/Berlin]")

public class WacodisProductDataEnvelope extends AbstractDataEnvelope implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("productType")
  private String productType = null;

  @JsonProperty("dataEnvelopeReferences")
  @Valid
  private List<String> dataEnvelopeReferences = new ArrayList<String>();

  @JsonProperty("dataEnvelopeServiceEndpoint")
  private String dataEnvelopeServiceEndpoint = null;

  @JsonProperty("process")
  private String process = null;

  @JsonProperty("serviceDefinition")
  private AbstractBackend serviceDefinition = null;

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

  public WacodisProductDataEnvelope dataEnvelopeReferences(List<String> dataEnvelopeReferences) {
    this.dataEnvelopeReferences = dataEnvelopeReferences;
    return this;
  }

  public WacodisProductDataEnvelope addDataEnvelopeReferencesItem(String dataEnvelopeReferencesItem) {
    this.dataEnvelopeReferences.add(dataEnvelopeReferencesItem);
    return this;
  }

  /**
   * array of identifiers that reference data envelopes the WaCoDiS product results from 
   * @return dataEnvelopeReferences
  **/
  @ApiModelProperty(required = true, value = "array of identifiers that reference data envelopes the WaCoDiS product results from ")
  @NotNull


  public List<String> getDataEnvelopeReferences() {
    return dataEnvelopeReferences;
  }

  public void setDataEnvelopeReferences(List<String> dataEnvelopeReferences) {
    this.dataEnvelopeReferences = dataEnvelopeReferences;
  }

  public WacodisProductDataEnvelope dataEnvelopeServiceEndpoint(String dataEnvelopeServiceEndpoint) {
    this.dataEnvelopeServiceEndpoint = dataEnvelopeServiceEndpoint;
    return this;
  }

  /**
   * contains the url of the service endpoint to retrieve DataEnvelopes that are included in the dataEnvelopeReferences attribute, for example http://localhost:8080/dataenvelopes/ (without the id) 
   * @return dataEnvelopeServiceEndpoint
  **/
  @ApiModelProperty(value = "contains the url of the service endpoint to retrieve DataEnvelopes that are included in the dataEnvelopeReferences attribute, for example http://localhost:8080/dataenvelopes/ (without the id) ")


  public String getDataEnvelopeServiceEndpoint() {
    return dataEnvelopeServiceEndpoint;
  }

  public void setDataEnvelopeServiceEndpoint(String dataEnvelopeServiceEndpoint) {
    this.dataEnvelopeServiceEndpoint = dataEnvelopeServiceEndpoint;
  }

  public WacodisProductDataEnvelope process(String process) {
    this.process = process;
    return this;
  }

  /**
   * name of the process that was responsible for creating the product 
   * @return process
  **/
  @ApiModelProperty(required = true, value = "name of the process that was responsible for creating the product ")
  @NotNull


  public String getProcess() {
    return process;
  }

  public void setProcess(String process) {
    this.process = process;
  }

  public WacodisProductDataEnvelope serviceDefinition(AbstractBackend serviceDefinition) {
    this.serviceDefinition = serviceDefinition;
    return this;
  }

  /**
   * Get serviceDefinition
   * @return serviceDefinition
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public AbstractBackend getServiceDefinition() {
    return serviceDefinition;
  }

  public void setServiceDefinition(AbstractBackend serviceDefinition) {
    this.serviceDefinition = serviceDefinition;
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
    return Objects.equals(this.productType, wacodisProductDataEnvelope.productType) &&
        Objects.equals(this.dataEnvelopeReferences, wacodisProductDataEnvelope.dataEnvelopeReferences) &&
        Objects.equals(this.dataEnvelopeServiceEndpoint, wacodisProductDataEnvelope.dataEnvelopeServiceEndpoint) &&
        Objects.equals(this.process, wacodisProductDataEnvelope.process) &&
        Objects.equals(this.serviceDefinition, wacodisProductDataEnvelope.serviceDefinition) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productType, dataEnvelopeReferences, dataEnvelopeServiceEndpoint, process, serviceDefinition, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisProductDataEnvelope {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    productType: ").append(toIndentedString(productType)).append("\n");
    sb.append("    dataEnvelopeReferences: ").append(toIndentedString(dataEnvelopeReferences)).append("\n");
    sb.append("    dataEnvelopeServiceEndpoint: ").append(toIndentedString(dataEnvelopeServiceEndpoint)).append("\n");
    sb.append("    process: ").append(toIndentedString(process)).append("\n");
    sb.append("    serviceDefinition: ").append(toIndentedString(serviceDefinition)).append("\n");
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

