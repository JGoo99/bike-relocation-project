package com.goo.bikerelocationproject.service.impl;

import static com.goo.bikerelocationproject.type.ErrorCode.OPEN_API_ERROR;
import static com.goo.bikerelocationproject.type.OpenApiDataType.BIKE_LIST;
import static com.goo.bikerelocationproject.type.OpenApiDataType.BIKE_LIST_REDIS;
import static com.goo.bikerelocationproject.type.OpenApiDataType.BIKE_STATION_MASTER;
import static com.goo.bikerelocationproject.type.RedisKey.REDIS_STATION;

import com.goo.bikerelocationproject.data.dto.BikeListApiDto;
import com.goo.bikerelocationproject.data.dto.BikeParkingInfoApiDto;
import com.goo.bikerelocationproject.data.dto.BikeParkingInfoDto;
import com.goo.bikerelocationproject.data.dto.BikeStationMasterApiDto;
import com.goo.bikerelocationproject.data.dto.ParsingResultDto;
import com.goo.bikerelocationproject.data.dto.StatusApiDto;
import com.goo.bikerelocationproject.data.entity.Station;
import com.goo.bikerelocationproject.exception.OpenApiException;
import com.goo.bikerelocationproject.exception.StationException;
import com.goo.bikerelocationproject.repository.StationRepo;
import com.goo.bikerelocationproject.service.StationOpenApiService;
import com.goo.bikerelocationproject.type.OpenApiDataType;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class StationOpenApiServiceImpl implements StationOpenApiService {

  private final StationRepo stationRepo;
  private final RedisTemplate<String, String> redisTemplate;

  @Value("${bike-list-api-key}")
  private String apiKey;

  @Override
  public ParsingResultDto saveOpenApiData() {

    ParsingResultDto result = new ParsingResultDto();
    result.setBikeListTotalCount(saveBikeListData());
    result.setSavedBikeStationMasterTotalCount(saveBikeStationMasterData());

    return result;
  }

  @Override
  public int saveBikeListData() {
    int bikeListTotalCount = 0;

    int pageNum = 0;
    boolean isRemain = true;

    List<Station> stations = new ArrayList<>();
    JsonObject objData = null;
    try {
      while (isRemain) {
        int start = 1000 * pageNum++ + 1;
        int end = 1000 * pageNum;

        Gson gson = new Gson();
        objData = (JsonObject) new JsonParser().parse(getJsonString(BIKE_LIST.getData(), start, end));
        JsonObject data = (JsonObject) objData.get("rentBikeStatus");
        JsonArray jsonArray = (JsonArray) data.get("row");

        int listTotalCount = Integer.parseInt(String.valueOf(data.get("list_total_count")));
        if (listTotalCount < 1000) {
          isRemain = false;
        }
        bikeListTotalCount += listTotalCount;

        for (int i = 0; i < jsonArray.size(); i++) {
          BikeListApiDto bikeListApiDto = gson.fromJson(jsonArray.get(i), BikeListApiDto.class);
          stations.add(BikeListApiDto.toEntity(bikeListApiDto));
        }
      }
    } catch (Exception e) {

      throwException(objData, BIKE_LIST);
    }
    stationRepo.saveAll(stations);

    return bikeListTotalCount;
  }

  @Override
  public int saveBikeStationMasterData() {
    int bikeStationMasterTotalCount = 0;

    int pageNum = 0;
    boolean isRemain = true;

    JsonObject objData = null;
    try {
      while (isRemain) {
        int start = 1000 * pageNum++ + 1;
        int end = 1000 * pageNum;

        Gson gson = new Gson();
        objData = (JsonObject) new JsonParser().parse(getJsonString(BIKE_STATION_MASTER.getData(), start, end));
        JsonObject data = (JsonObject) objData.get("bikeStationMaster");
        JsonArray jsonArray = (JsonArray) data.get("row");

        int listTotalCount = Integer.parseInt(String.valueOf(data.get("list_total_count")));
        if (listTotalCount < end) {
          isRemain = false;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
          BikeStationMasterApiDto bikeStationMasterApiDto =
              gson.fromJson(jsonArray.get(i), BikeStationMasterApiDto.class);

          Optional<Station> station =
              stationRepo.findById(
                  Long.valueOf(bikeStationMasterApiDto.getStationId().substring(3)));

          if (station.isPresent()) {
            Station selectedStation = station.get();
            selectedStation.setAddress1(bikeStationMasterApiDto.getAddress1());
            selectedStation.setAddress2(bikeStationMasterApiDto.getAddress2());
            stationRepo.save(selectedStation);
            bikeStationMasterTotalCount++;
          }
        }
      }
    } catch (Exception e) {

      throwException(objData, BIKE_STATION_MASTER);
    }
    return bikeStationMasterTotalCount;
  }

  @Override
  public void saveBikeParkingData() {

    int pageNum = 0;
    boolean isRemain = true;

    ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
    Set<TypedTuple<String>> set = new HashSet<>();
    JsonObject objData = null;
    try {
      while (isRemain) {
        int start = 1000 * pageNum++ + 1;
        int end = 1000 * pageNum;

        Gson gson = new Gson();
        objData = (JsonObject) new JsonParser().parse(getJsonString(BIKE_LIST.getData(), start, end));
        JsonObject data = (JsonObject) objData.get("rentBikeStatus");
        JsonArray jsonArray = (JsonArray) data.get("row");

        int listTotalCount = Integer.parseInt(String.valueOf(data.get("list_total_count")));
        if (listTotalCount < 1000) {
          isRemain = false;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
          BikeParkingInfoApiDto bikeParkingInfoApiDto =
              gson.fromJson(jsonArray.get(i), BikeParkingInfoApiDto.class);

          BikeParkingInfoDto bikeParkingInfoDto = BikeParkingInfoDto.fromApiDto(bikeParkingInfoApiDto);
          set.add(TypedTuple.of(bikeParkingInfoDto.toJson(), bikeParkingInfoDto.getBikeParkingRate()));
        }
      }
    } catch (Exception e) {

      throwException(objData, BIKE_LIST_REDIS);
    }
    operations.add(REDIS_STATION.getKey(), set);
    redisTemplate.expire(REDIS_STATION.getKey(), Duration.ofMinutes(5));
  }

  void throwException(JsonObject objData, OpenApiDataType openApiDataType) {
    if (objData != null) {
      Gson gson = new Gson();
      JsonObject statusInfo = (JsonObject) objData.get("RESULT");
      StatusApiDto statusApiDto = gson.fromJson(statusInfo, StatusApiDto.class);
      throw new OpenApiException(openApiDataType, statusApiDto);
    }

    throw new StationException(OPEN_API_ERROR);
  }

  @Override
  public String getJsonString(String dataType, int start, int end) {

    URI uri = UriComponentsBuilder
        .fromUriString("http://openapi.seoul.go.kr:8088/")
        .path("/{apiKey}/json/{dataType}/{start}/{end}/")
        .encode()
        .build()
        .expand(apiKey, dataType, start, end)
        .toUri();

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
    return response.getBody();
  }
}
