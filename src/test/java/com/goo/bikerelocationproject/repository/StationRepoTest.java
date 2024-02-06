package com.goo.bikerelocationproject.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.goo.bikerelocationproject.data.dto.StationInfoDto;
import com.goo.bikerelocationproject.data.entity.Station;
import com.goo.bikerelocationproject.type.RedisKey;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

@SpringBootTest
@Transactional
class StationRepoTest {

  @Autowired
  private StationRepo stationRepo;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;


  @Test
  void getAllStationWithPaging() {
    // given
    ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
    saveExampleRedisSet(zSetOperations, getExampleStationDataSet());

    stationRepo.save(getExampleStationEntity1());
    stationRepo.save(getExampleStationEntity2());

    // when
    Set<TypedTuple<String>> selected = zSetOperations.reverseRangeByScoreWithScores(
        RedisKey.REDIS_STATION.getKey(), 100, Double.MAX_VALUE, 0, 2);

    System.out.println(selected.size());
    List<StationInfoDto> stationInfoDtoList = new ArrayList<>();

    Objects.requireNonNull(selected).forEach(m -> {
      Optional<Station> station = stationRepo.findById(Long.parseLong(
          Objects.requireNonNull(m.getValue())));

      if (station.isPresent()) {
        stationInfoDtoList.add(StationInfoDto.fromEntity(station.get(), m.getScore()));
      }
    });

    // then
    assertEquals(2, stationInfoDtoList.size());
    assertEquals(2, stationInfoDtoList.get(0).getId());
    assertEquals(10, stationInfoDtoList.get(0).getRackTotalCount());
    assertEquals(27, stationInfoDtoList.get(0).getParkingBikeTotalCount());
    assertEquals(270.0, stationInfoDtoList.get(0).getBikeParkingRate());
  }

  @Test
  void getDetail() {
    // given
    ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
    Set<TypedTuple<String>> set = getExampleStationDataSet();
    saveExampleRedisSet(operations, set);

    Station station = stationRepo.save(getExampleStationEntity1());

    // when
    double score = operations.score(RedisKey.REDIS_STATION.getKey(),
        String.valueOf(station.getId()));

    // then
    assertEquals(score, 220.0);
  }

  Set<TypedTuple<String>> getExampleStationDataSet() {
    Set<TypedTuple<String>> set = new HashSet<>();

    set.add(TypedTuple.of("1", 220.0));
    set.add(TypedTuple.of("2", 270.0));

    return set;
  }

  void saveExampleRedisSet(ZSetOperations<String, String> operations, Set<TypedTuple<String>> set) {
    operations.add(RedisKey.REDIS_STATION.getKey(), set);
    redisTemplate.expire(RedisKey.REDIS_STATION.getKey(), Duration.ofSeconds(1));
  }

  Station getExampleStationEntity1() {
    return Station.builder()
        .id(1L)
        .stationName("3720. 우이천 창번교")
        .rackTotalCount(5)
        .build();
  }

  Station getExampleStationEntity2() {
    return Station.builder()
        .id(2L)
        .stationName("3753. 공항중학교 버스정류장")
        .rackTotalCount(10)
        .build();
  }
}