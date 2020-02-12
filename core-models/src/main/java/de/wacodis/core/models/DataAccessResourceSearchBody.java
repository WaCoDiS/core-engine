package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.wacodis.core.models.AbstractDataEnvelopeAreaOfInterest;
import de.wacodis.core.models.AbstractDataEnvelopeTimeFrame;
import de.wacodis.core.models.AbstractSubsetDefinition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * represents POST body parameters that will be used for requesting dataset resource from the DataAccess API
 */
@ApiModel(description = "represents POST body parameters that will be used for requesting dataset resource from the DataAccess API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-02-12T16:14:17.964+01:00[Europe/Berlin]")

public class DataAccessResourceSearchBody  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("areaOfInterest")
  private AbstractDataEnvelopeAreaOfInterest areaOfInterest = null;

  @JsonProperty("timeFrame")
  private AbstractDataEnvelopeTimeFrame timeFrame = null;

  @JsonProperty("inputs")
  @Valid
  private List<AbstractSubsetDefinition> inputs = new ArrayList<AbstractSubsetDefinition>();

  public DataAccessResourceSearchBody areaOfInterest(AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
    this.areaOfInterest = areaOfInterest;
    return this;
  }

  /**
   * Get areaOfInterest
   * @return areaOfInterest
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public AbstractDataEnvelopeAreaOfInterest getAreaOfInterest() {
    return areaOfInterest;
  }

  public void setAreaOfInterest(AbstractDataEnvelopeAreaOfInterest areaOfInterest) {
    this.areaOfInterest = areaOfInterest;
  }

  public DataAccessResourceSearchBody timeFrame(AbstractDataEnvelopeTimeFrame timeFrame) {
    this.timeFrame = timeFrame;
    return this;
  }

  /**
   * Get timeFrame
   * @return timeFrame
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public AbstractDataEnvelopeTimeFrame getTimeFrame() {
    return timeFrame;
  }

  public void setTimeFrame(AbstractDataEnvelopeTimeFrame timeFrame) {
    this.timeFrame = timeFrame;
  }

  public DataAccessResourceSearchBody inputs(List<AbstractSubsetDefinition> inputs) {
    this.inputs = inputs;
    return this;
  }

  public DataAccessResourceSearchBody addInputsItem(AbstractSubsetDefinition inputsItem) {
    this.inputs.add(inputsItem);
    return this;
  }

  /**
   * Get inputs
   * @return inputs
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<AbstractSubsetDefinition> getInputs() {
    return inputs;
  }

  public void setInputs(List<AbstractSubsetDefinition> inputs) {
    this.inputs = inputs;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataAccessResourceSearchBody dataAccessResourceSearchBody = (DataAccessResourceSearchBody) o;
    return Objects.equals(this.areaOfInterest, dataAccessResourceSearchBody.areaOfInterest) &&
        Objects.equals(this.timeFrame, dataAccessResourceSearchBody.timeFrame) &&
        Objects.equals(this.inputs, dataAccessResourceSearchBody.inputs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(areaOfInterest, timeFrame, inputs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataAccessResourceSearchBody {\n");
    
    sb.append("    areaOfInterest: ").append(toIndentedString(areaOfInterest)).append("\n");
    sb.append("    timeFrame: ").append(toIndentedString(timeFrame)).append("\n");
    sb.append("    inputs: ").append(toIndentedString(inputs)).append("\n");
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

