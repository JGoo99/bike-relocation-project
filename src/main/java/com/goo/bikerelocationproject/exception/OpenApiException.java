package com.goo.bikerelocationproject.exception;

import com.goo.bikerelocationproject.data.dto.StatusApiDto;
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

  public OpenApiException(OpenApiDataType openApiDataType, StatusApiDto statusApiDto) {
    this.openApiDataType = openApiDataType.getData();
    this.code = statusApiDto.getCode();
    this.message = statusApiDto.getMessage();
  }
}
