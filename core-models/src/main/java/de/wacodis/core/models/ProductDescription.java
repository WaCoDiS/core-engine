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
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-06-26T11:36:14.192+02:00[Europe/Berlin]")

public class ProductDescription  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("jobIdentifier")
  private String jobIdentifier = null;

  @JsonProperty("outputIdentifiers")
  @Valid
  private List<String> outputIdentifiers = new ArrayList<String>();

  @JsonProperty("productCollection")
  private String productCollection = null;

  public ProductDescription jobIdentifier(String jobIdentifier) {
    this.jobIdentifier = jobIdentifier;
    return this;
  }

  /**
   * wps job identifier 
   * @return jobIdentifier
  **/
  @ApiModelProperty(required = true, value = "wps job identifier ")
  @NotNull


  public String getJobIdentifier() {
    return jobIdentifier;
  }

  public void setJobIdentifier(String jobIdentifier) {
    this.jobIdentifier = jobIdentifier;
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductDescription productDescription = (ProductDescription) o;
    return Objects.equals(this.jobIdentifier, productDescription.jobIdentifier) &&
        Objects.equals(this.outputIdentifiers, productDescription.outputIdentifiers) &&
        Objects.equals(this.productCollection, productDescription.productCollection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jobIdentifier, outputIdentifiers, productCollection);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductDescription {\n");
    
    sb.append("    jobIdentifier: ").append(toIndentedString(jobIdentifier)).append("\n");
    sb.append("    outputIdentifiers: ").append(toIndentedString(outputIdentifiers)).append("\n");
    sb.append("    productCollection: ").append(toIndentedString(productCollection)).append("\n");
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

