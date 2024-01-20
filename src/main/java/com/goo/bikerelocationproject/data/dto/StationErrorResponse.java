package com.goo.bikerelocationproject.data.dto;

import com.goo.bikerelocationproject.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StationErrorResponse {

  private ErrorCode errorCode;
  private String errorMessage;
}
