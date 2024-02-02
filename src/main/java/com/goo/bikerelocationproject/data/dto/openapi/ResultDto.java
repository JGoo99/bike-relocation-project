package com.goo.bikerelocationproject.data.dto.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResultDto {

  @JsonProperty("CODE")
  private String code;

  @JsonProperty("MESSAGE")
  private String message;
}
