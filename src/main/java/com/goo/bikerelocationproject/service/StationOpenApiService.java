package com.goo.bikerelocationproject.service;

import com.goo.bikerelocationproject.data.dto.ParsingResultDto;
import reactor.core.publisher.Mono;

public interface StationOpenApiService {

  ParsingResultDto saveOpenApiData();
  int saveBikeListData();
  int saveBikeStationMasterData();
  Mono<String> getJsonString(String dataType, int start, int end);
  void saveBikeParkingData();
}
