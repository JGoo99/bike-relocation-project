package com.goo.bikerelocationproject.service;

import com.goo.bikerelocationproject.data.dto.ParsingResultDto;

public interface StationOpenApiService {

  ParsingResultDto getOpenApiData();
  void getBikeListData(ParsingResultDto parsingResultDto);
  void getBikeStationMasterData(ParsingResultDto parsingResultDto);
}
