package de.wacodis.core.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets WacodisJobStatus
 */
public enum WacodisJobStatus {
  
  WAITING("waiting"),
  
  RUNNING("running"),
  
  DELETED("deleted"),
  
  FINISHED("finished");

  private String value;

  WacodisJobStatus(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static WacodisJobStatus fromValue(String text) {
    for (WacodisJobStatus b : WacodisJobStatus.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + text + "'");
  }
}

