package com.goo.bikerelocationproject.data.dto;

import com.goo.bikerelocationproject.data.entity.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class StationInfoDto {

  private Long id;
  private String stationName;
  private String address1;
  private String address2;
  private int rackTotalCount;
  private int parkingBikeTotalCount;
  private double bikeParkingRate;

  public static StationInfoDto fromEntity(Station station, double bikeParkingRate) {
    return StationInfoDto.builder()
        .id(station.getId())
        .stationName(station.getStationName())
        .address1(station.getAddress1())
        .address2(station.getAddress2())
        .rackTotalCount(station.getRackTotalCount())
        .parkingBikeTotalCount((int) (bikeParkingRate * station.getRackTotalCount() / 100))
        .bikeParkingRate(bikeParkingRate)
        .build();
  }
}
