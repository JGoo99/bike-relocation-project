package com.goo.bikerelocationproject.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisKey {

  REDIS_STATION("station");

  private final String key;
}
