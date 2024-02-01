package com.goo.bikerelocationproject.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpenApiDataType {

  BIKE_LIST("bikeList"),
  BIKE_LIST_REDIS("bikeListRedis"),
  BIKE_STATION_MASTER("bikeStationMaster");

  private final String data;
}
