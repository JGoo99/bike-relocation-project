package com.goo.bikerelocationproject.service.impl;

import static com.goo.bikerelocationproject.type.OpenApiDataType.BIKE_LIST;
import static com.goo.bikerelocationproject.type.OpenApiDataType.BIKE_STATION_MASTER;
import static com.goo.bikerelocationproject.type.RedisKey.REDIS_STATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.goo.bikerelocationproject.data.dto.BikeListApiDto;
import com.goo.bikerelocationproject.data.dto.BikeParkingInfoApiDto;
import com.goo.bikerelocationproject.data.dto.BikeParkingInfoRedisDto;
import com.goo.bikerelocationproject.data.dto.BikeStationMasterApiDto;
import com.goo.bikerelocationproject.data.entity.Station;
import com.goo.bikerelocationproject.repository.StationRepo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootTest
@Transactional
class StationOpenApiRedisServiceImplTest {

  @Autowired
  RedisTemplate<String, String> redisTemplate;

  @Autowired
  private StationRepo stationRepo;

  @Autowired
  private WebClient webClient;

  @Test
  @DisplayName("DB 파싱[BikeListDtoJson -> Station]")
  void getBikeListData() {
    // given
    // when
    Gson gson = new Gson();
    List<Station> stations = new ArrayList<>();

    JsonObject objData = (JsonObject) JsonParser.parseString(
        Objects.requireNonNull(getData(BIKE_LIST.getData(), 1, 5).block()));
    JsonObject data = (JsonObject) objData.get("rentBikeStatus");
    JsonArray jsonArray = (JsonArray) data.get("row");

    int listTotalCount = Integer.parseInt(String.valueOf(data.get("list_total_count")));

    for (int i = 0; i < jsonArray.size(); i++) {
      BikeListApiDto bikeListApiDto = gson.fromJson(jsonArray.get(i), BikeListApiDto.class);
      stations.add(BikeListApiDto.toEntity(bikeListApiDto));
    }

    // then
    assertEquals(listTotalCount, 5);
    assertEquals(stations.get(0).getId(), 4L);
    assertEquals(stations.get(0).getStationName(), "102. 망원역 1번출구 앞");
    assertEquals(stations.get(0).getRackTotalCount(), 15);
  }

  @Test
  @DisplayName("DB 파싱[BikeStationMasterJson -> Station]")
  void getBikeStationMasterData() {

    // given
    Station station = Station.builder().id(10L).build();
    stationRepo.save(station);

    // when
    Gson gson = new Gson();
    JsonObject objData = (JsonObject) JsonParser.parseString(
        Objects.requireNonNull(getData(BIKE_STATION_MASTER.getData(), 1, 5).block()));
    JsonObject data = (JsonObject) objData.get("bikeStationMaster");
    JsonArray jsonArray = (JsonArray) data.get("row");

    int listTotalCount = Integer.parseInt(String.valueOf(data.get("list_total_count")));

    BikeStationMasterApiDto bikeStationMasterApiDto =
        gson.fromJson(jsonArray.get(0), BikeStationMasterApiDto.class);

    Optional<Station> st = stationRepo.findById(10L);
    if (st.isPresent()) {
      Station selectedStation = st.get();
      selectedStation.setAddress1(bikeStationMasterApiDto.getAddress1());
      selectedStation.setAddress2(bikeStationMasterApiDto.getAddress2());
    }

    Optional<Station> savedStation = stationRepo.findById(10L);

    // then
    assertEquals(listTotalCount, 3270);
    assertEquals(savedStation.get().getId(), 10L);
    assertEquals(savedStation.get().getAddress1(), "서울특별시 마포구 양화로 93");
    assertEquals(savedStation.get().getAddress2(), "427");
  }

  @Test
  @DisplayName("Redis 파싱[BikeListJson -> redis]")
  void getBikeListRedisData() {
    // given
    ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
    Set<TypedTuple<String>> set = new HashSet<>();

    Gson gson = new Gson();
    JsonObject objData = (JsonObject) JsonParser.parseString(
        Objects.requireNonNull(getData(BIKE_LIST.getData(), 1, 5).block()));
    JsonObject data = (JsonObject) objData.get("rentBikeStatus");
    JsonArray jsonArray = (JsonArray) data.get("row");

    // when
    for (int i = 0; i < jsonArray.size(); i++) {
      BikeParkingInfoApiDto bikeParkingInfoApiDto =
          gson.fromJson(jsonArray.get(i), BikeParkingInfoApiDto.class);

      BikeParkingInfoRedisDto bikeParkingInfoRedisDto = BikeParkingInfoRedisDto.fromApiDto(bikeParkingInfoApiDto);
      set.add(TypedTuple.of(bikeParkingInfoRedisDto.getStationId(), bikeParkingInfoRedisDto.getBikeParkingRate()));
    }

    Long count = operations.add(REDIS_STATION.getKey(), set);
    redisTemplate.expire(REDIS_STATION.getKey(), Duration.ofSeconds(1));

    // then
    assertEquals(count, 5);
  }

  @Test
  @DisplayName("Redis 테스트[save-single]")
  void saveSingle() {
    // given
    String stationId = "320";
    double bikeParkingRate = 120;
    BikeParkingInfoRedisDto bikeParkingInfoRedisDto = new BikeParkingInfoRedisDto(stationId, bikeParkingRate);

    // when
    ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
    boolean isSaved = zSetOperations.add(REDIS_STATION.getKey(), bikeParkingInfoRedisDto.getStationId(),
        bikeParkingRate);
    redisTemplate.expire(REDIS_STATION.getKey(), Duration.ofSeconds(1));

    // then
    assertTrue(isSaved);
  }

  @Test
  @DisplayName("Redis 테스트[save-all]")
  void saveAll() {
    // given
    double bikeParkingRate = 120;
    BikeParkingInfoRedisDto bikeParkingInfoRedisDto = new BikeParkingInfoRedisDto("320", bikeParkingRate);
    double bikeParkingRate2 = 98;
    BikeParkingInfoRedisDto bikeParkingInfoRedisDto2 = new BikeParkingInfoRedisDto("104", bikeParkingRate2);

    ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
    Set<TypedTuple<String>> set = new HashSet<>();
    set.add(TypedTuple.of(bikeParkingInfoRedisDto.getStationId(), bikeParkingRate));
    set.add(TypedTuple.of(bikeParkingInfoRedisDto2.getStationId(), bikeParkingRate2));
    Long count = zSetOperations.add(REDIS_STATION.getKey(), set);
    redisTemplate.expire(REDIS_STATION.getKey(), Duration.ofSeconds(1));

    assertEquals(count, 2L);
  }

  @Test
  @DisplayName("Redis 테스트[redis-get]")
  void get() {
    // given
    String stationId = "320";
    double bikeParkingRate = 120;
    BikeParkingInfoRedisDto bikeParkingInfoRedisDto = new BikeParkingInfoRedisDto(stationId, bikeParkingRate);

    // when
    ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
    zSetOperations.add(REDIS_STATION.getKey(), bikeParkingInfoRedisDto.getStationId(), bikeParkingRate);
    redisTemplate.expire(REDIS_STATION.getKey(), Duration.ofSeconds(1));

    // when
    Set<TypedTuple<String>> typedTuples = zSetOperations.rangeByScoreWithScores(
        REDIS_STATION.getKey(), 100, 1000);

    List<BikeParkingInfoRedisDto> list = new ArrayList<>();
    typedTuples.forEach(m -> list.add(BikeParkingInfoRedisDto.fromRedisData(m)));

    assertEquals(list.size(), 1);
    assertEquals(list.get(0).getStationId(), stationId);
    assertEquals(list.get(0).getBikeParkingRate(), bikeParkingRate);
  }

  public Mono<String> getData(String dataType, int start, int end) {

    return webClient.get().uri(uriBuilder ->
            uriBuilder
                .pathSegment(dataType, String.valueOf(start), String.valueOf(end))
                .path("/")
                .build())
        .retrieve()
        .bodyToMono(String.class);
  }
}