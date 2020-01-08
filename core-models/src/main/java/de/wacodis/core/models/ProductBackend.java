package de.wacodis.core.models;

import java.util.Objects;
import io.swagger.annotations.ApiModel;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * shall be used to determine the responsible product backend type 
 */
public enum ProductBackend {
  
  ARCGISIMAGESERVERBACKEND("ArcGISImageServerBackend"),
  
  GEOSERVERBACKEND("GeoServerBackend");

  private String value;

  ProductBackend(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ProductBackend fromValue(String text) {
    for (ProductBackend b : ProductBackend.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + text + "'");
  }
}

