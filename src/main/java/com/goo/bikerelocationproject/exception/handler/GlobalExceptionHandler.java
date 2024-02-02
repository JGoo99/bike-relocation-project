package com.goo.bikerelocationproject.exception.handler;

import com.goo.bikerelocationproject.data.dto.ApiErrorResponse;
import com.goo.bikerelocationproject.data.dto.StationErrorResponse;
import com.goo.bikerelocationproject.exception.OpenApiException;
import com.goo.bikerelocationproject.exception.StationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(OpenApiException.class)
  public ApiErrorResponse handleOpenApiException(OpenApiException e) {
    LOGGER.error("[open-api error]: " + e);

    return new ApiErrorResponse(e.getOpenApiDataType(), e.getCode(), e.getMessage());
  }

  @ExceptionHandler(StationException.class)
  public StationErrorResponse handleStationException(StationException e) {
    LOGGER.error("[station error]: " + e);

    return new StationErrorResponse(e.getCode(), e.getMessage());
  }
}
