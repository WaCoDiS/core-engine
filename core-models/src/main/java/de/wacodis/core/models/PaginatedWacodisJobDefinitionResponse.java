package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.wacodis.core.models.PaginatedResponse;
import de.wacodis.core.models.WacodisJobDefinition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * represents a paginated reponse for WaCoDiS job definitions
 */
@ApiModel(description = "represents a paginated reponse for WaCoDiS job definitions")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-01-24T15:11:15.577+01:00[Europe/Berlin]")

public class PaginatedWacodisJobDefinitionResponse extends PaginatedResponse implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("data")
  @Valid
  private List<WacodisJobDefinition> data = new ArrayList<WacodisJobDefinition>();

  public PaginatedWacodisJobDefinitionResponse data(List<WacodisJobDefinition> data) {
    this.data = data;
    return this;
  }

  public PaginatedWacodisJobDefinitionResponse addDataItem(WacodisJobDefinition dataItem) {
    this.data.add(dataItem);
    return this;
  }

  /**
   * Get data
   * @return data
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<WacodisJobDefinition> getData() {
    return data;
  }

  public void setData(List<WacodisJobDefinition> data) {
    this.data = data;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaginatedWacodisJobDefinitionResponse paginatedWacodisJobDefinitionResponse = (PaginatedWacodisJobDefinitionResponse) o;
    return Objects.equals(this.data, paginatedWacodisJobDefinitionResponse.data) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaginatedWacodisJobDefinitionResponse {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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

