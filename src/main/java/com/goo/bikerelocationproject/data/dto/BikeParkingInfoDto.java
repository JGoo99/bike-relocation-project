package com.goo.bikerelocationproject.data.dto;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BikeParkingInfoDto {

  private Long stationId;
  private double bikeParkingRate;

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public static BikeParkingInfoDto fromJson(String jsonString) {
    Gson gson = new Gson();
    return gson.fromJson(jsonString, BikeParkingInfoDto.class);
  }

  public static BikeParkingInfoDto fromApiDto(BikeParkingInfoApiDto dto) {
    return BikeParkingInfoDto.builder()
        .stationId(Long.parseLong(dto.getStationId().substring(3)))
        .bikeParkingRate(Double.parseDouble(dto.getBikeParkingRate()))
        .build();
  }
}
