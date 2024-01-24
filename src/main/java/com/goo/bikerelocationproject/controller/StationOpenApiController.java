package com.goo.bikerelocationproject.controller;

import com.goo.bikerelocationproject.data.dto.ParsingResultDto;
import com.goo.bikerelocationproject.service.impl.StationOpenApiServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StationOpenApiController {

  private final StationOpenApiServiceImpl stationApiService;

  @Scheduled(cron = "0 0 0 * * *")
  @PostMapping("/open-api-station")
  public ResponseEntity<ParsingResultDto> saveOpenApiData() {

    ParsingResultDto parsingResult = stationApiService.saveOpenApiData();

    return ResponseEntity.ok(parsingResult);
  }

  @Scheduled(cron = "0 0/5 0 * * *")
  @PostMapping("/open-api-parking")
  public ResponseEntity<Boolean> saveOpenApiRedisData() {

    stationApiService.saveBikeParkingData();

    return ResponseEntity.ok(true);
  }
}