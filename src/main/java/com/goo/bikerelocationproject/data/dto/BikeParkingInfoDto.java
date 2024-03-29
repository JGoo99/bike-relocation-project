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
public class BikeParkingInfoDto {

  private Long stationId;
  private double bikeParkingRate;

  public static BikeParkingInfoDto fromRedisData(TypedTuple<String> typedTuple) {
    return BikeParkingInfoDto.builder()
        .stationId(Long.parseLong(typedTuple.getValue()))
        .bikeParkingRate(typedTuple.getScore())
        .build();
  }
}
