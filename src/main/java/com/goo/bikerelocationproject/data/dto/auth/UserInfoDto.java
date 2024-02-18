package com.goo.bikerelocationproject.data.dto.auth;

import com.goo.bikerelocationproject.data.entity.CustomUser;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class UserInfoDto {

  private Long id;
  private String username;
  private String password;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static UserInfoDto fromEntity(CustomUser user) {
    return UserInfoDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .password(user.getPassword())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }
}
