package com.goo.bikerelocationproject.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpenApiDataType {

  BIKE_LIST("bikeLis"),
  BIKE_STATION_MASTER("bikeStationMaster");

  private final String data;
}
