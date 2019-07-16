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
 * SensorWebDataEnvelope
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-07-16T11:30:07.111+02:00[Europe/Berlin]")

public class SensorWebDataEnvelope extends AbstractDataEnvelope implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("serviceUrl")
  private String serviceUrl = null;

  @JsonProperty("offering")
  private String offering = null;

  @JsonProperty("featureOfInterest")
  private String featureOfInterest = null;

  @JsonProperty("observedProperty")
  private String observedProperty = null;

  @JsonProperty("procedure")
  private String procedure = null;

  public SensorWebDataEnvelope serviceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
    return this;
  }

  /**
   * Get serviceUrl
   * @return serviceUrl
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getServiceUrl() {
    return serviceUrl;
  }

  public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }

  public SensorWebDataEnvelope offering(String offering) {
    this.offering = offering;
    return this;
  }

  /**
   * Get offering
   * @return offering
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getOffering() {
    return offering;
  }

  public void setOffering(String offering) {
    this.offering = offering;
  }

  public SensorWebDataEnvelope featureOfInterest(String featureOfInterest) {
    this.featureOfInterest = featureOfInterest;
    return this;
  }

  /**
   * Get featureOfInterest
   * @return featureOfInterest
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getFeatureOfInterest() {
    return featureOfInterest;
  }

  public void setFeatureOfInterest(String featureOfInterest) {
    this.featureOfInterest = featureOfInterest;
  }

  public SensorWebDataEnvelope observedProperty(String observedProperty) {
    this.observedProperty = observedProperty;
    return this;
  }

  /**
   * Get observedProperty
   * @return observedProperty
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getObservedProperty() {
    return observedProperty;
  }

  public void setObservedProperty(String observedProperty) {
    this.observedProperty = observedProperty;
  }

  public SensorWebDataEnvelope procedure(String procedure) {
    this.procedure = procedure;
    return this;
  }

  /**
   * Get procedure
   * @return procedure
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public String getProcedure() {
    return procedure;
  }

  public void setProcedure(String procedure) {
    this.procedure = procedure;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SensorWebDataEnvelope sensorWebDataEnvelope = (SensorWebDataEnvelope) o;
    return Objects.equals(this.serviceUrl, sensorWebDataEnvelope.serviceUrl) &&
        Objects.equals(this.offering, sensorWebDataEnvelope.offering) &&
        Objects.equals(this.featureOfInterest, sensorWebDataEnvelope.featureOfInterest) &&
        Objects.equals(this.observedProperty, sensorWebDataEnvelope.observedProperty) &&
        Objects.equals(this.procedure, sensorWebDataEnvelope.procedure) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serviceUrl, offering, featureOfInterest, observedProperty, procedure, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SensorWebDataEnvelope {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    serviceUrl: ").append(toIndentedString(serviceUrl)).append("\n");
    sb.append("    offering: ").append(toIndentedString(offering)).append("\n");
    sb.append("    featureOfInterest: ").append(toIndentedString(featureOfInterest)).append("\n");
    sb.append("    observedProperty: ").append(toIndentedString(observedProperty)).append("\n");
    sb.append("    procedure: ").append(toIndentedString(procedure)).append("\n");
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

