package com.goo.bikerelocationproject.service;

import com.goo.bikerelocationproject.data.dto.ParsingResultDto;

public interface StationOpenApiService {

  ParsingResultDto saveOpenApiData();
  int saveBikeListData();
  int saveBikeStationMasterData();
  String getJsonString(String dataType, int start, int end);
  void saveBikeParkingData();
}
