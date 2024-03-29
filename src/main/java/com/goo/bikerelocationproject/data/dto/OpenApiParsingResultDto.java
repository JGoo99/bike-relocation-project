package com.goo.bikerelocationproject.data.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OpenApiParsingResultDto {

  private int bikeListTotalCount;
  private int savedBikeStationMasterTotalCount;
}