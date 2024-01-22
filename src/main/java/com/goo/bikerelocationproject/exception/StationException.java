package com.goo.bikerelocationproject.exception;

import com.goo.bikerelocationproject.type.ErrorCode;
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
public class StationException extends RuntimeException {

  private ErrorCode code;
  private String message;

  public StationException(ErrorCode errorCode) {
    this.code = errorCode;
    this.message = errorCode.getDescription();
  }
}
