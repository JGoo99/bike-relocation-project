package com.goo.bikerelocationproject.data.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BikeListDto {

  // url : https://data.seoul.go.kr/dataList/OA-15493/A/1/datasetView.do

  private RentBikeStatus rentBikeStatus;

  @JsonProperty("RESULT")
  private ResultDto result;

  @Getter
  @ToString
  public static class RentBikeStatus {

    @JsonProperty("list_total_count")
    private int listTotalCount;

    @JsonProperty("RESULT")
    private ResultDto result;

    private List<BikeListRowResponse> row;

    @Getter
    @ToString
    public static class BikeListRowResponse {

      @JsonProperty("rackTotCnt")
      private int rackTotalCount;

      private String stationName;

      @JsonProperty("parkingBikeTotCnt")
      private int parkingBikeTotalCount;

      @JsonProperty("shared")
      private double bikeParkingRate;

      private String stationId;
    }
  }
}
