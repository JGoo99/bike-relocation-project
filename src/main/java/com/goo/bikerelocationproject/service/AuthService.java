package com.goo.bikerelocationproject.service;

import com.goo.bikerelocationproject.data.dto.auth.JoinDto;
import com.goo.bikerelocationproject.data.dto.auth.UserInfoDto;

public interface AuthService {

  UserInfoDto join(JoinDto joinDto);
}
