package com.goo.bikerelocationproject.service.impl;

import com.goo.bikerelocationproject.data.dto.BikeListApiDto;
import com.goo.bikerelocationproject.data.dto.BikeStationMasterApiDto;
import com.goo.bikerelocationproject.data.dto.ParsingResultDto;
import com.goo.bikerelocationproject.data.dto.StatusApiDto;
import com.goo.bikerelocationproject.data.entity.Station;
import com.goo.bikerelocationproject.repository.StationRepo;
import com.goo.bikerelocationproject.service.StationOpenApiService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationOpenApiServiceImpl implements StationOpenApiService {

  @Value("${bike-list-api-key}")
  private String apiKey;

  private final StationRepo stationRepo;

  HttpURLConnection con = null;
  StringBuilder sb = null;
  Gson gson = null;
  URL url = null;

  @Override
  public ParsingResultDto getOpenApiData() {

    ParsingResultDto result = new ParsingResultDto();

    getBikeListData(result);
    getBikeStationMasterData(result);

    return result;
  }

  @Override
  public void getBikeListData(ParsingResultDto result) {
    int pageNum = 0;
    boolean isRemain = true;

    List<Station> stations = new ArrayList<>();
    while (isRemain) {
      int start = 1000 * pageNum++ + 1;
      int end = 1000 * pageNum;

      String baseUrl = getBaseUrl("bikeList", start, end);
      sb = new StringBuilder();
      try {
        url = new URL(baseUrl);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-type", "application/json");
        con.setDoOutput(true);

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        while (br.ready()) {
          sb.append(br.readLine());
        }
        con.disconnect();

      } catch (Exception e) {
        e.printStackTrace();
      }
      gson = new Gson();

      JsonObject objData = (JsonObject) new JsonParser().parse(sb.toString());
      JsonObject data = (JsonObject) objData.get("rentBikeStatus");
      JsonObject statusInfo = (JsonObject) data.get("RESULT");
      JsonArray jsonArray = (JsonArray) data.get("row");

      int listTotalCount = Integer.parseInt(String.valueOf(data.get("list_total_count")));
      if (listTotalCount < 1000) {
        isRemain = false;
      }

      StatusApiDto statusApiDto = gson.fromJson(statusInfo, StatusApiDto.class);
      result.setBikeListTotalCount(result.getBikeListTotalCount() + listTotalCount);
      result.getCode().add("[bikeList]: " +  statusApiDto.getCode());
      result.getMessage().add("[bikeList]: " + statusApiDto.getMessage());

      for (int i = 0; i < jsonArray.size(); i++) {
        BikeListApiDto bikeListApiDto = gson.fromJson(jsonArray.get(i), BikeListApiDto.class);
        stations.add(BikeListApiDto.toEntity(bikeListApiDto));
      }
    }
    stationRepo.saveAll(stations);
  }

  @Override
  public void getBikeStationMasterData(ParsingResultDto result) {
    int pageNum = 0;
    boolean isRemain = true;

    while (isRemain) {
      int start = 1000 * pageNum++ + 1;
      int end = 1000 * pageNum;

      String baseUrl = getBaseUrl("bikeStationMaster", start, end);
      sb = new StringBuilder();
      try {
        url = new URL(baseUrl);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-type", "application/json");
        con.setDoOutput(true);

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        while (br.ready()) {
          sb.append(br.readLine());
        }
        con.disconnect();

      } catch (Exception e) {
        e.printStackTrace();
      }
      gson = new Gson();

      JsonObject objData = (JsonObject) new JsonParser().parse(sb.toString());
      JsonObject data = (JsonObject) objData.get("bikeStationMaster");
      JsonObject statusInfo = (JsonObject) data.get("RESULT");
      JsonArray jsonArray = (JsonArray) data.get("row");

      int listTotalCount = Integer.parseInt(String.valueOf(data.get("list_total_count")));
      if (listTotalCount < end) {
        isRemain = false;
      }

      StatusApiDto statusApiDto = gson.fromJson(statusInfo, StatusApiDto.class);
      result.getCode().add("[bikeStationMaster]: " + statusApiDto.getCode());
      result.getMessage().add("[bikeStationMaster]: " + statusApiDto.getMessage());

      int savedDataCount = 0;
      for (int i = 0; i < jsonArray.size(); i++) {
        BikeStationMasterApiDto bikeStationMasterApiDto =
            gson.fromJson(jsonArray.get(i), BikeStationMasterApiDto.class);

        Optional<Station> station =
            stationRepo.findById(
                Long.valueOf(bikeStationMasterApiDto.getStationId().substring(3)));

        if (station.isPresent()) {
          Station selectedStation = station.get();
          selectedStation.setAddress1(bikeStationMasterApiDto.getAddress1());
          selectedStation.setAddress2(bikeStationMasterApiDto.getAddress2());
          stationRepo.save(selectedStation);
          savedDataCount++;
        }
      }
      result.setSavedBikeStationMasterTotalCount(result.getSavedBikeStationMasterTotalCount() + savedDataCount);
    }
  }

  String getBaseUrl(String dataType, int start, int end) {
    StringBuilder baseUrl = new StringBuilder();

    baseUrl.append("http://openapi.seoul.go.kr:8088/");
    baseUrl.append(apiKey);
    baseUrl.append("/json/");
    baseUrl.append(dataType);
    baseUrl.append("/");
    baseUrl.append(start);
    baseUrl.append("/");
    baseUrl.append(end);
    baseUrl.append("/");

    return baseUrl.toString();
  }
}
