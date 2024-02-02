package com.goo.bikerelocationproject.data.entity;

import com.goo.bikerelocationproject.data.dto.openapi.BikeListDto.RentBikeStatus.BikeListRowResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Station {

  @Id
  private Long id;
  private int rackTotalCount;
  private String stationName;
  private String address1;
  private String address2;

  public static Station fromApiDto(BikeListRowResponse dto) {
    return Station.builder()
        .id(Long.parseLong(dto.getStationId().substring(3)))
        .rackTotalCount(dto.getRackTotalCount())
        .stationName(dto.getStationName())
        .build();
  }
}
