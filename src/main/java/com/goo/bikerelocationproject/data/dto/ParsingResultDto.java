package com.goo.bikerelocationproject.data.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ParsingResultDto {

  int bikeListTotalCount;
  int savedBikeStationMasterTotalCount;
  List<String> code = new ArrayList<>();
  List<String> message = new ArrayList<>();
}