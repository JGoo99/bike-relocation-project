package com.goo.bikerelocationproject.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  OPEN_API_ERROR("open-api 데이터를 가져오는 것에 실패했습니다.");

  private final String description;
}
