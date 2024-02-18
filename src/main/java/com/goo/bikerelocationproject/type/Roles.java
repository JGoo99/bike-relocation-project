package com.goo.bikerelocationproject.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Roles {

  USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

  private final String role;
}
