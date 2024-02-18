package com.goo.bikerelocationproject.service.impl;

import static com.goo.bikerelocationproject.type.ErrorCode.ALREADY_EXIST_USERNAME;

import com.goo.bikerelocationproject.data.dto.auth.JoinDto;
import com.goo.bikerelocationproject.data.dto.auth.UserInfoDto;
import com.goo.bikerelocationproject.data.entity.CustomUser;
import com.goo.bikerelocationproject.exception.StationException;
import com.goo.bikerelocationproject.repository.UserRepo;
import com.goo.bikerelocationproject.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepo userRepo;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public UserInfoDto join(JoinDto joinDto) {
    if (userRepo.existsByEmail(joinDto.getEmail())) {
      throw new StationException(ALREADY_EXIST_USERNAME);
    }

    CustomUser user = JoinDto.toEntity(joinDto);
    user.setPassword(bCryptPasswordEncoder.encode(joinDto.getPassword()));

    return UserInfoDto.fromEntity(userRepo.save(user));
  }
}
