package com.goo.bikerelocationproject.service.impl;

import static com.goo.bikerelocationproject.type.OpenApiDataType.BIKE_LIST;
import static com.goo.bikerelocationproject.type.OpenApiDataType.BIKE_STATION_MASTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.goo.bikerelocationproject.data.dto.BikeListApiDto;
import com.goo.bikerelocationproject.data.dto.BikeStationMasterApiDto;
import com.goo.bikerelocationproject.data.entity.Station;
import com.goo.bikerelocationproject.repository.StationRepo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
@Transactional
class StationOpenApiServiceImplTest {

  @Autowired
  private StationRepo stationRepo;

  private final String baseUrl = "http://openapi.seoul.go.kr:8088/";

  @Test
  @DisplayName("DB 파싱[BikeListDtoJson -> Station]")
  void getBikeListData_parsingCheck() {

    // given
    // when
    List<Station> stations = new ArrayList<>();
    JsonObject objData = null;

    Gson gson = new Gson();
    objData = (JsonObject) new JsonParser().parse(getJsonString(BIKE_LIST.getData(), 1, 5));
    JsonObject data = (JsonObject) objData.get("rentBikeStatus");
    JsonArray jsonArray = (JsonArray) data.get("row");

    int listTotalCount = Integer.parseInt(String.valueOf(data.get("list_total_count")));

    for (int i = 0; i < jsonArray.size(); i++) {
      BikeListApiDto bikeListApiDto = gson.fromJson(jsonArray.get(i), BikeListApiDto.class);
      stations.add(BikeListApiDto.toEntity(bikeListApiDto));
    }

    // then
    // System.out.println(sb.toString());
    assertEquals(listTotalCount, 5);
    assertEquals(stations.get(0).getId(), 4L);
    assertEquals(stations.get(0).getStationName(), "102. 망원역 1번출구 앞");
    assertEquals(stations.get(0).getRackTotalCount(), 15);
  }

  @Test
  @DisplayName("DB 파싱[BikeStationMasterJson -> Station]")
  void getBikeStationMasterData_parsingCheck() {

    // given
    Station station = Station.builder().id(10L).build();
    stationRepo.save(station);

    // when
    Gson gson = new Gson();
    JsonObject objData = (JsonObject) new JsonParser().parse(getJsonString(BIKE_STATION_MASTER.getData(), 1, 5));
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

  public String getJsonString(String dataType, int start, int end) {
    URI uri = UriComponentsBuilder
        .fromUriString(baseUrl)
        .path("/{apiKey}/json/{dataType}/{start}/{end}/")
        .encode()
        .build()
        .expand("sample", dataType, start, end)
        .toUri();

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
    return response.getBody();
  }
}