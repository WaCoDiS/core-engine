package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * message to indicate a finished wps job 
 */
@ApiModel(description = "message to indicate a finished wps job ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-01-24T15:11:15.577+01:00[Europe/Berlin]")

public class ProductDescription  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("wpsJobIdentifier")
  private String wpsJobIdentifier = null;

  @JsonProperty("outputIdentifiers")
  @Valid
  private List<String> outputIdentifiers = new ArrayList<String>();

  @JsonProperty("productCollection")
  private String productCollection = null;

  @JsonProperty("dataEnvelopeReferences")
  @Valid
  private List<String> dataEnvelopeReferences = new ArrayList<String>();

  @JsonProperty("processingTool")
  private String processingTool = null;

  public ProductDescription wpsJobIdentifier(String wpsJobIdentifier) {
    this.wpsJobIdentifier = wpsJobIdentifier;
    return this;
  }

  /**
   * wps job identifier 
   * @return wpsJobIdentifier
  **/
  @ApiModelProperty(value = "wps job identifier ")


  public String getWpsJobIdentifier() {
    return wpsJobIdentifier;
  }

  public void setWpsJobIdentifier(String wpsJobIdentifier) {
    this.wpsJobIdentifier = wpsJobIdentifier;
  }

  public ProductDescription outputIdentifiers(List<String> outputIdentifiers) {
    this.outputIdentifiers = outputIdentifiers;
    return this;
  }

  public ProductDescription addOutputIdentifiersItem(String outputIdentifiersItem) {
    this.outputIdentifiers.add(outputIdentifiersItem);
    return this;
  }

  /**
   * wps output identifiers 
   * @return outputIdentifiers
  **/
  @ApiModelProperty(required = true, value = "wps output identifiers ")
  @NotNull

@Size(min=1) 
  public List<String> getOutputIdentifiers() {
    return outputIdentifiers;
  }

  public void setOutputIdentifiers(List<String> outputIdentifiers) {
    this.outputIdentifiers = outputIdentifiers;
  }

  public ProductDescription productCollection(String productCollection) {
    this.productCollection = productCollection;
    return this;
  }

  /**
   * collection to which the output data should be added 
   * @return productCollection
  **/
  @ApiModelProperty(required = true, value = "collection to which the output data should be added ")
  @NotNull


  public String getProductCollection() {
    return productCollection;
  }

  public void setProductCollection(String productCollection) {
    this.productCollection = productCollection;
  }

  public ProductDescription dataEnvelopeReferences(List<String> dataEnvelopeReferences) {
    this.dataEnvelopeReferences = dataEnvelopeReferences;
    return this;
  }

  public ProductDescription addDataEnvelopeReferencesItem(String dataEnvelopeReferencesItem) {
    this.dataEnvelopeReferences.add(dataEnvelopeReferencesItem);
    return this;
  }

  /**
   * Get dataEnvelopeReferences
   * @return dataEnvelopeReferences
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public List<String> getDataEnvelopeReferences() {
    return dataEnvelopeReferences;
  }

  public void setDataEnvelopeReferences(List<String> dataEnvelopeReferences) {
    this.dataEnvelopeReferences = dataEnvelopeReferences;
  }

  public ProductDescription processingTool(String processingTool) {
    this.processingTool = processingTool;
    return this;
  }

  /**
   * Get processingTool
   * @return processingTool
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getProcessingTool() {
    return processingTool;
  }

  public void setProcessingTool(String processingTool) {
    this.processingTool = processingTool;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductDescription productDescription = (ProductDescription) o;
    return Objects.equals(this.wpsJobIdentifier, productDescription.wpsJobIdentifier) &&
        Objects.equals(this.outputIdentifiers, productDescription.outputIdentifiers) &&
        Objects.equals(this.productCollection, productDescription.productCollection) &&
        Objects.equals(this.dataEnvelopeReferences, productDescription.dataEnvelopeReferences) &&
        Objects.equals(this.processingTool, productDescription.processingTool);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wpsJobIdentifier, outputIdentifiers, productCollection, dataEnvelopeReferences, processingTool);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductDescription {\n");
    
    sb.append("    wpsJobIdentifier: ").append(toIndentedString(wpsJobIdentifier)).append("\n");
    sb.append("    outputIdentifiers: ").append(toIndentedString(outputIdentifiers)).append("\n");
    sb.append("    productCollection: ").append(toIndentedString(productCollection)).append("\n");
    sb.append("    dataEnvelopeReferences: ").append(toIndentedString(dataEnvelopeReferences)).append("\n");
    sb.append("    processingTool: ").append(toIndentedString(processingTool)).append("\n");
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

