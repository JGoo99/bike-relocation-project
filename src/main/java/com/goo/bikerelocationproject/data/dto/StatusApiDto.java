package com.goo.bikerelocationproject.data.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StatusApiDto {

  @SerializedName("CODE")
  private String code;

  @SerializedName("MESSAGE")
  private String message;
}
