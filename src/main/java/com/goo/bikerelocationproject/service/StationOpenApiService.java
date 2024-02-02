package com.goo.bikerelocationproject.service;

import com.goo.bikerelocationproject.data.dto.OpenApiParsingResultDto;
import com.goo.bikerelocationproject.data.dto.openapi.BikeListDto;
import com.goo.bikerelocationproject.data.dto.openapi.BikeStationMasterDto;

public interface StationOpenApiService {

  OpenApiParsingResultDto saveOpenApiData();

  int saveBikeListData();

  int saveBikeStationMasterData();

  BikeListDto getBikeListData(String dataType, int start, int end);

  BikeStationMasterDto getBikeStationMasterData(String dataType, int start, int end);

  void saveBikeParkingData();
}
