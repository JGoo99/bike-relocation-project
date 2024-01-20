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
public class BikeStationMasterApiDto {

  // url : https://data.seoul.go.kr/dataList/OA-21235/S/1/datasetView.do

  @SerializedName("LENDPLACE_ID")
  private String stationId;

  @SerializedName("STATN_ADDR1")
  private String address1;

  @SerializedName("STATN_ADDR2")
  private String address2;
}
