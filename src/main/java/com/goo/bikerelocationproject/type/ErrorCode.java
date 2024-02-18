package com.goo.bikerelocationproject.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  OPEN_API_ERROR("open-api: 데이터 저장에 실패했습니다."),
  REDIS_NULL("redis 에서 값을 불러오지 못했습니다."),
  NOT_FOUND_STATION("대여소 정보가 존재하지 않습니다."),
  NOT_FOUND_USER("유저 정보가 존재하지 않습니다."),
  ALREADY_EXIST_USERNAME("이미 존재하는 아이디입니다.");

  private final String description;
}
