package com.goo.bikerelocationproject.exception;

import com.goo.bikerelocationproject.data.dto.api.ResultDto;
import com.goo.bikerelocationproject.type.OpenApiDataType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class OpenApiException extends RuntimeException {

  private String openApiDataType;
  private String code;
  private String message;

  public OpenApiException(ResultDto response, OpenApiDataType openApiDataType) {
    this.openApiDataType = openApiDataType.getData();
    this.code = response.getCode();
    this.message = response.getMessage();
  }
}
