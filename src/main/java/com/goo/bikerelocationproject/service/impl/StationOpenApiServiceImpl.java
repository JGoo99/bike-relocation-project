package com.goo.bikerelocationproject.service.impl;

import static com.goo.bikerelocationproject.type.ErrorCode.OPEN_API_ERROR;
import static com.goo.bikerelocationproject.type.OpenApiDataType.BIKE_LIST;
import static com.goo.bikerelocationproject.type.OpenApiDataType.BIKE_LIST_REDIS;
import static com.goo.bikerelocationproject.type.OpenApiDataType.BIKE_STATION_MASTER;
import static com.goo.bikerelocationproject.type.RedisKey.REDIS_STATION;

import com.goo.bikerelocationproject.data.dto.api.ResultDto;
import com.goo.bikerelocationproject.data.dto.api.BikeListDto;
import com.goo.bikerelocationproject.data.dto.api.BikeListDto.RentBikeStatus.BikeListRowResponse;
import com.goo.bikerelocationproject.data.dto.api.BikeStationMasterDto;
import com.goo.bikerelocationproject.data.dto.api.BikeStationMasterDto.BikeStationMaster.BikeStationMasterRowResponse;
import com.goo.bikerelocationproject.data.dto.ApiParsingResultDto;
import com.goo.bikerelocationproject.data.entity.Station;
import com.goo.bikerelocationproject.exception.OpenApiException;
import com.goo.bikerelocationproject.exception.StationException;
import com.goo.bikerelocationproject.repository.StationRepo;
import com.goo.bikerelocationproject.service.StationOpenApiService;
import com.goo.bikerelocationproject.type.OpenApiDataType;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class StationOpenApiServiceImpl implements StationOpenApiService {

  private final StationRepo stationRepo;
  private final RedisTemplate<String, String> redisTemplate;
  private final WebClient webClient;

  private final Logger LOGGER = LoggerFactory.getLogger(StationOpenApiServiceImpl.class);

  @Override
  public ApiParsingResultDto saveOpenApiData() {

    ApiParsingResultDto result = new ApiParsingResultDto();
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
    BikeListDto bikeListDto = null;
    ResultDto resultResponse = null;
    try {
      while (isRemain) {
        int start = 1000 * pageNum++ + 1;
        int end = 1000 * pageNum;

        bikeListDto = getBikeListData(BIKE_LIST.getData(), start, end);
        if (bikeListDto.getRentBikeStatus() == null) {
          resultResponse = bikeListDto.getResult();
        } else {
          resultResponse = bikeListDto.getRentBikeStatus().getResult();
        }

        int listTotalCount = bikeListDto.getRentBikeStatus().getListTotalCount();
        if (listTotalCount < 1000) {
          isRemain = false;
        }
        bikeListTotalCount += listTotalCount;

        List<BikeListRowResponse> dataList = bikeListDto.getRentBikeStatus().getRow();
        for (BikeListRowResponse data : dataList) {
          stations.add(Station.fromApiDto(data));
        }
      }
    } catch (Exception e) {

      throwException2(resultResponse, BIKE_LIST);
    }
    stationRepo.saveAll(stations);

    return bikeListTotalCount;
  }

  @Override
  public int saveBikeStationMasterData() {
    int bikeStationMasterTotalCount = 0;

    int pageNum = 0;
    boolean isRemain = true;

    BikeStationMasterDto bikeStationMasterDto = null;
    ResultDto resultResponse = null;
    try {
      while (isRemain) {
        int start = 1000 * pageNum++ + 1;
        int end = 1000 * pageNum;

        bikeStationMasterDto = getBikeStationMasterData(
            BIKE_STATION_MASTER.getData(), start, end);
        if (bikeStationMasterDto.getBikeStationMaster() == null) {
          resultResponse = bikeStationMasterDto.getResult();
        } else {
          bikeStationMasterDto.getBikeStationMaster().getResult();
        }

        int listTotalCount = bikeStationMasterDto.getBikeStationMaster()
            .getListTotalCount();
        if (listTotalCount < end) {
          isRemain = false;
        }

        List<BikeStationMasterRowResponse> dataList = bikeStationMasterDto.getBikeStationMaster()
            .getRow();
        for (int i = 0; i < dataList.size(); i++) {

          Optional<Station> station =
              stationRepo.findById(
                  Long.valueOf(dataList.get(i).getStationId().substring(3)));

          if (station.isPresent()) {
            Station selectedStation = station.get();
            selectedStation.setAddress1(dataList.get(i).getAddress1());
            selectedStation.setAddress2(dataList.get(i).getAddress2());
            stationRepo.save(selectedStation);
            bikeStationMasterTotalCount++;
          }
        }
      }
    } catch (Exception e) {

      throwException2(resultResponse, BIKE_STATION_MASTER);
    }
    return bikeStationMasterTotalCount;
  }

  @Override
  public void saveBikeParkingData() {

    int pageNum = 0;
    boolean isRemain = true;

    ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
    Set<TypedTuple<String>> set = new HashSet<>();
    BikeListDto bikeListDto = null;
    ResultDto resultResponse = null;
    try {
      while (isRemain) {
        int start = 1000 * pageNum++ + 1;
        int end = 1000 * pageNum;

        bikeListDto = getBikeListData(BIKE_LIST.getData(), start, end);
        if (bikeListDto.getRentBikeStatus() == null) {
          resultResponse = bikeListDto.getResult();
        } else {
          resultResponse = bikeListDto.getRentBikeStatus().getResult();
        }

        int listTotalCount = bikeListDto.getRentBikeStatus().getListTotalCount();
        if (listTotalCount < 1000) {
          isRemain = false;
        }

        List<BikeListRowResponse> dataList = bikeListDto.getRentBikeStatus().getRow();
        for (BikeListRowResponse data : dataList) {
          set.add(TypedTuple.of(data.getStationId().substring(3), data.getBikeParkingRate()));
        }
      }
    } catch (Exception e) {

      throwException2(resultResponse, BIKE_LIST_REDIS);
    }
    operations.add(REDIS_STATION.getKey(), set);
    redisTemplate.expire(REDIS_STATION.getKey(), Duration.ofMinutes(5));
  }

  void throwException2(ResultDto errorResponse, OpenApiDataType openApiDataType) {

    if (errorResponse != null) {
      throw new OpenApiException(errorResponse, openApiDataType);
    }
    throw new StationException(OPEN_API_ERROR);
  }

  @Override
  public BikeListDto getBikeListData(String dataType, int start, int end) {

    return webClient.get().uri(uriBuilder ->
            uriBuilder
                .pathSegment(dataType, String.valueOf(start), String.valueOf(end))
                .path("/")
                .build())
        .retrieve()
        .bodyToMono(BikeListDto.class).block();
  }

  @Override
  public BikeStationMasterDto getBikeStationMasterData(String dataType, int start,
      int end) {

    return webClient.get().uri(uriBuilder ->
            uriBuilder
                .pathSegment(dataType, String.valueOf(start), String.valueOf(end))
                .path("/")
                .build())
        .retrieve()
        .bodyToMono(BikeStationMasterDto.class).block();
  }
}
