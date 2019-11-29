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
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-11-29T15:03:40.466+01:00[Europe/Berlin]")

public class WacodisProductDataEnvelope extends AbstractDataEnvelope implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("productType")
  private String productType = null;

  @JsonProperty("dataEnvelopeReferences")
  @Valid
  private List<String> dataEnvelopeReferences = null;

  @JsonProperty("process")
  private String process = null;

  @JsonProperty("backendType")
  private AbstractBackend backendType = null;

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
    if (this.dataEnvelopeReferences == null) {
      this.dataEnvelopeReferences = new ArrayList<String>();
    }
    this.dataEnvelopeReferences.add(dataEnvelopeReferencesItem);
    return this;
  }

  /**
   * array of identfiers that reference data envelopes the WaCoDiS product results from 
   * @return dataEnvelopeReferences
  **/
  @ApiModelProperty(value = "array of identfiers that reference data envelopes the WaCoDiS product results from ")


  public List<String> getDataEnvelopeReferences() {
    return dataEnvelopeReferences;
  }

  public void setDataEnvelopeReferences(List<String> dataEnvelopeReferences) {
    this.dataEnvelopeReferences = dataEnvelopeReferences;
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

  public WacodisProductDataEnvelope backendType(AbstractBackend backendType) {
    this.backendType = backendType;
    return this;
  }

  /**
   * Get backendType
   * @return backendType
  **/
  @ApiModelProperty(value = "")

  @Valid

  public AbstractBackend getBackendType() {
    return backendType;
  }

  public void setBackendType(AbstractBackend backendType) {
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
    WacodisProductDataEnvelope wacodisProductDataEnvelope = (WacodisProductDataEnvelope) o;
    return Objects.equals(this.productType, wacodisProductDataEnvelope.productType) &&
        Objects.equals(this.dataEnvelopeReferences, wacodisProductDataEnvelope.dataEnvelopeReferences) &&
        Objects.equals(this.process, wacodisProductDataEnvelope.process) &&
        Objects.equals(this.backendType, wacodisProductDataEnvelope.backendType) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productType, dataEnvelopeReferences, process, backendType, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisProductDataEnvelope {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    productType: ").append(toIndentedString(productType)).append("\n");
    sb.append("    dataEnvelopeReferences: ").append(toIndentedString(dataEnvelopeReferences)).append("\n");
    sb.append("    process: ").append(toIndentedString(process)).append("\n");
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

