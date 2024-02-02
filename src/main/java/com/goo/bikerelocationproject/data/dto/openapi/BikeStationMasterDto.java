package com.goo.bikerelocationproject.data.dto.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BikeStationMasterDto {

  // url : https://data.seoul.go.kr/dataList/OA-21235/S/1/datasetView.do

  private BikeStationMaster bikeStationMaster;

  @JsonProperty("RESULT")
  private ResultDto result;

  @Getter
  @ToString
  public static class BikeStationMaster {

    @JsonProperty("list_total_count")
    private int listTotalCount;

    @JsonProperty("RESULT")
    private ResultDto result;

    private List<BikeStationMasterRowResponse> row;

    @Getter
    @ToString
    public static class BikeStationMasterRowResponse {

      @JsonProperty("LENDPLACE_ID")
      private String stationId;

      @JsonProperty("STATN_ADDR1")
      private String address1;

      @JsonProperty("STATN_ADDR2")
      private String address2;
    }
  }
}
