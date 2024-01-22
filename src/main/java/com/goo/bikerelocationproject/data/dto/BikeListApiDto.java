package com.goo.bikerelocationproject.data.dto;

import com.goo.bikerelocationproject.data.entity.Station;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class BikeListApiDto {

  // url : https://data.seoul.go.kr/dataList/OA-15493/A/1/datasetView.do

  private String stationId;

  @SerializedName("rackTotCnt")
  private String rackTotalCount;

  private String stationName;

  public static Station toEntity(BikeListApiDto bikeListApiDto) {
    return Station.builder()
        .id(Long.valueOf(bikeListApiDto.getStationId().substring(3)))
        .rackTotalCount(Integer.parseInt(bikeListApiDto.getRackTotalCount()))
        .stationName(bikeListApiDto.getStationName())
        .build();
  }
}
