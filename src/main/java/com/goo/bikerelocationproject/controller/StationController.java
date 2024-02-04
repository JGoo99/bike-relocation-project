package com.goo.bikerelocationproject.controller;

import com.goo.bikerelocationproject.data.dto.StationInfoDto;
import com.goo.bikerelocationproject.service.impl.StationServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/st")
public class StationController {

  private final StationServiceImpl stationService;

  @GetMapping("/list")
  public ResponseEntity<List<StationInfoDto>> getList(Pageable pageable) {

    return ResponseEntity.ok(stationService.getAllStation(pageable));
  }

  @GetMapping("/detail")
  public ResponseEntity<StationInfoDto> getDetail(Long stationId) {

    return ResponseEntity.ok(stationService.getDetails(stationId));
  }
}
