package com.goo.bikerelocationproject.data.dto.auth;

import com.goo.bikerelocationproject.data.entity.CustomUser;
import com.goo.bikerelocationproject.type.Roles;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class JoinDto {

  private String username;
  private String email;
  private String password;

  public static CustomUser toEntity(JoinDto joinDto) {
    return CustomUser.builder()
        .username(joinDto.getUsername())
        .email(joinDto.getEmail())
        .role(Roles.USER.getRole())
        .build();
  }
}
