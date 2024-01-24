package com.goo.bikerelocationproject.data.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class BikeParkingInfoApiDto {

  // url : https://data.seoul.go.kr/dataList/OA-15493/A/1/datasetView.do

  private String stationId;

  @SerializedName("shared")
  private String bikeParkingRate;
}
