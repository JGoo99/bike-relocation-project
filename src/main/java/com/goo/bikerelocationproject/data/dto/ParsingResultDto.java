package com.goo.bikerelocationproject.data.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ParsingResultDto {

  private int bikeListTotalCount;
  private int savedBikeStationMasterTotalCount;
}