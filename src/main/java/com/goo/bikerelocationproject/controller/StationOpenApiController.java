package com.goo.bikerelocationproject.controller;

import com.goo.bikerelocationproject.data.dto.ParsingResultDto;
import com.goo.bikerelocationproject.service.impl.StationOpenApiServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StationOpenApiController {

  private final StationOpenApiServiceImpl stationApiService;

  @PostMapping("/open-api")
  public ResponseEntity<ParsingResultDto> getOpenApiData() {

    ParsingResultDto parsingResult = stationApiService.getOpenApiData();

    return ResponseEntity.ok(parsingResult);
  }

}