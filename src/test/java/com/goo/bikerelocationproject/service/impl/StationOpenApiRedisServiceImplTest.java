package com.goo.bikerelocationproject.service.impl;

import static com.goo.bikerelocationproject.type.OpenApiDataType.BIKE_LIST;
import static com.goo.bikerelocationproject.type.OpenApiDataType.BIKE_STATION_MASTER;
import static com.goo.bikerelocationproject.type.RedisKey.REDIS_STATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.goo.bikerelocationproject.data.dto.api.BikeListDto;
import com.goo.bikerelocationproject.data.dto.api.BikeListDto.RentBikeStatus.BikeListRowResponse;
import com.goo.bikerelocationproject.data.dto.BikeParkingInfoDto;
import com.goo.bikerelocationproject.data.dto.api.BikeStationMasterDto;
import com.goo.bikerelocationproject.data.dto.api.BikeStationMasterDto.BikeStationMaster.BikeStationMasterRowResponse;
import com.goo.bikerelocationproject.data.entity.Station;
import com.goo.bikerelocationproject.repository.StationRepo;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
  @DisplayName("성공: DB 파싱[BikeListDtoJson -> Station]")
  void success_getBikeListData() {
    // given
    // when
    List<Station> stations = new ArrayList<>();
    BikeListDto bikeListDto = getBikeListApiData(BIKE_LIST.getData(), 1, 5);

    int listTotalCount = bikeListDto.getRentBikeStatus().getListTotalCount();
    List<BikeListRowResponse> bikeListRowRespons = bikeListDto.getRentBikeStatus()
        .getRow();
    for (BikeListRowResponse bikeListRowResponse : bikeListRowRespons) {
      stations.add(Station.fromApiDto(bikeListRowResponse));
    }

    // then
    assertEquals(listTotalCount, 5);
    assertEquals(stations.get(0).getId(), 4L);
    assertEquals(stations.get(0).getStationName(), "102. 망원역 1번출구 앞");
    assertEquals(stations.get(0).getRackTotalCount(), 15);
  }

  @Test
  @DisplayName("실패: DB 파싱[BikeListDtoJson -> Station]")
  void fail_getBikeListData() {
    // given
    // when
    BikeListDto bikeListDto = getBikeListApiData(BIKE_LIST.getData(), 1, -1);

    // then
    assertNull(bikeListDto.getRentBikeStatus());
    assertEquals(bikeListDto.getResult().getCode(), "ERROR-334");
    assertEquals(bikeListDto.getResult().getMessage(), "요청종료위치 보다 요청시작위치가 더 큽니다.\n"
        + "요청시작위치 정수 값은 요청종료위치 정수 값보다 같거나 작아야 합니다.");
  }

  @Test
  @DisplayName("성공: DB 파싱[BikeStationMasterJson -> Station]")
  void success_getBikeStationMasterData() {
    // given
    Station station = Station.builder().id(10L).build();
    stationRepo.save(station);

    // when
    BikeStationMasterDto bikeStationMasterApiDto = getBikeStationMaterData(
        BIKE_STATION_MASTER.getData(), 1, 1);

    int listTotalCount = bikeStationMasterApiDto.getBikeStationMaster().getListTotalCount();

    List<BikeStationMasterRowResponse> dataList = bikeStationMasterApiDto.getBikeStationMaster()
        .getRow();

    Optional<Station> st = stationRepo.findById(10L);
    if (st.isPresent()) {
      Station selectedStation = st.get();
      selectedStation.setAddress1(dataList.get(0).getAddress1());
      selectedStation.setAddress2(dataList.get(0).getAddress2());

      stationRepo.save(selectedStation);
    }

    Optional<Station> savedStation = stationRepo.findById(10L);

    // then
    assertEquals(listTotalCount, 3270);
    assertEquals(savedStation.get().getId(), 10L);
    assertEquals(savedStation.get().getAddress1(), "서울특별시 마포구 양화로 93");
    assertEquals(savedStation.get().getAddress2(), "427");
  }

  @Test
  @DisplayName("실패: DB 파싱[BikeListDtoJson -> Station]")
  void fail_getBikeStationMasterData() {
    // given
    // when
    BikeStationMasterDto bikeStationMasterDto = getBikeStationMaterData(
        BIKE_STATION_MASTER.getData(), 1, -1);

    // then
    assertNull(bikeStationMasterDto.getBikeStationMaster());
    assertEquals(bikeStationMasterDto.getResult().getCode(), "ERROR-334");
    assertEquals(bikeStationMasterDto.getResult().getMessage(),
        "요청종료위치 보다 요청시작위치가 더 큽니다.\n"
            + "요청시작위치 정수 값은 요청종료위치 정수 값보다 같거나 작아야 합니다.");
  }

  @Test
  @DisplayName("Redis 파싱[BikeListJson -> redis]")
  void getBikeListRedisData() {
    // given
    ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
    Set<TypedTuple<String>> set = new HashSet<>();

    BikeListDto bikeListDto = getBikeListApiData(BIKE_LIST.getData(), 1, 5);

    // when
    List<BikeListRowResponse> dataList = bikeListDto.getRentBikeStatus().getRow();
    for (BikeListRowResponse data : dataList) {
      set.add(TypedTuple.of(data.getStationId().substring(3), data.getBikeParkingRate()));
    }

    Long count = operations.add(REDIS_STATION.getKey(), set);
    redisTemplate.expire(REDIS_STATION.getKey(), Duration.ofSeconds(1));

    // then
    assertEquals(count, 5);
  }

  @Test
  @DisplayName("Redis 테스트[redis-get]")
  void get() {
    // given
    Long stationId = 320L;
    double bikeParkingRate = 120;
    BikeParkingInfoDto bikeParkingInfoDto = new BikeParkingInfoDto(stationId,
        bikeParkingRate);

    // when
    ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
    zSetOperations.add(REDIS_STATION.getKey(), String.valueOf(bikeParkingInfoDto.getStationId()),
        bikeParkingRate);
    redisTemplate.expire(REDIS_STATION.getKey(), Duration.ofSeconds(1));

    // when
    Set<TypedTuple<String>> typedTuples = zSetOperations.rangeByScoreWithScores(
        REDIS_STATION.getKey(), 100, 1000);

    List<BikeParkingInfoDto> list = new ArrayList<>();
    typedTuples.forEach(m -> list.add(BikeParkingInfoDto.fromRedisData(m)));

    assertEquals(list.size(), 1);
    assertEquals(list.get(0).getStationId(), stationId);
    assertEquals(list.get(0).getBikeParkingRate(), bikeParkingRate);
  }

  public BikeListDto getBikeListApiData(String dataType, int start, int end) {

    return webClient.get().uri(uriBuilder ->
            uriBuilder
                .pathSegment(dataType, String.valueOf(start), String.valueOf(end))
                .path("/")
                .build())
        .retrieve()
        .bodyToMono(BikeListDto.class).block();
  }

  public BikeStationMasterDto getBikeStationMaterData(String dataType, int start,
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