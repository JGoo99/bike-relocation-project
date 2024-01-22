package com.goo.bikerelocationproject.service;

import com.goo.bikerelocationproject.data.dto.ParsingResultDto;

public interface StationOpenApiService {

  ParsingResultDto getOpenApiData();
  int saveBikeListData();
  int saveBikeStationMasterData();
  String getJsonString(String a, int b, int c);
}
