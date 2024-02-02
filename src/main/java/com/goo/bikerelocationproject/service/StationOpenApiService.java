package com.goo.bikerelocationproject.service;

import com.goo.bikerelocationproject.data.dto.ApiParsingResultDto;
import com.goo.bikerelocationproject.data.dto.api.BikeListDto;
import com.goo.bikerelocationproject.data.dto.api.BikeStationMasterDto;

public interface StationOpenApiService {

  ApiParsingResultDto saveOpenApiData();

  int saveBikeListData();

  int saveBikeStationMasterData();

  BikeListDto getBikeListData(String dataType, int start, int end);

  BikeStationMasterDto getBikeStationMasterData(String dataType, int start, int end);

  void saveBikeParkingData();
}
