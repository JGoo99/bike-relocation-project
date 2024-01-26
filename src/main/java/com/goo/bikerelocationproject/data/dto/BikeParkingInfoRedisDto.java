package com.goo.bikerelocationproject.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BikeParkingInfoRedisDto {

  private String stationId;
  private double bikeParkingRate;

  public static BikeParkingInfoRedisDto fromRedisData(TypedTuple<String> typedTuple) {
    return BikeParkingInfoRedisDto.builder()
        .stationId(typedTuple.getValue())
        .bikeParkingRate(typedTuple.getScore())
        .build();
  }

  public static BikeParkingInfoRedisDto fromApiDto(BikeParkingInfoApiDto dto) {
    return BikeParkingInfoRedisDto.builder()
        .stationId(dto.getStationId().substring(3))
        .bikeParkingRate(Double.parseDouble(dto.getBikeParkingRate()))
        .build();
  }
}
