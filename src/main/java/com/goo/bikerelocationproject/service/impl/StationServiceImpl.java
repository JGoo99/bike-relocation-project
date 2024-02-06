package com.goo.bikerelocationproject.service.impl;

import static com.goo.bikerelocationproject.type.ErrorCode.REDIS_NULL;
import static com.goo.bikerelocationproject.type.RedisKey.REDIS_STATION;

import com.goo.bikerelocationproject.data.dto.StationInfoDto;
import com.goo.bikerelocationproject.data.entity.Station;
import com.goo.bikerelocationproject.exception.StationException;
import com.goo.bikerelocationproject.repository.StationRepo;
import com.goo.bikerelocationproject.service.StationService;
import com.goo.bikerelocationproject.type.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {

  private final StationRepo stationRepo;
  private final ZSetOperations<String, String> zSetOperations;

  @Override
  public List<StationInfoDto> getAllStation(Pageable pageable) {

    Set<TypedTuple<String>> selectedRedisDataSet = getReverseSortedRedis(pageable);

    List<StationInfoDto> stationInfoDtoList = new ArrayList<>();

    for (TypedTuple<String> set : selectedRedisDataSet) {
      Station station = stationRepo.findById(Long.parseLong(set.getValue()))
          .orElseThrow(() -> new StationException(REDIS_NULL));
      stationInfoDtoList.add(StationInfoDto.fromEntity(station, set.getScore()));
    }

    return stationInfoDtoList;
  }

  @Override
  public StationInfoDto getDetails(Long stationId) {

    Station station = stationRepo.findById(stationId)
        .orElseThrow(() -> new StationException(ErrorCode.NOT_FOUND_STATION));

    double bikeParkingRate = zSetOperations.score(REDIS_STATION.getKey(),
        String.valueOf(stationId));

    return StationInfoDto.fromEntity(station, bikeParkingRate);
  }

  Set<TypedTuple<String>> getReverseSortedRedis(Pageable pageable) {

    Set<TypedTuple<String>> set = zSetOperations.reverseRangeByScoreWithScores(
        REDIS_STATION.getKey(), 100, Double.MAX_VALUE, pageable.getPageNumber(),
        pageable.getPageSize());

    if (set == null || set.size() == 0) {
      throw new StationException(REDIS_NULL);
    }

    return set;
  }

}
