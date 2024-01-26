package com.goo.bikerelocationproject.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;

@Configuration
public class WebClientConfig {

  @Value("${bike-list-api-key}")
  private String API_KEY;

  private static final String BASE_URL = "http://openapi.seoul.go.kr:8088/{KEY}/{TYPE}";

  @Bean
  public WebClient stationWebClient() {
    DefaultUriBuilderFactory builderFactory = new DefaultUriBuilderFactory(BASE_URL);
    builderFactory.setEncodingMode(EncodingMode.URI_COMPONENT);

    Map<String, String> map = new HashMap<>();
    map.put("KEY", API_KEY);
    map.put("TYPE", "json");

    builderFactory.setDefaultUriVariables(map);

    return WebClient.builder()
        .uriBuilderFactory(builderFactory)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
}
