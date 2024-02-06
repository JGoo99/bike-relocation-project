package com.goo.bikerelocationproject.service;

import com.goo.bikerelocationproject.data.dto.StationInfoDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface StationService {

  List<StationInfoDto> getAllStation(Pageable pageable);

  StationInfoDto getDetails(Long stationId);
}
