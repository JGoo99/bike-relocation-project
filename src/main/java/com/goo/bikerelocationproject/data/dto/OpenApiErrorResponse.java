package com.goo.bikerelocationproject.data.dto;

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
public class OpenApiErrorResponse {

  private String dataType;
  private String errorCode;
  private String errorMessage;
}
