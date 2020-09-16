package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * abstract type to describe event-based execution of wacodis jobs 
 */
@ApiModel(description = "abstract type to describe event-based execution of wacodis jobs ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-09-16T18:02:07.609+02:00[Europe/Berlin]")

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = SingleJobExecutionEvent.class, name = "SingleJobExecutionEvent"),
})

public class AbstractWacodisJobExecutionEvent  implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * shall be used to determine the sub type of AbstractWacodisJobExecutionEvent 
   */
  public enum EventTypeEnum {
    SINGLEJOBEXECUTIONEVENT("SingleJobExecutionEvent");

    private String value;

    EventTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static EventTypeEnum fromValue(String text) {
      for (EventTypeEnum b : EventTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("eventType")
  private EventTypeEnum eventType = null;

  public AbstractWacodisJobExecutionEvent eventType(EventTypeEnum eventType) {
    this.eventType = eventType;
    return this;
  }

  /**
   * shall be used to determine the sub type of AbstractWacodisJobExecutionEvent 
   * @return eventType
  **/
  @ApiModelProperty(required = true, value = "shall be used to determine the sub type of AbstractWacodisJobExecutionEvent ")
  @NotNull


  public EventTypeEnum getEventType() {
    return eventType;
  }

  public void setEventType(EventTypeEnum eventType) {
    this.eventType = eventType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractWacodisJobExecutionEvent abstractWacodisJobExecutionEvent = (AbstractWacodisJobExecutionEvent) o;
    return Objects.equals(this.eventType, abstractWacodisJobExecutionEvent.eventType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AbstractWacodisJobExecutionEvent {\n");
    
    sb.append("    eventType: ").append(toIndentedString(eventType)).append("\n");
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

